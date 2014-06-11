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

import ashley.core.Entity;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.engine.Accessor;
import es.eucm.ead.engine.DefaultGameView;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockEntitiesLoader;
import es.eucm.ead.engine.mock.schema.MockEffect;
import es.eucm.ead.engine.mock.schema.MockEffectExecutor;
import es.eucm.ead.engine.mock.schema.MockModelComponent;
import es.eucm.ead.engine.processors.behaviors.TimersProcessor;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.systems.behaviors.TimersSystem;
import es.eucm.ead.engine.systems.effects.ChangeEntityPropertyExecutor;
import es.eucm.ead.engine.systems.effects.ChangeVarExecutor;
import es.eucm.ead.engine.systems.effects.controlstructures.IfExecutor;
import es.eucm.ead.engine.systems.effects.controlstructures.IfThenElseIfExecutor;
import es.eucm.ead.engine.systems.effects.controlstructures.ScriptCallExecutor;
import es.eucm.ead.engine.systems.effects.controlstructures.WhileExecutor;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.components.behaviors.timers.Timer;
import es.eucm.ead.schema.components.behaviors.timers.Timers;
import es.eucm.ead.schema.data.Script;
import es.eucm.ead.schema.data.VariableDef;
import es.eucm.ead.schema.effects.ChangeEntityProperty;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.controlstructures.If;
import es.eucm.ead.schema.effects.controlstructures.IfThenElseIf;
import es.eucm.ead.schema.effects.controlstructures.ScriptCall;
import es.eucm.ead.schema.effects.controlstructures.While;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests: {@link If}, {@link IfThenElseIf}, {@link While}, {@link ScriptCall}
 * 
 * Created by Javier Torrente on 13/05/14.
 */
public class ControlStructuresTest implements MockEffect.MockEffectListener {

	private MockEntitiesLoader entitiesLoader;
	private GameLoop gameLoop;
	private VariablesManager variablesManager;
	private EffectsSystem effectsSystem;
	private int executed;
	private int executed2;
	private int executed3;

	@BeforeClass
	public static void setupStatics() {
		MockApplication.initStatics();
	}

	@Before
	public void setup() {
		// Initialization
		executed = executed2 = executed3 = 0;
		entitiesLoader = new MockEntitiesLoader();
		gameLoop = entitiesLoader.getGameLoop();
		variablesManager = new VariablesManager(gameLoop,
				entitiesLoader.getComponentLoader(), new DefaultGameView(
						gameLoop));
		effectsSystem = new EffectsSystem(gameLoop, variablesManager);
		effectsSystem.registerEffectExecutor(ChangeEntityProperty.class,
				new ChangeEntityPropertyExecutor(variablesManager));
		effectsSystem.registerEffectExecutor(ScriptCall.class,
				new ScriptCallExecutor(effectsSystem, variablesManager));
		effectsSystem.registerEffectExecutor(IfThenElseIf.class,
				new IfThenElseIfExecutor(effectsSystem, variablesManager));
		effectsSystem.registerEffectExecutor(If.class, new IfExecutor(
				effectsSystem, variablesManager));
		effectsSystem.registerEffectExecutor(While.class, new WhileExecutor(
				effectsSystem, variablesManager));
		effectsSystem.registerEffectExecutor(MockEffect.class,
				new MockEffectExecutor());
		effectsSystem.registerEffectExecutor(ChangeVar.class,
				new ChangeVarExecutor(variablesManager));
		gameLoop.addSystem(effectsSystem);
		TimersSystem timersSystem = new TimersSystem(gameLoop, variablesManager);
		gameLoop.addSystem(timersSystem);
		entitiesLoader.getComponentLoader().registerComponentProcessor(
				Timers.class, new TimersProcessor(gameLoop));
	}

	@Test
	public void testWhile() {
		// register a variable to act as counter
		variablesManager.registerVar("counter", 0);
		// Iterate five times
		While whileEffect = new While();
		whileEffect.setCondition("(lt $counter i5)");
		whileEffect.getEffects().add(new MockEffect(this));
		ChangeVar changeVar = new ChangeVar();
		changeVar.setExpression("(+ $counter i1)");
		changeVar.setVariable("counter");
		whileEffect.getEffects().add(changeVar);
		createAndAddSimpleEntityWithEffect(whileEffect);
		gameLoop.update(0);
		gameLoop.update(0);
		assertEquals(5, executed);
	}

	@Test
	public void testIf() {
		testIf(If.class);
		testIf(IfThenElseIf.class);
	}

