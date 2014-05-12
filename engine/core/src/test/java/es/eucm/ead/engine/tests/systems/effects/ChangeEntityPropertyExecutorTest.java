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
package es.eucm.ead.engine.tests.systems.effects;

import es.eucm.ead.engine.Accessor;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.entities.ActorEntity;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockEngineComponent;
import es.eucm.ead.engine.mock.MockEntitiesLoader;
import es.eucm.ead.engine.mock.schema.MockModelComponent;
import es.eucm.ead.engine.systems.effects.ChangeEntityPropertyExecutor;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.effects.ChangeEntityProperty;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Javier Torrente on 6/05/14.
 */
public class ChangeEntityPropertyExecutorTest {

	private ChangeEntityPropertyExecutor executor;

	private EntitiesLoader entitiesLoader;

	private Accessor accessor;

	@BeforeClass
	public static void setUpClass() {
		MockApplication.initStatics();
	}

	@Before
	public void setUp() {
		entitiesLoader = new MockEntitiesLoader();
		accessor = new Accessor(null, entitiesLoader);
		executor = new ChangeEntityPropertyExecutor(accessor,
				new VariablesManager(accessor));
	}

	@Test
	public void test() {
		// Add a "mock" component into an entity
		ModelEntity entity = new ModelEntity();
		MockModelComponent mockModelComponent = new MockModelComponent();
		mockModelComponent.setFloatAttribute(10);
		entity.getComponents().add(mockModelComponent);

		// Add entity to gameLoop
		ActorEntity actorEntity = entitiesLoader.addEntity(entity);
		// Check its float value is correct
		assertTrue(10 == actorEntity.getComponent(MockEngineComponent.class)
				.getFloatAttribute());

		// Create effect
		ChangeEntityProperty changeEntityProp = new ChangeEntityProperty();
		changeEntityProp.setProperty("components<mock>.floatAttribute");
		changeEntityProp.setExpression("(* f50 (+ f1 f1))");
		executor.execute(actorEntity, changeEntityProp);
		assertTrue(100 == actorEntity.getComponent(MockEngineComponent.class)
				.getFloatAttribute());
	}
}
