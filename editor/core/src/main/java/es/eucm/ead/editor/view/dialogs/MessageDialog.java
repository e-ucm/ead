/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.view.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.Engine;

public class MessageDialog extends Dialog {

	// Constants of dialog levels
	public static final int none = -1;
	public static final int info = 0;
	public static final int warning = 1;
	public static final int error = 2;

	private Skin skin;

	/**
	 * Label to show the messages
	 */
	private Label label;

	/**
	 * Icon of the label
	 */
	private Image icon;

	/**
	 * Current level of the dialog (if not showing, level is
	 * {@link MessageDialog#none}
	 */
	private int currentLevel;

	/**
	 * Current messages showed by the dialog
	 */
	private Array<String> currentMessages;

	public MessageDialog(Skin skin) {
		super("", skin);
		this.skin = skin;

		label = new Label("", skin);
		label.setWrap(false);
		currentMessages = new Array<String>();
		currentLevel = none;
		icon = new Image();
		Table table = new Table();
		table.add(icon).top().pad(5);
		table.add(label).pad(5);
		getContentTable().add(table);

		button(Engine.i18n.m("general.ok"));
	}

	@Override
	protected void result(Object object) {
		// When OK is pressed
		currentMessages.clear();
		currentLevel = none;
	}

	/**
	 * Shows a message. If there is already a message showing, the incoming
	 * message is added as a new line
	 * 
	 * @param level
	 *            type of the message: {@link MessageDialog#info},
	 *            {@link MessageDialog#warning}, {@link MessageDialog#error}
	 * @param message
	 *            i18n key for the message
	 */
	public void showMessage(int level, String message) {
		if (level > currentLevel) {
			currentLevel = level;
		}

		// Update the icon
		switch (currentLevel) {
		case info:
			icon.setDrawable(skin, "info");
			break;
		case warning:
			icon.setDrawable(skin, "warning");
			break;
		case error:
			icon.setDrawable(skin, "error");
			break;
		}

		// Update the messages
		currentMessages.add(message);
		if (currentMessages.size > 1) {
			String text = "";
			for (String m : currentMessages) {
				text += Engine.i18n.m(m) + "\n";
			}
			label.setText(text);
		} else {
			label.setText(Engine.i18n.m(message));
		}
		show(this.getStage());
	}
}
