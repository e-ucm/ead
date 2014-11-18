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
package es.eucm.ead.editor.control.actions.editor;

import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.model.scene.SetSelectedScene;
import es.eucm.ead.editor.control.workers.Worker;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Addas an empty scene to the project
 */
public class AddScene extends EditorAction implements WorkerListener {

	@Override
	public void perform(Object... args) {
		controller.action(ExecuteWorker.class, AddSceneWorker.class, this);
	}

	@Override
	public void start() {
	}

	@Override
	public void result(Object... results) {
		controller.action(SetSelectedScene.class, results);
	}

	@Override
	public void done() {
	}

	@Override
	public void error(Throwable ex) {
	}

	@Override
	public void cancelled() {
	}

	public static class AddSceneWorker extends Worker {

		public AddSceneWorker() {
			super(true, false);
		}

		@Override
		protected void prepare() {
		}

		@Override
		protected boolean step() {
			ModelEntity scene = new ModelEntity();
			String sceneId = ProjectUtils.newSceneId(controller
					.getEditorGameAssets().projectFileHandle());
			controller.getEditorGameAssets().save(sceneId, scene);
			result(sceneId, scene);
			return true;
		}
	}
}
