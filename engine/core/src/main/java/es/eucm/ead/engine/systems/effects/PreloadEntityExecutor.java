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
package es.eucm.ead.engine.systems.effects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.assets.MediaResourcesLoader;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.utils.ReferenceUtils;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.effects.PreloadEntity;
import es.eucm.ead.schema.entities.ModelEntity;

import java.util.HashMap;

/**
 * Loads a specific entity plus all its binary resources associated in the
 * background (Executes effects of type {@link PreloadEntity}). The logic for
 * loading an entity is as follows: 1. The model entity is loaded from json, if
 * not loaded yet (see {@link #loadEntity(Entity, PreloadEntity)}). 2. The model
 * entity is searched for references to image, video and audio files. Any of
 * these files that are not yet available in memory are loaded. 3. Once the
 * model entity and all its binary resources are loaded, the set of post-effects
 * are launched.
 * 
 * Created by jtorrente on 04/11/2015.
 */
public class PreloadEntityExecutor extends EffectExecutor<PreloadEntity> {

	public static final String LOG_TAG = "PreloadEntityEffect";
	private EntitiesLoader entitiesLoader;
	private EffectsSystem effectsSystem;
	private VariablesManager variablesManager;
	private GameAssets gameAssets;
	private HashMap<String, Entity> localEntities;

	public PreloadEntityExecutor(EntitiesLoader entitiesLoader,
			final EffectsSystem effectsSystem,
			VariablesManager variablesManager, GameAssets gameAssets) {
		this.entitiesLoader = entitiesLoader;
		this.effectsSystem = effectsSystem;
		this.variablesManager = variablesManager;
		this.gameAssets = gameAssets;
		this.localEntities = new HashMap<String, Entity>();
	}

	@Override
	public void execute(Entity target, final PreloadEntity effect) {
		loadEntity(target, effect);
	}

