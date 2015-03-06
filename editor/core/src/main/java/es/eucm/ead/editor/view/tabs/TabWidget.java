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
package es.eucm.ead.editor.view.tabs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.engine.gdx.AbstractWidget;

/**
 * A widget representing a tab
 */
public class TabWidget extends AbstractWidget {

	private TabWidgetStyle style;

	private float titleMargin;

	private float titleHeight;

	private Label title;

	private LabelStyle titleStyle;

	private LabelStyle titleSelectedStyle;

	private WidgetGroup content;

	private float contentPrefHeight;

	private boolean selected;

	public TabWidget(Skin skin) {
		style = skin.get(TabWidgetStyle.class);
		titleStyle = skin.get("tab-title", LabelStyle.class);
		titleSelectedStyle = skin.get("tab-title-selected", LabelStyle.class);
		title = new Label("", titleStyle);
		title.setAlignment(Align.center);
		addActor(title);
		title.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Group parent = getParent();
				if (parent instanceof TabsPanel) {
					((TabsPanel) parent).setSelectedTab(TabWidget.this);
				}
			}
		});
	}

	/**
	 * Makes this tab selected/unselected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		if (selected) {
			toFront();
		}
		title.setStyle(selected ? titleSelectedStyle : titleStyle);
	}

	/**
	 * Sets the margin for the tab title
	 */
	public void setTitleMargin(float titleMargin) {
		this.titleMargin = titleMargin;
	}

	/**
	 * Sets the height for the tab title
	 */
	public void setTitleHeight(float titleHeight) {
		this.titleHeight = titleHeight;
	}

	/**
	 * Sets the text for the title
	 */
	public void setTitle(String title) {
		this.title.setText(title);
	}

	/**
	 * Sets the widget with the contents for the tab
	 */
	public TabWidget setContent(WidgetGroup content) {
		if (this.content != null) {
			this.content.remove();
		}
		this.content = content;
		this.contentPrefHeight = getPrefHeight(content);
		addActor(content);
		return this;
	}

	public float getTitlePrefWidth() {
		return title.getPrefWidth() + style.titlePad * 2;
	}

	public float getTitlePrefHeight() {
		return title.getPrefHeight() + style.titlePad * 2;
	}

	public WidgetGroup getContent() {
		return content;
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		float yOffset = getHeight() - titleHeight;
		style.contentBackground.draw(batch, 0, 0, getWidth(), yOffset);
		style.contentOverlay.draw(batch, 0, 0, getWidth(), yOffset);
		style.titleBackground.draw(batch, titleMargin, yOffset - 1,
				getTitlePrefWidth(), titleHeight + 1);
		if (selected) {
			style.titleOverlay.draw(batch, titleMargin, yOffset,
					getTitlePrefWidth(), titleHeight);
		}
		super.drawChildren(batch, parentAlpha);
	}

	@Override
	public float getPrefWidth() {
		return Math.max(getTitlePrefWidth(), getPrefWidth(content));
	}

	@Override
	public float getPrefHeight() {
		return getTitlePrefHeight() + contentPrefHeight;
	}

	@Override
	public void layout() {
		float yOffset = getHeight() - titleHeight;
		title.setBounds(titleMargin, yOffset, getTitlePrefWidth(), titleHeight);

		if (content != null) {
			content.setBounds(0, 0, getWidth(), yOffset);
		}
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		Actor actor = super.hit(x, y, touchable);
		return actor != this || y < getHeight() - titleHeight ? actor : null;
	}

	/**
	 * Style for the tab
	 */
	public static class TabWidgetStyle {

		/**
		 * Pad for the title label
		 */
		public float titlePad = 5.0f;

		/**
		 * Background of the title
		 */
		public Drawable titleBackground;

		/**
		 * Background of the content
		 */
		public Drawable contentBackground;

		/**
		 * Overlay of the title. Will be drawn over the title background
		 */
		public Drawable titleOverlay;

		/**
		 * Overlay of the content. Will be drawn over the content background
		 */
		public Drawable contentOverlay;

	}
}
