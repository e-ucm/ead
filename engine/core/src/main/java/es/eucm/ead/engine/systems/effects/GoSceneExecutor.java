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

import ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.GameView;
import es.eucm.ead.engine.components.RemoveEntityComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schemax.Layer;

public class GoSceneExecutor extends EffectExecutor<GoScene> {

	private EntitiesLoader entitiesLoader;

	private GameView gameView;

	public GoSceneExecutor(EntitiesLoader entitiesLoader, GameView gameView) {
		this.entitiesLoader = entitiesLoader;
		this.gameView = gameView;
	}

	@Override
	public void execute(Entity target, GoScene effect) {
		Group layer = gameView.getLayer(Layer.SCENE_CONTENT).getGroup();
		if (layer.getChildren().size == 1) {
			Object o = layer.getChildren().get(0).getUserObject();
			if (o instanceof EngineEntity) {
				((EngineEntity) o).add(gameLoop
						.createComponent(RemoveEntityComponent.class));
			} else {
				Gdx.app.error("GoSceneExecutor",
						"Scene layer doesn't have an entity as first child");
			}
		}

		entitiesLoader.loadEntity(effect.getSceneId(),
				new EntitiesLoader.EntityLoadedCallback() {
					@Override
					public void loaded(String path, EngineEntity engineEntity) {
						gameView.addEntityToLayer(Layer.SCENE_CONTENT,
								engineEntity);
					}

					@Override
					public void pathNotFound(String path) {
						sceneNotFound(path);
					}
				});
	}

	protected void sceneNotFound(String path) {
		Gdx.app.error("GoSceneExecutor", "No scene found in " + path);
	}
}
