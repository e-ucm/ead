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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.schemax.ModelStructure;

import java.io.File;
import java.io.FileFilter;

/**
 * Loads all children files that are sounds (ProjectUtils#isSupportedAudio) from
 * the ModelStructure#SOUNDS_FOLDER.
 * <dl>
 * <dt><strong>The result arguments are</strong></dt>
 * <dd><strong>None</strong>
 * </dl>
 */
public class LoadSounds extends Worker {

	private Array<FileHandle> projectPaths;
	private Skin skin;

	public LoadSounds() {
		super(true);
	}

	@Override
	public void setController(Controller controller) {
		super.setController(controller);
		projectPaths = new Array<FileHandle>();
		skin = controller.getApplicationAssets().getSkin();
	}

	@Override
	protected void prepare() {
		projectPaths.clear();
		FileHandle fileFolder = controller.getApplicationAssets().absolute(
				controller.getLoadingPath() + ModelStructure.SOUNDS_FOLDER);
		if (fileFolder.exists()) {
			projectPaths.addAll(fileFolder.list());
		}
	}

	@Override
	protected boolean step() {
		if (projectPaths == null || projectPaths.size == 0) {
			return true;
		}
		FileHandle file = projectPaths.removeIndex(0);
		if (ProjectUtils.isSupportedAudio(file)) {
			String name = file.name();
			result(ModelStructure.SOUNDS_FOLDER + name, name);
		}
		return projectPaths.size == 0;
	}
}
