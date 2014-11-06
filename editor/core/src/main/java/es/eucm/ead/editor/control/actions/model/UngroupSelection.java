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
package es.eucm.ead.editor.control.actions.model;

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Actions;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.actions.model.scene.RemoveChildFromEntity;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * 
 * Transforms the children of a selected group into individual entities
 * 
 */
public class UngroupSelection extends ModelAction implements SelectionListener {

	private Selection selection;

	private Actions actions;

	public UngroupSelection() {
		super(false, false, new Class[] {});
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		selection = controller.getModel().getSelection();
		actions = controller.getActions();

		controller.getModel().addSelectionListener(this);
		updateEnable();
	}

	@Override
	public Command perform(Object... args) {

		CompositeCommand command = new CompositeCommand();

		ModelEntity group = (ModelEntity) selection
				.getSingle(Selection.SCENE_ELEMENT);

		ModelEntity context = (ModelEntity) selection
				.getSingle(Selection.EDITED_GROUP);

		Array<ModelEntity> entities = group.getChildren();

		for (ModelEntity entity : group.getChildren()) {
			command.addCommand(actions.getAction(RemoveChildFromEntity.class)
					.perform(group, entity));
			command.addCommand(actions.getAction(AddChildToEntity.class)
					.perform(entity, context));
		}

		command.addCommand(actions.getAction(SetSelection.class).perform(
				Selection.EDITED_GROUP, Selection.SCENE_ELEMENT, entities));

		return command;
	}

	private void updateEnable() {
		setEnabled(selection.get(Selection.SCENE_ELEMENT).length == 1
				&& ((ModelEntity) selection.getSingle(Selection.SCENE_ELEMENT))
						.getChildren().size > 0);
	}

	@Override
	public void modelChanged(SelectionEvent event) {
		updateEnable();
	}

	@Override
	public boolean listenToContext(String contextId) {
		return contextId.equals(Selection.SCENE_ELEMENT);
	}

}
