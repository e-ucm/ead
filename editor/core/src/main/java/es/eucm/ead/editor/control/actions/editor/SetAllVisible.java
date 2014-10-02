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
package es.eucm.ead.editor.control.actions.editor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.control.commands.SelectionCommand;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.editor.components.ElementState;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;

/**
 * Does visible all actors in current scene.
 */
public class SetAllVisible extends ModelAction {

	@Override
	public Command perform(Object... args) {

		ModelEntity sceneEntity = (ModelEntity) controller.getModel()
				.getSelection().getSingle(Selection.SCENE);

		CompositeCommand command = new CompositeCommand();
		if (sceneEntity != null) {
			EngineEntity scene = controller.getEngine().getEntitiesLoader()
					.toEngineEntity(sceneEntity);
			setAllVisible(scene.getGroup(), command);
		}
		command.addCommand(new SelectionCommand(controller.getModel(), null,
				Selection.SCENE, sceneEntity));
		command.addCommand(new SelectionCommand(controller.getModel(),
				Selection.SCENE, Selection.SCENE_ELEMENT, new Object[] {}));
		return command;
	}

	private void setAllVisible(Actor actor, CompositeCommand command) {
		ModelEntity entity = Q.getModelEntity(actor);
		if (entity != null) {
			ElementState invisibility = Q.getComponent(entity,
					ElementState.class);
			if (invisibility.isInvisible()) {
				command.addCommand(new FieldCommand(invisibility,
						FieldName.INVISIBLE, false));
				command.addCommand(new FieldCommand(actor, FieldName.VISIBLE,
						true));
			}
			if (actor instanceof Group) {
				for (Actor child : ((Group) actor).getChildren()) {
					setAllVisible(child, command);
				}
			}
		}

	}
}
