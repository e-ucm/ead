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
package es.eucm.ead.editor.view.builders.mockup.gallery;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.panels.GalleryGrid;
import es.eucm.ead.engine.I18N;

/**
 * Abstract class. A layout that holds a top tool bar and a gallery grid in the
 * center..
 */
public abstract class BaseGallery implements ViewBuilder {

	private Table rootWindow;
	private GalleryGrid<Actor> galleryTable;

	@Override
	public String getName() {
		return null;
	}

	/**
	 * Make the Gallery view with three WidgetsGroup that return the bottom,
	 * center and topWidget functions. If any WidgetGroup is null, this is not
	 * added.
	 * */
	@Override
	public Actor build(Controller controller) {
		I18N i18n = controller.getEditorAssets().getI18N();
		Skin skin = controller.getEditorAssets().getSkin();
		final Vector2 viewport = controller.getPlatform().getSize();

		rootWindow = new Table().debug();
		rootWindow.setFillParent(true);

		WidgetGroup top = topWidget(viewport, i18n, skin, controller);
		WidgetGroup center = centerWidget(viewport, i18n, skin, controller);

		if (top != null) {
			rootWindow.add(top).expandX().fill();
		}
		if (center != null) {
			rootWindow.row();
			rootWindow.add(center).center().fill().expand();
		}
		addActorToHide(top);
		return rootWindow;
	}

	/**
	 * This adds an actor that will be hidden when we enter selection mode.
	 * Convenience method that shouldn't be overridden.
	 * 
	 * @param actorToHide
	 */
	protected void addActorToHide(Actor actorToHide) {
		galleryTable.addActorToHide(actorToHide);
	}

	/**
	 * This method constructs the top tool bar. You shouldn't override this
	 * method unless you know what you're doing.
	 * 
	 * @param viewport
	 * @param i18n
	 * @param skin
	 * @param controller
	 * @return
	 */
	protected WidgetGroup topWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {

		String search = i18n.m("general.gallery.search");
		TextField searchTf = new TextField("", skin);
		searchTf.setMessageText(search);
		searchTf.setMaxLength(search.length());
		String[] orders = new String[] { i18n.m("general.gallery.sort"),
				i18n.m("general.gallery.nameAZ"),
				i18n.m("general.gallery.nameZA"),
				i18n.m("general.gallery.more"), i18n.m("general.gallery.less") };

		SelectBox<String> order = new SelectBox<String>(skin);
		order.setItems(orders);

		ToolBar topBar = new ToolBar(viewport, skin);
		topBar.debug();
		topBar.add(topLeftButton(skin, controller)).left().expandX();
		topBar.right();
		topBar.add(searchTf, order);

		return topBar;
	}

	/**
	 * This method should never return null.
	 * 
	 * @param skin
	 * @return The button that will be placed at left in the top tool bar.
	 */
	protected abstract Button topLeftButton(Skin skin, Controller controller);

	/**
	 * This method constructs the central widget which is a {@link GalleryGrid}.
	 * You shouldn't override this method unless you know what you're doing.
	 * 
	 * @param viewport
	 * @param i18n
	 * @param skin
	 * @param controller
	 * @return
	 */
	protected WidgetGroup centerWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {

		Table centerWidget = new Table().debug();

		galleryTable = new GalleryGrid<Actor>(skin, 8, 4, viewport, rootWindow);

		addElementsToTheGallery(galleryTable, viewport, i18n, skin);

		ScrollPane sp = new ScrollPane(galleryTable);
		sp.setScrollingDisabled(true, false);

		centerWidget.add(sp).expand().fill();

		return centerWidget;
	}

	/**
	 * The desired elements that will be shown in the central panel should be
	 * placed here. Those elements should be added to the {@link GalleryGrid
	 * galleryTable}.
	 * 
	 * @param galleryTable
	 *            the holder of the elements
	 * @param viewport
	 * @param i18n
	 * @param skin
	 */
	protected abstract void addElementsToTheGallery(
			GalleryGrid<Actor> galleryTable, Vector2 viewport, I18N i18n,
			Skin skin);

	@Override
	public void initialize(Controller controller) {
	}

	@Override
	public void release(Controller controller) {
	}

}
