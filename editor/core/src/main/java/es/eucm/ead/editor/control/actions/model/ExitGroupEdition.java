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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.actions.model.scene.RemoveChildFromEntity;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Created by angel on 5/12/14.
 */
public class ExitGroupEdition extends ModelAction {

	private SetSelection setSelection;

	private RemoveChildFromEntity removeChildFromEntity;

	private AddChildToEntity addChildToEntity;

	public ExitGroupEdition() {
		super(true, false, Group.class, Group.class, Actor.class);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		setSelection = controller.getActions().getAction(SetSelection.class);
		removeChildFromEntity = controller.getActions().getAction(
				RemoveChildFromEntity.class);
		addChildToEntity = controller.getActions().getAction(
				AddChildToEntity.class);
	}

	@Override
	public Command perform(Object... args) {
		Group parent = (Group) args[0];
		Group oldGroup = (Group) args[1];
		Actor simplifiedGroup = (Actor) args[2];

		CompositeCommand commands = new CompositeCommand();

		ModelEntity editedGroup = Q.getModelEntity(parent);
		ModelEntity oldGroupEntity = Q.getModelEntity(oldGroup);
		ModelEntity sceneElement = Q.getModelEntity(simplifiedGroup);

		if (oldGroupEntity != sceneElement) {
			commands.addCommand(removeChildFromEntity.perform(editedGroup,
					oldGroupEntity));
			commands.addCommand(addChildToEntity.perform(sceneElement,
					editedGroup));
		}

		if (editedGroup != null) {
			commands.addCommand(setSelection.perform(Selection.SCENE,
					Selection.EDITED_GROUP, editedGroup));
			commands.addCommand(setSelection.perform(Selection.EDITED_GROUP,
					Selection.SCENE_ELEMENT, sceneElement));
		}
		return commands;
	}
}
