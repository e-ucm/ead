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
package es.eucm.ead.editor.control.workers;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.schemax.GameStructure;

/**
 * Receives a path (args[0]) and loads all its children that are images.
 * Automatically creates thumbnails for each image.
 * <dl>
 * <dt><strong>The result arguments are</strong></dt>
 * <dd><strong>args[0]</strong> <em>String</em> path to the image.
 * <dd><strong>args[1]</strong> <em>String</em> name of the image.</dd>
 * <dd><strong>args[2]</strong> <em>String</em> path to the thumbnail of the
 * image.</dd>
 * </dl>
 */
public class LoadFiles extends Worker {

	private Assets assets;

	private Array<FileHandle> projectPaths;
	private FileHandle thumbnailsFolder;

	public LoadFiles() {
		super(true);
	}

	@Override
	public void setController(Controller controller) {
		super.setController(controller);
		assets = controller.getEditorGameAssets();
	}

	@Override
	protected void prepare() {
		FileHandle fileFolder = assets.absolute(args[0].toString());
		if (fileFolder.exists()) {
			projectPaths = new Array<FileHandle>();
			projectPaths.addAll(fileFolder.list());
			if (projectPaths.size > 0) {
				thumbnailsFolder = fileFolder
						.child(GameStructure.THUMBNAILS_PATH);
				if (!thumbnailsFolder.exists()) {
					thumbnailsFolder.mkdirs();
				}
			}
		}
	}

	@Override
	protected boolean step() {
		if (projectPaths == null || projectPaths.size == 0) {
			return true;
		}
		FileHandle file = projectPaths.removeIndex(0);
		if (ProjectUtils.isSupportedImage(file)) {
			FileHandle thumbnail = thumbnailsFolder.child(file.name());
			String thumbnailPath = file.path();
			if (!thumbnail.exists()
					|| thumbnail.lastModified() < file.lastModified()) {
				if (controller.getPlatform().getImageUtils()
						.scale(file, thumbnail) != -1) {
					thumbnailPath = thumbnail.path();
				}
			}
			result(file.path(), file.nameWithoutExtension(), thumbnailPath);
		}
		return projectPaths.size == 0;
	}
}
