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
package es.eucm.ead.editor.view.widgets.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.engine.gdx.AbstractWidget;

/**
 * Represents a context menu item
 */
public class ContextMenuItem extends AbstractWidget implements Disableable {

	private LabelStyle labelStyle;

	private LabelStyle shortcutLabelStyle;

	private ContextMenuItemStyle style;

	private Label label;

	private Label shortcutLabel;

	private ContextMenu parentContextMenu;

	private ContextMenu childContextMenu;

	private ClickListener clickListener;

	private Image icon;

	private boolean disabled;

	/**
	 * Creates a context menu item
	 * 
	 * @param parent
	 *            context menu item parent
	 * @param text
	 *            text for the item
	 * @param skin
	 *            a skin
	 */
	public ContextMenuItem(ContextMenu parent, String text, Skin skin) {
		this.parentContextMenu = parent;

		style = skin.get(ContextMenuItemStyle.class);
		labelStyle = new LabelStyle();
		labelStyle.font = style.font;
		labelStyle.fontColor = style.fontColor;

		label = new Label(text, labelStyle);
		label.setTouchable(Touchable.disabled);
		addActor(label);

		addListener(clickListener = new ClickListener() {
			@Override
			public void enter(InputEvent event, float x, float y, int pointer,
					Actor fromActor) {
				super.enter(event, x, y, pointer, fromActor);
				setVisible(true);
				parentContextMenu.hideAllExcept(ContextMenuItem.this);
				event.stop();
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				// We don't want scene2d to consider this event for drag, so we
				// return false
				return false;
			}
		});
	}

	/**
	 * Sets a context menu that is shown when the mouse is over this item
	 * 
	 * @param submenu
	 *            the submenu to show
	 */
	public ContextMenuItem submenu(ContextMenu submenu) {
		this.childContextMenu = submenu;
		childContextMenu.setVisible(false);
		addActor(childContextMenu);
		return this;
	}

	/**
	 * Sets the value for the label of the item
	 */
	public ContextMenuItem label(String labelText) {
		label.setText(labelText);
		return this;
	}

	private void updateStyles() {
		labelStyle.fontColor = disabled && style.fontColorDisabled != null ? style.fontColorDisabled
				: style.fontColor;

		if (shortcutLabel != null) {
			shortcutLabelStyle.fontColor = disabled
					&& style.fontColorDisabled != null ? style.fontColorDisabled
					: style.fontColorShortcut;
		}
		if (icon != null) {
			icon.setColor(labelStyle.fontColor);
		}
	}

	@Override
	public void setDisabled(boolean isDisabled) {
		this.disabled = isDisabled;
		updateStyles();
	}

	public ContextMenuItem icon(Drawable drawable) {
		if (icon == null) {
			icon = new Image();
			addActor(icon);
		}
		icon.setDrawable(drawable);
		updateStyles();
		return this;
	}

	public ContextMenuItem shorcut(String shortcut) {
		if (shortcutLabel == null) {
			shortcutLabelStyle = new LabelStyle(labelStyle);
			shortcutLabelStyle.fontColor = style.fontColorShortcut;
			shortcutLabel = new Label(shortcut, shortcutLabelStyle);
			addActor(shortcutLabel);
		}
		shortcutLabel.setText(shortcut);
		updateStyles();
		return this;
	}

	@Override
	public void setVisible(boolean visible) {
		if (childContextMenu != null) {
			childContextMenu.setVisible(visible);
		}
	}

	@Override
	public float getPrefWidth() {
		return label.getPrefWidth()
				+ style.padLeft
				+ style.padRight
				+ style.labelMarginLeft
				+ (shortcutLabel == null ? 0 : shortcutLabel.getPrefWidth()
						+ style.shortcutMargin * 2);
	}

	@Override
	public float getPrefHeight() {
		return labelStyle.font.getLineHeight() + style.padBottom + style.padTop;
	}

	@Override
	public void drawChildren(Batch batch, float parentAlpha) {
		if (style.over != null && clickListener.isOver()) {
			float offset = icon != null ? style.padLeft : 0;
			style.over.draw(batch, style.margin + offset, style.margin,
					getWidth() - style.margin * 2 - offset, getHeight()
							- style.margin * 2);
		}

		if (style.arrow != null && childContextMenu != null) {
			float size = getHeight() / 3.5f;
			style.arrow.draw(batch, getWidth() - size,
					(getHeight() - size) / 2.0f, size, size);
		}
		super.drawChildren(batch, parentAlpha);
		batch.setColor(Color.WHITE);
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		return disabled ? null : super.hit(x, y, touchable);
	}

	@Override
	public void layout() {
		if (icon != null) {
			float width = Math.min(icon.getPrefWidth(), style.padLeft
					- style.margin);
			float height = Math.min(icon.getPrefHeight(), getHeight());
			float xOffset = (style.padLeft - style.margin - width) / 2.0f;
			float yOffset = (getHeight() - height) / 2.0f;
			setBounds(icon, style.margin + xOffset, style.margin + yOffset,
					width, height);
		}
		float yOffset = style.padBottom + style.font.getDescent() / 4.0f;
		setPosition(label, style.padLeft + style.labelMarginLeft, yOffset);

		if (shortcutLabel != null) {
			setPosition(shortcutLabel, getWidth() - shortcutLabel.getWidth()
					- style.margin - style.shortcutMargin, yOffset);
		}

		if (childContextMenu != null) {
			float height = childContextMenu.getPrefHeight();
			float width = childContextMenu.getPrefWidth();
			setBounds(childContextMenu, getWidth() - style.childOffset,
					getHeight() - height + style.childOffset, width, height);
		}
	}

	public static class ContextMenuItemStyle {

		public BitmapFont font;

		public Color fontColor, fontColorDisabled, fontColorShortcut;

		public Drawable over, arrow;

		public float padLeft, padRight, padBottom, padTop;

		public float margin = 1.0f;

		public float labelMarginLeft = 4.0f;

		public float shortcutMargin = 15.0f;

		public float childOffset = 2.0f;

	}
}
