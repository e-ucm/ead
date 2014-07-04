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
import com.badlogic.gdx.utils.Pool;
import es.eucm.ead.schema.components.Conversations;
import es.eucm.ead.schema.data.conversation.Conversation;

import java.util.HashMap;

/**
 * Simple container of conversations for engine entities. Engine equivalent to
 * {@link es.eucm.ead.schema.components.Conversations}.
 */
public class ConversationsComponent extends Component implements Pool.Poolable {

	protected HashMap<String, Conversation> conversationMap = new HashMap<String, Conversation>();

	public ConversationsComponent() {
	}

	public Conversation getConversation(String id) {
		return conversationMap.get(id);
	}

	public void initialize(Conversations schemaConversations) {
		Gdx.app.debug("[conv]", "Component built, initializing with "
				+ schemaConversations.getConversations().size
				+ " conversations: ");
		for (Conversation c : schemaConversations.getConversations()) {
			Gdx.app.debug("[conv]",
					" - added '" + c.getId() + "' with " + c.getNodes().size
							+ " nodes");
			conversationMap.put(c.getId(), c);
		}
	}

	@Override
	public void reset() {
		conversationMap.clear();
	}
}
