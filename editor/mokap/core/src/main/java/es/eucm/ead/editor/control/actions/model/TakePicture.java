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

import com.badlogic.gdx.files.FileHandle;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.background.BackgroundExecutor.BackgroundTaskListener;
import es.eucm.ead.editor.control.background.BackgroundTask;
import es.eucm.ead.editor.platform.MokapPlatform;
import es.eucm.ead.editor.platform.MokapPlatform.ImageCapturedListener;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ModelStructure;

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

	private FileHandle pictureFile;

	private ModelEntity sceneElement;

	public TakePicture() {
		super(true, false);
	}

	@Override
	public void perform(Object... args) {

		MokapPlatform platform = (MokapPlatform) controller.getPlatform();

		String pictureString = controller.getApplicationAssets().getI18N()
				.m("picture");

		pictureFile = null;
		EditorGameAssets assets = controller.getEditorGameAssets();

		FileHandle picRootPath = assets.absolute(assets.getLoadingPath()
				+ ModelStructure.IMAGES_FOLDER);

		pictureFile = ProjectUtils.getNonExistentFile(picRootPath,
				pictureString, ".jpg");

		platform.captureImage(pictureFile, importListener);
	}

	private final ImageCapturedListener importListener = new ImageCapturedListener() {

		@Override
		public void imageCaptured(Result result) {
			if (result.equals(Result.SUCCES)) {
				sceneElement = controller.getTemplates().createSceneElement(
						pictureFile.path(), true);
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

		}

		@Override
		public void error(Throwable e) {

		}

	};
}
