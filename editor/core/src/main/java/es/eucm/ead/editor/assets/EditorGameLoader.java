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
package es.eucm.ead.editor.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.engine.assets.SimpleLoader;
import es.eucm.ead.engine.assets.SimpleLoaderParameters;
import es.eucm.ead.schema.editor.actors.EditorScene;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.editor.game.EditorGame;

/**
 * Loads files corresponding to {@link EditorGame}.
 * 
 * Created by Javier Torrente on 9/03/14.
 */
public class EditorGameLoader extends SimpleLoader<EditorGame> {

	private Array<String> sceneIds;

	public EditorGameLoader(EditorGameAssets assets) {
		super(assets, EditorGame.class);
		sceneIds = new Array<String>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName,
			FileHandle file, SimpleLoaderParameters<EditorGame> parameter) {
		Array<AssetDescriptor> dependencies = super.getDependencies(fileName,
				file, parameter);
		FileHandle scenesPath = gameAssets
				.resolve(EditorGameAssets.SCENES_PATH);
		sceneIds.clear();
		for (FileHandle sceneFile : scenesPath.list()) {
			String sceneId = sceneFile.nameWithoutExtension();
			sceneIds.add(sceneId);
			dependencies.add(new AssetDescriptor(gameAssets
					.convertSceneNameToPath(sceneId), EditorScene.class,
					new SimpleLoaderParameters(parameter.loadedCallback)));
		}
		return dependencies;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName,
			FileHandle file, SimpleLoaderParameters<EditorGame> parameter) {
		// Note in EditorGame cannot be null
		if (object.getNotes() == null) {
			object.setNotes(new Note());
		}

		// Now, check if scene order must be set with default values (scene ids
		// in the order they've been loaded)
		if (object.getSceneorder().size() < sceneIds.size) {
			for (String sceneId : sceneIds) {
				if (!object.getSceneorder().contains(sceneId)) {
					object.getSceneorder().add(sceneId);
				}
			}
		}
	}
}
