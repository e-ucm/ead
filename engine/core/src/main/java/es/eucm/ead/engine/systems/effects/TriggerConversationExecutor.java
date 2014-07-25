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
package es.eucm.ead.engine.systems.effects;

import ashley.core.Entity;
import ashley.core.Family;
import com.badlogic.gdx.utils.IntMap;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.ConversationsComponent;
import es.eucm.ead.engine.components.NodeComponent;
import es.eucm.ead.schema.components.conversation.Conversation;
import es.eucm.ead.schema.effects.TriggerConversation;

public class TriggerConversationExecutor extends
		EffectExecutor<TriggerConversation> {

	private IntMap<Entity> conversationsEntities;

	@Override
	public void initialize(GameLoop gameLoop) {
		super.initialize(gameLoop);
		conversationsEntities = gameLoop.getEntitiesFor(Family
				.getFamilyFor(ConversationsComponent.class));
	}

	@Override
	public void execute(Entity target, TriggerConversation effect) {

		Conversation conversation = null;
		for (Entity entity : conversationsEntities.values()) {
			ConversationsComponent conversations = entity
					.getComponent(ConversationsComponent.class);

			conversation = conversations.getConversations().get(
					effect.getConversationId());

			if (conversation != null) {
				break;
			}
		}

		if (conversation != null) {
			NodeComponent node = gameLoop.createComponent(NodeComponent.class);
			node.set(conversation, effect.getNodeId());
			target.add(node);
		}
	}
}