	/**
	 * Checks if the entity to be loaded is already available in memory. If that
	 * is the case, it launches the post effects. Otherwise, it will ask the
	 * {@link GameAssets} object to start loading it.
	 */
	private void loadEntity(Entity target, final PreloadEntity effect) {
		this.localEntities.put(effect.getEntityUri(), target);
		if (!entitiesLoader.isEntityLoaded(effect.getEntityUri())) {
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					gameAssets.addAssetListener(new Callback(effect));
					gameAssets.load(effect.getEntityUri(), Object.class);
				}
			});
		} else {
			Gdx.app.log(LOG_TAG, "Entity in path: " + effect.getEntityUri()
					+ " was already loaded. Launching effects now (if any)");
			runPostEffects(effect);
		}
	}

	/**
	 * Gets notifications on the loading status of the entities/resources for a
	 * particular {@link PreloadEntity} effect.
	 */
	private class Callback implements Assets.AssetLoadingListener {

		private PreloadEntity effect;
		private ModelEntity modelEntity;
		private HashMap<String, Boolean> pendingAssets = new HashMap<String, Boolean>();
		private int counter = 0;
		private Listener listener = new Listener();

		public Callback(PreloadEntity effect) {
			this.effect = effect;
		}

		@Override
		public boolean listenTo(String fileName) {
			return fileName != null && (fileName.equals(effect.getEntityUri()));
		}

		@Override
		public void loaded(String fileName, Object asset, Assets assets) {
			loadBinaryRefs(fileName, asset);
		}

		/**
		 * Searches the {@link ModelEntity} loaded for binary references and
		 * starts loading all that have not been loaded yet. Invoked once the
		 * model entity has been read from json.
		 * 
		 * @param fileName
		 *            The path of the entity that was loaded
		 * @param asset
		 *            The {@link ModelEntity} object.
		 */
		private void loadBinaryRefs(String fileName, Object asset) {
			if (modelEntity == null && fileName.equals(effect.getEntityUri())) {
				Gdx.app.log(LOG_TAG, "Entity in path: " + fileName
						+ " was loaded. Loading referenced files now");
				modelEntity = (ModelEntity) asset;
				Array<String> binaryRefs = ReferenceUtils
						.listRefBinaries(modelEntity);
				int queued = 0;
				for (String binaryRef : binaryRefs) {
					if (checkPendingAsset(binaryRef)) {
						continue;
					}
					if (ReferenceUtils.hasImageExtension(binaryRef)) {
						if (loadImage(binaryRef)) {
							queued++;
						}
					} else if (ReferenceUtils.hasAudioExtension(binaryRef)) {
						if (loadSound(binaryRef)) {
							queued++;
						}
					} else if (ReferenceUtils.hasVideoExtension(binaryRef)) {
						// TODO Video is not really supported yet
					}
				}

				if (queued == 0) {
					Gdx.app.log(LOG_TAG,
							"No references to binary files found. Launching post-ffects now (if any)");
					runPostEffects(effect);
				}
			}
		}

		private synchronized boolean loadImage(final String imgUri) {
			if (gameAssets.isLoaded(imgUri)) {
				return false;
			}
			Gdx.app.log(LOG_TAG, "Queuing load of image " + imgUri
					+ ", referenced in " + effect.getEntityUri() + ".");
			pendingAssets.put(imgUri + ".tex", false);
			counter++;

			Gdx.app.postRunnable(new Runnable() {

				@Override
				public void run() {

					MediaResourcesLoader
							.loadImage(imgUri, gameAssets, listener);

				}
			});

			return true;
		}

		private synchronized boolean loadSound(final String soundUri) {
			if (gameAssets.isLoaded(soundUri)) {
				return false;
			}
			Gdx.app.log(LOG_TAG, "Queuing load of sound " + soundUri
					+ ", referenced in " + effect.getEntityUri() + ".");
			pendingAssets.put(soundUri, false);
			counter++;

			Gdx.app.postRunnable(new Runnable() {

				@Override
				public void run() {

					MediaResourcesLoader.loadAudio(soundUri, gameAssets,
							listener);

				}
			});

			return true;
		}

		/**
		 * Annotates that the resource {@code fileName} has been loaded and, if
		 * applicable, that there is one binary resource less to be loaded for
		 * this particular {@link PreloadEntity} effect. After that, if there
		 * are no more pending resources, the post effects are launched. Invoked
		 * after a binary resource has been loaded.
		 * 
		 * @param fileName
		 *            Name of the file that was loaded
		 */
		private synchronized void update(String fileName) {
			if (!pendingAssets.get(fileName)) {
				Gdx.app.log(LOG_TAG, "Resource or entity " + fileName
						+ ", referenced in " + effect.getEntityUri()
						+ ", was loaded. Counter is:" + (counter - 1));
				pendingAssets.put(fileName, true);
				counter--;
			}
			if (counter == 0) {
				Gdx.app.log(LOG_TAG, "Entity in path: " + effect.getEntityUri()
						+ " was fully loaded. Launching effects now (if any)");
				runPostEffects(effect);
			}
		}

		private synchronized boolean checkPendingAsset(String fileName) {
			return pendingAssets.containsKey(fileName);
		}

		@Override
		public void unloaded(String fileName, Assets assets) {
			// Do nothing, for now
		}

		private class Listener implements Assets.AssetLoadedCallback {
			@Override
			public void loaded(String fileName, Object asset) {
				update(fileName);
			}

			@Override
			public void error(String fileName, Class type, Throwable exception) {
				Gdx.app.error(LOG_TAG, "Impossible to load resource: "
						+ fileName, exception);
			}
		}
	}

	private void runPostEffects(PreloadEntity effect) {
		if (effect.getEffects().size > 0) {
			variablesManager.push();
			variablesManager.localOwnerVar(localEntities.get(effect
					.getEntityUri()));
			effectsSystem.executeEffectList(effect.getEffects());
			variablesManager.pop();
		}
	}
}
