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
import com.badlogic.gdx.utils.Pool.Poolable;
import es.eucm.ead.engine.systems.conversations.NodeSystem.RuntimeNode;
import es.eucm.ead.schema.components.conversation.Conversation;

/**
 * Represents an ongoing conversation
 */
public class NodeComponent extends Component implements Poolable {

	private Conversation conversation;

	private int startingNode;

	private boolean started;

	private RuntimeNode currentNode;

	public void set(Conversation conversation, int startingNode) {
		this.conversation = conversation;
		this.startingNode = startingNode;
		this.started = false;
	}

	public Conversation getConversation() {
		return conversation;
	}

	public int getStartingNode() {
		return startingNode;
	}

	public boolean isStarted() {
		return started;
	}

	public RuntimeNode getRuntimeNode() {
		return currentNode;
	}

	public void setCurrentNode(RuntimeNode currentNode) {
		this.currentNode = currentNode;
	}

	public void start() {
		started = true;
	}

	@Override
	public void reset() {
		conversation = null;
		currentNode = null;
		startingNode = -1;
		started = false;
	}

}
