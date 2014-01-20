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
package es.eucm.ead.mockup.core.view.ui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.mockup.core.control.screens.AbstractScreen;
import es.eucm.ead.mockup.core.control.screens.Screens;
import es.eucm.ead.mockup.core.view.UIAssets;
import es.eucm.ead.mockup.core.view.ui.Panel;

public class NavigationPanel extends Panel {

	private final float ICON_PAD_LEFT = 20f;
	private final float PANEL_PAD = 10f;
	private final float PANEL_WIDTH = AbstractScreen.stagew * .35f;
	private final float x = -PANEL_WIDTH, y = UIAssets.TOOLBAR_HEIGHT;

	public NavigationPanel(Skin skin) {
		this(skin, "default");
	}

	public NavigationPanel(Skin skin, String styleName) {
		super(skin, styleName);
		setBounds(x, y, PANEL_WIDTH, AbstractScreen.stageh - 2 * UIAssets.TOOLBAR_HEIGHT);
		setVisible(false);
		setModal(true);

		NavigationPanelStyle style = skin.get(styleName,
				NavigationPanelStyle.class);

		Label projectLabel = new Label("Proyecto", skin); //TODO use i18n in this class
		projectLabel.setAlignment(Align.center);

		Image projectImg = new Image(style.backButton); //back project img
		final Button projectButton = new Button(skin,
				"navigationPanelProject");

		projectButton.add(projectImg).padLeft(ICON_PAD_LEFT);
		projectButton.add(projectLabel).expand();

		Label editElementLabel = new Label("Editar Elemento", skin);
		editElementLabel.setAlignment(Align.center);
		Image editElementImg = new Image(style.editElement); //edit element img
		final Button editElementButton = new Button(skin,
				"navigationPanelRest");
		editElementButton.add(editElementImg).padLeft(ICON_PAD_LEFT);
		editElementButton.add(editElementLabel).expandX();

		Label editSceneLabel = new Label("Editar Escena", skin);
		editSceneLabel.setAlignment(Align.center);
		Image editSceneImg = new Image(style.editScene); //edit scene img
		final Button editSceneButton = new Button(skin,
				"navigationPanelRest");
		editSceneButton.add(editSceneImg).padLeft(ICON_PAD_LEFT);
		editSceneButton.add(editSceneLabel).expandX();

		Label galleryLabel = new Label("Galería", skin);
		galleryLabel.setAlignment(Align.center);
		Image galleryImg = new Image(style.gallery); //gallery img
		final Button galleryButton = new Button(skin,
				"navigationPanelRest");
		galleryButton.add(galleryImg).padLeft(ICON_PAD_LEFT);
		galleryButton.add(galleryLabel).expandX();

		Label lanuchGameLabel = new Label("Lanzar Juego", skin);
		lanuchGameLabel.setAlignment(Align.center);
		Image lanuchGameImg = new Image(style.launch); //launch img
		final Button lanuchGameButton = new Button(skin,
				"navigationPanelRest");
		lanuchGameButton.add(lanuchGameImg).padLeft(ICON_PAD_LEFT);
		lanuchGameButton.add(lanuchGameLabel).expandX();

		ClickListener mTransitionListenerListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				final Screens next = getNextScreen(event.getListenerActor());
				if (next == null) {
					return;
				}
				AbstractScreen.mockupController.getScreenController()
						.getCurrentScreen().exitAnimation(next);
			}

			private Screens getNextScreen(Actor target) {
				Screens next = null;
				if (target == projectButton) {
					next = Screens.PROJECT_MENU;
				} else if (target == editElementButton) {
					//next = Screens.PROJECT_GALLERY; //Edit Element TODO
				} else if (target == editSceneButton) {
					next = Screens.SCENE_EDITION;
				} else if (target == galleryButton) {
					next = Screens.GALLERY;
				}
				return next;
			}
		};
		projectButton.addListener(mTransitionListenerListener);
		editElementButton.addListener(mTransitionListenerListener);
		editSceneButton.addListener(mTransitionListenerListener);
		galleryButton.addListener(mTransitionListenerListener);

		pad(PANEL_PAD);
		defaults().expand().fill().space(PANEL_PAD).uniform();
		add(projectButton);
		row();
		add(editElementButton);
		row();
		add(editSceneButton);
		row();
		add(galleryButton);
		row();
		add(lanuchGameButton);
	}

	@Override
	public void show() {
		super.show();
		addAction(Actions.moveTo(0, y, fadeDuration));
	}

	@Override
	public void hide() {
		super.hide();
		addAction(Actions.moveTo(x, y, fadeDuration));
	}

	/**
	 * Define the style of a {@link NavigationPanelStyle NavigationPanelStyle}.
	 */
	static public class NavigationPanelStyle extends PanelStyle {
		
		public Drawable backButton, editElement, editScene, gallery, launch;

	}
}
