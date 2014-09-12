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
package es.eucm.ead.editor.control.actions.irreversibles.scene;

import es.eucm.ead.editor.control.ComponentId;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.irreversibles.IrreversibleAction;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.components.conversation.Conversation;
import es.eucm.ead.schema.components.conversation.LineNode;
import es.eucm.ead.schema.effects.TriggerConversation;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * Creates a {@link Conversation} component with
 * {@link ComponentId#PREFAB_CONVERSATION_ID} as conversation id and an empty
 * speaker and a {@link LineNode} with an empty line as the first node and adds
 * it to the current {@link Selection#SCENE_ELEMENT}. Also adds to the current
 * {@link Selection#SCENE_ELEMENT} a {@link TriggerConversation} effect executed
 * on a {@link Touch} event.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Conversation}</em> the conversation
 * component</dd>
 * </dl>
 */
public class CreateConversationOnTouch extends IrreversibleAction {

	public CreateConversationOnTouch() {
		super(ResourceCategory.SCENE, true, false, Conversation.class);

	}

	@Override
	protected void action(ModelEntity entity, Object[] args) {
		TriggerConversation triggerConversation = new TriggerConversation();
		triggerConversation.setNodeId(0);
		triggerConversation
				.setConversationId(ComponentId.PREFAB_CONVERSATION_ID);

		Behavior showTextBehavior = new Behavior();
		showTextBehavior.setId(ComponentId.PREFAB_SHOW_TEXT);
		showTextBehavior.setEvent(new Touch());
		showTextBehavior.getEffects().add(triggerConversation);

		Conversation conversation = (Conversation) args[0];
		conversation.setId(ComponentId.PREFAB_CONVERSATION);
		LineNode lineNode = new LineNode();
		lineNode.setSpeaker(0);
		lineNode.setLine("");

		conversation.setConversationId(ComponentId.PREFAB_CONVERSATION_ID);
		conversation.getNodes().add(lineNode);
		conversation.getSpeakers().add("");

		ModelEntity sel = (ModelEntity) controller.getModel().getSelection()
				.getSingle(Selection.SCENE_ELEMENT);
		sel.getComponents().add(conversation);
		sel.getComponents().add(showTextBehavior);
	}

}
