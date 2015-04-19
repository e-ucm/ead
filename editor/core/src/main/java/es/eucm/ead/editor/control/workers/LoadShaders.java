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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.schemax.ModelStructure;

import java.io.File;

/**
 * Loads all the shaders stored internally
 * <dl>
 * <dt><strong>The result arguments are</strong></dt>
 * <dd><strong>args[0]</strong> <em>String</em> path to the shader.fragment
 * file.
 * <dd><strong>args[1]</strong> <em>String</em> path to the thumbnail.</dd>
 * </dl>
 */
public class LoadShaders extends Worker {

	private Assets assets;

	private Array<FileHandle> projectPaths;

	public LoadShaders() {
		super(true);
	}

	@Override
	public void setController(Controller controller) {
		super.setController(controller);
		assets = controller.getApplicationAssets();
	}

	@Override
	protected void prepare() {
		if (projectPaths == null) {
			projectPaths = new Array<FileHandle>();
		} else {
			projectPaths.clear();
		}
		FileHandle shadersFolder = assets
				.resolve(ModelStructure.SHADERS_FOLDER);
		if (shadersFolder.exists()) {
			int i = 1;
			FileHandle child = shadersFolder.child(i++ + "");
			while (assets.checkFileExistence(child
					.child(ModelStructure.SHADER_THUMBNAIL_FILE))) {
				projectPaths.add(child);
				child = shadersFolder.child(i++ + "");
			}
		}
	}

	@Override
	protected boolean step() {
		if (projectPaths.size == 0) {
			return true;
		}
		FileHandle shader = projectPaths.removeIndex(0);
		FileHandle thumbnail = shader
				.child(ModelStructure.SHADER_THUMBNAIL_FILE);
		String thumbnailPath = thumbnail.path();
		result(shader.child(ModelStructure.SHADER_FILE).path(), thumbnailPath);
		return projectPaths.size == 0;
	}
}