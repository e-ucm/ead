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
package es.eucm.ead.engine.tests.systems.effects;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.assets.JsonLoader;
import es.eucm.ead.engine.assets.ScaledTexture;
import es.eucm.ead.engine.assets.loaders.ScaledTextureLoader;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.mock.MockFileHandle;
import es.eucm.ead.engine.mock.schema.MockEffect;
import es.eucm.ead.engine.processors.behaviors.BehaviorsProcessor;
import es.eucm.ead.engine.processors.renderers.FramesProcessor;
import es.eucm.ead.engine.processors.renderers.ImageProcessor;
import es.eucm.ead.engine.systems.effects.PlaySoundExecutor;
import es.eucm.ead.engine.systems.effects.PreloadEntityExecutor;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.effects.PlaySound;
import es.eucm.ead.schema.effects.PreloadEntity;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Frames;
import es.eucm.ead.schema.renderers.Image;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by jtorrente on 04/11/2015.
 */
public class PreloadEntityEffectTest extends EffectTest {

	public static final String ENTITY_URI = "entity1.json";
	public static final String IMAGE_01 = "image1.png";
	public static final String IMAGE_02 = "imgs/image2.jpeg";
	public static final String IMAGE_03 = "imgs/image3.JPG";
	public static final String SOUND_01 = "sound1.wav";
	public static final String SOUND_02 = "sounds/path/sound2.MP3";

	private boolean executed = false;

	@Before
	public void setup() {
		componentLoader.registerComponentProcessor(Behavior.class,
				new BehaviorsProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Frames.class,
				new FramesProcessor(gameLoop, gameAssets, componentLoader));
		componentLoader.registerComponentProcessor(Image.class,
				new ImageProcessor(gameLoop, gameAssets));
		effectsSystem.registerEffectExecutor(PlaySound.class,
				new PlaySoundExecutor(effectsSystem));
		effectsSystem.registerEffectExecutor(PreloadEntity.class,
				new PreloadEntityExecutor(entitiesLoader, effectsSystem,
						variablesManager, gameAssets));
		ModelEntity entity = entity((ModelEntity) null, 0, 0)
				.frames(0.1F, IMAGE_01, IMAGE_02, IMAGE_03).initBehavior()
				.playSound(SOUND_01).playSound(SOUND_02).getLastEntity();
		gameAssets
				.setLoader(Object.class, new ModelEntityLoaderForTest(entity));
		gameAssets.setLoader(ScaledTexture.class, new TextureLoaderForTest());
		gameAssets.setLoader(Sound.class, new SoundLoaderForTest());
	}

	@Test
	public void test() {
		ModelEntity firstEntity = entity((ModelEntity) null, 0, 0)
				.initBehavior()
				.preloadEntity(ENTITY_URI,
						new MockEffect(new MockEffect.MockEffectListener() {
							@Override
							public void executed() {
								executed = true;
							}
						})).getLastEntity();

		EngineEntity firstEngineEntity = entitiesLoader
				.toEngineEntity(firstEntity);
		gameLoop.addEntity(firstEngineEntity);
		update();
		update();
		update();
		assertFalse(executed);
		assertTrue(gameAssets.isLoaded(ENTITY_URI));
		update();
		assertTrue(gameAssets.isLoaded(IMAGE_01 + ".tex"));
		assertTrue(gameAssets.isLoaded(IMAGE_02 + ".tex"));
		assertTrue(gameAssets.isLoaded(IMAGE_03 + ".tex"));
		assertTrue(gameAssets.isLoaded(SOUND_01));
		assertTrue(gameAssets.isLoaded(SOUND_02));
		assertTrue(executed);
	}

	public void update() {
		gameAssets.update();
		super.update(0);
	}

	private static class ModelEntityLoaderForTest extends JsonLoader<Object> {

		public ModelEntityLoaderForTest(ModelEntity modelEntity) {
			super(null, Object.class);
			object = modelEntity;
		}

		@Override
		public FileHandle resolve(String fileName) {
			return new MockFileHandle("", Files.FileType.Absolute);
		}

		@Override
		public void loadAsync(AssetManager manager, String fileName,
				FileHandle file, AssetLoaderParameters<Object> parameter) {
		}
	}

	private static class TextureLoaderForTest extends ScaledTextureLoader {

