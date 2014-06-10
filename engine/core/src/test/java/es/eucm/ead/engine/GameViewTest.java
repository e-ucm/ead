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

import ashley.core.Entity;
import ashley.core.EntityListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockEngineComponent;
import es.eucm.ead.engine.mock.MockEntitiesLoader;
import es.eucm.ead.engine.mock.schema.MockModelComponent;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.Layer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Javier Torrente on 29/05/14.
 */
public class GameViewTest {

	// keeps count of the total number of engine entities in gameLoop
	private static int count = 0;

	@Test
	// Tests that entities can be added and removed from layers
	public void test() {
		count = 0;
		MockApplication.initStatics();
		EntitiesLoader entitiesLoader = new MockEntitiesLoader();
		GameLoop gameLoop = entitiesLoader.gameLoop;
		gameLoop.addEntityListener(new EntityListener() {
			@Override
			public void entityAdded(Entity entity) {
				count++;
			}

			@Override
			public void entityRemoved(Entity entity) {
				count--;
			}
		});

		DefaultGameView gameView = new DefaultGameView(gameLoop);

		// Populate layers with entities
		int entitiesPerLayer = 10;
		populateLayers(entitiesPerLayer, gameView, entitiesLoader, 0,
				Layer.HUD, Layer.SCENE, Layer.SCENE_CONTENT, Layer.SCENE_HUD);

		assertEquals("HUD layer should have " + entitiesPerLayer + " entities",
				entitiesPerLayer, gameView.getLayer(Layer.HUD).getGroup()
						.getChildren().size);
		assertEquals("SCENE layer should have " + (entitiesPerLayer + 2)
				+ " entities", entitiesPerLayer + 2,
				gameView.getLayer(Layer.SCENE).getGroup().getChildren().size);
		assertEquals(
				"SCENE_CONTENT layer should have " + entitiesPerLayer
						+ " entities",
				entitiesPerLayer,
				gameView.getLayer(Layer.SCENE_CONTENT).getGroup().getChildren().size);
		assertEquals(
				"SCENE_HUD layer should have " + entitiesPerLayer + " entities",
				entitiesPerLayer,
				gameView.getLayer(Layer.SCENE_HUD).getGroup().getChildren().size);

		assertEquals(45, sumUpLayer(gameView.getLayer(Layer.HUD)), 0.001F);
		assertEquals(45 + entitiesPerLayer * entitiesPerLayer * 1,
				sumUpLayer(gameView.getLayer(Layer.SCENE)), 0.001F);
		assertEquals(45 + entitiesPerLayer * entitiesPerLayer * 2,
				sumUpLayer(gameView.getLayer(Layer.SCENE_CONTENT)), 0.001F);
		assertEquals(45 + entitiesPerLayer * entitiesPerLayer * 3,
				sumUpLayer(gameView.getLayer(Layer.SCENE_HUD)), 0.001F);

		assertEquals(
				"There should be 40 entities +4 layers in total in the engine",
				44, count);

		// Test non-recursive clear
		gameView.clearLayer(Layer.SCENE, false);
		assertEquals("SCENE layer should have 2 entities only (sublayers)", 2,
				gameView.getLayer(Layer.SCENE).getGroup().getChildren().size);
		assertEquals(
				"SCENE_CONTENT layer should have " + entitiesPerLayer
						+ " entities",
				entitiesPerLayer,
				gameView.getLayer(Layer.SCENE_CONTENT).getGroup().getChildren().size);
		assertEquals(
				"SCENE_HUD layer should have " + entitiesPerLayer + " entities",
				entitiesPerLayer,
				gameView.getLayer(Layer.SCENE_HUD).getGroup().getChildren().size);
		assertEquals("There should be 30+4 entities in total in the engine",
				34, count);

		// Add back entities to layer
		populateLayers(entitiesPerLayer, gameView, entitiesLoader, 1,
				Layer.SCENE);
		// Test recursive clear
		gameView.clearLayer(Layer.SCENE, true);
		assertEquals("SCENE layer should have 2 entities only (sublayers)", 2,
				gameView.getLayer(Layer.SCENE).getGroup().getChildren().size);
		assertEquals("SCENE_CONTENT should have 0 entities", 0, gameView
				.getLayer(Layer.SCENE_CONTENT).getGroup().getChildren().size);
		assertEquals(
				"SCENE_HUD should have 0 entities",
				0,
				gameView.getLayer(Layer.SCENE_HUD).getGroup().getChildren().size);
		assertEquals(
				"There should be 10 entities + 4 layers in total in the engine",
				14, count);
	}

	private void populateLayers(int entitiesPerLayer, DefaultGameView gameView,
			EntitiesLoader entitiesLoader, int initialJValue, Layer... layers) {
		for (int j = 0; j < layers.length; j++) {
			for (int i = 0; i < entitiesPerLayer; i++) {
				gameView.addEntityToLayer(
						layers[j],
						entitiesLoader.toEngineEntity(createModelEntity(i
								+ (j + initialJValue) * entitiesPerLayer)));
			}
		}
	}

	private ModelEntity createModelEntity(float floatAttribute) {
		ModelEntity modelEntity = new ModelEntity();
		MockModelComponent mockModelComponent = new MockModelComponent();
		mockModelComponent.setFloatAttribute(floatAttribute);
		modelEntity.getComponents().add(mockModelComponent);
		return modelEntity;
	}

	private float sumUpLayer(EngineEntity layer) {
		float sum = 0;
		for (Actor actor : layer.getGroup().getChildren()) {
			if (actor.getUserObject() != null
					&& actor.getUserObject() instanceof EngineEntity) {
				EngineEntity entity = (EngineEntity) actor.getUserObject();
				if (entity.hasComponent(MockEngineComponent.class)) {
					sum += entity.getComponent(MockEngineComponent.class)
							.getFloatAttribute();
				}
			}
		}
		return sum;
	}
}
