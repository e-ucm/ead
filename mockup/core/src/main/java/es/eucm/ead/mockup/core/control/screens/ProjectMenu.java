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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.mockup.core.control.listeners.FocusListener;
import es.eucm.ead.mockup.core.view.UIAssets;
import es.eucm.ead.mockup.core.view.ui.CircularGroup;

public class ProjectMenu extends AbstractScreen {

	private Group rest, optionsGroup;

	@Override
	public void create() {
		setPreviousScreen(Screens.MAIN_MENU);

		this.optionsGroup = UIAssets.getOptionsGroup();

		super.root = new Group();
		root.setVisible(false);

		rest = new Group();

		Button t1 = new TextButton("Crear", skin, "default-thin");
		Button t2 = new TextButton("Elemento", skin);
		Button t3 = new TextButton("Galería", skin);
		Button t4 = new TextButton("Lanzar Juego", skin);
		Button t5 = new TextButton("Escena", skin);
		t5.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				mockupController.changeTo(Screens.SCENE_EDITION);
			}
		});

		CircularGroup cg = new CircularGroup(halfstageh - 60, 90, 360, true,
				t1, t2, t3, t4, t5);
		cg.setX(halfstagew);
		cg.setY(halfstageh);

		rest.addActor(cg);

		root.addActor(rest);
		stage.addActor(root);
	}

	@Override
	public void show() {
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
