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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.viewport.FitViewport;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schemax.Layer;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the "visual" part of the game, which is structured in
 * layers. Any element that is to be rendered on the screen has to belong to a
 * GameLayer. Adding elements to one layer or another just changes the order (Z)
 * they are painted.
 * 
 * The order of the layers, from top to bottom, is
 * <ol>
 * <li>HUD: displays game controls, on top of everything</li>
 * <li>SCENE_HUD: displays additional controls on top of the scene. For instance
 * if a character speaks some dialogue, the spoken text would appear at this
 * layer</li>
 * <li>SCENE_CONTENT: displays normal scene contents (characters, background)</li>
 * </ol>
 * 
 * Each layer can contain its own internal ordering.
 * 
 * For more information, visit: <a
 * href="https://github.com/e-ucm/ead/wiki/Game-view"
 * ·target="_blank">https://github.com/e-ucm/ead/wiki/Game-view</a>
 */
public class GameView extends WidgetGroup {

	private GameLoop gameLoop;

	private Map<Layer, EngineLayer> layers;

	public GameView(GameLoop gameLoop) {
		layers = new HashMap<Layer, EngineLayer>();
		this.gameLoop = gameLoop;
		initializeLayers();
	}

	/*
	 * Just creates the basic layer tree structure by iterating through all
	 * layers registered.
	 */
	private void initializeLayers() {
		for (Layer layer : Layer.values()) {
			EngineLayer engineLayer = new EngineLayer();
			layers.put(layer, engineLayer);
			// If it is root layer, add it directly to this group. Otherwise,
			// find its parent and add it to it
			if (layer.getParentLayer() == null) {
				addActor(engineLayer.getGroup());
			} else {
				addEntityToLayer(layer.getParentLayer(), engineLayer);
			}
		}
	}

	/**
	 * Empties the given layer, getting all children entities removed from the
	 * engine as well. All children layers are preserved.
	 * 
	 * @param layer
	 *            The layer to empty
	 * @param clearChildrenLayers
	 *            If true, it works recursively, clearing also any layer in its
	 *            subtree
	 * @throws java.lang.IllegalArgumentException
	 *             If the layer has children that do not belong to any engine
	 *             entity, or if that link cannot be resolved, to prevent
	 *             infinite loop happening.
	 */
	public void clearLayer(Layer layer, boolean clearChildrenLayers) {
		EngineEntity layerEntity = layers.get(layer);
		// Remove all child entities from the layerEntity (unless they are
		// another layer as well)
		int i = 0;
		while (i < layerEntity.getGroup().getChildren().size) {
			Actor actor = layerEntity.getGroup().getChildren().get(i);
			// It's a layer - don't remove (but clear its children recursively
			// if clearChildrenLayers is true)
			if (actor.getUserObject() != null
					&& actor.getUserObject() instanceof EngineLayer) {
				i++;
				if (clearChildrenLayers) {
					EngineLayer childrenLayer = (EngineLayer) actor
							.getUserObject();
					clearLayer(getLayerForEntity(childrenLayer), true);
				}
			}
			// It's a plain EngineEntity - remove
			else if (actor.getUserObject() != null
					&& actor.getUserObject() instanceof EngineEntity) {
				EngineEntity childEntityToRemove = (EngineEntity) actor
						.getUserObject();
				gameLoop.removeEntity(childEntityToRemove);
			}
			// There should be nothing more than EngineEntities or
			// Engine Layers. So in any other case, just throw an
			// exception:
			else {
				throw new IllegalArgumentException(
						"GameView has a child that does not belong to an EngineEntity or its user object is not set.");
			}
		}
	}

	private Layer getLayerForEntity(EngineLayer anEntity) {
		for (Layer key : layers.keySet()) {
			if (layers.get(key) == anEntity) {
				return key;
			}
		}
		return null;
	}

	/**
	 * Adds the given layer to the given entity. It just attaches the given
	 * {@code entity}'s group to the layer's group
	 * 
	 * @param layer
	 *            The layer
	 * @param entity
	 *            The entity to attach
	 */
	public void addEntityToLayer(Layer layer, EngineEntity entity) {
		layers.get(layer).getGroup().addActor(entity.getGroup());
	}

	/**
	 * @return The engine entity wrapping the content of the {@code layer}
	 *         specified
	 */
	public EngineEntity getLayer(Layer layer) {
		return layers.get(layer);
	}

	public void updateWorldSize(int width, int height) {
		getStage().setViewport(
				new FitViewport(width, height, getStage().getViewport()
						.getCamera()));
		getStage().getViewport().update(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight(), true);
	}

	// Just to differentiate GameView more easily. This also prevents
	// accidental removals since these entities are not kept in game loop.
	private class EngineLayer extends EngineEntity {

	}
}
