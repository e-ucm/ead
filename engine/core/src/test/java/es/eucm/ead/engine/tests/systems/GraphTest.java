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

import es.eucm.ead.engine.components.GraphComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.mock.schema.MockEffect;
import es.eucm.ead.engine.mock.schema.MockEffect.MockEffectListener;
import es.eucm.ead.engine.processors.LogicProcessor;
import es.eucm.ead.engine.systems.GraphSystem;
import es.eucm.ead.engine.tests.systems.effects.EffectTest;
import es.eucm.ead.schema.components.Logic;
import es.eucm.ead.schema.components.behaviors.events.Init;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.graph.model.Graph;
import es.eucm.graph.model.Node;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GraphTest extends EffectTest implements MockEffectListener {

	private int executed = 0;

	@Before
	public void setUp() {
		super.setUp();
		gameLoop.addSystem(new GraphSystem(gameLoop, variablesManager,
				effectsSystem));
		componentLoader.registerComponentProcessor(Logic.class,
				new LogicProcessor(gameLoop));
	}

	@Test
	public void test() {
		ModelEntity entity = new ModelEntity();
		Logic logic = new Logic();
		entity.getComponents().add(logic);

		Graph graph = new Graph(true);
		graph.getRoot().setContent(new Init());
		Node parent = graph.getRoot();
		for (int i = 0; i < 10; i++) {
			parent = addNode(i, graph, parent);
		}

		logic.getSequences().add(graph);

		EngineEntity engineEntity = entitiesLoader.toEngineEntity(entity);

		for (int i = 0; i < 10; i++) {
			gameLoop.update(0);
			assertEquals(i + 1, executed);
		}
		assertNull(engineEntity.getComponent(GraphComponent.class));
	}

	private Node addNode(int count, Graph graph, Node parent) {
		Node node = new Node();
		node.setId(count + "");
		node.setContent(new MockEffect(this));
		node.addFork("next");
		parent.getForks().get(0).setNext(node.getId());
		graph.getNodes().add(node);
		return node;
	}

	@Override
	public void executed() {
		executed++;
	}
}
