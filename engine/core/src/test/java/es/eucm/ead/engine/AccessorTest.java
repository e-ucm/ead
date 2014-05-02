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
import ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.VisibilityComponent;
import es.eucm.ead.engine.entities.ActorEntity;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockFiles;
import es.eucm.ead.engine.processors.TagsProcessor;
import es.eucm.ead.engine.processors.VisibilityProcessor;
import es.eucm.ead.engine.systems.variables.VariablesSystem;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.components.Visibility;
import es.eucm.ead.schema.components.game.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Tests {@link Accessor} Created by Javier Torrente on 10/04/14.
 */
public class AccessorTest {
	@BeforeClass
	public static void initStatics() {
		MockApplication.initStatics();
	}

	private Map<String, Object> getRootObjects() {
		// Create the object structure: a game with 1 scene that has 50
		// elements.

		ModelEntity game = new ModelEntity();
		Map<String, ModelEntity> sceneMap = new HashMap<String, ModelEntity>();
		ModelEntity scene1 = new ModelEntity();

		for (int i = 0; i < 50; i++) {
			ModelEntity sceneElement = new ModelEntity();
			Visibility visibilityComponent = new Visibility();
			visibilityComponent.setCondition(i % 5 == 0 ? "btrue" : "bfalse");
			sceneElement.getComponents().add(visibilityComponent);

			Tags tagsComponent = new Tags();
			for (int j = i; j < 50; j++) {
				tagsComponent.getTags().add("tag" + j);
			}
			sceneElement.getComponents().add(tagsComponent);

			if (i % 2 == 0) {
				ModelEntity child = new ModelEntity();
				Tags tagsComponent2 = new Tags();
				child.getComponents().add(tagsComponent2);
				tagsComponent2.getTags().add("Child");
				sceneElement.getChildren().add(child);

				sceneElement.setX(2.0F);
				sceneElement.setY(3.0F);
			}

			scene1.getChildren().add(sceneElement);
		}

		sceneMap.put("scene1", scene1);

		GameData gameData = new GameData();
		game.getComponents().add(gameData);
		gameData.setInitialScene("scene1");
		gameData.setWidth(1200);
		gameData.setHeight(800);

		// Create rootObjects
		Map<String, Object> rootObjects = new HashMap<String, Object>();
		rootObjects.put("game", game);
		rootObjects.put("scenes", sceneMap);

		return rootObjects;
	}

	@Test
	public void testSchema() {
		Map<String, Object> rootObjects = getRootObjects();
		Accessor accessor = new Accessor(rootObjects, new EntitiesLoader(
				new GameAssets(new MockFiles()), new GameLoop(),
				new GameLayers()));

		// Test things that should work
		Object object1 = accessor.resolve("scenes<scene1>.children[0]");
		assertTrue(object1.getClass() == ModelEntity.class);

		Object object2 = accessor.resolve("scenes<scene1>.children[0].x");
		assertTrue(((Float) object2).floatValue() == 2.0F);

		object2 = accessor.resolve("scenes<scene1>.children[1].y");
		assertTrue(((Float) object2).floatValue() == 0.0F);

		Object object3 = accessor.resolve("game");
		assertTrue(object3.getClass() == ModelEntity.class);

		Object object4 = accessor.resolve("game.components[0].width");
		assertTrue(object4.getClass() == Integer.class);
		assertTrue(((Integer) object4).intValue() == 1200);

		// Test malformed ids
		accessorExceptionExpected(accessor, "game.components[0].heights");
		accessorExceptionExpected(accessor, "scenes scene1>.children[0]");
		accessorExceptionExpected(accessor, "scenes>scene1>.children[0]");
		accessorExceptionExpected(accessor, "scenes.scene1>.children[0]");
		accessorExceptionExpected(accessor,
				"scenes<scene1>.children[0].children[0].tags[1]");
		accessorExceptionExpected(accessor,
				"scenes<scene1>.children[0].children[0].tag[0]");
		accessorExceptionExpected(accessor,
				"scenes<scene1>.children[0].children[0].tags [0]");
		accessorExceptionExpected(accessor,
				"scenes<scene1>.children[0].children[0]. tags[0]");
		accessorExceptionExpected(accessor,
				"scenes<scene1>.children[0].children[0].tags[0].");
		accessorExceptionExpected(accessor,
				"scenes<scene1>.children[0].children[0].tags[0");
		accessorExceptionExpected(accessor, "scenes<s1>");
		accessorExceptionExpected(accessor, "scenes<scene1>.children[zero]");
		accessorExceptionExpected(accessor, ".game");
		accessorExceptionExpected(accessor, "");
		accessorExceptionExpected(accessor, "games");

		boolean exceptionThrown = false;
		try {
			accessor.resolve(null);
		} catch (NullPointerException e) {
			exceptionThrown = true;
		}
		assertTrue("Null ids are not allowed", exceptionThrown);

	}

