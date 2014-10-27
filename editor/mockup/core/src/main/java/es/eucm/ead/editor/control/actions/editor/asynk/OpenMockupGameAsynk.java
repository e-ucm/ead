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
package es.eucm.ead.editor.control.actions.editor.asynk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;

import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.control.Toasts;
import es.eucm.ead.editor.control.actions.editor.OpenMockupGame;

/**
 * Opens the MockupGame asynchronously without blocking the main thread.
 * 
 * @see OpenMockupGame
 */
public class OpenMockupGameAsynk extends OpenMockupGame {

	private String path;
	private boolean done = true;

	private Toasts toasts;

	@Override
	public void perform(Object... args) {
		if (!done) {
			return;
		}
		done = false;

		updateTransition(args);
		toasts = ((MockupViews) controller.getViews()).getToasts();
		toasts.showNotification(controller.getApplicationAssets().getI18N()
				.m("openGame"));

		path = args[0].toString();
		Gdx.app.postRunnable(save);
	}

	private Runnable save = new Runnable() {

		@Override
		public void run() {
			load(path);
			MockupController mockupController = ((MockupController) controller);
			mockupController.getRootComponent().addActor(asynkAction);
		}
	};

	private Actor asynkAction = new Actor() {

		@Override
		public void act(float delta) {
			if (controller.getEditorGameAssets().isDoneLoading()) {
				toasts.hideNotification();
				finishLoading(path);
				remove();
				done = true;
			}
		}
	};

}
