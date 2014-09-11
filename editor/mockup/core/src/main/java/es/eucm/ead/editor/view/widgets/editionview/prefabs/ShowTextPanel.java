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
package es.eucm.ead.editor.view.widgets.editionview.prefabs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import es.eucm.ead.editor.control.ComponentId;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.irreversibles.scene.ChangeConversationMessage;
import es.eucm.ead.editor.control.actions.irreversibles.scene.CreateConversationOnTouch;
import es.eucm.ead.editor.control.actions.irreversibles.scene.RemoveComponents;
import es.eucm.ead.editor.platform.MockupPlatform;
import es.eucm.ead.schema.components.conversation.Conversation;
import es.eucm.ead.schema.components.conversation.LineNode;

public class ShowTextPanel extends PrefabComponentPanel implements
		TextInputListener {

	private static final String DEFAULT_SPEAKER_KEY = "edition.showText.defaultSpeaker";
	private static final String DEFAULT_TEXT_KEY = "edition.showText.defaultText";
	private static final float PAD = 20;

	private boolean isText;
	private TextButton speaker;
	private TextButton text;

	public ShowTextPanel(float iconPad, float size,
			final Controller controller, Actor touchable) {
		super("conversation80x80", iconPad, size, "edition.showText",
				ComponentId.PREFAB_CONVERSATION, controller, touchable);

		speaker = new TextButton("", skin, "white");
		speaker.pad(PAD);

		text = new TextButton("", skin, "white");
		text.pad(PAD);

		ChangeListener listener = new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Actor listener = event.getListenerActor();
				isText = listener == text;
				if (isText) {
					MockupPlatform platform = (MockupPlatform) controller
							.getPlatform();
					platform.getMultilineTextInput(ShowTextPanel.this, i18n
							.m("edition.showText.newText"), text.getText()
							.toString(), i18n);
				} else {
					Gdx.input.getTextInput(ShowTextPanel.this, i18n
							.m("edition.showText.newSpeaker"), speaker
							.getText().toString());
				}
			}
		};
		text.addListener(listener);
		text.getLabel().setAlignment(Align.left);
		speaker.addListener(listener);
		speaker.getLabel().setAlignment(Align.left);

		panel.pad(PAD).defaults().space(PAD);
		panel.add(speaker).expand().fill();
		panel.row();
		panel.add(text).expand().fill();
	}

	@Override
	protected void actualizePanel() {
		if (component == null) {
			speaker.setText(i18n.m(DEFAULT_SPEAKER_KEY));
			text.setText(i18n.m(DEFAULT_TEXT_KEY));
		} else {
			Conversation convers = ((Conversation) component);
			LineNode line = (LineNode) convers.getNodes().first();
			setText(line.getLine());

			String speaker = convers.getSpeakers().first();
			setSpeaker(speaker);
		}
	}

	private void setText(String newText) {
		if (!newText.isEmpty()) {
			this.text.setText(newText);
		} else {
			text.setText(i18n.m(DEFAULT_TEXT_KEY));
		}
	}

	private void setSpeaker(String newSpeaker) {
		if (!newSpeaker.isEmpty()) {
			speaker.setText(newSpeaker);
		} else {
			speaker.setText(i18n.m(DEFAULT_SPEAKER_KEY));
		}
	}

	@Override
	protected void trashClicked() {
		if (component != null) {
			controller.action(RemoveComponents.class,
					ComponentId.PREFAB_SHOW_TEXT);
		}
		super.trashClicked();
		panel.show();
	}

	@Override
	public void input(String text) {
		if (text == null) {
			return;
		}
		Conversation conversation = null;
		if (component == null) {
			conversation = new Conversation();
			controller.action(CreateConversationOnTouch.class, conversation);
			component = conversation;
		} else {
			conversation = (Conversation) component;
		}
		if (isText) {
			if (text.isEmpty()) {
				text = i18n.m(DEFAULT_TEXT_KEY);
			}
			controller.action(ChangeConversationMessage.class, false,
					conversation, text);
		} else {
			if (text.isEmpty()) {
				text = i18n.m(DEFAULT_SPEAKER_KEY);
			}
			controller.action(ChangeConversationMessage.class, true,
					conversation, text);
		}

		actualizePanel();
		panel.show();
		setUsed(true);
	}

	@Override
	public void canceled() {

	}
}
