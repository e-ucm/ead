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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ShowContextMenu;
import es.eucm.ead.editor.control.actions.editor.ShowTooltip;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.listeners.AsyncImage;
import es.eucm.ead.editor.view.listeners.VisibleActionListener;
import es.eucm.ead.editor.view.tooltips.Tooltip;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

public class WidgetBuilder {

	/**
	 * This represents 0.76 cm (48px) in MDPI, the size for the buttons that we
	 * want in all devices.
	 */
	public static float UNIT_SIZE = 48;

	private static Controller controller;

	private static Skin skin;

	private static float buttonSize;

	private static final ActorGestureListener TOOLTIP_LISTENER = new ActorGestureListener() {
		@Override
		public boolean longPress(Actor actor, float x, float y) {
			if (actor instanceof Tooltip) {
				controller.action(ShowTooltip.class,
						((Tooltip) actor).getTooltip(), actor);
				controller.getViews().getViewsContainer().getStage()
						.cancelTouchFocus(actor);
			}
			return true;
		}
	};

	public static void setController(Controller controller) {
		WidgetBuilder.controller = controller;
		WidgetBuilder.buttonSize = controller.getApplicationAssets().getSkin()
				.getDrawable("bg48").getMinHeight();
		skin = controller.getApplicationAssets().getSkin();
	}

	public static IconButton toolbarIcon(String icon, String tooltip) {
		return toolbarIcon(icon, tooltip, null);
	}

	public static IconButton toolbarIcon(String icon, String tooltip,
			Class action, Object... args) {
		IconButton iconButton = icon(icon, SkinConstants.STYLE_TOOLBAR, action,
				args);
		tooltip(iconButton, tooltip);
		return iconButton;
	}

	public static void tooltip(Actor actor, String tooltip) {
		if (tooltip != null && actor instanceof Tooltip) {
			((Tooltip) actor).setTooltip(tooltip);
			actor.addListener(TOOLTIP_LISTENER);
		}
	}

	public static IconButton toolbarIcon(String icon, String tooltip,
			boolean listenToAction, Class action, Object... args) {
		IconButton button = toolbarIcon(icon, tooltip, action, args);
		if (listenToAction) {
			button.pack();
			button.setTransform(true);
			button.setOrigin(Align.center);
			controller.getActions().addActionListener(action,
					new VisibleActionListener(button));
			button.setVisible(controller.getActions().getAction(action)
					.isEnabled());
		}
		return button;
	}

	public static MultiWidget multiToolbarIcon(IconButton... buttons) {
		MultiWidget multiButton = new MultiWidget(skin);
		for (IconButton button : buttons) {
			multiButton.addWidgets(button);
		}

		return multiButton;
	}

	public static IconButton toolbarIconWithMenu(String icon, Actor contextMenu) {
		IconButton iconButton = toolbarIcon(icon, null);
		launchContextMenu(iconButton, contextMenu);
		return iconButton;
	}

	public static void launchContextMenu(Actor actor, Actor contextMenu) {
		actionOnClick(actor, ShowContextMenu.class, actor, contextMenu, true);
	}

	public static IconButton icon(String icon, String style) {
		return icon(icon, style, (Class) null);
	}

	public static IconButton icon(String icon, String tooltip, String style) {
		IconButton iconButton = icon(icon, style);
		tooltip(iconButton, tooltip);
		return iconButton;
	}

	public static IconButton icon(String icon, String style, Class action,
			Object... args) {
		IconButton iconButton = new IconButton(icon, skin, style);
		actionOnClick(iconButton, action, args);
		return iconButton;
	}

	public static Button button(String style) {
		return new Button(skin, style);
	}

	public static Button button(String style, Class action, Object... args) {
		Button button = new Button(skin, style);
		actionOnClick(button, action, args);
		return button;
	}

	public static ImageButton imageButton(String style, Class action,
			Object... args) {
		ImageButton button = new ImageButton(skin, style);
		actionOnClick(button, action, args);
		return button;
	}

	public static ContextMenu iconLabelContextPanel(String... iconLabel) {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.background(skin.getDrawable(SkinConstants.DRAWABLE_PAGE));
		for (int i = 0; i < iconLabel.length; i += 2) {
			String icon = iconLabel[i];
			String label = iconLabel[i + 1];
			contextMenu.add(button(icon, label, SkinConstants.STYLE_CONTEXT))
					.fillX();
			contextMenu.row();
		}
		return contextMenu;
	}

	public static ContextMenu iconLabelContextPanel(Button... iconLabel) {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.background(skin.getDrawable(SkinConstants.DRAWABLE_PAGE));
		for (int i = 0; i < iconLabel.length; i++) {
			Button button = iconLabel[i];
			contextMenu.add(button).fillX();
			contextMenu.row();
		}
		return contextMenu;
	}

	public static Button button(String icon, String label, String style) {
		LinearLayout row = new LinearLayout(true);
		row.add(icon(icon, style));
		row.add(new Label(label, skin, style));
		row.addSpace();
		row.pad(0, 0, dpToPixels(16), 0);
		row.setTouchable(Touchable.disabled);

		Button button = button(style);
		button.setName(icon);
		button.add(row).fillX().expandX();
		return button;
	}

	public static Button button(String icon, String label, String style,
			Class action, Object... args) {
		Button button = button(icon, label, style);
		actionOnClick(button, action, args);
		return button;
	}

	public static void actionOnClick(Actor actor, Class action, Object... args) {
		if (action != null) {
			actor.addListener(new ActionOnClickListener(controller, action,
					args));
		}
	}

	public static float dpToPixels(float dp) {
		return (buttonSize / UNIT_SIZE) * dp;
	}

	public static Actor circlesMenu(int align, String[] icons, Class[] actions,
			Object[][] args) {
		if (icons.length != actions.length || actions.length != args.length) {
			throw new IllegalArgumentException(
					"Icons array needs to have the same length as actions array.");
		}
		Skin skin = controller.getApplicationAssets().getSkin();

		ButtonStyle circleStyle = skin.get(SkinConstants.STYLE_CIRCLE,
				ButtonStyle.class);

		CirclesMenu menu = new CirclesMenu(align);

		if (menu.isHorizontal()) {
			menu.defaultWidgetsMargin(0, 0, dpToPixels(8), 0);
		} else {
			menu.defaultWidgetsMargin(0, dpToPixels(8), 0, 0);
		}
		for (int i = 0; i < icons.length; i++) {
			ImageButtonStyle imageButtonStyle = new ImageButtonStyle(
					circleStyle);
			imageButtonStyle.imageUp = skin.getDrawable(icons[i]);
			ImageButton imageButton = new ImageButton(imageButtonStyle);
			actionOnClick(imageButton, actions[i], args[i]);
			menu.add(imageButton);
		}
		return menu;
	}

	/**
	 * Creates an image widget that dynamically reloads the image in the given
	 * path every time the image changes
	 * 
	 */
	public static AsyncImage asyncImage(String imagePath) {
		return new AsyncImage(imagePath, controller.getEditorGameAssets());
	}

	public static Tile tile(Actor background, String text) {
		Tile tile = new Tile(skin);
		tile.setBackground(background);
		tile.setText(text);
		return tile;
	}

	public static Image image(String drawable) {
		return new Image(skin, drawable);
	}

	public static Label label(String label, String styleEdition) {
		return new Label(label, skin, styleEdition);
	}
}
