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
package es.eucm.ead.editor.view.builders;

import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.Action.ActionListener;
import es.eucm.ead.editor.view.listeners.ActionOnDownListener;
import es.eucm.ead.editor.view.widgets.menu.ContextMenu;
import es.eucm.ead.editor.view.widgets.menu.ContextMenuItem;
import es.eucm.ead.editor.view.widgets.menu.Menu;
import es.eucm.ead.editor.view.widgets.menu.MenuItem;

/**
 * Builder to construct standard top bar menus. Menus contain 4 widget types:
 * {@link Menu}, which is the root of all the structure. {@link Menu} contains
 * {@link MenuItem}s, which are each of the individual buttons the user can
 * select in the menu. Each {@link MenuItem} contains a {@link ContextMenu},
 * that behaves as a drop-down list that appears when the item is clicked.
 * {@link ContextMenu} acts as a container of {@link ContextMenuItem}s, which
 * are buttons the user can click invoking some action.
 */
public class MenuBuilder {

	private Controller controller;

	public MenuBuilder(Controller controller) {
		this.controller = controller;
	}

	public Builder build() {
		return new Builder(controller.getApplicationAssets().getSkin());
	}

	public class Builder {
		private Menu menu;
		private MenuItem menuItem;
		private ContextMenuItem contextMenuItem;
		private Disableable disableable;
		private Class lastActionName;
		private Object[] lastActionArgs;

		public Builder(Skin skin) {
			menu = new Menu(skin);
		}

		/**
		 * 
		 * @return the menu built
		 */
		public Menu getMenu() {
			return menu;
		}

		/**
		 * Adds a {@link MenuItem} to the root menu with the given label. An
		 * empty drop-down {@link ContextMenu} will be added automatically to
		 * this menu item. Each subsequent call to
		 * {@link Builder#addContextItem(String, es.eucm.ead.editor.view.widgets.menu.ContextMenu)}
		 * or {@link Builder#addContextItem(String, Class, Object...)} will add
		 * a {@link ContextMenuItem} to the recently created empty
		 * {@link ContextMenu}
		 * 
		 * @param label
		 *            the label for the menu item
		 * @return the builder
		 */
		public Builder addMenuItem(String label) {
			menuItem = menu.item(label);
			disableable = menuItem;
			return this;
		}

		/**
		 * Adds a {@link ContextMenuItem} to the {@link ContextMenu} of the
		 * current {@link MenuItem}.
		 * 
		 * @param label
		 *            the label for the item
		 * @param actionClass
		 *            the action to execute when item is selected
		 * @param args
		 *            extra arguments for the action
		 * @return this builder (useful for concatenating calls)
		 */
		public Builder addContextItem(String label, Class actionClass,
				Object... args) {
			this.lastActionName = actionClass;
			this.lastActionArgs = args;
			contextMenuItem = menuItem.subitem(label);
			contextMenuItem.addListener(new ActionOnDownListener(controller,
					actionClass, args));
			// Enable state
			controller.getActions().addActionListener(actionClass,
					new EnableActionListener(contextMenuItem));
			contextMenuItem.setDisabled(!controller.getActions().isEnabled(
					actionClass));
			disableable = contextMenuItem;
			return this;
		}

		/**
		 * Adds a {@link ContextMenuItem} to the {@link ContextMenu} of the
		 * current {@link MenuItem}.
		 * 
		 * @param label
		 *            the label for the item
		 * @param actionListener
		 *            The input listener for the item
		 * @return this builder (useful for concatenating calls)
		 */
		public Builder addContextItem(String label,
				InputListener actionListener, Object... args) {
			contextMenuItem = menuItem.subitem(label);
			contextMenuItem.addListener(actionListener);
			disableable = contextMenuItem;
			return this;

		}

		/**
		 * Adds a {@link ContextMenuItem} to the {@link ContextMenu} of the
		 * current {@link MenuItem}. When the user hovers the mouse over it, it
		 * will show a sub menu
		 * 
		 * @param label
		 *            the label for the item
		 * @param contextMenu
		 *            the sub menu to show
		 * @return this builder (useful for concatenating calls)
		 */
		public Builder addContextItem(String label, ContextMenu contextMenu) {
			contextMenuItem = menuItem.subitem(label, contextMenu);
			disableable = contextMenuItem;
			return this;
		}

		/**
		 * Sets the icon for the last item created (through
		 * {@link Builder#addContextItem(String, Class, Object...)} or
		 * {@link Builder#addContextItem(String, es.eucm.ead.editor.view.widgets.menu.ContextMenu)}
		 * 
		 * @param drawable
		 *            the icon for the item
		 * @return this builder (useful for concatenating calls)
		 */
		public Builder setIcon(Drawable drawable) {
			contextMenuItem.setIcon(drawable);
			return this;
		}

		/**
		 * Sets the shortcut for the last item created (through
		 * {@link Builder#addContextItem(String, Class, Object...)} or
		 * {@link Builder#addContextItem(String, es.eucm.ead.editor.view.widgets.menu.ContextMenu)}
		 * . This method accomplishes two things: adds a label with the shortcut
		 * to the item, and registers the shortcut in the controller, associated
		 * to the last action added through
		 * {@link Builder#addContextItem(String, Class, Object...)}, so be aware
		 * of using this method AFTER the action is set
		 * 
		 * @param shortcut
		 *            a shortcut with label format. This label will be converted
		 *            to lowercase to be registered in the controller (i.e., if
		 *            the label is "Ctrl+O", the shortcut will be registered as
		 *            "ctrl+o", since shortcuts manager works with lowercase
		 * @return this builder (useful for concatenating calls)
		 */
		public Builder setShortcut(String shortcut) {
			controller.getKeyMap().registerShortcut(shortcut.toLowerCase(),
					lastActionName, lastActionArgs);
			contextMenuItem.setShorcut(shortcut);
			return this;
		}

		/**
		 * Sets initial state of the last item created (either a
		 * {@link ContextMenuItem} or a {@link MenuItem} to disable
		 * 
		 * @return this builder (useful for concatenating calls)
		 */
		public Builder disable() {
			disableable.setDisabled(true);
			return this;
		}

		/**
		 * Adds a separator in the current {@link ContextMenu}
		 * 
		 * @return this builder (useful for concatenating calls)
		 */
		public Builder addSeparator() {
			menuItem.separator();
			return this;
		}

	}

	public static class EnableActionListener implements ActionListener {
		private Disableable disableable;

		public EnableActionListener(Disableable disableable) {
			this.disableable = disableable;
		}

		@Override
		public void enableChanged(Class action, boolean enable) {
			disableable.setDisabled(!enable);
		}
	}
}
