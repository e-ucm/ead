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

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.assets.SimpleLoader;
import es.eucm.ead.engine.assets.SimpleLoaderParameters;

/**
 * Basic loader for the editor that can be extended to set up default values in
 * the object being loaded. See
 * {@link es.eucm.ead.editor.assets.SceneMetadataLoader} and
 * {@link es.eucm.ead.editor.assets.GameMetadataLoader} for examples.
 * 
 * Subclasses that need to deal with default values should do so in
 * {@link #fillInDefaultValuesInContentLoaded(Object, String, LoaderParametersWithModel)}
 * , which has to be defined by subclasses. In this method, the model is
 * available through the given
 * {@link es.eucm.ead.editor.assets.LoaderParametersWithModel} object. Created
 * by Javier Torrente on 9/03/14.
 */
public abstract class LoaderWithModelAccess<T> extends SimpleLoader<T> {

	public LoaderWithModelAccess(ProjectAssets assets, Class<T> clazz) {
		super(assets, clazz);

	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName,
			FileHandle file, SimpleLoaderParameters<T> parameter) {
		Array<AssetDescriptor> dependencies = super.getDependencies(fileName,
				file, parameter);
		// Before returning, process the model
		fillInDefaultValuesInContentLoaded(t, fileName,
				(LoaderParametersWithModel<T>) parameter);
		return dependencies;
	}

	/**
	 * The intention of this method is to let subclasses set default values in
	 * the {@code T} object being loaded in a standard way. See
	 * {@link es.eucm.ead.editor.assets.SceneMetadataLoader} and
	 * {@link es.eucm.ead.editor.assets.GameMetadataLoader} for examples.
	 * 
	 * This method is invoked after all dependencies have been resolved (see
	 * {@link es.eucm.ead.engine.assets.SimpleLoader#doDependenciesProcessing(Object, String)}
	 * and
	 * {@link es.eucm.ead.engine.assets.SimpleLoader#getDependencies(String, com.badlogic.gdx.files.FileHandle, es.eucm.ead.engine.assets.SimpleLoaderParameters)}
	 * ).
	 * 
	 * Subclasses implementing this method may assume access to game,
	 * gamemetadata, scenes and scenesmetadata is granted through object
	 * {@code parameter}. However, {@code parameter}'s pointers to game,
	 * gamemetadata, etc. may be null, since it's
	 * {@link es.eucm.ead.editor.assets.ProjectAssets}'s responsibility to set
	 * values for {@link es.eucm.ead.editor.assets.LoaderParametersWithModel}
	 * appropriately.
	 * 
	 * @param object
	 *            The object that is being loaded and parsed. After invoking
	 *            this method,
	 *            {@link #getDependencies(String, com.badlogic.gdx.files.FileHandle, es.eucm.ead.engine.assets.SimpleLoaderParameters)}
	 *            returns.
	 * @param fileName
	 *            The name of the file where this object was stored (e.g.
	 *            "scene0.json"). Subclasses may need access to the file name
	 *            since it's the only way to find out elements' id.
	 * @param parameter
	 *            The wrapper that gives access to the model and which provides
	 *            the callback
	 */
	protected abstract void fillInDefaultValuesInContentLoaded(T object,
			String fileName, LoaderParametersWithModel<T> parameter);
}
