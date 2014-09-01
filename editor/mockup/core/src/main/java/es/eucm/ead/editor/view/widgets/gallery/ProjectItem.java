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
package es.eucm.ead.editor.view.widgets.gallery;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.DeleteProject;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.builders.gallery.BaseGallery;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;

public class ProjectItem extends GalleryItem implements
		AssetLoadedCallback<Object> {

	private String projectPath;
	private ModelEntity project;
	private Controller controller;
	private Documentation documentation;
	private String alternativeName;

	public ProjectItem(Controller controller, String projectPath,
			BaseGallery gallery) {
		super(new Image(), "", 0f, 0f, true, controller.getApplicationAssets()
				.getSkin(), "project", false, gallery);
		this.controller = controller;
		alternativeName = gallery.getI18n().m("project") + " "
				+ projectPath.substring(projectPath.lastIndexOf("/") + 1);
		if (!projectPath.endsWith("/")) {
			projectPath += "/";
		}
		this.projectPath = projectPath;
		Assets assets = controller.getEditorGameAssets();
		assets.get(projectPath + GameStructure.GAME_FILE, Object.class, this,
				true);

	}

	@Override
	public void loaded(String fileName, Object asset) {
		if (asset == null) {
			return;
		}
		if (project == null) {
			// The game.json file was loaded
			if (asset instanceof ModelEntity) {
				this.project = (ModelEntity) asset;

				documentation = Q.getComponent(project, Documentation.class);
				((Label) name).setText(getDocumentationName());
				GameData gameData = Q.getComponent(project, GameData.class);
				String initialScene = gameData.getInitialScene();
				controller.getEditorGameAssets().get(
						projectPath + initialScene, Object.class, this);
			} else {
				clear();
				remove();
			}
		} else {
			// The initial scene was loaded
			// TODO load the initial scene thumbnail.
		}

	}

	@Override
	public void deleteItem() {
		controller.action(DeleteProject.class, projectPath);
	}

	@Override
	public String getName() {
		return getDocumentationName();
	}

	private String getDocumentationName() {
		if (documentation == null) {
			return alternativeName;
		}
		String name = documentation.getName();
		return name == null ? alternativeName
				: (name.isEmpty() ? alternativeName : name);
	}

	public String getProjectPath() {
		return projectPath;
	}
}
