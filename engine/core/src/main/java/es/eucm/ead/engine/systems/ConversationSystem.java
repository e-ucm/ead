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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.DialogueComponent;
import es.eucm.ead.engine.components.EffectsComponent;
import es.eucm.ead.engine.components.TalkComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.data.conversation.Node;

/**
 * A system for entities that are talking a talk (conversation).
 */
public class ConversationSystem extends IteratingSystem {

	private VariablesManager variablesManager;
	private GameLoop engine;

	// previous talk state
	private TalkComponent.TalkState previous = null;

	// id of last generation of dialogues
	private static int lastDialogueGenerationId = 0;

	public ConversationSystem(GameLoop engine, VariablesManager variablesManager) {
		super(Family.getFamilyFor(TalkComponent.class));
		this.variablesManager = variablesManager;
		this.engine = engine;
	}

	public class ConversationDialogueCallback implements
			DialogueComponent.DialogueCallback {
		private TalkComponent talk;

		private ConversationDialogueCallback(TalkComponent talk) {
			this.talk = talk;
		}

		@Override
		public void dialogueChanged(DialogueComponent component) {
			if (!component.isDismissed()) {
				talk.setNodeAndState(talk.getCurrentNode(), talk.getTalkState()
						.next());
				Gdx.app.debug("[CS]",
					"callback: switching to next state");
			}
			component.setDismissed(true);
			for (EngineEntity ee : component.getRenderingEntities()) {
				Gdx.app.debug("[CS]", "dismissing dialogue entity");
				engine.removeEntity(ee);
			}
		}
	}

	public class ConversationMenuCallback implements
			DialogueComponent.DialogueCallback {
		private TalkComponent talk;
		private Array<Node> options;

		private ConversationMenuCallback(TalkComponent talk, Array<Node> options) {
			this.talk = talk;
			this.options = options;
		}

		@Override
		public void dialogueChanged(DialogueComponent component) {
			if (!component.isDismissed()) {
				talk.setNodeAndState(options.get(component.getMenuChoice()),
						TalkComponent.TalkState.Breathing);
				Gdx.app.debug("[CS]",
					"callback: switching to menu selection");
			}
			component.setDismissed(true);
			for (EngineEntity ee : component.getRenderingEntities()) {
				Gdx.app.debug("[CS]", "dismissing dialogue entity");
				engine.removeEntity(ee);
			}
		}
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
		Node currentNode = talk.getCurrentNode();

		if (!talk.isTalking()) {
			Gdx.app.debug("[CS]",
					"no outgoing nodes from " + currentNode.getId()
							+ ": finished");
			entity.remove(TalkComponent.class);
			return;
		}

		Array<EngineEntity> speakers = talk.getCurrentSpeakers();

		switch (talk.getTalkState()) {
		case Breathing: {
			// add dialogue text next to each speaker
			DialogueComponent dc = new DialogueComponent();
			String[] keys = new String[currentNode.getLines().size];
			int i=0;
			for (String key : currentNode.getLines()) {
				keys[i++] = key;
			}
			dc.init(keys, new ConversationDialogueCallback(talk), false);
			for (EngineEntity speaker : speakers) {
				speaker.add(dc);
			}
			// go to wait state
			talk.setNodeAndState(currentNode, TalkComponent.TalkState.Talking);
			break;
		}
		case Talking: {
			break;
		}
		case Acting: {
			// execute effects
			if (currentNode.getEffect() != null) {
				EffectsComponent ec = new EffectsComponent();
				ec.getEffectList().add(currentNode.getEffect());
				entity.add(ec);
			}

			// find next actions
			Array<Node> available = talk.nextNodes();
			Array<Node> satisfyConditions = new Array<Node>();
			for (Node n : available) {
				if (variablesManager.evaluateCondition(n.getCondition(), true)) {
					satisfyConditions.add(n);
				}
			}
			if (satisfyConditions.size == 1) {
				// select only choice, do not display menu
				talk.setNodeAndState(satisfyConditions.first(),
						TalkComponent.TalkState.Breathing);
			} else if (satisfyConditions.size > 1) {
				EngineEntity firstSpeaker = speakers.get(0);
				// show menu for first speaker
				DialogueComponent dc = new DialogueComponent();
				String[] keys = new String[satisfyConditions.size];
				int i=0;
				for (Node node: satisfyConditions) {
					keys[i++] = node.getLines().get(0);
				}
				dc.init(keys,
					new ConversationMenuCallback(talk, satisfyConditions),
					true);
				firstSpeaker.add(dc);
				// go to wait state
				talk.setNodeAndState(currentNode,
						TalkComponent.TalkState.Thinking);
			} else if (satisfyConditions.size == 0) {
				// this is the end
				talk.setNodeAndState(currentNode,
						TalkComponent.TalkState.Thinking);
			}
			break;
		}
		case Thinking: {
			break;
		}
		}
	}
}
