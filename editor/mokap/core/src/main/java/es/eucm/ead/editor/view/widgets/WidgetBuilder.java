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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ShowContextMenu;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

public class WidgetBuilder {

	private static Controller controller;

	public static void setController(Controller controller) {
		WidgetBuilder.controller = controller;
	}

	public static IconButton toolbarIcon(Skin skin, String icon) {
		return toolbarIcon(skin, icon, null);
	}

	public static IconButton toolbarIcon(Skin skin, String icon, Class action,
			Object... args) {
		return icon(skin, icon, SkinConstants.STYLE_TOOLBAR_ICON, action, args);
	}

	public static IconButton toolbarIconWithMenu(Skin skin, String icon,
			Actor contextMenu) {
		IconButton iconButton = toolbarIcon(skin, icon);
		launchContextMenu(iconButton, contextMenu);
		return iconButton;
	}

	public static void launchContextMenu(Actor actor, Actor contextMenu) {
		actor.addListener(new ActionOnClickListener(controller,
				ShowContextMenu.class, actor, contextMenu, true));
	}

	public static IconButton icon(Skin skin, String icon, String style) {
		return icon(skin, icon, style, null);
	}

	public static IconButton icon(Skin skin, String icon, String style,
			Class action, Object... args) {
		IconButton iconButton = new IconButton(icon, skin, style);
		if (action != null) {
			iconButton.addListener(new ActionOnClickListener(controller,
					action, args));
		}
		return iconButton;
	}

	public static Button button(Skin skin, String style) {
		return new Button(skin, style);
	}

	public static ContextMenu iconLabelContextPanel(Skin skin,
			String... iconLabel) {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.background(skin.getDrawable(SkinConstants.DRAWABLE_PAGE));
		for (int i = 0; i < iconLabel.length; i += 2) {
			String icon = iconLabel[i];
			String label = iconLabel[i + 1];
			contextMenu.add(
					button(skin, icon, label, SkinConstants.STYLE_CONTEXT))
					.fillX();
			contextMenu.row();
		}
		return contextMenu;
	}

	public static ContextMenu iconLabelContextPanel(Skin skin,
			Button... iconLabel) {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.background(skin.getDrawable(SkinConstants.DRAWABLE_PAGE));
		for (int i = 0; i < iconLabel.length; i++) {
			Button button = iconLabel[i];
			contextMenu.add(button).fillX();
			contextMenu.row();
		}
		return contextMenu;
	}

	public static Button button(Skin skin, String icon, String label,
			String style) {
		LinearLayout row = new LinearLayout(true);
		row.add(icon(skin, icon, style));
		row.add(new Label(label, skin, style));
		row.addSpace();
		row.pad(0, 0, 16, 0);

		Button button = button(skin, style);
		button.add(row).fillX().expandX();
		return button;
	}

	public static Button button(Skin skin, String icon, String label,
			String style, Class action, Object... args) {
		Button button = button(skin, icon, label, style);
		button.addListener(new ActionOnClickListener(controller, action, args));
		return button;
	}
}
