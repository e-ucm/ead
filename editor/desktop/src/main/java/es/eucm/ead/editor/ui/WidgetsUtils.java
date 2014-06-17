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
package es.eucm.ead.editor.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.Action;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.listeners.ActionOnDownListener;
import es.eucm.ead.editor.view.listeners.EnableActionListener;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.menu.ContextMenu;
import es.eucm.ead.editor.view.widgets.menu.ContextMenuItem;

/**
 * Contains a bunch of util methods to create editor widgets
 */
public class WidgetsUtils {

	// MENU ITEM METHODS
	/**
	 * Adds new {@link ContextMenuItem} to the {@link ContextMenu} with and
	 * specific {@link Action} to be triggered when it will be pressed.
	 * 
	 * The {@link ContextMenuItem} is added to the {@link Action} listeners as
	 * {@link Disableable} in order to be enable or not taking into account the
	 * state of the {@link Action}.
	 * 
	 * @param contextMenu
	 *            The {@link ContextMenu} menu where the new created
	 *            {@link ContextMenuItem} will be added.
	 * @param label
	 *            The text associated to the new created {@link ContextMenuItem}
	 * @param disabled
	 *            Set the enable/disable initial property of the menu item.
	 * @param action
	 *            The {@link Action} to be triggered when pressed
	 * @param args
	 *            Args for the {@link Action}
	 * 
	 * @return The {@link ContextMenu} with the new born {@link ContextMenuItem}
	 *         added on it.
	 * 
	 */
	public static <T extends Action> ContextMenu menuItem(
			Controller controller, ContextMenu contextMenu, String label,
			boolean disabled, Class<T> action, Object... args) {

		ContextMenuItem item = contextMenu.item(label);

		// adding a listener to item to perform the action when pressed
		item.addListener(new ActionOnDownListener(controller, action, args));
		// adding a listener to the action with the Context menu item
		// to be notified about changes in the action state
		item.setDisabled(disabled);
		controller.getActions().addActionListener(action,
				new EnableActionListener(item));

		return contextMenu;
	}

	// ICON METHODS
	/**
	 * Creates a {@link IconButton} with and specific {@link Action} to be
	 * triggered when it will be pressed. It will be initialy enabled or
	 * disabled according to disabled parameter.
	 * 
	 * @return a button with the given drawable, that, when clicked executes the
	 *         given {@link Action}
	 */
	public static <T extends Action> IconButton createIcon(
			Controller controller, String drawable, float imagePadding,
			boolean disabled, Skin skin, String tooltip, Class<T> editorAction,
			Object... actionArgs) {
		IconButton iconButton = new IconButton(drawable, imagePadding, skin);
		iconButton.addListener(new ActionOnClickListener(controller,
				editorAction, actionArgs));
		iconButton.setTooltip(tooltip);
		iconButton.setDisabled(disabled);
		// FIXME remove this if: now is added to prevent not implemented
		// actions to destroy everything
		if (editorAction != null) {
			controller.getActions().addActionListener(editorAction,
					new EnableActionListener(iconButton));
		}
		return iconButton;
	}

	/**
	 * @return a button with the given drawable, that, when clicked executes the
	 *         given {@link Action}. By default this method initialize the
	 *         button enable.
	 */
	public static <T extends Action> IconButton createEnabledIcon(
			Controller controller, String drawable, float imagePadding,
			Skin skin, String tooltip, Class<T> editorAction,
			Object... actionArgs) {

		return createIcon(controller, drawable, imagePadding, false, skin,
				tooltip, editorAction, actionArgs);
	}

	/**
	 * @return a button with the given drawable, that, when clicked executes the
	 *         given {@link Action}. By default this method set the button as
	 *         enable.
	 */
	public static <T extends Action> IconButton createDisabledIcon(
			Controller controller, String drawable, float imagePadding,
			Skin skin, String tooltip, Class<T> editorAction,
			Object... actionArgs) {

		return createIcon(controller, drawable, imagePadding, true, skin,
				tooltip, editorAction, actionArgs);
	}

}
