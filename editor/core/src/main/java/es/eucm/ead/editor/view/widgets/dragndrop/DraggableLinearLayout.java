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
package es.eucm.ead.editor.view.widgets.dragndrop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SnapshotArray;

import es.eucm.ead.editor.view.widgets.dragndrop.DraggableGridLayout.DropGridEvent;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

/**
 * A {@link LinearLayout} that extends {@link DraggableScrollPane} and supports
 * drag'n drop actions with its items.
 * 
 */
public class DraggableLinearLayout extends DraggableScrollPane {

	protected static final float PAD = 5F;

	protected LinearLayout itemsList;

	/**
	 * Creates a horizontal draggable linear layout.
	 */
	public DraggableLinearLayout() {
		this(true);
	}

	public DraggableLinearLayout(boolean horizontal) {
		super(null);
		setWidget(itemsList = new LinearLayout(horizontal).pad(PAD));
		setScrollingDisabled(!horizontal, horizontal);
	}

	@Override
	public void addActor(Actor actor) {
		addActorAt(-1, actor);
	}

	@Override
	public void addActorAt(int index, Actor actor) {
		itemsList.add(index, actor).margin(PAD);
		addSource(newSource(actor));
		addTarget(newTarget(actor));
	}

	/**
	 * Centers the scroll at the actor available in the given index, in the next
	 * frame. This method can be if an actor has just been added and its layout
	 * wasn't invoked yet because this method will be executed at the end of the
	 * frame via {@link Application#postRunnable(Runnable)}.
	 * 
	 * @param index
	 */
	public void centerScrollAt(final int index) {
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				SnapshotArray<Actor> children = itemsList.getChildren();
				if (index < children.size) {
					Actor actor = children.get(index);
					centerScrollAt(actor);
				}
			}
		});
	}

	/**
	 * Invoked when the {@link Runnable#run()}, from
	 * {@link #centerScrollAt(int)} method, gets executed. Convenience method
	 * that could be overridden by subclasses.
	 * 
	 * @param actor
	 */
	protected void centerScrollAt(Actor actor) {
		setScrollX(actor.getX() - getWidth() * .5f + actor.getWidth() * .5f);
	}

	private Source newSource(Actor widget) {
		return new Source(widget) {

			@Override
			public Payload dragStart(InputEvent event, float x, float y,
					int pointer) {
				// Necessary to be able to drag and drop
				setCancelTouchFocus(false);

				// Necessary to stop the scroll from moving while dragging
				cancel();

				// Set the actor displayed while dragging
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
				// Return the ScrollPane to its original state
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

	private Target newTarget(Actor widget) {
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

				// Compute its new position
				Actor targetActor = getActor();
				int targetIdx = children.indexOf(targetActor, true);

				if (x > targetActor.getWidth() * .5f) {
					++targetIdx;
				}

				Actor dropActor = payload.getDragActor();
				itemsList.add(targetIdx, dropActor);
				centerScrollAt(targetIdx);
				fireDrop(dropActor, (Integer) payload.getObject(), targetIdx);
			}

			/**
			 * Fires that some actor has been dropped
			 */
			private void fireDrop(Actor actor, int oldIndex, int newIndex) {
				DropListEvent dropEvent = Pools.obtain(DropListEvent.class);
				dropEvent.actor = actor;
				dropEvent.oldIndex = oldIndex;
				dropEvent.newIndex = newIndex;
				fire(dropEvent);
				Pools.free(dropEvent);
			}
		};
	}

	/**
	 * Base class to listen to {@link DropListEvent}s produced by
	 * {@link DraggableLinearLayout}. Also listens to {@link DropGridEvent}s
	 * produced by {@link DraggableGridLayout}.
	 */
	public static class DropListener<T extends Event> implements EventListener {

		@Override
		public boolean handle(Event event) {
			if ((event instanceof DropListEvent)
					|| (event instanceof DropGridEvent)) {
				actorDropped((T) event);
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
		public void actorDropped(T event) {

		}
	}

	public static class DropListEvent extends Event {

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
	}
}
