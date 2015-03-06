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
package es.eucm.ead.editor.view.widgets.galleries;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.Tabs;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.basegalleries.ThumbnailsGallery;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

public class TabsGallery extends AbstractWidget {

	protected final I18N i18N;
	protected Skin skin;

	private TextField search;

	private ThumbnailsGallery[] galleries;

	protected Tabs tabs;

	protected LinearLayout toolbar;

	protected LinearLayout topRow;

	protected ThumbnailsGallery currentGallery;

	protected LinearLayout searchWidget;

	private Actor toolbarIcon;

	private Label title;

	private Button actionButton;

	private Drawable background;

	public TabsGallery(String title, Skin skin, I18N i18N) {
		this(title, SkinConstants.IC_MOKAP, skin, i18N);
	}

	public TabsGallery(String title, String icon, Skin skin, I18N i18N) {

		setFillParent(true);

		this.skin = skin;
		this.i18N = i18N;

		toolbar = new LinearLayout(false);
		toolbar.add(topRow = buildTopRow(title, icon)).expandX();
		toolbar.add(tabs = new Tabs(skin)).left();
		toolbar.background(skin.getDrawable(SkinConstants.DRAWABLE_TOOLBAR));
		toolbar.backgroundColor(skin.getColor(SkinConstants.COLOR_BROWN_MOKA));

		addActor(toolbar);

		background = skin.getDrawable(SkinConstants.DRAWABLE_GRAY_100);

	}

	@Override
	public void layout() {
		super.layout();
		toolbar.pack();
		toolbar.setSize(getWidth(), toolbar.getHeight());
		toolbar.toFront();
		currentGallery.setSize(getWidth(), getHeight() - toolbar.getHeight()
				* 0.5f);

		if (actionButton != null) {
			actionButton.setPosition(getWidth() - actionButton.getWidth()
					- WidgetBuilder.dpToPixels(32),
					WidgetBuilder.dpToPixels(32));
		}
		positionGallery();
		searchWidget.setWidth(getWidth() * 0.4f);
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		if (background != null) {
			background.draw(batch, 0, 0, getWidth(), getHeight());
		}
		super.drawChildren(batch, parentAlpha);
	}

	protected LinearLayout buildTopRow(String text, String icon) {
		LinearLayout toolbar = new LinearLayout(true);
		toolbar.background(skin.getDrawable(SkinConstants.DRAWABLE_BLANK));
		toolbar.backgroundColor(skin.getColor(SkinConstants.COLOR_BROWN_MOKA));
		toolbar.add(toolbarIcon = WidgetBuilder.toolbarIcon(icon, null));
		toolbar.add(
				title = WidgetBuilder.label(text, SkinConstants.STYLE_TOOLBAR))
				.marginLeft(WidgetBuilder.dpToPixels(8));
		toolbar.addSpace();
		toolbar.add(searchWidget = buildSearchButton()).right();

		return toolbar;
	}

	public void changeTitle(String text) {
		title.setText(text);
	}

	public void changeColor(Color color) {
		topRow.backgroundColor(color);
		toolbar.backgroundColor(color);
	}

	public Actor getToolbarIcon() {
		return toolbarIcon;
	}

	public String getSearchText() {
		return search.getText();
	}

	public void setSearchText(String search) {
		this.search.setText(search);
	}

	protected LinearLayout buildSearchButton() {
		LinearLayout searchActor = new LinearLayout(true);

		IconButton icon = WidgetBuilder.toolbarIcon(SkinConstants.IC_SEARCH,
				i18N.m("search"));

		search = new TextField("", skin);
		search.addListener(new InputListener() {
			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ENTER) {
					loadContents();
					return true;
				}
				return false;
			}
		});

		searchActor.add(icon);
		searchActor.add(search).marginRight(WidgetBuilder.dpToPixels(16));

		return searchActor;
	}

	public void loadContents() {
		if (currentGallery != null && currentGallery.getParent() != null) {
			currentGallery
					.loadContents(searchWidget.isVisible() ? getSearchText()
							: "");
		}
	}

	public void setTabs(String[] tabName, ThumbnailsGallery... tabWidget) {
		galleries = tabWidget;
		tabs.setItems(tabName);
		tabs.addListener(new Tabs.TabListener() {

			@Override
			public void changed(Tabs.TabEvent event) {
				changeTab();
			}
		});
		changeTab();
	}

	private void changeTab() {
		if (currentGallery != null) {
			currentGallery.remove();
		}
		currentGallery = galleries[tabs.getSelectedTabIndex()];
		addActor(currentGallery);
		if (actionButton != null) {
			actionButton.remove();
		}
		if (currentGallery.getActionButton() != null) {
			addActor(actionButton = currentGallery.getActionButton());
		}
		searchWidget.setVisible(currentGallery.isSearchEnabled());
		loadContents();
	}

	private float lastScrollY = 0;

	@Override
	public void act(float delta) {
		super.act(delta);
		toolbar.setY(Math.max(Math.min(
				toolbar.getY()
						+ Math.round(currentGallery.getGallery().getScrollY()
								- lastScrollY),
				getHeight() - toolbar.getHeight() * 0.5f), getHeight()
				- toolbar.getHeight()));
		positionGallery();

		lastScrollY = currentGallery.getGallery().getScrollY();
	}

	private void positionGallery() {
		currentGallery.setPosition(0,
				toolbar.getY() - currentGallery.getHeight());
	}
}
