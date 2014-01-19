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

	private float x, y;

	public NavigationPanel(Skin skin) {
		this(skin, "default");
	}

	public NavigationPanel(Skin skin, String styleName) {
		super(skin, styleName);
		float w = AbstractScreen.stagew * .35f;
		this.x = -w;
		this.y = UIAssets.TOOLBAR_HEIGHT;
		setBounds(x, y, w, AbstractScreen.stageh - 2 * UIAssets.TOOLBAR_HEIGHT);
		setVisible(false);
		setModal(true);

		NavigationPanelStyle style = skin.get(styleName,
				NavigationPanelStyle.class);

		Label cbs1 = new Label("Proyecto", skin);
		cbs1.setAlignment(Align.center);

		Image backImg = new Image(style.backButton); //back project img
		final Button navigationPanelProject = new Button(skin,
				"navigationPanelProject");

		navigationPanelProject.add(backImg);
		navigationPanelProject.add(cbs1).expand().fill();

		Label cbs2 = new Label("Editar Elemento", skin);
		cbs2.setFontScale(1f);
		Image backImg2 = new Image(style.editElement); //edit element img
		final Button navigationPanelProject2 = new Button(skin,
				"navigationPanelRest");
		navigationPanelProject2.add(backImg2).padLeft(20);
		navigationPanelProject2.add(cbs2).expandX().fill();

		Label cbs3 = new Label("Editar Escena", skin);
		cbs3.setFontScale(1f);
		Image backImg3 = new Image(style.editScene); //edit scene img
		final Button navigationPanelProject3 = new Button(skin,
				"navigationPanelRest");
		navigationPanelProject3.add(backImg3).padLeft(20);
		navigationPanelProject3.add(cbs3).expandX().fill();

		Label cbs4 = new Label("Galería", skin);
		cbs4.setFontScale(1f);
		Image backImg4 = new Image(style.gallery); //gallery img
		final Button navigationPanelProject4 = new Button(skin,
				"navigationPanelRest");
		navigationPanelProject4.add(backImg4).padLeft(20);
		navigationPanelProject4.add(cbs4).expandX().fill();

		Label cbs5 = new Label("Lanzar Juego", skin);
		cbs5.setFontScale(1f);
		Image backImg5 = new Image(style.launch); //launch img
		final Button navigationPanelProject5 = new Button(skin,
				"navigationPanelRest");
		navigationPanelProject5.add(backImg5).padLeft(20);
		navigationPanelProject5.add(cbs5).expandX().fill();

		ClickListener mListener = new ClickListener() {
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
				if (target == navigationPanelProject) {
					next = Screens.PROJECT_MENU;
				} else if (target == navigationPanelProject2) {
					//next = Screens.PROJECT_GALLERY; //Edit Element TODO
				} else if (target == navigationPanelProject3) {
					next = Screens.SCENE_EDITION;
				} else if (target == navigationPanelProject4) {
					next = Screens.GALLERY;
				}
				return next;
			}
		};
		navigationPanelProject.addListener(mListener);
		navigationPanelProject2.addListener(mListener);
		navigationPanelProject3.addListener(mListener);
		navigationPanelProject4.addListener(mListener);

		float PAD = 40;
		pad(PAD);
		defaults().expand().fill().space(PAD).uniform();
		add(navigationPanelProject);
		row();
		add(navigationPanelProject2);
		row();
		add(navigationPanelProject3);
		row();
		add(navigationPanelProject4);
		row();
		add(navigationPanelProject5);
		row();
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
	 * Define the style of a {@link Panel panel}.
	 * 
	 */
	static public class NavigationPanelStyle extends PanelStyle {

		public Drawable backButton, editElement, editScene, gallery, launch;

	}
}
