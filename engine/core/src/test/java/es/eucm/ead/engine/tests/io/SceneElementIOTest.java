/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.engine.tests.io;

import es.eucm.ead.engine.Engine;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.components.Transformation;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schema.renderers.Renderer;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SceneElementIOTest extends SchemaIOTest {
	@Test
	public void testSceneElement() {
		SceneElement sceneElement = schemaIO.fromJson(SceneElement.class,
				Engine.assets.resolve("sceneelement.json"));
		assertNotNull(sceneElement);
		assertEquals(sceneElement.isEnable(), false);
		assertEquals(sceneElement.isVisible(), true);
		assertNotNull(sceneElement.getBehaviors());
		assertEquals(sceneElement.getBehaviors().size(), 0);
		assertNotNull(sceneElement.getActions());
		assertEquals(sceneElement.getActions().size(), 0);
		assertNull(sceneElement.getRef());

		Renderer renderer = sceneElement.getRenderer();
		assertTrue(renderer instanceof Image);
		assertEquals(((Image) renderer).getUri(), "image.png");

		Transformation t = sceneElement.getTransformation();
		assertNotNull(t);
		assertEquals(t.getRotation(), 45.0f);
	}

	@Test
	public void testSceneElementRef() {
		SceneElement sceneElement = schemaIO.fromJson(SceneElement.class,
				Engine.assets.resolve("sceneelementref.json"));
		assertNotNull(sceneElement);
		assertEquals(sceneElement.isEnable(), true);
		assertEquals(sceneElement.isVisible(), true);
		assertNotNull(sceneElement.getBehaviors());
		assertEquals(sceneElement.getBehaviors().size(), 0);
		assertNotNull(sceneElement.getActions());
		assertEquals(sceneElement.getActions().size(), 0);
		assertEquals(sceneElement.getRef(), "sceneelement.json");

		Renderer renderer = sceneElement.getRenderer();
		assertTrue(renderer instanceof Image);
		assertEquals(((Image) renderer).getUri(), "image2.png");

		Transformation t = sceneElement.getTransformation();
		assertNotNull(t);
		assertEquals(t.getRotation(), 45.0f);

	}
}
