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
package es.eucm.ead.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController;
import es.eucm.ead.editor.control.actions.editor.OpenApplication;
import es.eucm.ead.editor.control.actions.editor.Save;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;

public class MokapApplicationListener extends EditorApplicationListener {

	private SaveTask saveTask = new SaveTask();

	public MokapApplicationListener(Platform platform) {
		super(platform);
	}

	@Override
	public void create() {
		Gdx.graphics.setContinuousRendering(false);
		Timer.schedule(saveTask, 20, 20);
		super.create();
	}

	@Override
	protected void initialize() {
		super.initialize();
		controller.action(OpenApplication.class);
		stage.setActionsRequestRendering(true);
	}

	@Override
	public void render() {
		super.render();
		if (!controller.getEditorGameAssets().isDoneLoading()
				|| !controller.getApplicationAssets().isDoneLoading()) {
			Gdx.graphics.requestRendering();
		}
	}

	@Override
	public void resize(int width, int height) {
		super.stage.getViewport().update(width, height, true);
	}

	protected Controller buildController() {
		WidgetGroup modalContainer = new WidgetGroup();
		modalContainer.setFillParent(true);

		WidgetGroup viewContainer = new WidgetGroup();
		viewContainer.setFillParent(true);

		stage.addActor(viewContainer);
		stage.addActor(modalContainer);

		MokapController controller = new MokapController(this.platform,
				Gdx.files, viewContainer, modalContainer);
		WidgetBuilder.setController(controller);
		Q.setController(controller);
		return controller;
	}

	@Override
	public void resume() {
		if (!saveTask.isScheduled()) {
			Timer.schedule(saveTask, 20, 20);
		}
		super.resume();
	}

	@Override
	public void pause() {
		saveTask.cancel();
		((MokapController) controller).pause();
		controller.action(Save.class);
	}

	public class SaveTask extends Task {

		private Runnable runnable = new Runnable() {
			@Override
			public void run() {
				controller.action(Save.class);
			}
		};

		@Override
		public void run() {
			Gdx.app.postRunnable(runnable);
			Gdx.graphics.requestRendering();
		}
	}
}
