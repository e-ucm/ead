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
package es.eucm.ead.engine.demos;

import es.eucm.ead.editor.demobuilder.EditorDemoBuilder;
import es.eucm.ead.editor.demobuilder.ConversationBuilder;
import es.eucm.ead.editor.demobuilder.ConversationBuilder.ForkBuilder;

/**
 * Created by angel on 24/07/14.
 */
public class MeetingAFriendDemo extends EditorDemoBuilder {

	public MeetingAFriendDemo() {
		super("planes-demo");
	}

	@Override
	public String[] assetPaths() {
		return new String[] { "images/background.png", "images/rocks_down.png",
				"images/rocks_up.png", "images/starGold.png", "plane/red.json" };
	}

	@Override
	public String getName() {
		return "Meeting a friend";
	}

	@Override
	protected void doBuild() {
		String conversationId = "c";
		singleSceneGame(assets[0]);

		ConversationBuilder conversation = initBehavior(getLastScene(),
				makeTriggerConversation(conversationId, 0)).conversation(
				getLastEntity(), conversationId);

		ForkBuilder option = conversation
				.speakers("Angus", "Friederick", "green").start()
				.line(0, "Hello!").line(1, "Hi there! How are you?").options();
		option.start("Fine, thanks").line(1, "I'm glad you are fine")
				.nextNode(0);
		option.start("Really bad, actually").line(1, "I'm glad you are bad")
				.nextNode(0);
	}

	public static void main(String[] args) {
		MeetingAFriendDemo demo = new MeetingAFriendDemo();
		demo.run();
	}
}
