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
package es.eucm.ead.engine.gdx;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

public class URLTextureLoader extends
		AsynchronousAssetLoader<Texture, TextureParameter> {

	private TextureLoader textureLoader;

	private Pixmap pixmap;

	public URLTextureLoader(FileHandleResolver resolver) {
		super(resolver);
		textureLoader = new TextureLoader(resolver);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName,
			FileHandle file, TextureParameter parameter) {
		if (file instanceof URLFileHandle
				&& parameter instanceof URLTextureParameter
				&& ((URLTextureParameter) parameter).writePixmapTo != null) {
			URLTextureParameter param = (URLTextureParameter) parameter;
			FileHandle writePixmapTo = param.writePixmapTo;
			if (param.override || !writePixmapTo.exists()) {
				pixmap = new Pixmap(file);
				PixmapIO.writePNG(writePixmapTo, pixmap);
			} else {
				textureLoader.loadAsync(manager, fileName, writePixmapTo,
						parameter);
			}
		} else {
			textureLoader.loadAsync(manager, fileName, file, parameter);
		}
	}

	@Override
	public Texture loadSync(AssetManager manager, String fileName,
			FileHandle file, TextureParameter parameter) {
		if (file instanceof URLFileHandle
				&& parameter instanceof URLTextureParameter
				&& ((URLTextureParameter) parameter).writePixmapTo != null) {
			if (pixmap != null) {
				Texture texture = new Texture(pixmap);
				pixmap.dispose();
				pixmap = null;
				return texture;
			} else {
				return textureLoader.loadSync(manager, fileName,
						((URLTextureParameter) parameter).writePixmapTo,
						parameter);
			}
		} else {
			return textureLoader.loadSync(manager, fileName, file, parameter);
		}
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName,
			FileHandle file, TextureParameter parameter) {
		return null;
	}

	static public class URLTextureParameter extends TextureParameter {

		public URLTextureParameter() {
		}

		public URLTextureParameter(FileHandle writePixmapTo) {
			this.writePixmapTo = writePixmapTo;
		}

		public URLTextureParameter(FileHandle writePixmapTo, boolean override) {
			this.writePixmapTo = writePixmapTo;
			this.override = override;
		}

		/**
		 * If the texture is from an URL, the loaded pixmap is written to this
		 * path if the file doesn't exist or if
		 * {@link URLTextureParameter#override} is true. It is always saved in
		 * PNG format.
		 */
		public FileHandle writePixmapTo;

		/**
		 * If true then the {@link URLTextureParameter#writePixmapTo} file will
		 * be overridden even if it already exists.
		 */
		private boolean override = false;
	}
}
