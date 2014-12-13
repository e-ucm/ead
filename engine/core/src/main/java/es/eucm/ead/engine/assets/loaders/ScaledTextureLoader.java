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

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.assets.GameAssets.ImageUtils;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.assets.ScaledTexture;
import es.eucm.ead.schemax.GameStructure;

public class ScaledTextureLoader
		extends
		AsynchronousAssetLoader<ScaledTexture, AssetLoaderParameters<ScaledTexture>> {

	private GameAssets gameAssets;

	private ImageUtils imageUtils;

	private Vector2 size = new Vector2();

	private float scale;

	private AssetDescriptor assetDescriptor;

	public ScaledTextureLoader(GameAssets gameAssets, ImageUtils imageUtils) {
		super(gameAssets);
		this.gameAssets = gameAssets;
		this.imageUtils = imageUtils;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName,
			FileHandle file, AssetLoaderParameters<ScaledTexture> parameter) {
	}

	@Override
	public ScaledTexture loadSync(AssetManager manager, String fileName,
			FileHandle file, AssetLoaderParameters<ScaledTexture> parameter) {
		return new ScaledTexture((Texture) manager.get(assetDescriptor), scale);
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName,
			FileHandle file, AssetLoaderParameters<ScaledTexture> parameter) {
		FileHandle imageFile = file.sibling(file.nameWithoutExtension());
		imageUtils.imageSize(imageFile, size);
		if (imageUtils.validSize(size)) {
			scale = 1.0f;
			return Array.with(assetDescriptor = new AssetDescriptor(imageFile,
					Texture.class));
		} else {
			FileHandle scaleProperty = gameAssets
					.resolveProject(GameStructure.METADATA_PATH
							+ imageFile.name() + ".prop");
			FileHandle scaled = gameAssets
					.resolveProject(GameStructure.METADATA_PATH
							+ imageFile.name() + ".scaled");
			if (!imageFile.exists() || !scaleProperty.exists()) {
				scale = imageUtils.scale(imageFile, scaled);
				scaleProperty.writeString(scale + "", false);
			} else {
				scale = Float.parseFloat(scaleProperty.readString());
			}
			return Array.with(assetDescriptor = new AssetDescriptor(scaled,
					Texture.class));
		}
	}

}
