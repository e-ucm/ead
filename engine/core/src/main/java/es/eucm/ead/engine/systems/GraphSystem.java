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
package es.eucm.ead.engine.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.GraphComponent;
import es.eucm.ead.engine.components.GraphComponent.RuntimeGraph;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.effects.Effect;

public class GraphSystem extends ConditionalSystem {

	private EffectsSystem effectsSystem;

	public GraphSystem(GameLoop gameLoop, VariablesManager variablesManager,
			EffectsSystem effectsSystem) {
		super(gameLoop, variablesManager, Family.all(GraphComponent.class)
				.get());
		this.effectsSystem = effectsSystem;
	}

	@Override
	public void doProcessEntity(Entity entity, float deltaTime) {
		GraphComponent graphComponent = entity
				.getComponent(GraphComponent.class);
		for (RuntimeGraph graph : graphComponent.getGraphs()) {
			Object o = graph.currentNode.getContent();
			if (o instanceof Effect) {
				effectsSystem.execute((Effect) o);
			}
			if (graph.currentNode.getForks().size() == 1) {
				graph.currentNode = graph.graph.getNode(graph.currentNode
						.getForks().get(0).getNext());
			}
		}

		graphComponent.clean();

		if (graphComponent.getGraphs().size == 0) {
			entity.remove(GraphComponent.class);
		}
	}
}
