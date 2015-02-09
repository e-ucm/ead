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
package es.eucm.ead.engine.systems.conversations;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.NodeComponent;
import es.eucm.ead.schema.components.conversation.Conversation;
import es.eucm.ead.schema.components.conversation.Node;
import es.eucm.ead.schema.components.conversation.SimpleNode;

import java.util.HashMap;
import java.util.Map;

public class NodeSystem extends IteratingSystem {

	private GameLoop gameLoop;

	private Map<Class, Class> nodeClasses;

	public NodeSystem(GameLoop gameLoop) {
		super(Family.all(NodeComponent.class).get());
		this.gameLoop = gameLoop;
		nodeClasses = new HashMap<Class, Class>();
	}

	public void registerNodeClass(Class<? extends Node> nodeClass,
			Class<? extends RuntimeNode> runtimeClass) {
		nodeClasses.put(nodeClass, runtimeClass);
	}

	@Override
	public void processEntity(Entity entity, float delta) {
		NodeComponent nodeComponent = entity.getComponent(NodeComponent.class);

		if (!nodeComponent.isStarted()) {
			nodeComponent.start();

			nodeComponent.setCurrentNode(createNode(entity,
					nodeComponent.getConversation(),
					nodeComponent.getStartingNode()));
		}

		RuntimeNode runtimeNode = nodeComponent.getRuntimeNode();
		if (runtimeNode == null) {
			entity.remove(NodeComponent.class);
		} else if (runtimeNode.update(delta)) {
			int nextNode = runtimeNode.nextNode();
			nodeComponent.setCurrentNode(createNode(entity,
					nodeComponent.getConversation(), nextNode));
			Pools.free(runtimeNode);
		}
	}

	private RuntimeNode createNode(Entity entity, Conversation conversation,
			int nodeId) {
		Node node = getNode(conversation, nodeId);
		Class runtimeClass = node == null ? null : nodeClasses.get(node
				.getClass());
		if (runtimeClass == null) {
			if (node != null) {
				Gdx.app.error("NodeSystem", "No node for " + node.getClass()
						+ ". Conversation will end.");
			}
			return null;
		}

		RuntimeNode runtimeNode = (RuntimeNode) Pools.obtain(runtimeClass);
		runtimeNode.setGameLoop(gameLoop);
		runtimeNode.setEntity(entity);
		runtimeNode.setConversation(conversation);
		runtimeNode.setNode(node);

		return runtimeNode;
	}

	private Node getNode(Conversation conversation, int nodeId) {
		if (nodeId == RuntimeNode.END_NODE) {
			return null;
		}

		for (Node node : conversation.getNodes()) {
			if (node.getId() == nodeId) {
				return node;
			}
		}
		return null;
	}

	public abstract static class RuntimeNode<T extends Node> {

		public static int END_NODE = -1;

		/**
		 * Conversation owner
		 */
		protected Entity entity;

		protected GameLoop gameLoop;

		protected Conversation conversation;

		protected T node;

		public void setEntity(Entity entity) {
			this.entity = entity;
		}

		public void setConversation(Conversation conversation) {
			this.conversation = conversation;
		}

		public void setGameLoop(GameLoop gameLoop) {
			this.gameLoop = gameLoop;
		}

		public T getNode() {
			return node;
		}

		public void setNode(T node) {
			this.node = node;
		}

		public abstract boolean update(float delta);

		public abstract int nextNode();

	}

	public abstract static class SimpleRuntimeNode<T extends SimpleNode>
			extends RuntimeNode<T> {

		public int nextNode() {
			return node.getNextNodeId();
		}
	}
}
