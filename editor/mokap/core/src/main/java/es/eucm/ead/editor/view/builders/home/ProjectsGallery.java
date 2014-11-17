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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.NewGame;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.Gallery;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;

public class ProjectsGallery extends AbstractWidget {

	private Controller controller;

	private Gallery gallery;

	private Button addProject;

	private ProjectsListener projectsListener;

	public ProjectsGallery(Controller c) {
		gallery = new Gallery(Gdx.graphics.getHeight() / 2.15f, 3);
		this.controller = c;
		addActor(gallery);
		addActor(addProject = WidgetBuilder.button(SkinConstants.STYLE_ADD));
		addProject.addListener(new ClickListener() {

			FileHandle projectsFolder = controller
					.getApplicationAssets()
					.absolute(
							controller.getPlatform().getDefaultProjectsFolder());

			@Override
			public void clicked(InputEvent event, float x, float y) {
				ModelEntity game = new ModelEntity();
				GameData gameData = Q.getComponent(game, GameData.class);
				gameData.setWidth(Gdx.graphics.getWidth());
				gameData.setHeight(Gdx.graphics.getHeight());
				controller.action(NewGame.class,
						projectsFolder.child("project" + TimeUtils.millis())
								.path(), game);
			}
		});
		projectsListener = new ProjectsListener(controller, gallery);
	}

	public void prepare() {
		projectsListener.prepare();
	}

	@Override
	public void layout() {
		setBounds(gallery, 0, 0, getWidth(), getHeight());
		float width = getPrefWidth(addProject);
		setBounds(addProject,
				getWidth() - width - WidgetBuilder.dpToPixels(16),
				WidgetBuilder.dpToPixels(16), width, getPrefHeight(addProject));
	}
}
