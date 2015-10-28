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
import es.eucm.ead.engine.components.LineComponent;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.systems.conversations.NodeSystem.SimpleRuntimeNode;
import es.eucm.ead.engine.variables.ReservedVariableNames;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.components.conversation.Conversation;
import es.eucm.ead.schema.components.conversation.LineNode;

public class LineRuntimeNode extends SimpleRuntimeNode<LineNode> {

	public static final String LINE_ENDED_VAR_SUFFIX = "_line_ended";

	private VariablesManager variablesManager;
	private String lineEndedVar;

	@Override
	public void setGameLoop(GameLoop gameLoop) {
		super.setGameLoop(gameLoop);
		this.variablesManager = gameLoop.getSystem(EffectsSystem.class)
				.getVariablesManager();
	}

	@Override
	public void setNode(LineNode node) {
		super.setNode(node);
		lineEndedVar = getLineEndedVar(conversation);
		variablesManager.setValue(lineEndedVar, false, true);
		LineComponent line = gameLoop.createComponent(LineComponent.class);
		line.setSpeaker(conversation.getSpeakers().get(node.getSpeaker()));
		line.setLine(node.getLine());
		line.setConversation(conversation);
		line.setNode(node);

		entity.add(line);
	}

	public static String getLineEndedVar(Conversation conversation) {
		return ReservedVariableNames.RESERVED_VAR_PREFIX
				+ conversation.getConversationId() + LINE_ENDED_VAR_SUFFIX;
	}

	@Override
	public boolean update(float delta) {
		return (Boolean) variablesManager.getValue(lineEndedVar);
	}
}
