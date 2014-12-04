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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.ReadOnlySerializer;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;

import es.eucm.ead.engine.assets.Assets;

/**
 * Extension of skin to load some custom resources
 */
public class ExtendedSkin extends Skin {

	private final Assets assets;

	public ExtendedSkin(Assets assets, TextureAtlas atlas) {
		super(atlas);
		this.assets = assets;
	}

	@Override
	protected Json getJsonLoader(FileHandle skinFile) {
		Json json = super.getJsonLoader(skinFile);
		Serializer<BitmapFont> defaultSerializer = json
				.getSerializer(BitmapFont.class);
		json.setSerializer(BitmapFont.class, new TTFBitmapFontSerializer(this,
				assets, defaultSerializer, skinFile));
		return json;
	}

	/**
	 * Adds all named texture regions from the atlas. The atlas will not be
	 * automatically disposed when the skin is disposed.
	 */
	public void addRegions(TextureAtlas atlas) {
		Array<TextureAtlas.AtlasRegion> regions = atlas.getRegions();
		for (int i = 0, n = regions.size; i < n; i++) {
			TextureAtlas.AtlasRegion region = regions.get(i);
			String name = region.name;
			if (region.index != -1) {
				name += String.valueOf(region.index);
			}
			add(name, region, TextureRegion.class);
		}
	}

	/**
	 * 
	 * @param regionName
	 * @return an array with the {@link TextureRegion} that have an index != -1,
	 *         or null if none are found.
	 * 
	 */
	private Array<TextureRegion> findRegionsWithIndex(String regionName) {
		Array<TextureRegion> regions = null;
		int i = 0;
		TextureRegion region = optional(regionName + (i++), TextureRegion.class);
		if (region != null) {
			regions = new Array<TextureRegion>(true, 5, TextureRegion.class);
			while (region != null) {
				regions.add(region);
				region = optional(regionName + (i++), TextureRegion.class);
			}
		}
		return regions;
	}

	/**
	 * Serializer to create bitmap fonts form ttf files
	 */
	private static class TTFBitmapFontSerializer extends
			ReadOnlySerializer<BitmapFont> {

		private final Assets assets;
		private final ExtendedSkin skin;
		private Serializer<BitmapFont> defaultSerializer;

		private FileHandle skinFile;

		private TTFBitmapFontSerializer(ExtendedSkin skin, Assets assets,
				Serializer<BitmapFont> defaultSerializer, FileHandle skinFile) {
			this.defaultSerializer = defaultSerializer;
			this.skinFile = skinFile;
			this.assets = assets;
			this.skin = skin;
		}

		@Override
		public BitmapFont read(Json json, JsonValue jsonValue, Class aClass) {
			String path = json.readValue("file", String.class, jsonValue);
			if (path == null
					|| !(path.endsWith(".ttf") || path.endsWith(".otf"))) {

				try {
					return defaultSerializer.read(json, jsonValue, aClass);
				} catch (SerializationException ex) {

					FileHandle fontFile = skinFile.parent().child(path);
					Array<TextureRegion> regionsWithIndex = skin
							.findRegionsWithIndex(fontFile
									.nameWithoutExtension());
					if (regionsWithIndex != null) {
						int scaledSize = json.readValue("scaledSize",
								int.class, -1, jsonValue);
						Boolean flip = json.readValue("flip", Boolean.class,
								false, jsonValue);

						BitmapFont font = new BitmapFont(new BitmapFontData(
								fontFile, flip), regionsWithIndex.items, true);
						// Scaled size is the desired cap height to scale the
						// font
						// to.
						if (scaledSize != -1)
							font.setScale(scaledSize / font.getCapHeight());

						return font;
					}
					throw new SerializationException(ex);
				}
			} else {
				FileHandle fontFile = skinFile.parent().child(path);
				if (!assets.checkFileExistence(fontFile)) {
					fontFile = Gdx.files.internal(path);
				}
				if (!assets.checkFileExistence(fontFile)) {
					throw new SerializationException("Font file not found: "
							+ fontFile);
				}
				FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
						fontFile);
				FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

				Integer dpSize = json.readValue("dpSize", int.class, -1,
						jsonValue);
				if (dpSize == -1) {
					parameter.size = json.readValue("size", int.class, 12,
							jsonValue);
				} else {
					parameter.size = (int) (dpSize * Gdx.graphics.getDensity());
				}
				BitmapFont font = generator.generateFont(parameter);
				font.setOwnsTexture(true);
				generator.dispose();
				return font;
			}
		}
	}
}
