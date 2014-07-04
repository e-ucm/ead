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

import ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.data.conversation.Conversation;
import es.eucm.ead.schema.data.conversation.Node;

import java.util.HashMap;

/**
 * Talking behavior, used to keep state in conversations.
 */
public class TalkComponent extends Component {

	private HashMap<Integer, Node> idToNode;
	private Array<Array<EngineEntity>> speakers;
	private Array<EngineEntity> temporaries;

	public enum TalkState {
		/**
		 * current node has not yet been rendered
		 */
		Breathing,
		/**
		 * current node is being rendered
		 */
		Talking,
		/**
		 * current node rendered, but effects not yet executed
		 */
		Acting,
		/**
		 * current node is finished, but next has not yet been chosen
		 */
		Thinking;
		/**
		 * returns the next talk state
		 */
		public TalkState next() {
			TalkState[] all = values();
			for (int i = 0; i < all.length; i++) {
				if (all[i].equals(this)) {
					return all[(i + 1) % all.length];
				}
			}
			// never reached
			return Breathing;
		}
	}

	/**
	 * current node
	 */
	private Node currentNode;

	/**
	 * progress within current node (see state definitions)
	 */
	private TalkState talkState;

	public TalkComponent() {
	}

	/**
	 * Returns the current speakers
	 */
	public Array<EngineEntity> getCurrentSpeakers() {
		return speakers.get(currentNode.getSpeaker());
	}

	public Array<EngineEntity> getTemporaries() {
		return temporaries;
	}

	public void addTemporary(EngineEntity entity) {
		temporaries.add(entity);
	}

	/**
	 * initializes a talk
	 */
	public void initialize(Conversation conversation,
			Array<Array<EngineEntity>> speakers, int startNode) {
		this.idToNode = new HashMap<Integer, Node>(conversation.getNodes().size);
		for (Node n : conversation.getNodes()) {
			Gdx.app.debug("[tc]",
					"node " + n.getId() + " has " + n.getOutgoing().size
							+ " outgoing");
			idToNode.put(n.getId(), n);
		}
		this.speakers = speakers;
		this.currentNode = idToNode.get(startNode);
		this.talkState = TalkState.Breathing;
		this.temporaries = new Array<EngineEntity>();
	}

	/**
	 * @return true if there are more accessible nodes, false if not really.
	 */
	public boolean isTalking() {
		return !(talkState.equals(TalkState.Thinking) && currentNode
				.getOutgoing().size == 0);
	}

	/**
	 * @return all accessible nodes from this one, if any. Note that they may
	 *         have conditions and therefore require evaluation.
	 */
	public Array<Node> nextNodes() {
		Array<Node> next = new Array<Node>();
		for (int i : currentNode.getOutgoing()) {
			next.add(idToNode.get(i));
		}
		return next;
	}

	/**
	 * @return current node
	 */
	public Node getCurrentNode() {
		return currentNode;
	}

	/**
	 * @return current talk state
	 */
	public TalkState getTalkState() {
		return talkState;
	}

	/**
	 * sets the current node & talk state
	 */
	public void setNodeAndState(Node node, TalkState state) {
		currentNode = node;
		talkState = state;
	}
}
