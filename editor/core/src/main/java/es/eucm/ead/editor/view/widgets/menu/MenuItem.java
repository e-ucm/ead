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
package es.eucm.ead.editor.view.widgets.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.view.widgets.AbstractWidget;

public class MenuItem extends AbstractWidget implements Disableable {

	private ContextMenu contextMenu;

	private Label label;

	private Menu parentMenu;

	private MenuItemStyle style;

	private LabelStyle labelStyle;

	private boolean disabled;

	public MenuItem(Menu parentMenu, String text, Skin skin) {
		this.parentMenu = parentMenu;
		style = skin.get(MenuItemStyle.class);
		labelStyle = new LabelStyle();
		labelStyle.font = style.font;
		labelStyle.fontColor = style.fontColor;
		this.label = new Label(text, labelStyle);

		contextMenu = new ContextMenu(skin);
		contextMenu.setVisible(false);

		addActor(this.label);
		addActor(contextMenu);

		addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (contextMenu.isVisible()) {
					MenuItem.this.parentMenu.selected(null, true);
				} else {
					MenuItem.this.parentMenu.selected(MenuItem.this, true);
				}
				event.stop();
				return false;
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer,
					Actor fromActor) {
				MenuItem.this.parentMenu.selected(MenuItem.this, false);
			}
		});
	}

	@Override
	public void setDisabled(boolean isDisabled) {
		this.disabled = isDisabled;
		labelStyle.fontColor = disabled && style.fontColorDisabled != null ? style.fontColorDisabled
				: style.fontColor;
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		return disabled ? null : super.hit(x, y, touchable);
	}

	public void setVisible(boolean visible) {
		contextMenu.setVisible(visible);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (contextMenu.isVisible() && style.opened != null) {
			style.opened.draw(batch, getX(), getY(), getWidth(), getHeight());
		}
		super.draw(batch, parentAlpha);
	}

	public ContextMenuItem subitem(String label, ContextMenu submenu) {
		return contextMenu.item(label, submenu);
	}

	public ContextMenuItem subitem(String label) {
		return contextMenu.item(label);
	}

	public void separator() {
		contextMenu.separator();
	}

	@Override
	public float getPrefWidth() {
		return label.getPrefWidth() + style.pad * 2;
	}

	@Override
	public float getPrefHeight() {
		return labelStyle.font.getLineHeight() + style.padTop + style.padBottom;
	}

	@Override
	public void layout() {
		super.layout();
		setBounds(label, style.pad, -label.getStyle().font.getDescent(),
				getWidth(), getHeight());
		float height = contextMenu.getPrefHeight();
		float width = contextMenu.getPrefWidth();
		setBounds(contextMenu, 0, -height, width, height);
	}

	public static class MenuItemStyle {
		public Drawable opened;
		public float pad = 7f;
		public float padTop = 3.f, padBottom = 3.f;
		public BitmapFont font;
		public Color fontColor, fontColorDisabled;
	}
}
