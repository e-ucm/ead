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
package es.eucm.ead.editor.control.actions.model.scene;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.control.commands.ListCommand.AddToListCommand;
import es.eucm.ead.editor.control.commands.ListCommand.RemoveFromListCommand;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldNames;

/**
 * Reads the hierarchy of a recently unbgrouped group, performing the necessary
 * commands over the {@link ModelEntity}s associated to the arguments to
 * replicate the actual hierarchy in the given arguments.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Group}</em> the parent of the
 * ungrouped actors</dd>
 * <dd><strong>args[1]</strong> <em>{@link Group}</em> the old group, removed
 * from the parent</dd>
 * <dd><strong>args[2]</strong> <em>{@link Array}</em> an array with
 * {@link Actor}s forming the old group</dd>
 * </dl>
 */
public class UngroupHierarchyToEntities extends ModelAction {

	private MultipleActorTransformToEntity multipleActorTransformToEntity;

	public UngroupHierarchyToEntities() {
		super(true, false, Group.class, Group.class, Array.class);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		multipleActorTransformToEntity = controller.getActions().getAction(
				MultipleActorTransformToEntity.class);
	}

	@Override
	public Command perform(Object... args) {

		Group parent = (Group) args[0];
		Group oldGroup = (Group) args[1];
		Array<Actor> ungrouped = (Array<Actor>) args[2];

		ModelEntity parentEntity = Model.getModelEntity(parent);
		ModelEntity oldGroupEntity = Model.getModelEntity(oldGroup);

		// Copy actors transformations
		CompositeCommand command = multipleActorTransformToEntity
				.perform(ungrouped);

		// Remove from old group and add it to new group
		for (Actor actor : ungrouped) {
			ModelEntity actorEntity = Model.getModelEntity(actor);

			command.addCommand(new RemoveFromListCommand(oldGroupEntity
					.getChildren(), actorEntity));

			command.addCommand(new AddToListCommand(parentEntity.getChildren(),
					actorEntity));
			command.addCommand(new FieldCommand(Model.getComponent(actorEntity,
					Parent.class), FieldNames.PARENT, parentEntity));

		}

		// Remove old group
		command.addCommand(new RemoveFromListCommand(
				parentEntity.getChildren(), oldGroupEntity));

		return command;
	}
}
