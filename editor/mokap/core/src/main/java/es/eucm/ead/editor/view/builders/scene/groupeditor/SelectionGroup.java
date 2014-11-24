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
package es.eucm.ead.editor.view.builders.scene.groupeditor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Predicate;
import es.eucm.ead.editor.view.builders.scene.groupeditor.GroupEditor.GroupEditorStyle;
import es.eucm.ead.editor.view.widgets.AbstractWidget;

public class SelectionGroup extends AbstractWidget {

	private GroupEditor groupEditor;

	private GroupEditorStyle style;

	private SelectionBoxPredicate findSelectBox = new SelectionBoxPredicate();

	public SelectionGroup(GroupEditor groupEditor, GroupEditorStyle style) {
		this.groupEditor = groupEditor;
		this.style = style;
	}

	public void pressed(Actor actor) {
		SelectionBox selectionBox = findOrCreateSelectionBox(actor);
		if (selectionBox.isSelected()) {
			selectionBox.moving();
		}
	}

	public void selected(Actor actor) {
		SelectionBox selectionBox = findSelectionBox(actor);
		if (selectionBox != null && selectionBox.isPressed()) {
			selectionBox.selected();
		}
	}

	private SelectionBox findOrCreateSelectionBox(Actor target) {
		SelectionBox selectionBox = findSelectionBox(target);
		return selectionBox == null ? createSelectionBox(target) : selectionBox;
	}

	private SelectionBox findSelectionBox(Actor target) {
		findSelectBox.setActor(target);
		return (SelectionBox) findActor(this, findSelectBox);
	}

	private SelectionBox createSelectionBox(Actor target) {
		SelectionBox selectionBox = Pools.obtain(SelectionBox.class);
		selectionBox.setTarget(target, groupEditor, style);
		addActor(selectionBox);
		return selectionBox;
	}

	public void move(float deltaX, float deltaY) {
		for (Actor selectionBox : getChildren()) {
			if (((SelectionBox) selectionBox).isMoving()) {
				selectionBox.moveBy(deltaX, deltaY);
			} else if (((SelectionBox) selectionBox).isPressed()) {
				removeSelectionBox(selectionBox);
			}
		}
	}

	private void removeSelectionBox(Actor selectionBox) {
		selectionBox.remove();
		Pools.free(selectionBox);
	}

	public void unselect(Actor actor) {
		SelectionBox selectionBox = findSelectionBox(actor);
		removeSelectionBox(selectionBox);
	}

	public static class SelectionBoxPredicate implements Predicate<Actor> {

		private Actor actor;

		public void setActor(Actor actor) {
			this.actor = actor;
		}

		@Override
		public boolean evaluate(Actor selectionBox) {
			return selectionBox.getUserObject() == actor;
		}
	}

}
