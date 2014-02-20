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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.view.widgets.layouts.LeftRightLayout;

public class Dialog extends AbstractWidget {

	private DialogStyle style;

	private LeftRightLayout titleBar;

	private WidgetGroup root;

	private float titleHeight;

	private boolean maximized = false;

	private float oldX;

	private float oldY;

	private float oldWidth;

	private float oldHeight;

	public Dialog(Skin skin) {
		style = skin.get(DialogStyle.class);
		titleBar = new LeftRightLayout(style.titleBackground).margin(5.0f);
		addButtons(skin);
		addActor(titleBar);
	}

	private void addButtons(Skin skin) {
		Image close = new Image(skin, "close");
		close.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Dialog.this.remove();
			}
		});
		Image maximize = new Image(skin, "maximize");
		maximize.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				maximize();
			}
		});
		titleBar.right(close);
		titleBar.right(maximize);

		titleBar.addListener(new InputListener() {

			float startX;

			float startY;

			float dialogX;

			float dialogY;

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (maximized) {
					return false;
				}
				dialogX = getX();
				dialogY = getY();
				startX = event.getStageX();
				startY = event.getStageY();
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				setPosition(dialogX + event.getStageX() - startX, dialogY
						+ event.getStageY() - startY);
			}
		});

		titleBar.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (getTapCount() > 1) {
					maximize();
				}
			}
		});
	}

	private void maximize() {
		if (maximized) {
			setBounds(oldX, oldY, oldWidth, oldHeight);
		} else {
			oldWidth = getWidth();
			oldHeight = getHeight();
			oldX = getX();
			oldY = getY();
			setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			center();
		}
		maximized = !maximized;
	}

	public Dialog title(String title) {
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = style.titleFont;
		labelStyle.fontColor = style.titleFontColor;
		Label titleLabel = new Label(title, labelStyle);
		titleBar.left(titleLabel);
		return this;
	}

	public Dialog root(WidgetGroup root) {
		this.root = root;
		addActor(root);
		return this;
	}

	@Override
	public float getPrefWidth() {
		return getChildrenMaxWidth() + style.pad * 2.0f;
	}

	@Override
	public float getPrefHeight() {
		return getChildrenTotalHeight() + style.pad * 2.0f;
	}

	private float getTitlePrefHeight() {
		return titleBar.getPrefHeight();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		style.background.draw(batch, getX(), getY(), getWidth(), getHeight());
		super.draw(batch, parentAlpha);
	}

	public void center() {
		// Dialog has a special behavior, so it set its bounds itself
		float width = this.getWidth();
		float height = this.getHeight();
		float x = (getStage().getWidth() - width) / 2.0f;
		float y = (getStage().getHeight() - height) / 2.0f;
		setPosition(x, y);
	}

	@Override
	public void layout() {
		float y = getHeight();
		// Title layout
		titleHeight = getTitlePrefHeight();
		y -= titleHeight;

		titleBar.setBounds(0, y, getWidth(), titleHeight);
		root.setBounds(0, 0, getWidth(), getHeight() - titleHeight - style.pad);
	}

	public static class DialogStyle {

		public BitmapFont titleFont;

		public Color titleFontColor;

		public Drawable background, titleBackground;

		public float pad = 10.0f;
	}
}
