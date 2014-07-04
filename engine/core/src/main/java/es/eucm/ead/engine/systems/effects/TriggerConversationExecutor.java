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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
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
		Class componentClass = ConversationsComponent.class;
		Family f = Family.getFamilyFor(componentClass);
		IntMap<Entity> all = gameLoop.getEntitiesFor(f);
		Entity e = all.iterator().next().value;
		return ((ConversationsComponent) e.getComponent(componentClass))
				.getConversation(id);
	}

	@Override
	public void execute(Entity owner, TriggerConversation effect) {
		Conversation conversation = getConversation(effect.getName());

		Gdx.app.log("[trigger-conv]",
				"Launching conversation '" + effect.getName() + "'");

		// get the cast together
		Array<Array<EngineEntity>> speakers = new Array<Array<EngineEntity>>();
		for (Speaker speaker : conversation.getSpeakers()) {
			Array<EngineEntity> matches = new Array<EngineEntity>();
			try {
				matches = (Array) variablesManager.evaluateExpression(speaker
						.getSelector());
				Gdx.app.log("[trigger-conv]",
						"Found actor for role " + speaker.getSelector() + ": "
								+ matches);
			} catch (Exception e) {
				Gdx.app.log(
						"[trigger-conv]",
						"Error evaluating " + speaker.getSelector() + ": "
								+ e.getMessage());
			}
			speakers.add(matches);
		}

		// add the relevant effect at the highest level
		TalkComponent tc = new TalkComponent();
		tc.initialize(conversation, speakers, effect.getNode());
		owner.add(tc);
	}
}
