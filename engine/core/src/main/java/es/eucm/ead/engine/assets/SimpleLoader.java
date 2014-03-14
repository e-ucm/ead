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
package es.eucm.ead.engine.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.EngineAssets;

/**
 * Simple Loader to deal with a particular type of file that can be loaded.
 * Examples of file types are images, game.json, scene.json and textstyle.json.
 * 
 * This class can be extended, for example to make additional dependency
 * processing (see {@link #doDependenciesProcessing(Object, String)})
 * 
 * Created by Javier Torrente on 27/02/14.
 */
public class SimpleLoader<T> extends
		AsynchronousAssetLoader<T, SimpleLoaderParameters<T>> {

	protected EngineAssets engineAssets;

	protected T object;

	protected Class<T> clazz;

	public SimpleLoader(EngineAssets engineAssets, Class<T> clazz) {
		super(engineAssets);
		this.engineAssets = engineAssets;
		this.clazz = clazz;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName,
			FileHandle file, SimpleLoaderParameters<T> parameter) {
		object = engineAssets.fromJson(clazz, file);
		doDependenciesProcessing(object, fileName);
		return engineAssets.popDependencies();
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName,
			FileHandle file, SimpleLoaderParameters<T> parameter) {
	}

	@Override
	public T loadSync(AssetManager manager, String fileName, FileHandle file,
			SimpleLoaderParameters<T> parameter) {
		return object;
	}

	/**
	 * Child classes may override this method to make further dependency
	 * management if needed
	 * 
	 * @param object
	 *            The object that is being loaded and parsed. After invoking
	 *            {@link #doDependenciesProcessing(Object, String)} object is
	 *            returned
	 * 
	 * @param fileName
	 *            The fileName being loaded (e.g. "scenes/scene0.json")
	 */
	protected void doDependenciesProcessing(T object, String fileName) {

	}
}
