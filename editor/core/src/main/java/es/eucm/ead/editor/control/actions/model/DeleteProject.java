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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import es.eucm.ead.editor.control.Commands;
import es.eucm.ead.editor.control.Commands.CommandListener;
import es.eucm.ead.editor.control.Commands.CommandsStack;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.editor.model.events.ResourceEvent;
import es.eucm.ead.editor.model.events.ResourceEvent.Type;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * <p>
 * Deletes a project
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * *
 * <dd><strong>args[0]</strong> <em>{@link Float}</em> time to wait before
 * actually delete the project (enabling undo for that time)</dd>
 * <dd><strong>args[1]</strong> <em>String (optional)</em> path to the project
 * to delete. If it is not set, it uses the path in {@link Selection#RESOURCE}</dd>
 * </dl>
 */
public class DeleteProject extends ModelAction {

	public DeleteProject() {
		super(true, false, new Class[] { Float.class }, new Class[] {
				Float.class, String.class });
	}

	@Override
	public Command perform(Object... args) {
		float delay = (Float) args[0];
		String path = (String) (args.length == 2 ? args[1] : controller
				.getModel().getSelection().getSingle(Selection.RESOURCE));
		return new DeleteProjectCommand(path, delay);
	}

	class DeleteProjectCommand extends Command implements CommandListener {

		DeleteTask task;

		float delay;

		DeleteProjectCommand(String path, float delay) {
			this.task = new DeleteTask(path);
			this.delay = delay;
		}

		@Override
		public ModelEvent doCommand() {
			Timer.schedule(task, delay);
			controller.getCommands().addCommandListener(this);
			return new ResourceEvent(Type.REMOVED, controller.getModel(),
					task.projectPath, null, ResourceCategory.GAME);
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public ModelEvent undoCommand() {
			if (task.isScheduled()) {
				task.cancel();
				return new ResourceEvent(Type.ADDED, controller.getModel(),
						task.projectPath, null, ResourceCategory.GAME);
			}
			return null;
		}

		@Override
		public boolean modifiesResource() {
			return false;
		}

		private void forceDelete() {
			if (task.isScheduled()) {
				task.cancel();
				task.run();
			}
		}

		@Override
		public void doCommand(Commands commands, Command command) {
			if (command.modifiesResource()) {
				forceDelete();
			}
		}

		@Override
		public void undoCommand(Commands commands, Command command) {
		}

		@Override
		public void redoCommand(Commands commands, Command command) {
		}

		@Override
		public void savePointUpdated(Commands commands, Command savePoint) {
		}

		@Override
		public void cleared(Commands commands) {
		}

		@Override
		public void contextPushed(Commands commands) {
			forceDelete();
		}

		@Override
		public void contextPopped(Commands commands,
				CommandsStack poppedContext, boolean merge) {
			forceDelete();
		}

		class DeleteTask extends Task {

			private String projectPath;

			DeleteTask(String projectPath) {
				this.projectPath = projectPath;
			}

			@Override
			public void run() {
				FileHandle projectFh = Gdx.files.absolute(projectPath);
				if (projectFh.exists() && projectFh.isDirectory()) {
					projectFh.deleteDirectory();
				}
				controller.getCommands().removeCommandListener(
						DeleteProjectCommand.this);
			}
		}
	}
}