	public void testIf(Class<? extends If> clazz) {
		try {
			executed = 0;
			If ifEffect = ClassReflection.newInstance(clazz);
			ifEffect.setCondition("btrue");
			ifEffect.getEffects().add(new MockEffect(this));
			createAndAddSimpleEntityWithEffect(ifEffect);
			gameLoop.update(0);
			gameLoop.update(0);
			assertEquals(1, executed);
		} catch (ReflectionException e) {
			e.printStackTrace();
		}

		try {
			executed = 0;
			If ifEffect = ClassReflection.newInstance(clazz);
			ifEffect.getEffects().add(new MockEffect(this));
			createAndAddSimpleEntityWithEffect(ifEffect);
			gameLoop.update(0);
			gameLoop.update(0);
			assertEquals(0, executed);
		} catch (ReflectionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testIfThenElse() {
		executed = executed2 = 0;
		IfThenElseIf ifEffect = new IfThenElseIf();
		ifEffect.setCondition("btrue");
		ifEffect.getEffects().add(new MockEffect(this));
		ifEffect.getElse().add(
				new MockEffect(new MockEffect.MockEffectListener() {
					@Override
					public void executed() {
						executed2++;
					}
				}));
		createAndAddSimpleEntityWithEffect(ifEffect);
		gameLoop.update(0);
		gameLoop.update(0);
		assertEquals(1, executed);
		assertEquals(0, executed2);

		executed = executed2 = 0;
		IfThenElseIf ifEffect2 = new IfThenElseIf();
		ifEffect.getEffects().add(new MockEffect(this));
		ifEffect2.getElse().add(
				new MockEffect(new MockEffect.MockEffectListener() {
					@Override
					public void executed() {
						executed2++;
					}
				}));
		createAndAddSimpleEntityWithEffect(ifEffect2);
		gameLoop.update(0);
		gameLoop.update(0);
		assertEquals(0, executed);
		assertEquals(1, executed2);
	}

	@Test
	public void testIfThenElseIfElse() {
		executed = executed2 = executed3 = 0;
		IfThenElseIf ifEffect = new IfThenElseIf();
		ifEffect.getEffects().add(new MockEffect(this));
		If elseIf = new If();
		elseIf.setCondition("btrue");
		ifEffect.getElseIfList().add(elseIf);
		elseIf.getEffects().add(
				new MockEffect(new MockEffect.MockEffectListener() {
					@Override
					public void executed() {
						executed2++;
					}
				}));
		ifEffect.getElse().add(
				new MockEffect(new MockEffect.MockEffectListener() {
					@Override
					public void executed() {
						executed3++;
					}
				}));
		createAndAddSimpleEntityWithEffect(ifEffect);
		gameLoop.update(0);
		gameLoop.update(0);
		assertEquals(0, executed);
		assertEquals(1, executed2);
		assertEquals(0, executed3);

		executed = executed2 = executed3 = 0;
		IfThenElseIf ifEffect2 = new IfThenElseIf();
		ifEffect2.getEffects().add(new MockEffect(this));
		If elseIf2 = new If();
		ifEffect2.getElseIfList().add(elseIf2);
		elseIf2.getEffects().add(
				new MockEffect(new MockEffect.MockEffectListener() {
					@Override
					public void executed() {
						executed2++;
					}
				}));
		ifEffect2.getElse().add(
				new MockEffect(new MockEffect.MockEffectListener() {
					@Override
					public void executed() {
						executed3++;
					}
				}));
		createAndAddSimpleEntityWithEffect(ifEffect2);
		gameLoop.update(0);
		gameLoop.update(0);
		assertEquals(0, executed);
		assertEquals(0, executed2);
		assertEquals(1, executed3);
	}

	@Test
	public void testScriptCall() {
		// Add one entity
		EngineEntity entity1 = entitiesLoader
				.toEngineEntity(createModelEntityWithInitialization("btrue",
						"i90", "group.x", "(+ $var2 i10)"));
		EngineEntity entity2 = entitiesLoader
				.toEngineEntity(createModelEntityWithInitialization(null, null,
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
				.toEngineEntity(createModelEntityWithInitialization(null, null,
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
				.toEngineEntity(createModelEntityWithInitialization("bfalse",
						"i50", "group.x", "$var2", "$var1"));
		EngineEntity entity5 = entitiesLoader
				.toEngineEntity(createModelEntityWithInitialization("btrue",
						"i50", "group.x", "$var2", "$var1"));
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
				If ifStructure = new If();
				ifStructure.getEffects().add(changeEntityProperty);
				ifStructure.setCondition(args[++i]);
				script.getEffects().add(ifStructure);
			} else {
				script.getEffects().add(changeEntityProperty);
			}
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

	private Entity createAndAddSimpleEntityWithEffect(Effect effect) {
		ModelEntity modelEntity = new ModelEntity();

		Timer timer = new Timer();
		timer.setTime(0);
		timer.getEffects().add(effect);
		Timers timers = new Timers();
		timers.getTimers().add(timer);

		modelEntity.getComponents().add(timers);
		Entity entity = entitiesLoader.toEngineEntity(modelEntity);
		gameLoop.addEntity(entity);
		return entity;
	}

	@Override
	public void executed() {
		executed++;
	}
}
