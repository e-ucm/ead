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
package es.eucm.ead.engine.demobuilder;

import es.eucm.ead.schema.components.conversation.ConditionedNode;
import es.eucm.ead.schema.components.conversation.Conversation;
import es.eucm.ead.schema.components.conversation.EffectsNode;
import es.eucm.ead.schema.components.conversation.ForkNode;
import es.eucm.ead.schema.components.conversation.LineNode;
import es.eucm.ead.schema.components.conversation.Node;
import es.eucm.ead.schema.components.conversation.OptionNode;
import es.eucm.ead.schema.components.conversation.SimpleNode;
import es.eucm.ead.schema.components.conversation.WaitNode;
import es.eucm.ead.schema.effects.Effect;

public class ConversationBuilder {

	private Conversation conversation;

	private int lastId;

	public ConversationBuilder(Conversation conversation) {
		this.conversation = conversation;
	}

	private <T extends Node> T node(Node previousNode, T node) {
		node.setId(lastId++);
		conversation.getNodes().add(node);
		if (previousNode instanceof SimpleNode) {
			((SimpleNode) previousNode).setNextNodeId(node.getId());
		} else if (previousNode instanceof ForkNode) {
			((ForkNode) previousNode).getNextNodeIds().add(node.getId());
		}
		return node;
	}

	public ConversationBuilder speakers(String... speakers) {
		conversation.getSpeakers().addAll(speakers);
		return this;
	}

	public BranchBuilder start() {
		return new BranchBuilder(null);
	}

	public class BranchBuilder {

		private Node lastNode;

		public BranchBuilder(Node lastNode) {
			this.lastNode = lastNode;
		}

		private <T extends Node> T node(T node) {
			lastNode = ConversationBuilder.this.node(lastNode, node);
			return node;
		}

		public BranchBuilder wait(float time) {
			node(new WaitNode()).setTime(time);
			return this;
		}

		public BranchBuilder effects(Effect... effects) {
			node(new EffectsNode()).getEffects().addAll(effects);
			return this;
		}

		public BranchBuilder line(int speaker, String line) {
			LineNode lineNode = node(new LineNode());
			lineNode.setSpeaker(speaker);
			lineNode.setLine(line);
			return this;
		}

		public BranchBuilder line(String speaker, String line) {
			return line(conversation.getSpeakers().indexOf(speaker, false),
					line);
		}

		public ForkBuilder conditions() {
			return new ForkBuilder(node(new ConditionedNode()));
		}

		public ForkBuilder options() {
			return new ForkBuilder(node(new OptionNode()));
		}

		public void nextNode(int nodeId) {
			if (lastNode instanceof SimpleNode) {
				((SimpleNode) lastNode).setNextNodeId(nodeId);
			}
		}
	}

	public class ForkBuilder {

		private ForkNode forkNode;

		public ForkBuilder(ForkNode forkNode) {
			this.forkNode = forkNode;
		}

		public int getNodeId() {
			return forkNode.getId();
		}

		public BranchBuilder start(String string) {
			if (forkNode instanceof OptionNode) {
				((OptionNode) forkNode).getOptions().add(string);
			} else if (forkNode instanceof ConditionedNode) {
				((ConditionedNode) forkNode).getConditions().add(string);
			}
			return new BranchBuilder(forkNode);
		}

	}
}
