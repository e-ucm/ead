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
package es.eucm.ead.editor.view.builders.classic.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.DialogBuilder;
import es.eucm.ead.editor.view.controllers.DialogController;
import es.eucm.ead.editor.view.controllers.DialogController.DialogButtonListener;
import es.eucm.ead.editor.view.widgets.Dialog;
import es.eucm.ead.engine.I18N;

/**
 * Modal dialog to show an informative message.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link String}</em> title for the dialog</dd>
 * <dd><strong>args[1]</strong> <em>{@link String}</em> message of the dialog</dd>
 * <dd><strong>args[2..n]</strong>
 * <em>Couples  of {@link String} and {@link DialogButtonListener}</em>. If
 * args[i] is a string, a button with that string is added. That button will be
 * associated with the {@link DialogButtonListener} in args[i+1]</dd>
 * </dl>
 */
public class InfoDialogBuilder implements DialogBuilder {

	private I18N i18N;

	private DialogController dialogController;

	private Label infoLabel;

	@Override
	public void initialize(Controller controller) {
		i18N = controller.getApplicationAssets().getI18N();
		Skin skin = controller.getApplicationAssets().getSkin();

		dialogController = new DialogController(skin, true, false);
		infoLabel = new Label("", skin);
		infoLabel.setWidth(800);
		dialogController.content(infoLabel);
	}

	@Override
	public Dialog getDialog(Object... args) {
		infoLabel.setText(i18N.m((String) args[1]));
		dialogController.clearButtons().title(i18N.m((String) args[0]));
		for (int i = 2; i < args.length - 1; i += 2) {
			String buttonText = (String) args[i];
			DialogButtonListener dialogButtonListener = (DialogButtonListener) args[i + 1];
			dialogController.button(buttonText, dialogButtonListener);
		}
		return dialogController.getDialog();
	}

	@Override
	public void release(Controller controller) {

	}
}
