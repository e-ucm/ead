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
package es.eucm.ead.editor.assets.loaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.ReadOnlySerializer;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;

/**
 * Extension of skin to load some custom resources
 */
public class ExtendedSkin extends Skin {

	public ExtendedSkin(TextureAtlas atlas) {
		super(atlas);
	}

	@Override
	protected Json getJsonLoader(FileHandle skinFile) {
		Json json = super.getJsonLoader(skinFile);
		Serializer<BitmapFont> defaultSerializer = json
				.getSerializer(BitmapFont.class);
		json.setSerializer(BitmapFont.class, new TTFBitmapFontSerializer(
				defaultSerializer, skinFile));
		return json;
	}

	/**
	 * Serializer to create bitmap fonts form ttf files
	 */
	private static class TTFBitmapFontSerializer extends
			ReadOnlySerializer<BitmapFont> {

		private Serializer<BitmapFont> defaultSerializer;

		private FileHandle skinFile;

		private TTFBitmapFontSerializer(
				Serializer<BitmapFont> defaultSerializer, FileHandle skinFile) {
			this.defaultSerializer = defaultSerializer;
			this.skinFile = skinFile;
		}

		@Override
		public BitmapFont read(Json json, JsonValue jsonValue, Class aClass) {
			String path = json.readValue("file", String.class, jsonValue);
			if (path == null || !path.endsWith(".ttf")) {
				return defaultSerializer.read(json, jsonValue, aClass);
			} else {
				FileHandle fontFile = skinFile.parent().child(path);
				if (!fontFile.exists()) {
					fontFile = Gdx.files.internal(path);
				}
				if (!fontFile.exists()) {
					throw new SerializationException("Font file not found: "
							+ fontFile);
				}
				FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
						fontFile);
				FreeTypeFontParameter parameter = new FreeTypeFontParameter();
				parameter.size = json.readValue("size", int.class, 12,
						jsonValue);
				BitmapFont font = generator.generateFont(parameter);
				generator.dispose();
				return font;
			}
		}
	}
}
