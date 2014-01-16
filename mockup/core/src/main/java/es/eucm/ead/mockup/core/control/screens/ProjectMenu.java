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
package es.eucm.ead.mockup.core.control.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.mockup.core.control.listeners.FocusListener;
import es.eucm.ead.mockup.core.view.UIAssets;
import es.eucm.ead.mockup.core.view.ui.CircularGroup;

public class ProjectMenu extends AbstractScreen {

	private Group optionsGroup;
	private Button escena, galeria;

	@Override
	public void create() {
		setPreviousScreen(Screens.MAIN_MENU);

		this.optionsGroup = UIAssets.getOptionsGroup();

		super.root = new Group();
		root.setVisible(false);

		MyClickListener mListener = new MyClickListener();
		Button t2 = new TextButton("Elemento", skin);
		galeria = new TextButton("Galería", skin);
		galeria.addListener(mListener);
		Button t4 = new TextButton("Lanzar Juego", skin);
		escena = new TextButton("Escena", skin);
		escena.addListener(mListener);

		CircularGroup cg = new CircularGroup(halfstageh - 60, 135, 360, true,
				escena, t2, galeria, t4);
		cg.setX(halfstagew);
		cg.setY(halfstageh);

		Table t = new Table();
		t.setBounds(0, 0, stagew, UIAssets.TOOLBAR_HEIGHT * 2f);
		t.pad(30f);

		Label cbs = new Label("Tomar Foto", skin);
		cbs.setFontScale(1f);
		Image backImg = new Image(skin.getRegion("icon-blitz")); //foto
		final Button takePicture = new Button(skin, "navigationPanelRest");
		takePicture.add(backImg);
		takePicture.row();
		takePicture.add(cbs).expandX().fill();

		Label cbs1 = new Label("Aquí empieza el juego", skin);
		cbs1.setFontScale(1f);
		Image backImg1 = new Image(skin.getRegion("icon-blitz")); //scene
		final Button navigationPanelProject1 = new Button(skin,
				"navigationPanelProject");
		navigationPanelProject1.add(backImg1);
		navigationPanelProject1.row();
		navigationPanelProject1.add(cbs1).expandX().fill();

		Label cbs2 = new Label("Grabar Vídeo", skin);
		cbs2.setFontScale(1f);
		Image backImg2 = new Image(skin.getRegion("icon-blitz")); //video
		final Button recordVideo = new Button(skin, "navigationPanelRest");
		recordVideo.add(backImg2);
		recordVideo.row();
		recordVideo.add(cbs2).expandX().fill();

		ClickListener mTransitionLIstener = new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				final Screens next = getNextScreen(event.getListenerActor());
				if (next == null) {
					return;
				}
				exitAnimation(next);
			}

			private Screens getNextScreen(Actor target) {
				Screens next = null;
				if (target == takePicture) {
					next = Screens.PICTURE;
				} else if (target == recordVideo) {
					next = Screens.RECORDING;
				}
				return next;
			}
		};
		takePicture.addListener(mTransitionLIstener);
		recordVideo.addListener(mTransitionLIstener);

		t.add(takePicture).left();
		t.add(navigationPanelProject1).expandX();
		t.add(recordVideo).right();

		root.addActor(t);
		root.addActor(cg);

		stage.addActor(root);
	}

	private class MyClickListener extends ClickListener {

		@Override
		public void clicked(InputEvent event, float x, float y) {
			final Screens next = getNextScreen(event.getListenerActor());
			if (next == null) {
				return;
			}
			exitAnimation(next);
		}

		private Screens getNextScreen(Actor target) {
			Screens next = null;
			if (target == escena) {
				next = Screens.SCENE_EDITION;
			} else if (target == galeria) {
				next = Screens.GALLERY;
			}
			return next;
		}
	}

	@Override
	public void show() {
		super.show();
		root.setVisible(true);
		this.optionsGroup.setVisible(true);
	}

	@Override
	public void act(float delta) {
		stage.act(delta);
	}

	@Override
	public void draw() {
		stage.draw();
	}

	@Override
	public void hide() {
		root.setVisible(false);
		this.optionsGroup.setVisible(false);
	}

	@Override
	public void onBackKeyPressed() {
		Actor p = UIAssets.getOptionsGroup().findActor(
				UIAssets.OPTIONS_PANEL_NAME);
		if (p.isVisible()) {
			mockupController.hide((FocusListener) p);
		} else {
			super.onBackKeyPressed();
		}
	}
}
