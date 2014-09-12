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
package es.eucm.ead.engine.systems.conversations;

import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.renderers.OptionsComponent;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.systems.conversations.NodeSystem.RuntimeNode;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.engine.variables.VarsContext;
import es.eucm.ead.schema.components.conversation.Conversation;
import es.eucm.ead.schema.components.conversation.OptionNode;

public class OptionRuntimeNode extends RuntimeNode<OptionNode> {

	public static final String OPTION_SELECTED_VAR_SUFFIX = "_option_selected";

	public static final int NO_OPTION_SELECTED = -1;

	private VariablesManager variablesManager;

	private String optionSelectedVar;

	@Override
	public void setGameLoop(GameLoop gameLoop) {
		super.setGameLoop(gameLoop);
		this.variablesManager = gameLoop.getSystem(EffectsSystem.class)
				.getVariablesManager();
	}

	@Override
	public void setNode(OptionNode node) {
		super.setNode(node);
		optionSelectedVar = getOptionSelectedVar(conversation);
		variablesManager.setValue(optionSelectedVar, NO_OPTION_SELECTED, true);
		OptionsComponent option = gameLoop
				.createComponent(OptionsComponent.class);
		option.setOptions(node.getOptions());
		option.setNode(node);
		option.setConversation(conversation);

		entity.add(option);
	}

	public static String getOptionSelectedVar(Conversation conversation) {
		return VarsContext.RESERVED_VAR_PREFIX
				+ conversation.getConversationId() + OPTION_SELECTED_VAR_SUFFIX;
	}

	@Override
	public boolean update(float delta) {
		return (Integer) (variablesManager.getValue(optionSelectedVar)) != NO_OPTION_SELECTED;
	}

	@Override
	public int nextNode() {
		int optionSelected = (Integer) (variablesManager
				.getValue(optionSelectedVar));
		if (optionSelected >= 0 && optionSelected < node.getNextNodeIds().size) {
			return node.getNextNodeIds().get(optionSelected);
		} else {
			return END_NODE;
		}
	}
}
