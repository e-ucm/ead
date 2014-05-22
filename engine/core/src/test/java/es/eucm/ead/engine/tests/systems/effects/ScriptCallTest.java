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
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockEntitiesLoader;
import es.eucm.ead.engine.mock.schema.MockModelComponent;
import es.eucm.ead.engine.processors.behaviors.TimersProcessor;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.systems.behaviors.TimersSystem;
import es.eucm.ead.engine.systems.effects.ChangeEntityPropertyExecutor;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.components.behaviors.timers.Timer;
import es.eucm.ead.schema.components.behaviors.timers.Timers;
import es.eucm.ead.schema.data.Script;
import es.eucm.ead.schema.data.VariableDef;
import es.eucm.ead.schema.effects.ChangeEntityProperty;
import es.eucm.ead.schema.effects.ScriptCall;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Javier Torrente on 13/05/14.
 */
public class ScriptCallTest {

	@BeforeClass
	public static void setupStatics() {
		MockApplication.initStatics();
	}

	@Test
	public void test() {
		// Initialization
		MockEntitiesLoader entitiesLoader = new MockEntitiesLoader();
		GameLoop gameLoop = entitiesLoader.getGameLoop();
		VariablesManager variablesManager = new VariablesManager(
				entitiesLoader.getComponentLoader());
		EffectsSystem effectsSystem = new EffectsSystem(gameLoop,
				variablesManager);
		effectsSystem.registerEffectExecutor(ChangeEntityProperty.class,
				new ChangeEntityPropertyExecutor(variablesManager));
		gameLoop.addSystem(effectsSystem);
		TimersSystem timersSystem = new TimersSystem(gameLoop, variablesManager);
		gameLoop.addSystem(timersSystem);
		entitiesLoader.getComponentLoader().registerComponentProcessor(
				Timers.class, new TimersProcessor(gameLoop));

		// Add one entity
		EngineEntity entity1 = entitiesLoader
				.addEntity(createModelEntityWithInitialization("btrue", "i90",
						"group.x", "(+ $var2 i10)"));
		EngineEntity entity2 = entitiesLoader
				.addEntity(createModelEntityWithInitialization(null, null,
						"group.x", "$var2"));
		gameLoop.update(0);
		gameLoop.update(0);
		assertEquals(
				"The x attribute of the entity has not been initialized properly",
				100, entity1.getGroup().getX(), 0);

		// Test default values
		assertEquals(
				"The x attribute of the entity has not taken the default value",
				10, entity2.getGroup().getX(), 0);

		// Test not valid argument values
		EngineEntity entity3 = entitiesLoader
				.addEntity(createModelEntityWithInitialization(null, null,
						"group.x", "$var1"));
		try {
			gameLoop.update(0);
			gameLoop.update(0);
			assertEquals(
					"The x attribute of the entity should not have been initialized, as the expression returns an incompatible object type",
					0, entity3.getGroup().getX(), 0);
		} catch (Accessor.AccessorException e) {
			fail("Exception should have been captured elsewhere");
		}

		// Test variables can be used in any expression contained in the
		// EffectsComponent (including, for example, the condition)
		EngineEntity entity4 = entitiesLoader
				.addEntity(createModelEntityWithInitialization("bfalse", "i50",
						"group.x", "$var2", "$var1"));
		EngineEntity entity5 = entitiesLoader
				.addEntity(createModelEntityWithInitialization("btrue", "i50",
						"group.x", "$var2", "$var1"));
		gameLoop.update(0);
		gameLoop.update(0);
		assertEquals(
				"The x attribute of the entity should not have been initialized, as the condition is not met (var1=false)",
				0, entity4.getGroup().getX(), 0);
		assertEquals(
				"The x attribute of the entity should have been initialized, as the condition is met (var1=true)",
				50, entity5.getGroup().getX(), 0);
	}

	/**
	 * Creates a model entity that is initialized with the given arguments.
	 * Arguments are expected to be passed following the next structure:
	 * 
	 * (property, expression, condition?) where property is the property to be
	 * initialized following the Accessor schema (e.g. "group.x"), expression is
	 * the expression used to determine the new value for the property, and
	 * condition, which is optional for the last element, is the boolean
	 * expression that must be met in order to actually get the effect executed.
	 */
	private ModelEntity createModelEntityWithInitialization(String var1Value,
			String var2Value, String... args) {
		ModelEntity modelEntity = new ModelEntity();
		Script script = new Script();
		ScriptCall scriptCall = new ScriptCall();
		scriptCall.setScript(script);
		for (int i = 0; i < args.length; i++) {
			ChangeEntityProperty changeEntityProperty = new ChangeEntityProperty();
			changeEntityProperty.setProperty(args[i]);
			changeEntityProperty.setExpression(args[++i]);
			if (i < args.length - 1) {
				changeEntityProperty.setCondition(args[++i]);
			}
			script.getEffects().add(changeEntityProperty);
		}

		VariableDef var1 = new VariableDef();
		var1.setName("var1");
		var1.setType(VariableDef.Type.BOOLEAN);
		var1.setInitialValue("true");
		script.getInputArguments().add(var1);

		VariableDef var2 = new VariableDef();
		var2.setName("var2");
		var2.setType(VariableDef.Type.INTEGER);
		var2.setInitialValue("10");
		script.getInputArguments().add(var2);

		if (var1Value != null)
			scriptCall.getInputArgumentValues().add(var1Value);
		if (var2Value != null)
			scriptCall.getInputArgumentValues().add(var2Value);

		Timer timer = new Timer();
		timer.setTime(0);
		timer.getEffects().add(scriptCall);
		Timers timers = new Timers();
		timers.getTimers().add(timer);

		modelEntity.getComponents().add(timers);
		// Add also mock component so there are more things that can be accessed
		MockModelComponent mockModelComponent = new MockModelComponent();
		mockModelComponent.setFloatAttribute(5);
		modelEntity.getComponents().add(mockModelComponent);
		return modelEntity;
	}
}
