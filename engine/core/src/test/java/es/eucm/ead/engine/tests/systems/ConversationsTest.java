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
package es.eucm.ead.engine.tests.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import es.eucm.ead.engine.GleanerSystemForTest;
import es.eucm.ead.engine.components.LineComponent;
import es.eucm.ead.engine.components.NodeComponent;
import es.eucm.ead.engine.components.renderers.OptionsComponent;
import es.eucm.ead.engine.processors.ConversationProcessor;
import es.eucm.ead.engine.processors.behaviors.BehaviorsProcessor;
import es.eucm.ead.engine.systems.conversations.ConditionedRuntimeNode;
import es.eucm.ead.engine.systems.conversations.EffectsRuntimeNode;
import es.eucm.ead.engine.systems.conversations.LineRuntimeNode;
import es.eucm.ead.engine.systems.conversations.NodeSystem;
import es.eucm.ead.engine.systems.conversations.OptionRuntimeNode;
import es.eucm.ead.engine.systems.conversations.WaitRuntimeNode;
import es.eucm.ead.engine.systems.effects.ChangeVarExecutor;
import es.eucm.ead.engine.systems.effects.TriggerConversationExecutor;
import es.eucm.ead.engine.tests.systems.effects.EffectTest;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.conversation.ConditionedNode;
import es.eucm.ead.schema.components.conversation.Conversation;
import es.eucm.ead.schema.components.conversation.EffectsNode;
import es.eucm.ead.schema.components.conversation.LineNode;
import es.eucm.ead.schema.components.conversation.OptionNode;
import es.eucm.ead.schema.components.conversation.WaitNode;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.TriggerConversation;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConversationsTest extends EffectTest {

	private ModelEntity scene;

	private int optionSelection;

	@Before
	public void setUp() {
		super.setUp();
		/*
		 * new DemoBuilder() {
		 * 
		 * @Override protected void doBuild() { entity(null, 0, 0); scene =
		 * getLastEntity(); ForkBuilder options = conversation(scene, "c")
		 * .speakers("speaker1", "speaker2").start().wait(5.0f) .options();
		 * ForkBuilder conditions = options.start("left")
		 * .effects(makeChangeVar("left", "btrue")).conditions();
		 * 
		 * conditions.start("$left").line(0, "left");
		 * conditions.start("(not $left)").line(1, "right");
		 * 
		 * options.start("right").effects(makeChangeVar("left", "bfalse"))
		 * .nextNode(conditions.getNodeId());
		 * 
		 * initBehavior(scene, makeTriggerConversation("c", 0)); } }.doBuild();
		 */

		effectsSystem.registerEffectExecutor(ChangeVar.class,
				new ChangeVarExecutor(variablesManager,
						new GleanerSystemForTest(gameLoop)));
		effectsSystem.registerEffectExecutor(TriggerConversation.class,
				new TriggerConversationExecutor());

		// Order of adding systems matters
		gameLoop.addSystem(new MockLineSystem());
		gameLoop.addSystem(new MockOptionSystem());
		NodeSystem nodeSystem = new NodeSystem(gameLoop);
		nodeSystem.registerNodeClass(WaitNode.class, WaitRuntimeNode.class);
		nodeSystem.registerNodeClass(LineNode.class, LineRuntimeNode.class);
		nodeSystem.registerNodeClass(EffectsNode.class,
				EffectsRuntimeNode.class);
		nodeSystem.registerNodeClass(ConditionedNode.class,
				ConditionedRuntimeNode.class);
		nodeSystem.registerNodeClass(OptionNode.class, OptionRuntimeNode.class);
		gameLoop.addSystem(nodeSystem);

		componentLoader.registerComponentProcessor(Conversation.class,
				new ConversationProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Behavior.class,
				new BehaviorsProcessor(gameLoop));

		gameLoop.addEntity(entitiesLoader.toEngineEntity(scene));

	}

	public void testLeftBranchConversation() {
		ImmutableArray<Entity> nodeEntities = gameLoop.getEntitiesFor(Family
				.all(NodeComponent.class).get());
		ImmutableArray<Entity> optionEntities = gameLoop.getEntitiesFor(Family
				.all(OptionsComponent.class).get());
		ImmutableArray<Entity> lineEntities = gameLoop.getEntitiesFor(Family
				.all(LineComponent.class).get());

		assertEquals(0, nodeEntities.size());
		// Start conversation
		gameLoop.update(0);
		assertEquals(1, nodeEntities.size());

		Entity scene = nodeEntities.iterator().next();

		NodeComponent nodeComponent = scene.getComponent(NodeComponent.class);

		assertFalse(nodeComponent.isStarted());
		gameLoop.update(0);
		assertTrue(nodeComponent.isStarted());
		assertEquals(0, nodeComponent.getRuntimeNode().getNode().getId());
		for (int i = 0; i < 4; i++) {
			assertEquals(WaitRuntimeNode.class, nodeComponent.getRuntimeNode()
					.getClass());
			gameLoop.update(1.0f);
		}

		assertEquals(0, optionEntities.size());

		gameLoop.update(1.0f);
		assertEquals(1, optionEntities.size());
		assertEquals(OptionRuntimeNode.class, nodeComponent.getRuntimeNode()
				.getClass());

		OptionsComponent optionsComponent = scene
				.getComponent(OptionsComponent.class);
		assertEquals(2, optionsComponent.getOptions().size);

		optionSelection = 0;
		// Go to effects node
		gameLoop.update(0.0f);
		// Execute effects
		gameLoop.update(0.0f);

		assertTrue(effectsSystem.getVariablesManager().evaluateCondition(
				"$left", false));
		// Process condition node
		gameLoop.update(0.0f);
		assertEquals(1, lineEntities.size());

		LineComponent lineComponent = scene.getComponent(LineComponent.class);
		assertEquals("speaker1", lineComponent.getSpeaker());
		assertEquals("left", lineComponent.getLine());

		// Line ends
		gameLoop.update(0.0f);
		assertEquals(0, lineEntities.size());
		assertEquals(0, optionEntities.size());
		// Conversation ends
		gameLoop.update(0.0f);
		assertEquals(0, nodeEntities.size());
	}

	public void testRightBranchConversation() {
		ImmutableArray<Entity> nodeEntities = gameLoop.getEntitiesFor(Family
				.all(NodeComponent.class).get());

		// Start conversation
		gameLoop.update(0);
		Entity scene = nodeEntities.iterator().next();
		// Pass to wait node
		gameLoop.update(0);
		// Pass to option node
		gameLoop.update(5.0f);

		optionSelection = 1;
		// Go to effects node
		gameLoop.update(0.0f);
		// Execute effects
		gameLoop.update(0.0f);

		assertFalse(effectsSystem.getVariablesManager().evaluateCondition(
				"$left", false));
		// Process condition node
		gameLoop.update(0.0f);

		LineComponent lineComponent = scene.getComponent(LineComponent.class);
		assertEquals("speaker2", lineComponent.getSpeaker());
		assertEquals("right", lineComponent.getLine());

		// Line ends
		gameLoop.update(0.0f);
		// Conversation ends
		gameLoop.update(0.0f);
		assertEquals(0, nodeEntities.size());
	}

	public class MockLineSystem extends IteratingSystem {

		public MockLineSystem() {
			super(Family.all(LineComponent.class).get());
		}

		@Override
		public void processEntity(Entity entity, float v) {
			LineComponent line = entity.getComponent(LineComponent.class);
			effectsSystem.getVariablesManager().setValue(
					LineRuntimeNode.getLineEndedVar(line.getConversation()),
					true, true);
			entity.remove(LineComponent.class);
		}
	}

	public class MockOptionSystem extends IteratingSystem {
		public MockOptionSystem() {
			super(Family.all(OptionsComponent.class).get());
		}

		@Override
		public void processEntity(Entity entity, float v) {
			OptionsComponent options = entity
					.getComponent(OptionsComponent.class);
			effectsSystem.getVariablesManager().setValue(
					OptionRuntimeNode.getOptionSelectedVar(options
							.getConversation()), optionSelection, true);
			entity.remove(OptionsComponent.class);
		}
	}

}
