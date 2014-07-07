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
package es.eucm.ead.engine.systems;

import ashley.core.Entity;
import ashley.core.Family;
import ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.EffectsComponent;
import es.eucm.ead.engine.components.TalkComponent;
import es.eucm.ead.engine.components.dialogues.DialogueComponent;
import es.eucm.ead.engine.components.dialogues.MenuDialogueComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.data.conversation.Node;

/**
 * A system for entities that are talking a talk (conversation).
 */
public class ConversationSystem extends IteratingSystem {

	private VariablesManager variablesManager;
	private GameLoop engine;

	public ConversationSystem(GameLoop engine, VariablesManager variablesManager) {
		super(Family.getFamilyFor(TalkComponent.class));
		this.variablesManager = variablesManager;
		this.engine = engine;
	}

	/**
	 * Iterate through the conversation nodes until the conversation finishes.
	 * 
	 * @param entity
	 * @param delta
	 */
	@Override
	public void processEntity(Entity entity, float delta) {

		TalkComponent talk = entity.getComponent(TalkComponent.class);

		if (!talk.isTalking()) {
			entity.remove(TalkComponent.class);
			return;
		}

		Node currentNode = talk.getCurrentNode();

		switch (talk.getTalkState()) {
		case BREATHING: {
			// add dialogue text next to each speaker
			DialogueComponent dc = new DialogueComponent();
			dc.init(currentNode.getLines());
			talk.setRenderer(dc);
			Array<EngineEntity> speakers = talk.getCurrentSpeakers();
			for (EngineEntity speaker : speakers) {
				speaker.add(dc);
			}
			// go to wait state
			talk.setNodeAndState(currentNode, TalkComponent.TalkState.TALKING);
			break;
		}
		case TALKING: {
			if (talk.getRenderer().hasChanged()) {
				TalkComponent.TalkState next = talk.getTalkState().next();
				talk.setNodeAndState(talk.getCurrentNode(), next);
				talk.dismissDialogueInstances(engine);
			}
			break;
		}
		case ACTING: {
			// execute effects
			if (currentNode.getEffect() != null) {
				EffectsComponent ec = new EffectsComponent();
				ec.getEffectList().add(currentNode.getEffect());
				entity.add(ec);
			}

			// find accessible nodes that are available given their conditions
			Array<Node> candidates = talk.accessibleNodes();
			Array<Node> validChoices = talk.getNextNodes();
			validChoices.clear();
			for (Node n : candidates) {
				if (variablesManager.evaluateCondition(n.getCondition(), true)) {
					validChoices.add(n);
				}
			}

			if (validChoices.size == 1) {
				// select only choice, do not display menu
				talk.setNodeAndState(validChoices.first(),
						TalkComponent.TalkState.BREATHING);
			} else if (validChoices.size > 1) {
				Array<EngineEntity> speakers = talk.getCurrentSpeakers();
				EngineEntity firstSpeaker = speakers.get(0);
				// show menu for first speaker
				MenuDialogueComponent dc = new MenuDialogueComponent();
				Array<String> keys = new Array<String>();
				for (Node node : validChoices) {
					keys.add(node.getLines().get(0));
				}
				dc.init(keys);
				firstSpeaker.add(dc);
				talk.setRenderer(dc);
				// go to wait state
				talk.setNodeAndState(currentNode,
						TalkComponent.TalkState.THINKING);
			} else if (validChoices.size == 0) {
				// end the conversation
				entity.remove(TalkComponent.class);
			}
			break;
		}
		case THINKING: {
			if (talk.getRenderer().hasChanged()) {
				MenuDialogueComponent menuDialogue = (MenuDialogueComponent) talk
						.getRenderer();
				int choice = menuDialogue.getMenuChoice();
				talk.setNodeAndState(talk.getNextNodes().get(choice),
						TalkComponent.TalkState.BREATHING);
				talk.dismissDialogueInstances(engine);
			}
			break;
		}
		}
	}
}
