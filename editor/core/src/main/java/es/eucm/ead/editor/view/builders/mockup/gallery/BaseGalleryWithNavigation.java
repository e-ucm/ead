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

import java.util.Comparator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.mockup.Navigation;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.buttons.DescriptionCard;
import es.eucm.ead.editor.view.widgets.mockup.panels.HiddenPanel;
import es.eucm.ead.engine.I18N;

/**
 * Abstract class. This implementation of {@link BaseGallery} also has a
 * navigation button with its {@link HiddenPanel panel}, a filter button with
 * its {@link HiddenPanel panel} and a bottom tool bar with one or two buttons
 * (Take picture and Take photo).
 * 
 * This is the base implementation for the galleries that will display scenes,
 * elements or both.
 */
public abstract class BaseGalleryWithNavigation<T extends DescriptionCard>
		extends BaseGallery<T> {

	private Navigation navigation;
	private HiddenPanel filterPanel;

	@Override
	public Actor build(Controller controller) {
		I18N i18n = controller.getEditorAssets().getI18N();
		Skin skin = controller.getEditorAssets().getSkin();
		final Vector2 viewport = controller.getPlatform().getSize();

		navigation = new Navigation(viewport, controller, skin);
		Table rootWindow = (Table) super.build(controller);
		WidgetGroup bottom = bottomWidget(viewport, i18n, skin, controller);

		if (bottom != null) {
			rootWindow.row();
			rootWindow.add(bottom).expandX().fill();
		}
		addActorToHide(bottom);
		return rootWindow;
	}

	@Override
	protected Button topLeftButton(Vector2 viewport, Skin skin,
			Controller controller) {
		return navigation.getButton();
	}

	@Override
	protected WidgetGroup topWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		Table top = (Table) super.topWidget(viewport, i18n, skin, controller);

		Button filterButton = new TextButton(i18n.m("general.gallery.filter"),
				skin);
		filterButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (BaseGalleryWithNavigation.this.filterPanel.isVisible()) {
					BaseGalleryWithNavigation.this.filterPanel.hide();
				} else {
					BaseGalleryWithNavigation.this.filterPanel.show();
				}
				return false;
			}
		});

		top.add(filterButton);

		return top;
	}

	@Override
	protected WidgetGroup centerWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		Table centerWidget = (Table) super.centerWidget(viewport, i18n, skin,
				controller);

		this.filterPanel = filterPanel(i18n, skin);
		Container wrapper = new Container(this.filterPanel);
		wrapper.setFillParent(true);
		wrapper.right().top();
		centerWidget.addActor(wrapper);

		Container navWrapper = new Container(navigation.getPanel());
		navWrapper.setFillParent(true);
		navWrapper.top().left().fillY();
		centerWidget.addActor(navWrapper);
		return centerWidget;
	}

	/**
	 * This method constructs the bottom tool bar. This tool bar usually has one
	 * or two buttons (Take picture and Take video).
	 * 
	 * @param viewport
	 * @param i18n
	 * @param skin
	 * @param controller
	 * @return The widget that will be placed at the bottom of the view.
	 */
	protected WidgetGroup bottomWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		ToolBar botBar = new ToolBar(viewport, skin, 0.04f);

		Button bottomLeftButton = bottomLeftButton(viewport, i18n, skin,
				controller);
		if (bottomLeftButton != null) {
			botBar.add(bottomLeftButton).left().expandX();
		}
		Button bottomRightButton = bottomRightButton(viewport, i18n, skin,
				controller);
		if (bottomRightButton != null) {
			botBar.add(bottomRightButton).right();
		}

		return botBar;
	}

	@Override
	protected void addSortingsAndComparators(Array<String> shortings,
			ObjectMap<String, Comparator<T>> comparators, I18N i18n) {
		// Do nothing since we won't have additional sorting methods in
		// ElementGallery, SceneGallery or Gallery
	}

	/**
	 * This method must return the filter panel. A panel composed by a list of
	 * tags usually.
	 * 
	 * @param i18n
	 * @param skin
	 * @return
	 */
	protected abstract HiddenPanel filterPanel(I18N i18n, Skin skin);

	/**
	 * This method should return the button that will be placed at left in the
	 * bottom tool bar.
	 * 
	 * @param viewport
	 * @param i18n
	 * @param skin
	 * @param controller
	 * @return
	 */
	protected abstract Button bottomLeftButton(Vector2 viewport, I18N i18n,
			Skin skin, Controller controller);

	/**
	 * This method should return the button that will be placed at right in the
	 * bottom tool bar.
	 * 
	 * @param viewport
	 * @param i18n
	 * @param skin
	 * @param controller
	 * @return
	 */
	protected abstract Button bottomRightButton(Vector2 viewport, I18N i18n,
			Skin skin, Controller controller);
}
