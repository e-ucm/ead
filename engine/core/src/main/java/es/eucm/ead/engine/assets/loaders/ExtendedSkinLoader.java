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
package es.eucm.ead.engine.assets.loaders;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import es.eucm.ead.engine.assets.Assets;

public class ExtendedSkinLoader extends SkinLoader {

	private final Assets assets;

	public ExtendedSkinLoader(Assets resolver) {
		super(resolver);
		this.assets = resolver;
	}

	@Override
	public Skin loadSync(AssetManager manager, String fileName,
			FileHandle file, SkinParameter parameter) {
		String textureAtlasPath;
		ObjectMap<String, Object> resources;
		if (parameter == null) {
			textureAtlasPath = file.pathWithoutExtension() + ".atlas";
			resources = null;
		} else {
			textureAtlasPath = parameter.textureAtlasPath;
			resources = parameter.resources;
		}
		TextureAtlas atlas = manager.get(textureAtlasPath, TextureAtlas.class);
		Skin skin = new ExtendedSkin(assets, atlas);
		if (resources != null) {
			for (Entry<String, Object> entry : resources.entries()) {
				skin.add(entry.key, entry.value);
			}
		}
		skin.load(file);
		return skin;
	}
}
