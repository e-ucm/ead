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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.editor.Editor;
import es.eucm.ead.editor.view.options.OptionsPanel;
import es.eucm.ead.engine.I18N;

public class OptionsDialog extends Dialog {

	private DialogListener dialogListener;

	public OptionsDialog(Skin skin) {
		super("", skin);
	}

	/**
	 * Shows an option in this dialog
	 * 
	 * @param optionPanel
	 *            the options panel
	 * @param dialogListener
	 *            the listener waiting for the result
	 * @param buttonKeys
	 *            the i18n keys for the buttons to be added
	 * @return this dialog
	 */
	public Dialog show(OptionsPanel optionPanel, DialogListener dialogListener,
			String... buttonKeys) {
		this.dialogListener = dialogListener;
		getButtonTable().clearChildren();
		getContentTable().clearChildren();
		for (String buttonKey : buttonKeys) {
			this.button(I18N.m(buttonKey), buttonKey);
		}
		Table content = optionPanel.getControl(Editor.controller
				.getCommandManager(), Editor.assets.getSkin());
		content.setFillParent(true);
		content.debug();

		Table tableContent = getContentTable();
		if (Editor.debug) {
			tableContent.debug();
		}
		tableContent.add(content);

		Editor.controller.getModel().addModelListener(optionPanel);
		return super.show(Editor.stage);
	}

	@Override
	protected void result(Object object) {
		dialogListener.button(object.toString());
	}
}
