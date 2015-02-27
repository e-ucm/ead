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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ShowModal;
import es.eucm.ead.editor.view.listeners.LongPressListener;
import es.eucm.ead.editor.view.widgets.ContextMenu;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.basegalleries.ThumbnailsGallery;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.Cell;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.GalleryStyle;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.utils.EngineUtils;

/**
 * A thumbnail gallery that provides a context menu when long pressing the
 * thumbnails
 */
public abstract class ContextMenuGallery extends ThumbnailsGallery {

	protected Controller controller;

	private ContextMenu contextMenu;

	public ContextMenuGallery(float rows, int columns, Assets assets,
			Controller controller, String actionIcon) {
		super(rows, columns, assets, controller.getApplicationAssets()
				.getSkin(), controller.getApplicationAssets().getI18N(),
				controller.getApplicationAssets().getSkin()
						.get(GalleryStyle.class), actionIcon);
		this.controller = controller;
	}

	public ContextMenuGallery(float rows, int columns, Assets assets,
			Controller controller, String galleryStyle, String actionIcon) {
		super(rows, columns, assets, controller.getApplicationAssets()
				.getSkin(), controller.getApplicationAssets().getI18N(),
				controller.getApplicationAssets().getSkin()
						.get(galleryStyle, GalleryStyle.class), actionIcon);
		this.controller = controller;
	}

	public ContextMenuGallery(float rows, int columns, Assets assets,
			String galleryStyle, Controller controller) {
		super(rows, columns, assets, controller.getApplicationAssets()
				.getSkin(), controller.getApplicationAssets().getI18N(),
				galleryStyle);
		this.controller = controller;
	}

	public ContextMenuGallery(float rows, int columns, Assets assets,
			Skin skin, I18N i18N, GalleryStyle galleryStyle,
			Controller controller) {
		super(rows, columns, assets, skin, i18N, galleryStyle);
		this.controller = controller;
	}

	public void setContextMenu(Button... buttons) {
		this.contextMenu = WidgetBuilder.iconLabelContextPanel(buttons);
		addListener(new LongPressListener() {
			@Override
			public void longPress(float x, float y) {
				// If the view is still visible
				if (getStage() != null) {
					getStage().cancelTouchFocus();
					Actor actor = EngineUtils.getDirectChild(gallery.getGrid(),
							hit(x, y, true));
					if (actor instanceof Cell) {
						Tile tile = (Tile) ((Cell) actor).getActor();
						tileLongPressed(tile.getName());
						controller.action(ShowModal.class, contextMenu, x, y);
					}
				}
			}
		});
		this.contextMenu.addHideRunnable(new Runnable() {
			@Override
			public void run() {
				contextMenuHidden();
			}
		});

	}

	/**
	 * A tile was long pressed, and the context menu will appear after this
	 * method is executed
	 * 
	 * @param tileName
	 */
	public void tileLongPressed(String tileName) {

	}

	/**
	 * Context menu has been hidden
	 */
	public void contextMenuHidden() {

	}
}
