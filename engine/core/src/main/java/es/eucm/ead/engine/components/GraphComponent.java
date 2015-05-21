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
package es.eucm.ead.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

import com.badlogic.gdx.utils.Pools;
import es.eucm.graph.model.Graph;
import es.eucm.graph.model.Node;

public class GraphComponent extends Component implements Poolable {

	private Array<RuntimeGraph> graphs = new Array<RuntimeGraph>();

	public Array<RuntimeGraph> getGraphs() {
		return graphs;
	}

	public void add(Graph graph, Node currentNode) {
		graphs.add(new RuntimeGraph(graph, currentNode));
	}

	@Override
	public boolean combine(Component component) {
		if (component instanceof GraphComponent) {
			graphs.addAll(((GraphComponent) component).graphs);
			return true;
		}
		return false;
	}

	@Override
	public void reset() {
		graphs.clear();
	}

	/**
	 * Removes graphs finished
	 */
	public void clean() {
		Array<RuntimeGraph> tmp = Pools.obtain(Array.class);
		for (RuntimeGraph graph : graphs) {
			if (graph.currentNode == null) {
				tmp.add(graph);
			}
		}

		for (RuntimeGraph graph : tmp) {
			graphs.removeValue(graph, true);
		}
		tmp.clear();
		Pools.free(tmp);
	}

	public class RuntimeGraph {

		public Graph graph;

		public Node currentNode;

		public RuntimeGraph(Graph graph, Node currentNode) {
			this.graph = graph;
			this.currentNode = currentNode;
		}
	}
}
