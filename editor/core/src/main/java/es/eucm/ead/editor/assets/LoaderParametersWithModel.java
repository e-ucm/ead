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
package es.eucm.ead.editor.assets;

import es.eucm.ead.engine.assets.SimpleLoaderParameters;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneMetadata;
import es.eucm.ead.schema.game.Game;
import es.eucm.ead.schema.game.GameMetadata;

import java.util.Map;

/**
 * This class is intended to provide access to the model (
 * {@link es.eucm.ead.schema.game.Game},
 * {@link es.eucm.ead.schema.game.GameMetadata},
 * {@link es.eucm.ead.schema.actors.Scene}s and
 * {@link es.eucm.ead.schema.actors.SceneMetadata}s) to the
 * {@link es.eucm.ead.editor.assets.LoaderWithModelAccess}. This is necessary
 * for loaders that require setting default values in the model. Created by
 * Javier Torrente on 9/03/14.
 */
public class LoaderParametersWithModel<T> extends SimpleLoaderParameters<T> {
	protected Game game;
	protected GameMetadata gameMetadata;
	protected Map<String, Scene> scenes;
	protected Map<String, SceneMetadata> scenesMetadata;

	public LoaderParametersWithModel(LoadedCallback loadedCallback) {
		this(loadedCallback, null, null, null, null);
	}

	public LoaderParametersWithModel(LoadedCallback loadedCallback, Game game,
			GameMetadata gameMetadata, Map<String, Scene> scenes,
			Map<String, SceneMetadata> scenesMetadata) {
		super(loadedCallback);
		this.game = game;
		this.gameMetadata = gameMetadata;
		this.scenes = scenes;
		this.scenesMetadata = scenesMetadata;
	}

	public Game getGame() {
		return game;
	}

	public GameMetadata getGameMetadata() {
		return gameMetadata;
	}

	public Map<String, Scene> getScenes() {
		return scenes;
	}

	public Map<String, SceneMetadata> getScenesMetadata() {
		return scenesMetadata;
	}
}
