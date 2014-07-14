/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2014 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          CL Profesor Jose Garcia Santesmases 9,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.eucm.ead.editor.view.widgets.focus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SnapshotArray;

import es.eucm.ead.editor.view.widgets.DraggableScrollPane;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

/**
 * A {@link ScrollPane} that holds a list of {@link FocusItem items} that can
 * gain focus by clicking on them.
 */
public class FocusItemList extends DraggableScrollPane {

	protected static final float PAD = 5F;

	protected LinearLayout itemsList;

	protected FocusItem currentFocus;

	/**
	 * Creates a horizontal focus item list with draggable elements.
	 */
	public FocusItemList() {
		this(true);
	}

	public FocusItemList(boolean horizontal) {
		super(null);

		itemsList = new LinearLayout(horizontal);
		setWidget(itemsList);
		itemsList.pad(PAD);

		setScrollingDisabled(false, true);

		addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor target = event.getTarget();

				while (target != null && !(target instanceof FocusItem)) {
					target = target.getParent();
				}

				if (target != null) {
					FocusItem curr = ((FocusItem) target);
					setFocusIfNeeded(curr);
				}
			}
		});
	}

	private void setFocusIfNeeded(FocusItem curr) {
		if (currentFocus != curr) {
			if (currentFocus != null) {
				setFocus(currentFocus, false);
			}
			setFocus(curr, true);
			fireEvent(curr, -1, -1, DropEvent.Type.FOCUSED);
			currentFocus = curr;
		}
	}

	public void addFocusItem(FocusItem image) {
		addFocusItemAt(-1, image);
	}

	public void addFocusItemAt(int index, FocusItem item) {
		item.pad(PAD);
		itemsList.add(index, item).margin(PAD);
		addSource(newSource(item));
		addTarget(newTarget(item));
	}

	protected void setFocus(FocusItem item, boolean focus) {
		item.setFocus(focus);
	}

	public void centerScrollAt(final int index) {
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				Actor actor = itemsList.getChildren().get(index);
				setScrollX(actor.getX() - getWidth() * .5f + actor.getWidth()
						* .5f);
				if (actor instanceof FocusItem) {
					FocusItem newFocus = ((FocusItem) actor);
					if (currentFocus != null) {
						currentFocus.setFocus(false);
					}
					newFocus.setFocus(true);
					currentFocus = newFocus;
				}
			}
		});
	}

	private Source newSource(FocusItem widget) {
		return new Source(widget) {

			@Override
			public Payload dragStart(InputEvent event, float x, float y,
					int pointer) {
				// Necessary to be able to drag and drop
				setCancelTouchFocus(false);

				// Necessary to stop the scroll from moving while dragging
				cancel();

				// Set the actor displayed while dragging
				// It must have the same size as the source image
				Actor actor = getActor();
				Payload payload = new Payload();
				payload.setDragActor(actor);
				payload.setObject(itemsList.getChildren().indexOf(actor, true));
				itemsList.removeActor(actor);

				return payload;
			}

			@Override
			public void dragStop(InputEvent event, float x, float y,
					int pointer, Payload payload, Target target) {
				// Return the ScrollPane to it's original state
				setCancelTouchFocus(true);

				if (target == null) {
					// The pay load was not dropped over a target, thus put it
					// back to where it came from.
					itemsList.add((Integer) payload.getObject(),
							payload.getDragActor());
				}
			}
		};
	}

	private Target newTarget(FocusItem widget) {
		return new Target(widget) {

			@Override
			public boolean drag(Source source, Payload payload, float x,
					float y, int pointer) {
				return true;
			}

			@Override
			public void drop(Source source, Payload payload, float x, float y,
					int pointer) {
				SnapshotArray<Actor> children = itemsList.getChildren();

				// Compute it's new position
				Actor targetActor = getActor();
				int targetIdx = children.indexOf(targetActor, true);

				// If the position is higher than half of the width of the
				// target frame the add the source after the target
				if (x > targetActor.getWidth() * .5f) {
					++targetIdx;
				}

				Actor dropActor = payload.getDragActor();
				itemsList.add(targetIdx, dropActor);
				centerScrollAt(targetIdx);
				fireEvent(dropActor, (Integer) payload.getObject(), targetIdx,
						DropEvent.Type.DROPPED);
			}
		};
	}

	/**
	 * Base class to listen to {@link DropEvent}s produced by
	 * {@link FocusItemList}.
	 */
	public static class DropListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			if (event instanceof DropEvent) {
				DropEvent dropEvent = (DropEvent) event;
				switch (((DropEvent) event).type) {
				case DROPPED:
					actorDropped(dropEvent);
					break;
				case FOCUSED:
					focusChanged(dropEvent);
					break;
				}
			}
			return true;
		}

		/**
		 * The dragged actor has been dropped on a valid target in a new
		 * position.
		 * 
		 * @param event
		 *            the event
		 */
		public void actorDropped(DropEvent event) {

		}

		/**
		 * A new actor has gained focus
		 * 
		 * @param event
		 */
		public void focusChanged(DropEvent event) {

		}
	}

	public static class DropEvent extends Event {

		private Type type;
		private Actor actor;
		private int oldIndex, newIndex;

		public Actor getActor() {
			return actor;
		}

		public int getOldIndex() {
			return oldIndex;
		}

		public int getNewIndex() {
			return newIndex;
		}

		@Override
		public void reset() {
			super.reset();
			this.actor = null;
		}

		static private enum Type {
			DROPPED, FOCUSED
		}
	}

	/**
	 * Fires that some actor has been dropped or has gained focus
	 */
	private void fireEvent(Actor actor, int oldIndex, int newIndex,
			DropEvent.Type type) {
		DropEvent dropEvent = Pools.obtain(DropEvent.class);
		dropEvent.type = type;
		dropEvent.actor = actor;
		dropEvent.oldIndex = oldIndex;
		dropEvent.newIndex = newIndex;
		fire(dropEvent);
		Pools.free(dropEvent);
	}

}
