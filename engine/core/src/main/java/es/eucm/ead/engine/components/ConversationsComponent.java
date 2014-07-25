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
import com.badlogic.gdx.utils.Pool.Poolable;
import es.eucm.ead.schema.components.conversation.Conversation;

import java.util.HashMap;
import java.util.Map;

public class ConversationsComponent extends Component implements Poolable {

	private Map<String, Conversation> conversations;

	public ConversationsComponent() {
		conversations = new HashMap<String, Conversation>();
	}

	public Map<String, Conversation> getConversations() {
		return conversations;
	}

	public void add(Conversation conversation) {
		conversations.put(conversation.getId(), conversation);
	}

	@Override
	public boolean combine(Component component) {
		if (component instanceof ConversationsComponent) {
			for (Conversation conv : ((ConversationsComponent) component)
					.getConversations().values()) {
				add(conv);
			}
			return true;
		}
		return false;
	}

	@Override
	public void reset() {
		conversations.clear();
	}
}
