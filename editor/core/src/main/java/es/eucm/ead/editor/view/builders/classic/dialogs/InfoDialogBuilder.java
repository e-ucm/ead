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
import es.eucm.ead.editor.view.widgets.Dialog;
import es.eucm.ead.editor.view.widgets.TextArea;
import es.eucm.ead.editor.view.widgets.layouts.TopBottomLayout;

/**
 * Non-modal dialog without maximize button to show information.
 * 
 * Created by Angel-E-UCM on 28/03/14.
 */
public class InfoDialogBuilder implements DialogBuilder {

	public static String NAME = "infoDialog";

	private DialogController dialogController;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Dialog build(Controller controller, Object... arguments) {

		// There are only one argument and last argument is the body of the
		// dialog
		String infoMessage = (String) arguments[0];

		Skin skin = controller.getApplicationAssets().getSkin();

		// creates a dialog non-modal and without maximizer button
		dialogController = new DialogController(skin, false, false);

		TopBottomLayout messageContainer = new TopBottomLayout();
		Label label = new Label(infoMessage, skin);
		messageContainer.addTop(label);

		return dialogController.title("").root(messageContainer).getDialog();
	}
}
