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

import com.badlogic.gdx.Gdx;
import es.eucm.ead.schemax.ModelStructure;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Deals with game loading. Games can be loaded through
 * {@link GameLoader#loadGame(String, boolean)}.
 */
public class GameLoader implements AssetLoadedCallback<Object> {

	public static final String DEFAULT_SKIN = "skins/engine/skin";

	private GameAssets gameAssets;

	private EntitiesLoader entitiesLoader;

	public GameLoader(GameAssets gameAssets, EntitiesLoader entitiesLoader) {
		this.entitiesLoader = entitiesLoader;
		this.gameAssets = gameAssets;
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
		gameAssets.loadSkin(DEFAULT_SKIN);
		gameAssets.getI18N().setLang(null);
		gameAssets.get(ModelStructure.GAME_FILE, Object.class, this);
	}

	@Override
	public void loaded(String fileName, Object modelEntity) {
		entitiesLoader.toEngineEntity((ModelEntity) modelEntity);
	}

	@Override
	public void error(String fileName, Class type, Throwable exception) {
		Gdx.app.error("GameLoader", "Invalid game " + fileName, exception);
	}
}
