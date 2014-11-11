/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2014 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          CL Profesor Jose Garcia Santesmases 9,
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
package es.eucm.ead.editor.view.builders.home;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.actions.editor.Exit;
import es.eucm.ead.editor.control.actions.editor.NewGame;
import es.eucm.ead.editor.control.actions.editor.OpenGame;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;

public class HomeView implements ViewBuilder, BackListener {

	private Actor view;

	private Controller controller;

	@Override
	public void initialize(Controller c) {
		this.controller = c;
		ApplicationAssets assets = controller.getApplicationAssets();
		Skin skin = assets.getSkin();

		LinearLayout projects = new LinearLayout(false);
		final FileHandle projectsFolder = assets.absolute(controller
				.getPlatform().getDefaultProjectsFolder());
		if (projectsFolder.exists()) {
			Array<String> projectPaths = ProjectUtils
					.findProjects(projectsFolder);
			for (final String path : projectPaths) {
				Label label = new Label(path, skin);
				label.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent event, float x, float y) {
						controller.action(OpenGame.class, path);
					}
				});
				projects.add(label);
			}
			Label add = new Label("Add project", skin);
			add.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					ModelEntity game = new ModelEntity();
					GameData gameData = Q.getComponent(game, GameData.class);
					gameData.setWidth(Gdx.graphics.getWidth());
					gameData.setHeight(Gdx.graphics.getHeight());
					controller.action(NewGame.class,
							projectsFolder
									.child("project" + TimeUtils.millis())
									.path(), game);
				}
			});
			projects.add(add);

		} else {
			projectsFolder.mkdirs();
		}
		projects.addSpace();
		view = projects;
	}

	@Override
	public Actor getView(Object... args) {
		return view;
	}

	@Override
	public void release(Controller controller) {

	}

	@Override
	public void onBackPressed() {
		controller.action(Exit.class, false);
	}
}