	@Test
	/**
	 * Tests that given an {@link Entity}, its components are accessible through accessor
	 */
	public void testEngineComponents() {
		Map<String, Object> rootObjects = getRootObjects();

		GameAssets gameAssets = new GameAssets(new MockFiles());
		GameLoop gameLoop = new GameLoop();
		GameLayers gameLayers = new GameLayers();
		EntitiesLoader entitiesLoader = new EntitiesLoader(gameAssets,
				gameLoop, gameLayers);

		entitiesLoader.registerComponentProcessor(Visibility.class,
				new VisibilityProcessor(gameLoop));
		entitiesLoader.registerComponentProcessor(Tags.class,
				new TagsProcessor(gameLoop));

		Accessor accessor = new Accessor(rootObjects, entitiesLoader);

		ModelEntity gameEntity = (ModelEntity) rootObjects.get("game");
		entitiesLoader.addEntity(gameEntity);
		Map<String, ModelEntity> scenes = (Map<String, ModelEntity>) rootObjects
				.get("scenes");
		for (ModelEntity scene : scenes.values()) {
			entitiesLoader.addEntity(scene);
		}

		boolean notEmptyMap = false;
		IntMap<Entity> map = gameLoop.getEntitiesFor(Family
				.getFamilyFor(VisibilityComponent.class));
		for (IntMap.Entry<Entity> entry : map.entries()) {
			notEmptyMap = true;
			Entity entity = entry.value;
			accessor.getRootObjects().clear();
			accessor.getRootObjects().put("$_this", entity);
			assertTrue(accessor.resolve("$_this.group.x") instanceof Float);
			assertTrue(accessor.resolve("$_this.group.scaleX") instanceof Float);
			assertTrue(accessor
					.resolve("$_this.components<es.eucm.ead.schema.components.Visibility>") instanceof VisibilityComponent);
			assertTrue(accessor.resolve("$_this.components<visibility>") instanceof VisibilityComponent);
			accessorExceptionExpected(accessor, "$_this.components<visibilit>");
			assertTrue(accessor
					.resolve("$_this.components<es.eucm.ead.engine.components.VisibilityComponent>") instanceof VisibilityComponent);
		}

		assertTrue(notEmptyMap);
	}

	private void accessorExceptionExpected(Accessor accessor, String id) {
		boolean exceptionThrown = false;
		try {
			accessor.resolve(id);
		} catch (Accessor.AccessorException e) {
			exceptionThrown = true;
			Gdx.app.debug(
					"Accessor",
					"Exception thrown (as expected) with message:"
							+ e.getMessage());
		}
		assertTrue(
				"When the name of a property or object does not exist, or the ID provided is not well formed, an exception should be thrown",
				exceptionThrown);
	}

	@Test
	public void testComplexMapAndList() {
		Map<String, List<Map<String, List<List<List<Integer>>>>>> complexMap = new HashMap<String, List<Map<String, List<List<List<Integer>>>>>>();
		complexMap.put("key1",
				new ArrayList<Map<String, List<List<List<Integer>>>>>());
		complexMap.get("key1").add(
				new HashMap<String, List<List<List<Integer>>>>());
		complexMap.get("key1").get(0)
				.put("key2", new ArrayList<List<List<Integer>>>());
		complexMap.get("key1").get(0).get("key2")
				.add(new ArrayList<List<Integer>>());
		complexMap.get("key1").get(0).get("key2").get(0)
				.add(new ArrayList<Integer>());
		complexMap.get("key1").get(0).get("key2").get(0).get(0).add(123);

		Map<String, Object> rootObjects = new HashMap<String, Object>();
		rootObjects.put("complexMap", complexMap);
		Accessor accessor = new Accessor(rootObjects, null);
		Object object = accessor.resolve("complexMap<key1>[0]<key2>[0][0][0]");
		assertTrue(object.getClass() == Integer.class);
		assertTrue(((Integer) object).intValue() == 123);
	}
}
