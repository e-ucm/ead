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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController;
import es.eucm.ead.editor.control.actions.editor.ImportProject;
import es.eucm.ead.editor.control.actions.editor.OpenApplication;
import es.eucm.ead.editor.control.actions.editor.Save;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.platform.MokapPlatform;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.home.HomeView;

public class MokapApplicationListener extends EditorApplicationListener {

	private SaveTask saveTask = new SaveTask();

	private Label performance;

	private float time;

	public MokapApplicationListener(Platform platform) {
		super(platform);
	}

	@Override
	public void create() {
		Gdx.graphics.setContinuousRendering(false);
		super.create();
	}

	@Override
	protected void initialize() {
		super.initialize();
		controller.getModel().addLoadListener(
				new UpdateSaveTaskAfterLoadUnload());
		controller.action(OpenApplication.class);
		stage.setActionsRequestRendering(true);
		if (platform.isDebug()) {
			performance = new Label("", controller.getApplicationAssets()
					.getSkin()
					.get(SkinConstants.STYLE_PERFORMANCE, LabelStyle.class));
			performance.setTouchable(Touchable.disabled);
			performance.setAlignment(Align.bottomLeft);
			stage.addActor(performance);
		}
	}

	@Override
	public void render() {
		super.render();
		if (platform.isDebug()) {
			time += Gdx.graphics.getDeltaTime();
			int div = (int) (time * 10) % 4;

			String perf = "";
			perf += "Rnderng: "
					+ (div == 0 ? "|" : div == 1 ? "/" : div == 2 ? "-" : "\\")
					+ "\n";
			perf += "MokAsst: " + controller.getEditorGameAssets().count()
					+ "\n";
			perf += "AppAsst: " + controller.getApplicationAssets().count()
					+ "\n";
			perf += "NatHeap: " + Gdx.app.getNativeHeap() / 1000000 + " MB\n";
			perf += "JavHeap: " + Gdx.app.getJavaHeap() / 1000000 + " MB\n";
			perf += "Workers: " + controller.getWorkerExecutor().countWorkers()
					+ "\n";
			perf += controller.getModel().countListeners();
			perf += "BgTasks: "
					+ controller.getBackgroundExecutor().countTasks() + "\n";
			performance.setText(perf);
			performance.pack();
		}
	}

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

		return new MokapController(this.platform, Gdx.files, viewContainer,
				modalContainer);
	}

	@Override
	public void resume() {
		if (!saveTask.isScheduled()) {
			Timer.schedule(saveTask, 20, 20);
		}
		super.resume();
		handleImport();
	}

	private void handleImport() {
		MokapPlatform platform = (MokapPlatform) controller.getPlatform();
		Object[] appArgs = platform.getApplicationArguments();
		String importProjectPath = (appArgs == null || appArgs.length != 1) ? null
				: (String) appArgs[0];
		ViewBuilder currentView = controller.getViews().getCurrentView();
		Class elseView = currentView == null ? HomeView.class : currentView
				.getClass();
		if (importProjectPath != null && !importProjectPath.isEmpty()
				&& importProjectPath.endsWith(ProjectUtils.ZIP_EXTENSION)) {
			controller.action(ImportProject.class, elseView,
					new OpenApplication.ShowErrorToastCallback(controller));
		}
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

	/**
	 * Make sure SaveTask is canceled when a game is unloaded, and resumed when
	 * it a new one is loaded
	 */
	public class UpdateSaveTaskAfterLoadUnload implements
			Model.ModelListener<LoadEvent> {
		public void modelChanged(LoadEvent event) {
			if (event.getType() == LoadEvent.Type.LOADED
					&& !saveTask.isScheduled()) {
				Timer.schedule(saveTask, 20, 20);
			} else if (event.getType() == LoadEvent.Type.UNLOADED
					&& saveTask.isScheduled()) {
				saveTask.cancel();
			}
		}
	}
}
