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
package es.eucm.ead.editor.view.builders.scene;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.editionview.GroupEditor;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;

public class SceneEditor extends AbstractWidget {

	private Controller controller;

	private Model model;

	private EntitiesLoader entitiesLoader;

	private GroupEditor groupEditor;

	public SceneEditor(Controller c) {
		this.controller = c;
		this.model = controller.getModel();
		this.entitiesLoader = controller.getEngine().getEntitiesLoader();
		addActor(groupEditor = new GroupEditor());
		groupEditor.setBackground(controller.getApplicationAssets().getSkin()
				.getDrawable("blank"));
	}

	public void prepare() {
		readSceneContext();
	}

	protected void readSceneContext() {
		ModelEntity sceneEntity = (ModelEntity) model.getSelection().getSingle(
				Selection.SCENE);
		if (sceneEntity != null) {
			EngineEntity scene = entitiesLoader.toEngineEntity(sceneEntity);

			/*
			 * All the assets must be loaded, so all actors has their correct
			 * width and height
			 */
			controller.getEditorGameAssets().finishLoading();
			GameData gameData = Q.getComponent(model.getGame(), GameData.class);

			scene.getGroup().setSize(gameData.getWidth(), gameData.getHeight());
			groupEditor.setRootGroup(scene.getGroup());
		} else {
			groupEditor.setRootGroup(null);
		}
	}

	@Override
	public void layout() {
		setBounds(groupEditor, 0, 0, getWidth(), getHeight());
	}
}
