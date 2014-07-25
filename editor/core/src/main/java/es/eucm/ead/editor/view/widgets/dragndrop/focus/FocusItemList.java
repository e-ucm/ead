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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

import es.eucm.ead.editor.view.widgets.dragndrop.DraggableLinearLayout;

/**
 * A {@link ScrollPane} that holds a list of {@link FocusItem items} that can
 * gain focus by clicking on them.
 */
public class FocusItemList extends DraggableLinearLayout {

	protected ButtonGroup buttonGroup;

	/**
	 * Creates a horizontal focus item list with draggable elements.
	 */
	public FocusItemList() {
		this(true);
	}

	public FocusItemList(boolean horizontal) {
		super(horizontal);
		buttonGroup = new ButtonGroup();
	}

	public void addActor(Actor actor) {
		addActorAt(-1, actor);
	}

	public void addActorAt(int index, Actor actor) {
		if (actor instanceof Button) {
			buttonGroup.add((Button) actor);
		}
		super.addActorAt(index, actor);
	}

	@Override
	protected void clearDrag() {
		buttonGroup.getAllChecked().clear();
		buttonGroup.getButtons().clear();
		super.clearDrag();
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
}
