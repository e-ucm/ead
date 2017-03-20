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

import com.badlogic.gdx.audio.Music;

/**
 * Simple tool with static methods for loading media resources in the background
 * like sounds, music and images using a {@link GameAssets} object Created by
 * jtorrente on 06/11/2015.
 */
public class MediaResourcesLoader {
	public static final long MAX_COMPRESSED_SOUND_SIZE = 150 * 1024;

	/**
	 * Tells the {@link GameAssets} object to load the image (png, jpg, etc)
	 * file at path {@code imageUri} and notify {@code callback} when it's ready
	 * or an error occurred.
	 * 
	 * @param imageUri
	 *            The path of the image to load (e.g. file1.png,
	 *            images/file2.jpg)
	 * @param gameAssets
	 *            The object that will actually load the resource in the
	 *            background
	 * @param callback
	 *            Object that gets notifications on the status of the loading
	 *            task
	 */
	public static void loadImage(String imageUri, GameAssets gameAssets,
			final Assets.AssetLoadedCallback callback) {
		gameAssets.get(imageUri + ".tex", ScaledTexture.class, callback);
	}

	/**
	 * Tells the {@link GameAssets} object to load the audio (wav, mp3) file at
	 * path {@code soundUri} and notify {@code callback} when it's ready or an
	 * error occurred. If the file's weight is smaller than
	 * {@value #MAX_COMPRESSED_SOUND_SIZE}, the audio file will be loaded using
	 * Libgdx's {@link com.badlogic.gdx.audio.Sound}. Otherwise it will be
	 * loaded using {@link com.badlogic.gdx.audio.Music}
	 * 
	 * @param soundUri
	 *            The path of the sound or music to load (e.g. file1.wav,
	 *            sounds/file2.mp3)
	 * @param gameAssets
	 *            The object that will actually load the resource in the
	 *            background
	 * @param callback
	 *            Object that gets notifications on the status of the loading
	 *            task
	 */
	public static void loadAudio(String soundUri, GameAssets gameAssets,
			final Assets.AssetLoadedCallback callback) {
		// files over 150k should be streamed as music; asume ~ 10x compression
		if (gameAssets.resolve(soundUri).length() < MAX_COMPRESSED_SOUND_SIZE) {
			gameAssets
					.get(soundUri,
							com.badlogic.gdx.audio.Sound.class,
							new Assets.AssetLoadedCallback<com.badlogic.gdx.audio.Sound>() {
								@Override
								public void loaded(String fileName,
										com.badlogic.gdx.audio.Sound asset) {
									callback.loaded(fileName, asset);
								}

								@Override
								public void error(String fileName, Class type,
										Throwable exception) {
									callback.error(fileName, type, exception);
								}
							});
		} else {
			gameAssets.get(soundUri, com.badlogic.gdx.audio.Music.class,
					new Assets.AssetLoadedCallback<Music>() {
						@Override
						public void loaded(String fileName,
								com.badlogic.gdx.audio.Music asset) {
							callback.loaded(fileName, asset);
						}

						@Override
						public void error(String fileName, Class type,
								Throwable exception) {
							callback.error(fileName, type, exception);
						}
					});
		}
	}

}
