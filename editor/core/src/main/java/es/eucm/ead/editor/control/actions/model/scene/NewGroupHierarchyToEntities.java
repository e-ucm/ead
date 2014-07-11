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
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.control.commands.ListCommand.AddToListCommand;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;

/**
 * Reads the hierarchy of a recently created group, performing the necessary
 * commands over the {@link ModelEntity}s associated to the arguments to
 * replicate the actual hierarchy in the given arguments.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Group}</em> the parent of the new
 * group</dd>
 * <dd><strong>args[1]</strong> <em>{@link Group}</em> the recently created
 * group</dd>
 * <dd><strong>args[2]</strong> <em>{@link Array}</em> an array with
 * {@link Actor}s forming the new group</dd>
 * </dl>
 */
public class NewGroupHierarchyToEntities extends ModelAction {

	private Array<ModelEntity> tmpEntities = new Array<ModelEntity>();

	private GameLoop gameLoop;

	private RemoveChildrenFromEntity removeChildrenFromEntity;

	private MultipleActorTransformToEntity multipleActorTransformToEntity;

	public NewGroupHierarchyToEntities() {
		super(true, false, Group.class, Group.class, Array.class);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		gameLoop = controller.getEngine().getGameLoop();
		removeChildrenFromEntity = controller.getActions().getAction(
				RemoveChildrenFromEntity.class);
		multipleActorTransformToEntity = controller.getActions().getAction(
				MultipleActorTransformToEntity.class);
	}

	@Override
	public CompositeCommand perform(Object... args) {
		Group parent = (Group) args[0];
		Group newGroup = (Group) args[1];
		Array<Actor> grouped = (Array<Actor>) args[2];

		ModelEntity parentEntity = Q.getModelEntity(parent);

		tmpEntities.clear();

		// Remove grouped children from their old parent
		for (Actor actor : grouped) {
			ModelEntity entity = Q.getModelEntity(actor);
			if (entity != null) {
				tmpEntities.add(entity);
			}
		}
		CompositeCommand command = removeChildrenFromEntity.perform(
				parentEntity, tmpEntities);

		// Create entity for new group
		ModelEntity newGroupEntity = new ModelEntity();
		newGroupEntity.setX(newGroup.getX());
		newGroupEntity.setY(newGroup.getY());
		newGroupEntity.setOriginX(newGroup.getOriginX());
		newGroupEntity.setOriginY(newGroup.getOriginY());
		newGroupEntity.setScaleY(newGroup.getScaleY());
		newGroupEntity.setScaleX(newGroup.getScaleX());
		newGroupEntity.setRotation(newGroup.getRotation());
		Q.getComponent(newGroupEntity, Parent.class).setParent(parentEntity);

		EngineEntity engineEntity = gameLoop.createEntity();
		engineEntity.setGroup(newGroup);
		engineEntity.setModelEntity(newGroupEntity);

		// Add new group to parent. To listen correctly to events, the group
		// must be added first to the parent, and then children to the new group
		command.addCommand(new AddToListCommand(parentEntity, parentEntity
				.getChildren(), newGroupEntity));

		for (Actor actor : grouped) {
			ModelEntity entity = Q.getModelEntity(actor);
			if (entity != null) {
				command.addCommand(new FieldCommand(Q.getComponent(entity,
						Parent.class), FieldName.PARENT, newGroupEntity));
				command.addCommand(new AddToListCommand(newGroupEntity,
						newGroupEntity.getChildren(), entity));
			}
		}

		command.addAll(multipleActorTransformToEntity.perform(grouped)
				.getCommandList());
		return command;
	}
}