		private static final byte[] IMAGE_BYTES = { -119, 80, 78, 71, 13, 10,
				26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 1, 0, 0, 0, 1, 8,
				2, 0, 0, 0, -112, 119, 83, -34, 0, 0, 0, 9, 112, 72, 89, 115,
				0, 0, 46, 35, 0, 0, 46, 35, 1, 120, -91, 63, 118, 0, 0, 1, 54,
				105, 67, 67, 80, 80, 104, 111, 116, 111, 115, 104, 111, 112,
				32, 73, 67, 67, 32, 112, 114, 111, 102, 105, 108, 101, 0, 0,
				120, -38, -83, -114, -79, 74, -61, 80, 20, 64, -49, -117, -94,
				-30, 80, 43, 4, 113, 112, 120, -109, 40, 40, -74, -22, 96, -58,
				-92, 45, 69, 16, -84, -43, 33, -55, -42, -92, -95, 74, 105, 18,
				94, 94, -43, 126, -124, -93, 91, 7, 23, 119, -65, -64, -55, 81,
				112, 80, -4, 2, -1, 64, 113, -22, -32, 16, 33, -125, -125, 8,
				-98, -23, -36, -61, -27, 114, -63, -88, -40, 117, -89, 97,
				-108, 97, 16, 107, -43, 110, 58, -46, -11, 124, 57, -5, -60,
				12, 83, 0, -48, 9, -77, -44, 110, -75, 14, 0, -30, 36, -114,
				-8, -63, -25, 43, 2, -32, 121, -45, -82, 59, 13, -2, -58, 124,
				-104, 42, 13, 76, -128, -19, 110, -108, -123, 32, 42, 64, -1,
				66, -89, 26, -60, 24, 48, -125, 126, -86, 65, -36, 1, -90, 58,
				105, -41, 64, 60, 0, -91, 94, -18, 47, 64, 41, -56, -3, 13, 40,
				41, -41, -13, 65, 124, 0, 102, -49, -11, 124, 48, -26, 0, 51,
				-56, 125, 5, 48, 117, 116, -87, 1, 106, 73, 58, 82, 103, -67,
				83, 45, -85, -106, 101, 73, -69, -101, 4, -111, 60, 30, 101,
				58, 26, 100, 114, 63, 14, 19, -107, 38, -86, -93, -93, 46,
				-112, -1, 7, -64, 98, -66, -40, 110, 58, 114, -83, 106, 89,
				123, -21, -4, 51, -82, -25, -53, -36, -34, -113, 16, -128, 88,
				122, 44, 90, 65, 56, 84, -25, -33, 42, -116, -99, -33, -25,
				-30, -58, 120, 25, 14, 111, 97, 122, 82, -76, -35, 43, -72,
				-39, -128, -123, -21, -94, -83, 86, -95, -68, 5, -9, -29, 47,
				-64, -58, 79, -3, -24, 90, 79, 98, 0, 0, 0, 32, 99, 72, 82, 77,
				0, 0, 122, 37, 0, 0, -128, -125, 0, 0, -7, -1, 0, 0, -128, -24,
				0, 0, 82, 8, 0, 1, 21, 88, 0, 0, 58, -105, 0, 0, 23, 111, -41,
				90, 31, -112, 0, 0, 0, 18, 73, 68, 65, 84, 120, -38, 98, 120,
				-38, -50, 4, 0, 0, 0, -1, -1, 3, 0, 3, -61, 1, 111, 67, -8, 61,
				30, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126 };
		private static final ScaledTexture TEXTURE = buildTexture();

		private static ScaledTexture buildTexture() {
			Pixmap pixmap = new Pixmap(IMAGE_BYTES, 0, IMAGE_BYTES.length);
			Texture texture = new Texture(pixmap);
			ScaledTexture scaledTexture = new ScaledTexture(texture, 1.0F);
			return scaledTexture;
		}

		public TextureLoaderForTest() {
			super(null, null);
		}

		@Override
		public FileHandle resolve(String fileName) {
			return new MockFileHandle("", Files.FileType.Absolute);
		}

		@Override
		public ScaledTexture loadSync(AssetManager manager, String fileName,
				FileHandle file, AssetLoaderParameters<ScaledTexture> parameter) {
			return TEXTURE;
		}

		@Override
		public Array<AssetDescriptor> getDependencies(String fileName,
				FileHandle file, AssetLoaderParameters<ScaledTexture> parameter) {
			return null;
		}
	}

	private static class SoundLoaderForTest extends SoundLoader {

		public SoundLoaderForTest() {
			super(null);
		}

		@Override
		public FileHandle resolve(String fileName) {
			return new MockFileHandle("", Files.FileType.Absolute) {
				public long length() {
					return 1;
				}
			};
		}

		@Override
		public void loadAsync(AssetManager manager, String fileName,
				FileHandle file, SoundParameter parameter) {
		}

		@Override
		public Sound loadSync(AssetManager manager, String fileName,
				FileHandle file, SoundParameter parameter) {
			Sound sound = new Sound() {
				@Override
				public long play() {
					return 0;
				}

				@Override
				public long play(float volume) {
					return 0;
				}

				@Override
				public long play(float volume, float pitch, float pan) {
					return 0;
				}

				@Override
				public long loop() {
					return 0;
				}

				@Override
				public long loop(float volume) {
					return 0;
				}

				@Override
				public long loop(float volume, float pitch, float pan) {
					return 0;
				}

				@Override
				public void stop() {

				}

				@Override
				public void pause() {

				}

				@Override
				public void resume() {

				}

				@Override
				public void dispose() {

				}

				@Override
				public void stop(long soundId) {

				}

				@Override
				public void pause(long soundId) {

				}

				@Override
				public void resume(long soundId) {

				}

				@Override
				public void setLooping(long soundId, boolean looping) {

				}

				@Override
				public void setPitch(long soundId, float pitch) {

				}

				@Override
				public void setVolume(long soundId, float volume) {

				}

				@Override
				public void setPan(long soundId, float pan, float volume) {

				}

				@Override
				public void setPriority(long soundId, int priority) {

				}
			};
			return sound;
		}

		@Override
		public Array<AssetDescriptor> getDependencies(String fileName,
				FileHandle file, SoundParameter parameter) {
			return null;
		}
	}
}
