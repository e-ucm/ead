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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.mockup.core.control.listeners.FocusListener;
import es.eucm.ead.mockup.core.view.UIAssets;
import es.eucm.ead.mockup.core.view.ui.CircularGroup;
import es.eucm.ead.mockup.core.view.ui.MenuButton;

public class ProjectMenu extends AbstractScreen {

	private final float PANEL_MENU_BUTTON_WIDTH_HEIGHT = stageh * .2f;
	private Group optionsGroup;

	@Override
	public void create() {
		setPreviousScreen(Screens.MAIN_MENU);

		this.optionsGroup = UIAssets.getOptionsGroup();

		super.root = new Group();
		root.setVisible(false);
		
		final Button scene, element, gallery, play, 
				takePictureButton, initialSceneButton, recordVideoButton;		
		scene = new MenuButton("Escena", skin, "ic_editstage",
				PANEL_MENU_BUTTON_WIDTH_HEIGHT, PANEL_MENU_BUTTON_WIDTH_HEIGHT);//TODO use i18n in this class
		element = new MenuButton("Elemento", skin, "ic_editelement",
				PANEL_MENU_BUTTON_WIDTH_HEIGHT, PANEL_MENU_BUTTON_WIDTH_HEIGHT);
		gallery = new MenuButton("Galeria", skin, "ic_galery",
				PANEL_MENU_BUTTON_WIDTH_HEIGHT, PANEL_MENU_BUTTON_WIDTH_HEIGHT);
		play = new MenuButton("Jugar", skin, "ic_playgame",
				PANEL_MENU_BUTTON_WIDTH_HEIGHT, PANEL_MENU_BUTTON_WIDTH_HEIGHT);

		CircularGroup cg = new CircularGroup(halfstageh - 80, 135, 360, true,
				scene, element, gallery, play);
		cg.setX(halfstagew);
		cg.setY(halfstageh * 1.1f);

		Table bottomButtonsTable = new Table();
		bottomButtonsTable.setBounds(0, 0, stagew, UIAssets.TOOLBAR_HEIGHT * 2f);
		
		takePictureButton = new MenuButton("Tomar Foto", skin, "ic_photocamera");
		initialSceneButton = new MenuButton("Aquí empieza el juego", skin, "icon-blitz");
		recordVideoButton = new MenuButton("Grabar Vídeo", skin, "ic_videocamera");
		
		ClickListener mTransitionListener = new ClickListener() {

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
				if (target == scene) {
					next = Screens.SCENE_EDITION;
				} else if (target == gallery) {
					next = Screens.GALLERY;
				} else if (target == takePictureButton) {
					next = Screens.PICTURE;
				} else if (target == recordVideoButton) {
					next = Screens.RECORDING;
				}
				return next;
			}
		};
		scene.addListener(mTransitionListener);
		element.addListener(mTransitionListener);
		gallery.addListener(mTransitionListener);
		play.addListener(mTransitionListener);
		takePictureButton.addListener(mTransitionListener);
		recordVideoButton.addListener(mTransitionListener);

		bottomButtonsTable.add(takePictureButton).fill().left();
		bottomButtonsTable.add(initialSceneButton).height(bottomButtonsTable.getHeight()).expandX();
		bottomButtonsTable.add(recordVideoButton).fill().right();

		root.addActor(bottomButtonsTable);
		root.addActor(cg);

		stage.addActor(root);
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
