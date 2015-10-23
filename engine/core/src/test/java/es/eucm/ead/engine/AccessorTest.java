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

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import es.eucm.ead.engine.components.VisibilityComponent;
import es.eucm.ead.engine.processors.TagsProcessor;
import es.eucm.ead.engine.processors.VisibilityProcessor;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.components.Visibility;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests {@link Accessor} Created by Javier Torrente on 10/04/14.
 */
public class AccessorTest extends EngineTest {

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

		MockAccessorComponent mockAccessorComponent = new MockAccessorComponent();
		game.getComponents().add(mockAccessorComponent);
		mockAccessorComponent.setInitialScene("scene1");
		mockAccessorComponent.setWidth(1200);
		mockAccessorComponent.setHeight(800);

		// Create rootObjects
		Map<String, Object> rootObjects = new HashMap<String, Object>();
		rootObjects.put("game", game);
		rootObjects.put("scenes", sceneMap);

		return rootObjects;
	}

	@Test
	public void testReadSchema() {
		Map<String, Object> rootObjects = getRootObjects();
		accessor.setRootObjects(rootObjects);

		// Test things that should work
		Object object1 = accessor.get("scenes<scene1>.children[0]");
		assertTrue(object1.getClass() == ModelEntity.class);

		Object object2 = accessor.get("scenes<scene1>.children[0].x");
		assertTrue(((Float) object2) == 2.0F);

		object2 = accessor.get("scenes<scene1>.children[1].y");
		assertTrue(((Float) object2) == 0.0F);

		Object object3 = accessor.get("game");
		assertTrue(object3.getClass() == ModelEntity.class);

		Object object4 = accessor.get("game.components[0].width");
		assertTrue(object4.getClass() == Integer.class);
		assertTrue(((Integer) object4) == 1200);

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
			accessor.get(null);
		} catch (NullPointerException e) {
			exceptionThrown = true;
		}
		assertTrue("Null ids are not allowed", exceptionThrown);

	}

	@SuppressWarnings("unchecked")
	@Test
	/**
	 * Tests that given an {@link Entity}, its components are accessible through accessor
	 */
	public void testEngineComponents() {
		Map<String, Object> rootObjects = getRootObjects();

		componentLoader.registerComponentProcessor(Visibility.class,
				new VisibilityProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Tags.class,
				new TagsProcessor(gameLoop));

		accessor.setRootObjects(rootObjects);

		ModelEntity gameEntity = (ModelEntity) rootObjects.get("game");
		entitiesLoader.toEngineEntity(gameEntity);
		Map<String, ModelEntity> scenes = (Map<String, ModelEntity>) rootObjects
				.get("scenes");
		for (ModelEntity scene : scenes.values()) {
			entitiesLoader.toEngineEntity(scene);
		}

		boolean notEmptyMap = false;
		ImmutableArray<Entity> map = gameLoop.getEntitiesFor(Family.all(
				VisibilityComponent.class).get());
		for (Entity entity : map) {
			notEmptyMap = true;
			accessor.getRootObjects().clear();
			accessor.getRootObjects().put("$_this", entity);
			assertTrue(accessor.get("$_this.group.x") instanceof Float);
			assertTrue(accessor.get("$_this.group.scaleX") instanceof Float);
			assertTrue(accessor
					.get("$_this.components<es.eucm.ead.schema.components.Visibility>") instanceof VisibilityComponent);
			assertTrue(accessor.get("$_this.components<visibility>") instanceof VisibilityComponent);
			accessorExceptionExpected(accessor, "$_this.components<visibilit>");
			assertTrue(accessor
					.get("$_this.components<es.eucm.ead.engine.components.VisibilityComponent>") instanceof VisibilityComponent);
		}

		assertTrue(notEmptyMap);
	}

	private void accessorExceptionExpected(Accessor accessor, String id) {
		boolean exceptionThrown = false;
		try {
			accessor.get(id);
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
		accessor.setRootObjects(rootObjects);
		Object object = accessor.get("complexMap<key1>[0]<key2>[0][0][0]");
		assertTrue(object.getClass() == Integer.class);
		assertTrue(((Integer) object) == 123);
	}

	@Test
	public void testReadWriteAccess() {
		// Create objects to test
		Object1 object1 = new Object1();
		object1.array = new Array<Object2>();
		object1.list = new ArrayList<Object2>();
		object1.cMap = new HashMap<Class, Object2>();
		object1.sMap = new HashMap<String, Object2>();
		object1.iMap = new IntMap<Object2>();
		object1.fMap = new ObjectMap<Float, Object2>();
		object1.a1 = 1;
		object1.o2 = new Object2(2, 3);

		// Populate containers
		object1.sMap.put("AAA", new Object2(4, 5));
		object1.sMap.put("BBB", new Object2(6, 7));
		object1.iMap.put(0, new Object2(8, 9));
		object1.iMap.put(1, new Object2(10, 11));
		object1.fMap.put(2.0F, new Object2(12, 13));
		object1.fMap.put(3.0F, new Object2(14, 15));
		object1.cMap.put(Float.class, new Object2(16, 17));
		object1.cMap.put(Double.class, new Object2(18, 19));

		object1.array.add(new Object2(20, 21));
		object1.array.add(new Object2(22, 23));
		object1.list.add(new Object2(24, 25));
		object1.list.add(new Object2(26, 27));

		// Create accessor
		Map<String, Object> rootObjects = new HashMap<String, Object>();
		rootObjects.put("o1", object1);
		accessor.setRootObjects(rootObjects);

		// Test write-read basic type field
		assertEquals(1, accessor.get("o1.a1"));
		accessor.set("o1.a1", 100);
		assertEquals(100, accessor.get("o1.a1"));

		accessor.set(object1, "a1", 200);
		assertEquals(200, accessor.get("o1.a1"));

		// Test write-read complex type field
		accessor.set("o1.o2", new Object2(200, 300));
		assertEquals(200, accessor.get("o1.o2.a2"));
		assertEquals(300, accessor.get("o1.o2.b2"));

		// Test writing null
		accessor.set("o1.o2", null);
		assertNull(accessor.get("o1.o2"));

		// Test writing Array
		assertEquals(21, accessor.get("o1.array[0].b2"));
		accessor.set("o1.array[0]", new Object2(200, 210));
		assertEquals(210, accessor.get("o1.array[0].b2"));

		// Test writing list
		assertEquals(26, accessor.get("o1.list[1].a2"));
		accessor.set("o1.list[1]", new Object2(260, 270));
		assertEquals(260, accessor.get("o1.list[1].a2"));

		// Test writing not valid index in list
		try {
			accessor.get(object1, "list[2]");
			fail("An AccessorException was expected");
		} catch (Throwable throwable) {
			assertEquals(Accessor.AccessorException.class, throwable.getClass());
		}

		// Test writing maps
		accessor.set("o1.sMap<AAA>", new Object2(1000, 1000));
		accessor.set("o1.iMap<1>", new Object2(1000, 1000));
		accessor.set("o1.fMap<2.0>", new Object2(1000, 1000));
		accessor.set(object1, "cMap<java.lang.Double>", new Object2(1000, 1000));

		assertEquals(1000, accessor.get("o1.sMap<AAA>.a2"));
		assertEquals(1000, accessor.get(object1, "iMap<1>.a2"));
		assertEquals(1000, accessor.get("o1.fMap<2.0>.a2"));
		assertEquals(1000, accessor.get("o1.cMap<java.lang.Double>.a2"));

		// Test accessing map with not valid key type
		try {
			accessor.get("o1.iMap<A>");
			fail("An AccessorException was expected");
		} catch (Throwable throwable) {
			assertEquals(Accessor.AccessorException.class, throwable.getClass());
		}

		// Test accessing map key does not exist
		try {
			accessor.get("o1.fMap<4.0>");
			fail("An AccessorException was expected");
		} catch (Throwable throwable) {
			assertEquals(Accessor.AccessorException.class, throwable.getClass());
		}

	}

	@Test
	public void testEnums() {
		Object2 object2 = new Object2(0, 0);
		Map<String, Object> rootObjects = new HashMap<String, Object>();
		rootObjects.put("o2", object2);
		accessor.setRootObjects(rootObjects);

		// Test write enum
		// assertEquals(1, accessor.get("o2.c2"));
		accessor.set("o2.c2", 1);
		assertEquals(Enum1.CONSTANT2, object2.c2);
		try {
			accessor.set("o2.c2", 4);
			fail("An exception should have been thrown");
		} catch (Accessor.AccessorException e) {
		}
		assertEquals(Enum1.CONSTANT2, object2.c2);
		accessor.set("o2.c2", "CONSTANT3");
		assertEquals(Enum1.CONSTANT3, object2.c2);
		accessor.set("o2.c2", "constant2");
		assertEquals(Enum1.CONSTANT2, object2.c2);
		try {
			accessor.set("o2.c2", "c1");
			fail("An exception should have been thrown");
		} catch (Accessor.AccessorException e) {
		}
		assertEquals(Enum1.CONSTANT2, accessor.get("o2.c2"));
	}

	private enum Enum1 {
		CONSTANT1("c1"), CONSTANT2("c2"), CONSTANT3("c3");
		private String val;

		private Enum1(String v) {
			this.val = v;
		}
	}

	private class Object1 {
		public int a1;
		public Object2 o2;
		public Array<Object2> array;
		public List<Object2> list;

		public Map<String, Object2> sMap;
		public IntMap<Object2> iMap;
		public ObjectMap<Float, Object2> fMap;
		public Map<Class, Object2> cMap;
	}

	private class Object2 {

		public int a2;
		public int b2;
		public Enum1 c2;

		public Object2(int a, int b) {
			this.a2 = a;
			this.b2 = b;
			this.c2 = Enum1.CONSTANT1;
		}
	}

	public static class MockAccessorComponent extends ModelComponent {

		private String initialScene;
		private int width;
		private int height;

		public void setInitialScene(String initialScene) {
			this.initialScene = initialScene;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}
	}
}
