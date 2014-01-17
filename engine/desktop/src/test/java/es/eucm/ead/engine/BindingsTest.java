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
package es.eucm.ead.engine;

import es.eucm.ead.engine.BindingsLoader.BindingListener;
import es.eucm.ead.engine.application.TestGame;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests that all bindings in bindings.json are correct
 */
public class BindingsTest implements BindingListener {

	private BindingsLoader bindingsLoader;

	@Before
	public void setUp() {
		new TestGame();
		bindingsLoader = new BindingsLoader();
	}

	@Test
	public void testEmptyBindings() {
		String json = "[";
		json += "]";
		assertTrue(bindingsLoader.load(json));
	}

	@Test
	public void testErrorBindings() {
		assertFalse(bindingsLoader.load("ñor"));
	}

	@Test
	public void testSimpleBindings() {
		String json = "[[java.lang, java.lang],[Object, Object],[Object]]";
		bindingsLoader.addBindingListener(this);
		assertTrue(bindingsLoader.load(json));
		assertTrue(bindingsLoader.removeBindingListener(this));
	}

	@Test
	public void testInternalBindings() {
		assertTrue(bindingsLoader.load(Engine.assets.resolve("bindings.json")));
	}

	@Test
	public void testEngineLoadBindings() {
		assertTrue(Engine.engine.loadBindings());
	}

	@Override
	public void bind(String alias, Class schemaClass, Class engineClass) {
		assertEquals(alias, "object");
		assertEquals(Object.class, schemaClass);
		if (engineClass != null) {
			assertEquals(Object.class, engineClass);
		}
	}
}
