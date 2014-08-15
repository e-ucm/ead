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
package es.eucm.ead.editor.view.builders.gallery;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.DropDown;
import es.eucm.ead.editor.view.widgets.GalleryItem;
import es.eucm.ead.editor.view.widgets.Toolbar;
import es.eucm.ead.editor.view.widgets.ToolbarIcon;

public abstract class BaseGallery<T extends GalleryItem> implements ViewBuilder {

	private static final float TOOLBAR_SIZE = 0.07f, ICON_PAD = 5,
			SMALL_PAD = 20;

	private Table view;

	protected Skin skin;

	private Array<T> items;

	private float height;

	private Table gallery;

	@Override
	public void initialize(Controller controller) {
		this.skin = controller.getApplicationAssets().getSkin();
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);

		height = controller.getPlatform().getSize().y;

		view = new Table();
		view.align(Align.top);
		view.setFillParent(true);

		Toolbar topBar = new Toolbar(skin, "white_top") {
			@Override
			public float getPrefHeight() {
				return TOOLBAR_SIZE * height;
			}
		};

		topBar.align(Align.right);

		Actor backButton = createBackButton();
		if (backButton != null) {
			topBar.add(backButton).expand().left().padLeft(SMALL_PAD);
		}

		Actor toolbarText = createToolbarText();
		topBar.add(toolbarText).expand().center();

		Actor search = createSearchWidget();
		topBar.add(search).padRight(SMALL_PAD);

		Actor reorder = addReorderWidget();
		topBar.add(reorder).padRight(SMALL_PAD);

		Actor settings = createSettings();
		if (settings != null) {
			topBar.add(settings).padRight(SMALL_PAD);
		}

		loadItems();

		view.add(topBar).expandX().fill();
		view.row();
		view.add(gallery).expandX().fill();
	}

	protected Actor addReorderWidget() {
		// TODO reorder functionality
		DropDown reorder = new DropDown(skin);
		Array array = new Array();
		array.add(new ToolbarIcon("reorderAZ80x80", ICON_PAD, TOOLBAR_SIZE
				* height, skin));
		array.add(new ToolbarIcon("reorderZA80x80", ICON_PAD, TOOLBAR_SIZE
				* height, skin));
		reorder.setItems(array);

		return reorder;
	};

	protected Actor createSearchWidget() {
		// TODO search widget
		return new ToolbarIcon("search80x80", ICON_PAD, TOOLBAR_SIZE * height,
				skin);
	};

	protected void loadItems() {
		// TODO complete gallery
		gallery = new Table();

		Image logo = new Image(skin, "eAdventure");
		gallery.add(new GalleryItem(logo, "", 20, 0, true, skin));
	};

	protected abstract Actor createSettings();

	protected abstract Actor createPlayButton();

	protected abstract Actor createBackButton();

	protected abstract Actor createToolbarText();

	@Override
	public void release(Controller controller) {

	}

	@Override
	public Actor getView(Object... args) {
		return view;
	}
}
