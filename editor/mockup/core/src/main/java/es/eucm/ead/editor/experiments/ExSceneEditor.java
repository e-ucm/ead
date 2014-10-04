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
package es.eucm.ead.editor.experiments;

import com.badlogic.gdx.scenes.scene2d.Group;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.entities.ModelEntity;

public class ExSceneEditor extends Group {

	private Controller controller;

	private Model model;

	private EntitiesLoader entitiesLoader;

	private ExTouchListener listener;

	public ExSceneEditor(Controller controller) {
		this.controller = controller;
		this.entitiesLoader = controller.getEngine().getEntitiesLoader();
		this.model = controller.getModel();
		this.addListener(listener = new ExTouchListener(controller));
	}

	public void prepare() {
		ModelEntity sceneEntity = (ModelEntity) model.getSelection().getSingle(
				Selection.SCENE);
		if (sceneEntity != null) {
			EngineEntity scene = entitiesLoader.toEngineEntity(sceneEntity);
			controller.getEditorGameAssets().finishLoading();
			clearChildren();
			addActor(scene.getGroup());
		}
	}
}
