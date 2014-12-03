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
package es.eucm.ead.editor.view.builders.scene.groupeditor.input;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.view.builders.scene.SceneEditor;
import es.eucm.ead.editor.view.builders.scene.groupeditor.GroupEditor;
import es.eucm.ead.editor.view.builders.scene.groupeditor.SelectionBox;
import es.eucm.ead.editor.view.builders.scene.groupeditor.SelectionGroup;
import es.eucm.ead.editor.view.builders.scene.groupeditor.inputstatemachine.InputStateMachine;
import es.eucm.ead.engine.utils.EngineUtils;

public class EditStateMachine extends InputStateMachine {

	private SceneEditor sceneEditor;

	private GroupEditor groupEditor;

	private SelectionGroup selectionGroup;

	SelectionBox selectionBox;

	Actor actor;

	public EditStateMachine(SceneEditor sceneEditor, GroupEditor groupEditor,
			SelectionGroup selectionGroup) {
		this.sceneEditor = sceneEditor;
		this.groupEditor = groupEditor;
		this.selectionGroup = selectionGroup;
		addState(new NoPointersState(this));
		addState(new NothingPressedState(this));
		addState(new SelectionBoxPressedState(this));
		addState(new ActorPressedState(this));
		addState(new ScaleState(this));
		addState(new RotateState(this));
		addState(new CameraPanState(this));
		setState(NoPointersState.class);
	}

	@Override
	public void drag(InputEvent event, float x, float y, int pointer) {
		super.drag(event, x, y, pointer);
		selectionGroup.refreshSelectionBoxes();
	}

	public void fireTransformed() {
		groupEditor.fireTransformed();
	}

	public void fireSelection() {
		groupEditor.fireSelection();
	}

	Array<Actor> getSelection() {
		return selectionGroup.getSelection();
	}

	public boolean isMultiSelection() {
		return groupEditor.isMultipleSelection();
	}

	public boolean isOnlySelection() {
		return groupEditor.isOnlySelection();
	}

	void setSelectionBox(SelectionBox selectionBox) {
		this.selectionBox = selectionBox;
		this.actor = selectionBox.getTarget();
	}

	void enterFullScreen() {
		sceneEditor.enterFullScreen();
	}

	void exitFullScreen() {
		sceneEditor.exitFullScreen();
	}

	void unsetActors() {
		this.selectionBox = null;
		this.actor = null;
	}

	void setActor(Actor actor) {
		this.selectionBox = null;
		this.actor = EngineUtils.getDirectChild(groupEditor.getRootGroup(),
				actor);
	}

	void pressActor() {
		selectionGroup.pressed(actor);
	}

	void unpressActor() {
		selectionGroup.removeFromSelection(actor);
	}

	void clearSelection() {
		selectionGroup.clearChildren();
	}

	void selectActor() {
		selectionGroup.select(actor);
		fireSelection();
	}

	void pressSelectBox() {
		selectionBox.moving();
	}

	void unpressSelectBox() {
		selectionBox.selected();
	}

	void move() {
		selectionGroup.move(-getDeltaX1(), -getDeltaY1());
	}

	public void pan() {
		groupEditor.pan(-getDeltaX1(), -getDeltaY1());
	}

	void showLayerSelector(float x, float y) {
		setState(NoPointersState.class);
		groupEditor.selectLayer(x, y);
	}

	Actor getTarget(InputEvent event) {
		if (event.getTarget() == groupEditor) {
			return null;
		} else {
			Actor actor = EngineUtils.getDirectChild(selectionGroup,
					event.getTarget());
			if (actor == null) {
				return EngineUtils.getDirectChild(groupEditor.getRootGroup(),
						event.getTarget());
			} else {
				return actor;
			}
		}
	}

}
