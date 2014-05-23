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
package es.eucm.ead.editor.ui.maintoolbar;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.editor.Copy;
import es.eucm.ead.editor.control.actions.editor.Cut;
import es.eucm.ead.editor.control.actions.editor.OpenGame;
import es.eucm.ead.editor.control.actions.editor.Paste;
import es.eucm.ead.editor.control.actions.editor.Redo;
import es.eucm.ead.editor.control.actions.editor.Save;
import es.eucm.ead.editor.control.actions.editor.Undo;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.Separator;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

/**
 * Main toolbar, with the main menu and global tools: save, cut, copy, paste,
 * undo, redo, search...
 */
public class MainToolbar extends LinearLayout {

	private Controller controller;

	public MainToolbar(Skin skin, Controller controller) {
		super(true);
		this.controller = controller;

		I18N i18N = controller.getApplicationAssets().getI18N();

		add(createIcon("logomenu64x64", skin, OpenGame.class)).expand(true,
				true);

		LinearLayout controlsTop = new LinearLayout(true);
		controlsTop.add(createIcon("back24x24", skin, null));
		controlsTop.add(createIcon("forward24x24", skin, null));

		TextField searchTextField = new TextField("", skin);
		searchTextField.setMessageText(i18N.m("general.search"));

		controlsTop.add(searchTextField).margin(5, 0, 0, 0).expandX();

		LinearLayout controlsBottom = new LinearLayout(true);
		controlsBottom.add(createIcon("save24x24", skin, Save.class));
		controlsBottom.add(new Separator(false, skin));
		controlsBottom.add(createIcon("cut24x24", skin, Cut.class));
		controlsBottom.add(createIcon("copy24x24", skin, Copy.class));
		controlsBottom.add(createIcon("paste24x24", skin, Paste.class));
		controlsBottom.add(new Separator(false, skin));
		controlsBottom.add(createIcon("undo24x24", skin, Undo.class));
		controlsBottom.add(createIcon("redo24x24", skin, Redo.class));

		LinearLayout container = new LinearLayout(false).pad(5)
				.defaultWidgetsMargin(0);
		container.add(controlsTop).left();
		container.add(controlsBottom).left();

		add(container).centerY();
	}

	/**
	 * 
	 * @return a button with the given drawable, that, when clicked executes the
	 *         given {@link EditorAction}
	 */
	private <T extends EditorAction> IconButton createIcon(String drawable,
			Skin skin, Class<T> editorAction) {
		IconButton iconButton = new IconButton(drawable, 5, skin);
		iconButton.addListener(new ActionOnClickListener(controller,
				editorAction));
		return iconButton;
	}

}
