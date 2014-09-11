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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.background.BackgroundExecutor.BackgroundTaskListener;
import es.eucm.ead.editor.control.background.BackgroundTask;
import es.eucm.ead.editor.platform.MockupPlatform;
import es.eucm.ead.editor.platform.MockupPlatform.ImageCapturedListener;
import es.eucm.ead.editor.view.widgets.Notification;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;

/**
 * <p>
 * Takes a picture and imports it to the currently selected scene as a child.
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>None</strong></dd>
 * </dl>
 */
public class TakePicture extends EditorAction {

	private Notification importingNotif, errorImporting;

	private FileHandle pictureFile;

	private ModelEntity sceneElement;

	private Stage stage;

	public TakePicture() {
		super(true, false);
	}

	@Override
	public void perform(Object... args) {
		stage = ((MockupController) controller).getRootComponent().getStage();

		if (importingNotif == null) {
			Skin skin = controller.getApplicationAssets().getSkin();
			importingNotif = new Notification(skin);
			errorImporting = new Notification(skin);
		}

		MockupPlatform platform = (MockupPlatform) controller.getPlatform();

		String pictureString = controller.getApplicationAssets().getI18N()
				.m("picture");

		int i = 0;

		pictureFile = null;
		Assets assets = controller.getEditorGameAssets();

		String picRootPath = GameStructure.IMAGES_FOLDER + pictureString;
		do {
			pictureFile = assets.resolve(picRootPath + (++i) + ".jpg");
		} while (pictureFile.exists());

		platform.captureImage(pictureFile.file(), importListener);
	}

	private final ImageCapturedListener importListener = new ImageCapturedListener() {

		@Override
		public void imageCaptured(boolean success) {
			if (success) {
				importingNotif.clearChildren();
				importingNotif.text(
						controller.getApplicationAssets().getI18N()
								.m("repository.importing")).show(stage);

				sceneElement = controller.getTemplates().createSceneElement(
						pictureFile.path());
				controller.getBackgroundExecutor().submit(importElemTask,
						dummyListener);
			}
		}
	};

	private final BackgroundTask<Boolean> importElemTask = new BackgroundTask<Boolean>() {

		@Override
		public Boolean call() throws Exception {
			controller.action(AddSceneElement.class, sceneElement);
			return true;
		}
	};

	private final BackgroundTaskListener<Boolean> dummyListener = new BackgroundTaskListener<Boolean>() {

		@Override
		public void completionPercentage(float percentage) {

		}

		@Override
		public void done(BackgroundExecutor backgroundExecutor, Boolean result) {
			importingNotif.hide();
		}

		@Override
		public void error(Throwable e) {
			importingNotif.hide();
			errorImporting.clearChildren();
			errorImporting.text(
					controller.getApplicationAssets().getI18N()
							.m("repository.importingError")).show(stage, 3F);
		}

	};
}
