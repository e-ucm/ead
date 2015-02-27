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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController;
import es.eucm.ead.editor.control.Views;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.workers.ExportProject;
import es.eucm.ead.editor.control.workers.Worker;
import es.eucm.ead.editor.platform.MokapPlatform;
import es.eucm.ead.editor.utils.Actions2;
import es.eucm.ead.editor.view.Modal;
import es.eucm.ead.editor.view.widgets.LoadingBar;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.modals.ModalContainer;
import es.eucm.ead.editor.view.widgets.modals.TextDialog;
import es.eucm.ead.engine.I18N;

/**
 * Export a project to a given .zip file and share it.
 * <dl>
 * <dt><strong>Arguments</strong></dt> <strong>None</strong>
 */
public class ShareProject extends EditorAction implements Worker.WorkerListener {

	private ModalContainer container;
	private LoadingBar loadingBar;

	public ShareProject() {
		super(true, false);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		ApplicationAssets applicationAssets = controller.getApplicationAssets();
		Skin skin = applicationAssets.getSkin();
		loadingBar = new LoadingBar(skin, WidgetBuilder.dpToPixels(8));
		container = new ModalContainer(skin, new ProgressDialog(skin,
				applicationAssets.getI18N()));
	}

	@Override
	public void perform(Object... args) {
		controller.action(Save.class);
		controller.getViews().showModal(container, 0, 0);
		loadingBar.setCompletion(0f);
	}

	@Override
	public void start() {

	}

	@Override
	public void result(Object... results) {
		Object result = results[0];
		if (result instanceof Float) {
			loadingBar.setCompletion((Float) result);
		} else {
			FileHandle compressedProject = (FileHandle) results[0];
			ApplicationAssets applicationAssets = controller
					.getApplicationAssets();
			((MokapPlatform) controller.getPlatform()).sendProject(
					compressedProject, applicationAssets.getI18N());

		}
	}

	@Override
	public void done() {
		controller.getViews().hideModal();
	}

	@Override
	public void error(Throwable ex) {
		controller.getViews().hideModal();
		controller.action(ShowTextDialog.class, controller
				.getApplicationAssets().getI18N().m("project.export.error"));

	}

	@Override
	public void cancelled() {
		controller.getViews().hideModal();
	}

	private class ProgressDialog extends Table implements Modal,
			MokapController.BackListener {

		private CharSequence cancelText;
		private TextButton cancel;
		private I18N i18N;

		private Runnable exportProject = new Runnable() {
			@Override
			public void run() {
				controller.action(ExecuteWorker.class, ExportProject.class,
						ShareProject.this);
				cancel.setDisabled(false);
			}
		};

		public ProgressDialog(Skin skin, I18N i18N) {
			this(skin.get(TextDialog.TextDialogStyle.class), i18N);
		}

		public ProgressDialog(TextDialog.TextDialogStyle style, I18N i18n) {
			background(style.background);
			this.i18N = i18n;
			float pad24dp = WidgetBuilder.dpToPixels(24);
			float pad16dp = WidgetBuilder.dpToPixels(16);
			pad(pad24dp, pad24dp, pad16dp, pad24dp);

			add(new Label(i18N.m("project.prepare"), style.textStyle)).expand()
					.fill();

			row();
			add(loadingBar).expandX().fillX().pad(pad16dp);
			cancel = WidgetBuilder.dialogButton(i18N.m("cancel"),
					style.buttonStyle);

			cancelText = i18N.m("cancel").toUpperCase();
			cancel.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					cancelExport();
				}

			});
			row();
			add(cancel).expandX().right();

		}

		private void cancelExport() {
			controller.getWorkerExecutor().cancel(ExportProject.class,
					ShareProject.this);
			cancel.setDisabled(true);
			cancel.setText(i18N.m("cancelling").toUpperCase());
		}

		@Override
		public void show(Views views) {
			((Layout) getParent()).layout();
			float y = getY();
			setY(Gdx.graphics.getHeight());
			clearActions();
			cancel.setText(cancelText.toString());
			addAction(Actions.sequence(
					Actions2.moveToY(y, 0.33f, Interpolation.exp5Out),
					Actions.run(exportProject)));
		}

		@Override
		public void hide(Runnable runnable) {
			addAction(Actions.sequence(Actions2.moveToY(
					Gdx.graphics.getHeight(), 0.33f, Interpolation.exp5Out),
					Actions.run(runnable)));
		}

		@Override
		public boolean hideAlways() {
			return false;
		}

		@Override
		public float getPrefWidth() {
			return Gdx.graphics.getWidth() * .7f;
		}

		@Override
		public boolean onBackPressed() {
			cancelExport();
			return true;
		}
	}

}
