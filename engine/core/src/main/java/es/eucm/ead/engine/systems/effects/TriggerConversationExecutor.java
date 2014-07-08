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

import ashley.core.Component;
import ashley.core.Entity;
import ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.components.ConversationsComponent;
import es.eucm.ead.engine.components.TalkComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.data.conversation.Conversation;
import es.eucm.ead.schema.data.conversation.Speaker;
import es.eucm.ead.schema.effects.TriggerConversation;

/**
 * Launches an in-game conversation.
 */
public class TriggerConversationExecutor extends
		EffectExecutor<TriggerConversation> {

	private VariablesManager variablesManager;

	public TriggerConversationExecutor(VariablesManager variablesManager) {
		this.variablesManager = variablesManager;
	}

	private Conversation getConversation(String id) {
		Class conversationsClass = ConversationsComponent.class;
		Family f = Family.getFamilyFor(conversationsClass);
		for (Entity e : gameLoop.getEntitiesFor(f).values()) {
			Component c = e.getComponent(conversationsClass);
			Conversation conv = ((ConversationsComponent) c)
					.getConversation(id);
			if (conv != null) {
				return conv;
			}
		}
		Gdx.app.error("[trigger-conv]", "No conversation found with ID " + id
				+ "", new IllegalArgumentException("Invalid conversation '"
				+ id + "'"));
		return null;
	}

	@Override
	public void execute(Entity owner, TriggerConversation effect) {
		Conversation conversation = getConversation(effect.getName());

		// get the cast together
		Array<Array<EngineEntity>> speakers = new Array<Array<EngineEntity>>();
		for (Speaker speaker : conversation.getSpeakers()) {
			Array<EngineEntity> matches = new Array<EngineEntity>();
			try {
				matches = (Array) variablesManager.evaluateExpression(speaker
						.getSelector());
			} catch (Exception e) {
				Gdx.app.error(
						"[trigger-conv]",
						"Error evaluating " + speaker.getSelector() + ": "
								+ e.getMessage());
			}
			speakers.add(matches);
		}

		// initialize the TalkComponent that will manage the rest
		TalkComponent tc = gameLoop.addAndGetComponent(owner,
				TalkComponent.class);
		tc.initialize(conversation, speakers, effect.getNode());
	}
}
