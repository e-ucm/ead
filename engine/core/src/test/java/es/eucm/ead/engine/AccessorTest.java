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
import es.eucm.ead.engine.mock.MockApplication;
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

	@Test
	public void testSchema() {
		// Create the object structure: a game with 1 scene that has 50
		// elements.
		/*
		 * Game game = new Game(); Map<String, Scene> sceneMap = new
		 * HashMap<String, Scene>(); Scene scene1 = new Scene();
		 * scene1.setChildren(new ArrayList<SceneElement>()); for (int i = 0; i
		 * < 50; i++) { SceneElement sceneElement = new SceneElement();
		 * sceneElement.setVisible(i % 5 == 0); sceneElement.setTags(new
		 * ArrayList<String>()); for (int j = i; j < 50; j++) {
		 * sceneElement.getTags().add("tag" + j); } sceneElement.setChildren(new
		 * ArrayList<SceneElement>()); if (i % 2 == 0) { SceneElement child =
		 * new SceneElement(); child.setTags(new ArrayList<String>());
		 * child.getTags().add("Child"); sceneElement.getChildren().add(child);
		 * 
		 * Transformation transformation = new Transformation();
		 * transformation.setX(2.0F); transformation.setY(3.0F);
		 * sceneElement.setTransformation(transformation); }
		 * 
		 * scene1.getChildren().add(sceneElement); } sceneMap.put("scene1",
		 * scene1); game.setInitialScene("scene1"); game.setWidth(1200);
		 * game.setHeight(800);
		 * 
		 * // Create accessor Map<String, Object> rootObjects = new
		 * HashMap<String, Object>(); rootObjects.put("game", game);
		 * rootObjects.put("scenes", sceneMap); Accessor accessor = new
		 * Accessor(rootObjects);
		 * 
		 * // Test things that should work Object object1 =
		 * accessor.resolve("scenes<scene1>.children[0]");
		 * assertTrue(object1.getClass() == SceneElement.class);
		 * 
		 * Object object2 = accessor.resolve("game");
		 * assertTrue(object2.getClass() == Game.class);
		 * 
		 * Object object3 = accessor.resolve("game.width");
		 * assertTrue(object3.getClass() == Integer.class);
		 * assertTrue(((Integer) object3).intValue() == 1200);
		 * 
		 * // Test malformed ids accessorExceptionExpected(accessor,
		 * "game.heights"); accessorExceptionExpected(accessor,
		 * "scenes scene1>.children[0]"); accessorExceptionExpected(accessor,
		 * "scenes>scene1>.children[0]"); accessorExceptionExpected(accessor,
		 * "scenes.scene1>.children[0]"); accessorExceptionExpected(accessor,
		 * "scenes<scene1>.children[0].children[0].tags[1]");
		 * accessorExceptionExpected(accessor,
		 * "scenes<scene1>.children[0].children[0].tag[0]");
		 * accessorExceptionExpected(accessor,
		 * "scenes<scene1>.children[0].children[0].tags [0]");
		 * accessorExceptionExpected(accessor,
		 * "scenes<scene1>.children[0].children[0]. tags[0]");
		 * accessorExceptionExpected(accessor,
		 * "scenes<scene1>.children[0].children[0].tags[0].");
		 * accessorExceptionExpected(accessor,
		 * "scenes<scene1>.children[0].children[0].tags[0");
		 * accessorExceptionExpected(accessor, "scenes<s1>");
		 * accessorExceptionExpected(accessor, "scenes<scene1>.children[zero]");
		 * accessorExceptionExpected(accessor, ".game");
		 * accessorExceptionExpected(accessor, "");
		 * accessorExceptionExpected(accessor, "games");
		 * 
		 * boolean exceptionThrown = false; try { accessor.resolve(null); }
		 * catch (NullPointerException e) { exceptionThrown = true; }
		 * assertTrue("Null ids are not allowed", exceptionThrown);
		 */
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
		Accessor accessor = new Accessor(rootObjects);
		Object object = accessor.resolve("complexMap<key1>[0]<key2>[0][0][0]");
		assertTrue(object.getClass() == Integer.class);
		assertTrue(((Integer) object).intValue() == 123);
	}
}
