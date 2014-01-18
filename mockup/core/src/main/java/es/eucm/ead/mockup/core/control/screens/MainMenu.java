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
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.mockup.core.view.UIAssets;
import es.eucm.ead.mockup.core.view.ui.CircularGroup;

public class MainMenu extends AbstractScreen {

	private Group optionsGroup, cg;
	private Button newProject, projectGallery;
	private Array<Actor> mProjects;
	private Dialog exitDialog;

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

		cg = new CircularGroup(halfstageh, 135, 180, true, newProject,
				projectGallery);
		cg.setX(halfstagew);
		cg.setY(halfstageh);

		//Scan for projects aviable here...

		Table projectsTable = new Table();
		//projectsTable.debug();
		projectsTable.defaults().space(10);
		ScrollPane sp = new ScrollPane(projectsTable);
		sp.setBounds(stagew * .1f, 10, stagew * .8f, stageh * .2f);
		sp.setScrollingDisabled(false, true);
		Texture t = new Texture(Gdx.files.internal("mockup/temp/proyecto.png"));
		t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		final int PROJECTS = 8;
		mProjects = new Array<Actor>(false, PROJECTS);

		for (int i = 0; i < PROJECTS; ++i) {
			Image im = new Image(t);
			im.addListener(mClickListener);

			projectsTable.add(im);

			mProjects.add(im);
		}

		root.addActor(sp);
		root.addActor(cg);
		stage.addActor(root);

		exitDialog = new Dialog("¿Salir?", skin, "exit-dialog") {
			protected void result(Object object) {
				if ((Boolean) object) {
					Gdx.app.exit();
				}
			}
		}.text("¿Estás seguro?").button("Salir", true).button("¡Todavía no!",
				false).key(Keys.BACK, false).key(Keys.ENTER, true); // TODO use i18n
		exitDialog.setMovable(false);
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
			} else {
				for (Actor project : mProjects) {
					if (target == project) {
						next = Screens.PROJECT_MENU;
					}
				}
			}
			return next;
		}
	}

	@Override
	public void show() {
		super.show();
		root.setVisible(true);
		optionsGroup.setVisible(true);
	}

	@Override
	public void act(float delta) {
		stage.act(delta);
	}

	@Override
	public void draw() {
		stage.draw();
		//Table.drawDebug(stage);
	}

	@Override
	public void hide() {
		root.setVisible(false);
		optionsGroup.setVisible(false);
	}

	@Override
	public void onBackKeyPressed() {
		exitDialog.show(stage);

	}
}
