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
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Iterates over an array of {@link Actor}s, and apply its transformation to its
 * associated {@link ModelEntity}, if any
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Array}</em> An array with
 * {@link Actor}s/dd>
 * <dd><strong>args[1]</strong> <em>boolean</em> (Optional) if the action must
 * combine with the previous action. Default is {@code false}</dd>
 * </dl>
 * {@link #perform(Object...)} will return {@code null} if no entity is
 * associated with the actor
 */
public class MultipleActorTransformToEntity extends ModelAction {

	private ActorTransformToEntity actorTransformToEntity;

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		actorTransformToEntity = controller.getActions().getAction(
				ActorTransformToEntity.class);
	}

	@Override
	public boolean validate(Object... args) {
		if (args.length == 0 || args.length > 2) {
			return false;
		}

		if (args[0] instanceof Array) {
			Array list = (Array) args[0];
			for (Object o : list) {
				if (!(o instanceof Actor)) {
					return false;
				}
			}
		}

		return args.length == 1 || args[1] instanceof Boolean;
	}

	@Override
	public CompositeCommand perform(Object... args) {
		Array<Actor> actors = (Array<Actor>) args[0];
		boolean combine = args.length > 1 ? (Boolean) args[1] : false;

		CompositeCommand compositeCommand = new CompositeCommand();
		compositeCommand.setForceCombine(combine);

		for (Actor actor : actors) {
			CompositeCommand transform = actorTransformToEntity.perform(actor);
			if (transform != null && transform.getCommandList().size > 0) {
				compositeCommand.addCommand(transform);
			}
		}
		return compositeCommand;
	}
}
