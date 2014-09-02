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

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

import com.badlogic.gdx.scenes.scene2d.Stage;

import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.background.BackgroundExecutor.BackgroundTaskListener;
import es.eucm.ead.editor.control.background.BackgroundTask;

public class OpenMockupGame extends OpenGame {
	/**
	 * Saving interval in seconds.
	 */
	private static final float SAVE_DELAY = 30f;

	private boolean saving = false;

	@Override
	public void perform(Object... args) {
		String path = args[0].toString();
		fileChosen(path);
		if (!path.equals(controller.getEditorGameAssets().getLoadingPath())) {
			throw new EditorActionException("Failed opening: " + path
					+ ", probably deleted.");
		}
		if (!saving) {
			saving = true;
			((Stage) args[1])
					.addAction(forever(delay(SAVE_DELAY, run(saveGame))));
		}
	}

	private final Runnable saveGame = new Runnable() {
		@Override
		public void run() {
			controller.getBackgroundExecutor().submit(saveTask, saveListener);
		}

		private final BackgroundTaskListener<Boolean> saveListener = new BackgroundTaskListener<Boolean>() {

			@Override
			public void completionPercentage(float percentage) {
			}

			@Override
			public void done(BackgroundExecutor backgroundExecutor,
					Boolean result) {
			}

			@Override
			public void error(Throwable e) {
			}
		};

		private final BackgroundTask<Boolean> saveTask = new BackgroundTask<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				controller.action(ForceSave.class);
				return true;
			}
		};
	};
}
