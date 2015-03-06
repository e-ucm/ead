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

import java.util.Comparator;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Predicate;

import es.eucm.ead.editor.view.builders.scene.groupeditor.GroupEditor.GroupEditorStyle;
import es.eucm.ead.engine.gdx.AbstractWidget;

public class SelectionGroup extends AbstractWidget {

	private GroupEditor groupEditor;

	private GroupEditorStyle style;

	private SelectionBoxPredicate findSelectBox = new SelectionBoxPredicate();

	private Array<Actor> selection = new Array<Actor>();

	private ActorComparator comparator;

	public SelectionGroup(GroupEditor groupEditor, GroupEditorStyle style) {
		this.groupEditor = groupEditor;
		this.style = style;
		this.comparator = new ActorComparator();
	}

	public void pressed(Actor actor) {
		findOrCreateSelectionBox(actor);
	}

	public void select(Actor actor) {
		if (!groupEditor.isMultipleSelection()) {
			clearChildren();
		}

		if (groupEditor.isMultipleSelection()
				&& selection.contains(actor, true)) {
			removeFromSelection(actor);
		} else {
			SelectionBox selectionBox = findOrCreateSelectionBox(actor);
			if (selectionBox != null) {
				selection.add(actor);
				selection.sort(comparator);
				selectionBox.selected();
			}
		}
	}

	public void removeFromSelection(Actor actor) {
		selection.removeValue(actor, true);
		SelectionBox selectionBox = findSelectionBox(actor);
		if (selectionBox != null) {
			remove(selectionBox);
		}
	}

	@Override
	public void clearChildren() {
		Actor[] actors = getChildren().begin();
		for (int i = 0, n = getChildren().size; i < n; i++) {
			removeActor(actors[i]);
		}
		getChildren().end();
		getChildren().clear();
		selection.clear();
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
			selectionBox.moveBy(deltaX, deltaY);
		}
	}

	private void remove(Actor selectionBox) {
		selectionBox.remove();
		Pools.free(selectionBox);
	}

	public Array<Actor> getSelection() {
		return selection;
	}

	public void refreshSelectionBoxes() {
		for (Actor selectionBox : getChildren()) {
			((SelectionBox) selectionBox).readTargetBounds();
		}
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

	public static class ActorComparator implements Comparator<Actor> {

		@Override
		public int compare(Actor actor, Actor actor2) {
			return actor.getZIndex() - actor2.getZIndex();
		}
	}

}
