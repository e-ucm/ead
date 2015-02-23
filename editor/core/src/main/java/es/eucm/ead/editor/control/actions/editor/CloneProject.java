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

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.background.BackgroundExecutor.BackgroundTaskListener;
import es.eucm.ead.editor.control.background.CloneProjectTask;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.EmptyCommand;

/**
 * <p>
 * Clones a project
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * *
 * <dd><strong>args[0]</strong> <em>{@link BackgroundTaskListener}</em> Will be
 * notified with the path of the cloned project once it's finished cloning</dd>
 * <dd><strong>args[1]</strong> <em>Array</em> A list with the existing project
 * names. It will be used to generate an unique name</dd>
 * <dd><strong>args[2]</strong> <em>String (optional)</em> path to the project
 * to clone. If it is not set, it uses the path in {@link Selection#RESOURCE}</dd>
 * </dl>
 */
public class CloneProject extends ModelAction {

	public CloneProject() {
		super(true, false, new Class[] { BackgroundTaskListener.class,
				Array.class }, new Class[] { BackgroundTaskListener.class,
				Array.class, String.class });
	}

	@Override
	public Command perform(Object... args) {
		BackgroundTaskListener<Object[]> listener = (BackgroundTaskListener<Object[]>) args[0];
		String path = (String) (args.length == 3 ? args[2] : controller
				.getModel().getSelection().getSingle(Selection.RESOURCE));
		controller.getBackgroundExecutor().submit(
				new CloneProjectTask(controller, path, (Array) args[1]),
				listener);
		// An empty command with resources modified to true forces pending
		// actions tha currently can be undone, like DeleteProject
		return new EmptyCommand(true);
	}
}
