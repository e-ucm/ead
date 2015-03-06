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
package es.eucm.ead.editor.widgets;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.menu.ContextMenu;

public class ContextMenuTest extends AbstractWidgetTest {
	@Override
	public AbstractWidget createWidget(Controller controller) {
		Skin skin = controller.getApplicationAssets().getSkin();
		final ContextMenu contextMenu = new ContextMenu(skin);
		contextMenu.item("New game").shorcut("Ctrl+N");
		contextMenu.item("Open game").shorcut("Ctrl+O")
				.icon(skin.getDrawable("undo24x24")).setDisabled(true);
		contextMenu.item("Save").shorcut("Ctrl+S")
				.icon(skin.getDrawable("save24x24"));

		ContextMenu recent = new ContextMenu(skin);

		recent.item("Parity");
		recent.item("La dama Boba");
		recent.item("Lost in Space <XML>");

		contextMenu.separator();
		contextMenu.item("Recents", recent);
		contextMenu.separator();

		ContextMenu exportMobile = new ContextMenu(skin);
		exportMobile.item("Android");
		exportMobile.item("iOS");
		exportMobile.item("Windows Phone");

		ContextMenu exportDesktop = new ContextMenu(skin);
		exportDesktop.item("Windows");
		exportDesktop.item("Linux");
		exportDesktop.item("Mac");
		exportDesktop.separator();
		exportDesktop.item("Multiplatform...").setDisabled(true);

		ContextMenu export = new ContextMenu(skin);

		export.item("Desktop", exportDesktop);
		export.item("Mobile", exportMobile);

		contextMenu.item("Export", export);

		contextMenu.separator();
		contextMenu.item("Exit");

		setFillWindow(true);

		AbstractWidget widget = new AbstractWidget() {

			@Override
			public void layout() {
				setBounds(contextMenu, 0,
						getHeight() - contextMenu.getPrefHeight(),
						contextMenu.getPrefWidth(), contextMenu.getPrefHeight());
			}
		};

		widget.addActor(contextMenu);
		return widget;
	}

	public static void main(String[] args) {
		new LwjglApplication(new ContextMenuTest(), "Context menu test", 1000,
				600);
	}
}
