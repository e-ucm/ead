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
package es.eucm.ead.editor.view.widgets.mockup;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ConfirmationDialog extends Dialog {

	private static final float DEFAULT_DIALOG_PADDING_LEFT_RIGHT = 20f;

	/**
	 * Creates a decision dialog with two buttons, passing a Boolean = true
	 * Object to {@link #result(Object)} for the accept button and Boolean =
	 * false for the deny button. Some arguments may be null, in which case no
	 * view will be shown for that argument. An automatic shortcut will be added
	 * for the BACK key (Android), that will pass Boolean = false to
	 * {@link #result(Object)}.
	 * 
	 * @param title
	 * @param text
	 *            may be null.
	 * @param acceptText
	 *            may be null.
	 * @param denyText
	 *            may be null.
	 * @param skin
	 */
	public ConfirmationDialog(String title, String text, String acceptText,
			String denyText, Skin skin) {
		super(title, skin, "exit-dialog");
		this.initialize(title, text, acceptText, denyText);
	}

	private void initialize(String title, String text, String acceptText,
			String denyText) {
		super.setModal(true);
		super.setMovable(false);
		super.setResizable(false);
		super.key(Keys.BACK, false);
		if (text != null)
			super.text(text);
		if (acceptText != null)
			super.button(acceptText, true);
		if (denyText != null)
			super.button(denyText, false);
		super.padLeft(DEFAULT_DIALOG_PADDING_LEFT_RIGHT);
		super.padRight(DEFAULT_DIALOG_PADDING_LEFT_RIGHT);
		super.padBottom(DEFAULT_DIALOG_PADDING_LEFT_RIGHT * .65f);
	}

	@Override
	public boolean isVisible() {
		return super.getParent() != null;
	}

	@Override
	public Dialog show(Stage stage) {
		if (!this.isVisible()) {
			return super.show(stage);
		}
		return null;
	}
}
