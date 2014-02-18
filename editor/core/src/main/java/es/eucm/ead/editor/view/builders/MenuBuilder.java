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
package es.eucm.ead.editor.view.builders;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.menu.ContextMenu;
import es.eucm.ead.editor.view.widgets.menu.ContextMenuItem;
import es.eucm.ead.editor.view.widgets.menu.Menu;
import es.eucm.ead.editor.view.widgets.menu.MenuItem;

public class MenuBuilder {

	private Controller controller;

	public MenuBuilder(Controller controller) {
		this.controller = controller;
	}

	public Builder build() {
		return new Builder(controller.getEditorAssets().getSkin());
	}

	public class Builder {
		private Menu menu;
		private MenuItem menuItem;
		private ContextMenuItem contextMenuItem;
		private Disableable disableable;

		public Builder(Skin skin) {
			menu = new Menu(skin);
		}

		public Menu done() {
			return menu;
		}

		public Builder menu(String label) {
			menuItem = menu.item(label);
			disableable = menuItem;
			return this;
		}

		public Builder context(String label, String action, Object... args) {
			contextMenuItem = menuItem.subitem(label);
			contextMenuItem.addListener(new ActionOnClickListener(controller,
					action, args));
			disableable = contextMenuItem;
			return this;
		}

		public Builder context(String label, ContextMenu contextMenu) {
			contextMenuItem = menuItem.subitem(label, contextMenu);
			disableable = contextMenuItem;
			return this;
		}

		public Builder icon(Drawable drawable) {
			contextMenuItem.setIcon(drawable);
			return this;
		}

		public Builder disable() {
			disableable.setDisabled(true);
			return this;
		}
	}
}
