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
package es.eucm.ead.engine.tests.systems.behaviors;

import es.eucm.ead.engine.ComponentLoader;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.entities.ActorEntity;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockFiles;
import es.eucm.ead.engine.mock.schema.MockEffect;
import es.eucm.ead.engine.mock.schema.MockEffectExecutor;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.data.VariableDef;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Before;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class BehaviorTest {

	protected GameLoop gameLoop;

	private EntitiesLoader entitiesLoader;

	private ComponentLoader componentLoader;

	protected VariablesManager variablesManager;

	protected EffectsSystem effectsSystem;

	@Before
	public void setUp() {
		MockApplication.initStatics();

		gameLoop = new GameLoop();

		GameAssets gameAssets = new GameAssets(new MockFiles());
		componentLoader = new ComponentLoader(gameAssets);

		entitiesLoader = new EntitiesLoader(null, componentLoader, gameLoop,
				null);

		variablesManager = new VariablesManager(componentLoader);
		effectsSystem = new EffectsSystem(gameLoop, variablesManager);
		gameLoop.addSystem(effectsSystem);
		effectsSystem.registerEffectExecutor(MockEffect.class,
				new MockEffectExecutor());

		addSystems(gameLoop);

		Map<Class, ComponentProcessor> componentProcessors = new HashMap<Class, ComponentProcessor>();
		registerComponentProcessors(gameLoop, componentProcessors);
		for (Entry<Class, ComponentProcessor> e : componentProcessors
				.entrySet()) {
			componentLoader
					.registerComponentProcessor(e.getKey(), e.getValue());
		}
	}

	/**
	 * Adds a variable that can be used in the test
	 */
	protected void addVariable(String name, VariableDef.Type type,
			String initialValue) {
		VariableDef variableDef = new VariableDef();
		variableDef.setInitialValue(initialValue);
		variableDef.setName(name);
		variableDef.setType(type);
		List<VariableDef> variableDefList = new ArrayList<VariableDef>();
		variableDefList.add(variableDef);
		variablesManager.registerVariables(variableDefList);
	}

	protected void setVariableValue(String name, String expression) {
		variablesManager.setValue(name, expression);
	}

	/**
	 * Converts a model entity into an engine entity, and adds it to the game
	 * loop
	 * 
	 * @return the engine entity
	 */
	protected ActorEntity addEntity(ModelEntity modelEntity) {
		return entitiesLoader.addEntity(modelEntity);
	}

	/**
	 * Adds the require component processors for the test
	 */
	protected abstract void registerComponentProcessors(GameLoop gameLoop,
			Map<Class, ComponentProcessor> componentProcessors);

	/**
	 * Adds the require systems for the test
	 */
	public abstract void addSystems(GameLoop gameLoop);

}
