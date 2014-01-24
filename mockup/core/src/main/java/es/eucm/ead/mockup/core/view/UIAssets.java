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
package es.eucm.ead.mockup.core.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.mockup.core.control.screens.AbstractScreen;
import es.eucm.ead.mockup.core.view.ui.components.NavigationPanel;
import es.eucm.ead.mockup.core.view.ui.components.OptionsPanel;

public class UIAssets {

	private static Group optionsGroup, navigationGroup;
	private static boolean created = false;
	public static final String OPTIONS_PANEL_NAME = "o";
	public static final String NAVIGATION_PANEL_NAME = "n";

	/* Some constant sizes */
	public static float TOOLBAR_HEIGHT;
	public static float OPTIONS_BUTTON_WIDTH_HEIGHT;
	public static float NAVIGATION_BUTTON_WIDTH_HEIGHT;
	public static float GALLERY_PROJECT_HEIGHT;

	public static void create() {
		created = true;
		initSizes();
		createOptionsGroup();
		createNavigationGroup();
	}

	private static void initSizes() {
		TOOLBAR_HEIGHT = AbstractScreen.stageh * .085f;
		NAVIGATION_BUTTON_WIDTH_HEIGHT = TOOLBAR_HEIGHT;
		OPTIONS_BUTTON_WIDTH_HEIGHT = AbstractScreen.stageh * .1f;
		GALLERY_PROJECT_HEIGHT = AbstractScreen.stageh * .3f;
	}

	private static void createOptionsGroup() {
		optionsGroup = new Group();
		optionsGroup.setVisible(false);
		final OptionsPanel p = new OptionsPanel(AbstractScreen.skin, "dialog");
		p.setName(OPTIONS_PANEL_NAME);
		final Button options = new ImageButton(AbstractScreen.skin);
		options.setBounds(AbstractScreen.stagew - OPTIONS_BUTTON_WIDTH_HEIGHT,
				AbstractScreen.stageh - OPTIONS_BUTTON_WIDTH_HEIGHT,
				OPTIONS_BUTTON_WIDTH_HEIGHT, OPTIONS_BUTTON_WIDTH_HEIGHT);
		options.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!p.isVisible()) {
					AbstractScreen.mockupController.show(p);
				} else {
					AbstractScreen.mockupController.hide(p);
				}
			}
		});
		Texture t = new Texture(
				Gdx.files.internal("mockup/temp/logo_plano.png"));
		t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		Image i = new Image(t);
		i.setTouchable(Touchable.disabled);
		i.setX(AbstractScreen.halfstagew - t.getWidth() / 2f);
		i.setY(AbstractScreen.halfstageh - t.getHeight() / 2f);

		optionsGroup.addActor(i);
		optionsGroup.addActor(p);
		optionsGroup.addActor(options);
	}

	private static void createNavigationGroup() {
		navigationGroup = new Group();
		navigationGroup.setVisible(false);
		final NavigationPanel p = new NavigationPanel(AbstractScreen.skin,
				"default");
		p.setName(NAVIGATION_PANEL_NAME);
		p.setModal(false);
		final Button navigation = new ImageButton(AbstractScreen.skin);
		navigation.setBounds(0, AbstractScreen.stageh
				- NAVIGATION_BUTTON_WIDTH_HEIGHT,
				NAVIGATION_BUTTON_WIDTH_HEIGHT, NAVIGATION_BUTTON_WIDTH_HEIGHT);
		navigation.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// event.cancel();
				if (!p.isVisible()) {
					AbstractScreen.mockupController.show(p);
				} else {
					AbstractScreen.mockupController.hide(p);
				}
			}
		});

		navigationGroup.addActor(p);
		navigationGroup.addActor(navigation);
	}

	public static void addActors() {
		AbstractScreen.stage.addActor(navigationGroup);
		AbstractScreen.stage.addActor(optionsGroup);
	}

	/**
	 * Used to display the configuration.
	 */
	public static Group getOptionsGroup() {
		return optionsGroup;
	}

	public static Group getNavigationGroup() {
		return navigationGroup;
	}

	public static boolean isCreated() {
		return created;
	}
}
