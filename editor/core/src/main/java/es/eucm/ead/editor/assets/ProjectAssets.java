/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.assets;

import com.badlogic.gdx.Files;

import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;
import es.eucm.ead.editor.assets.loaders.ProjectLoader;
import es.eucm.ead.editor.assets.loaders.ProjectLoader.ProjectParameter;
import es.eucm.ead.editor.model.Project;
import es.eucm.ead.engine.Assets;

/**
 * Extends engine assets to also load editor objects
 */
public class ProjectAssets extends Assets {

	public static final String PROJECT_FILE = "project.json";

	/**
	 * Creates an assets handler
	 * 
	 * @param files
	 *            object granting access to files
	 */
	public ProjectAssets(Files files) {
		super(files);
	}

	@Override
	protected void setLoaders() {
		super.setLoaders();
		setLoader(Project.class, new ProjectLoader(this));
	}

	public void loadProject(LoadedCallback callback) {
		load(PROJECT_FILE, Project.class, new ProjectParameter(callback));
	}

	public void toJsonPath(Object object, String path) {
		toJson(object, resolve(path));
	}
}
