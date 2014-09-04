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
package es.eucm.ead.editor.control.actions;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.scene.NewScene;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.ListCommand.AddToListCommand;
import es.eucm.ead.editor.control.commands.ListCommand.RemoveFromListCommand;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * 
 * Adds a {@link Behavior } with {@link GoScene} {@link Effect} that goes to a
 * new scene created in this action.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Behavior}</em> where is added the
 * {@link GoScene} if is null creates a new {@link Behavior}, if not replaces
 * the effect</dd>
 * </dl>
 */
public class SelectionGoToNewScene extends ModelAction {

	public SelectionGoToNewScene() {
		super(true, true, Behavior.class);
	}

	@Override
	public Command perform(Object... args) {

		Model model = controller.getModel();

		String id = model.createId(ResourceCategory.SCENE);
		ModelEntity scene = new ModelEntity();

		CompositeCommand command = controller.getActions()
				.getAction(NewScene.class).perform("", id, scene);

		ModelEntity sceneElement = (ModelEntity) model.getSelection()
				.getSingle(Selection.SCENE_ELEMENT);
		Behavior oldBehavior = (Behavior) args[0];

		Behavior behavior = new Behavior();
		behavior.setEvent(new Touch());
		GoScene go = new GoScene();
		go.setSceneId(id);
		behavior.getEffects().add(go);

		if (oldBehavior != null) {
			command.addCommand(new RemoveFromListCommand(sceneElement,
					sceneElement.getComponents(), oldBehavior));
		}

		command.addCommand(new AddToListCommand(sceneElement, sceneElement
				.getComponents(), behavior));

		return command;
	}

}
