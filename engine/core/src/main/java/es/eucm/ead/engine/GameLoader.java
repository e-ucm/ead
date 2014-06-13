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
package es.eucm.ead.engine;

import es.eucm.ead.schemax.GameStructure;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Deals with game loading. Games can be loaded through
 * {@link GameLoader#loadGame(String, boolean)}.
 */
public class GameLoader implements AssetLoadedCallback<ModelEntity> {

	public static final String DEFAULT_SKIN = "skins/engine/skin";

	private GameAssets gameAssets;

	private EntitiesLoader entitiesLoader;

	private GameLoop gameLoop;

	public GameLoader(GameLoop gameLoop, GameAssets gameAssets,
			EntitiesLoader entitiesLoader) {
		this.entitiesLoader = entitiesLoader;
		this.gameAssets = gameAssets;
		this.gameLoop = gameLoop;
	}

	/**
	 * @return the entities loader
	 */
	public EntitiesLoader getEntitiesLoader() {
		return entitiesLoader;
	}

	/**
	 * Loads a game stored in a path
	 * 
	 * @param path
	 *            the path for the game
	 * @param internal
	 *            if the path has as root the classpath
	 */
	public void loadGame(String path, boolean internal) {
		gameAssets.setLoadingPath(path, internal);
		gameAssets.get(GameStructure.GAME_FILE, ModelEntity.class, this);
		gameAssets.loadSkin(DEFAULT_SKIN);
	}

	@Override
	public void loaded(String fileName, ModelEntity asset) {
		entitiesLoader.toEngineEntity(asset);
	}
}
