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
package es.eucm.ead.engine.java.tests;

import com.badlogic.gdx.Gdx;
import es.eucm.ead.engine.BindLoader;
import es.eucm.ead.engine.BindLoader.BindListener;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests that all binds in binds.json are correct
 */
public class BindsTest extends LwjglTest implements BindListener {

	private BindLoader bindLoader;

	@Before
	public void setUp() {
		super.setUp();
		bindLoader = new BindLoader();
	}

	@Test
	public void testEmptyBinds() {
		String json = "[";
		json += "]";
		assertTrue(bindLoader.load(json));
	}

	@Test
	public void testErrorBinds() {
		assertFalse(bindLoader.load("ñor"));
	}

	@Test
	public void testSimpleBind() {
		String json = "[[java.lang, java.lang],[Object, Object],[Object]]";
		bindLoader.addBindListener(this);
		assertTrue(bindLoader.load(json));
		assertTrue(bindLoader.removeBindListener(this));
	}

	@Test
	public void testInternalBinds() {
		assertTrue(bindLoader.load(Gdx.files.classpath("binds.json")));
	}

	@Override
	public void bind(String alias, Class schemaClass, Class coreClass) {
		assertEquals(alias, "object");
		assertEquals(Object.class, schemaClass);
		if (coreClass != null) {
			assertEquals(Object.class, coreClass);
		}
	}
}
