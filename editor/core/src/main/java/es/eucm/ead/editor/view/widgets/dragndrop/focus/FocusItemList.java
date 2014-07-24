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
package es.eucm.ead.editor.view.widgets.dragndrop.focus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Pools;

import es.eucm.ead.editor.view.widgets.dragndrop.DraggableLinearLayout;

/**
 * A {@link ScrollPane} that holds a list of {@link FocusItem items} that can
 * gain focus by clicking on them.
 */
public class FocusItemList extends DraggableLinearLayout {

	protected FocusItem currentFocus;

	/**
	 * Creates a horizontal focus item list with draggable elements.
	 */
	public FocusItemList() {
		this(true);
	}

	public FocusItemList(boolean horizontal) {
		super(horizontal);

		addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor target = event.getTarget();

				while (target != null && !(target instanceof FocusItem)) {
					target = target.getParent();
				}

				if (target != null) {
					FocusItem curr = ((FocusItem) target);
					if (currentFocus != curr) {
						setFocus(curr, true);
					}
				}
			}
		});
	}

	private void setFocus(FocusItem curr, boolean fire) {
		if (currentFocus != null) {
			currentFocus.setFocus(false);
		}
		curr.setFocus(true);
		if (fire) {
			fireFocus(curr);
		}
		currentFocus = curr;
	}

	public void addActor(FocusItem item) {
		addActorAt(-1, item);
	}

	public void addActorAt(int index, FocusItem item) {
		item.pad(PAD);
		super.addActorAt(index, item);
	}

	@Override
	protected void centerScrollAt(Actor actor) {
		super.centerScrollAt(actor);
		if (actor instanceof FocusItem) {
			setFocus((FocusItem) actor, false);
		}
	}

	/**
	 * Base class to listen to {@link FocusEvent}s produced by
	 * {@link FocusItemList}.
	 */
	public static class FocusListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			if (event instanceof FocusEvent) {
				focusChanged((FocusEvent) event);
			}
			return true;
		}

		/**
		 * A new actor has gained focus
		 * 
		 * @param event
		 */
		public void focusChanged(FocusEvent event) {

		}
	}

	public static class FocusEvent extends Event {

		private Actor actor;

		public Actor getActor() {
			return actor;
		}

		public void setActor(Actor actor) {
			this.actor = actor;
		}

		@Override
		public void reset() {
			super.reset();
			this.actor = null;
		}
	}

	/**
	 * Fires that some actor has gained focus
	 */
	private void fireFocus(Actor actor) {
		FocusEvent dropEvent = Pools.obtain(FocusEvent.class);
		dropEvent.actor = actor;
		fire(dropEvent);
		Pools.free(dropEvent);
	}
}
