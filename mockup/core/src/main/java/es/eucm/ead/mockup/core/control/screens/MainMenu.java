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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.mockup.core.control.ScreenController;
import es.eucm.ead.mockup.core.facade.IAnswerListener;
import es.eucm.ead.mockup.core.view.UIAssets;
import es.eucm.ead.mockup.core.view.ui.CircularGroup;

public class MainMenu extends AbstractScreen implements IAnswerListener {

	private boolean close;
	private Group optionsGroup, cg;
	private Color prevColor;
	private Button newProject, projectGallery;

	@Override
	public void create() {
		this.optionsGroup = UIAssets.getOptionsGroup();

		super.root = new Group();
		root.setVisible(false);

		MyClickListener mClickListener = new MyClickListener();
		newProject = new TextButton("Nuevo Proyecto", skin, "default-thin");
		newProject.addListener(mClickListener);
		projectGallery = new TextButton("Galería de Proyectos", skin);
		projectGallery.addListener(mClickListener);
		Button t3 = new TextButton("Grabar Video", skin);
		Button t4 = new TextButton("Tomar Foto", skin);

		cg = new CircularGroup(halfstageh, 135, 360, true, newProject,
				projectGallery, t3, t4);
		cg.setX(halfstagew);
		cg.setY(halfstageh);

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
			if (target == newProject) {
				next = Screens.PROJECT_MENU;
			} else if (target == projectGallery) {
				next = Screens.PROJECT_GALLERY;
			}
			return next;
		}
	}

	@Override
	public void show() {
		super.show();
		root.setVisible(true);
		optionsGroup.setVisible(true);
		prevColor = ScreenController.CLEAR_COLOR;
		ScreenController.CLEAR_COLOR = Color.ORANGE;
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
		ScreenController.CLEAR_COLOR = prevColor;
		root.setVisible(false);
		optionsGroup.setVisible(false);
	}

	@Override
	public void onBackKeyPressed() {
		if (!close) {
			close = true;
			mockupController.getResolver().showDecisionBox(
					IAnswerListener.QUESTION_EXIT, "Salir", "¿Estás seguro?",
					"Sí", "No", this); //TODO use I18N
		}
	}

	@Override
	public void onReceiveAnswer(int question, int answer) {
		if (question == IAnswerListener.QUESTION_EXIT) {
			if (close) {
				if (answer == IAnswerListener.QUESTION_EXIT_ANSWER_YES) {
					Gdx.app.exit();
				} else {
					close = false;
				}
			}
		}
	}

}
