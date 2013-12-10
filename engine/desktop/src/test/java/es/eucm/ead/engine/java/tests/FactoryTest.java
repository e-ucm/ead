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
import es.eucm.ead.engine.BindingsLoader;
import es.eucm.ead.engine.BindingsLoader.BindingListener;
import es.eucm.ead.engine.Factory;
import es.eucm.ead.engine.java.tests.application.engineobjects.TestEngineObject;
import es.eucm.ead.engine.java.tests.application.schema.TestSchemaObject;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FactoryTest extends LwjglTest implements BindingListener {

	private Map<Class, Class> relations;

	private BindingsLoader bindingsLoader;

	@Before
	public void setUp() {
		super.setUp();
		bindingsLoader = new BindingsLoader();
	}

	@Test
	public void testFactoryGet() {
		String bindingsJson = "[[es.eucm.ead.engine.java.tests.application.schema, es.eucm.ead.engine.java.tests.application.engineobjects],[TestSchemaObject, TestEngineObject]]";
		Factory factory = new Factory();
		bindingsLoader.addBindingListener(factory);
		bindingsLoader.load(bindingsJson);
		TestSchemaObject schemaObject = new TestSchemaObject();
		assertEquals(factory.getEngineObject(schemaObject).getClass(),
				TestEngineObject.class);
		assertTrue(bindingsLoader.removeBindingListener(factory));
	}

	@Test
	public void testFactoryLoadBindings() {
		relations = new HashMap<Class, Class>();

		Factory factory = new Factory();
		bindingsLoader.addBindingListener(factory);
		bindingsLoader.addBindingListener(this);

		// We don't test factory.get here because some engine objects rely on
		// the correct initialization of assets and other engine components
		assertTrue(bindingsLoader.load(Gdx.files.internal("bindings.json")));
		assertTrue(bindingsLoader.removeBindingListener(this));

		for (Entry<Class, Class> e : relations.entrySet()) {
			assertTrue(factory.containsRelation(e.getKey()));
		}
	}

	@Override
	public void bind(String alias, Class schemaClass, Class engineClass) {
		if (engineClass != null) {
			relations.put(schemaClass, engineClass);
		}
	}
}
