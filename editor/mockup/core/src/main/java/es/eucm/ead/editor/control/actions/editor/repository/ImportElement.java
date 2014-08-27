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
package es.eucm.ead.editor.control.actions.editor.repository;

import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.control.RepositoryManager.OnEntityImportedListener;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.background.BackgroundExecutor.BackgroundTaskListener;
import es.eucm.ead.editor.control.background.BackgroundTask;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * <p>
 * Tries to import a {@link ModelEntity} from the repository to the current
 * project.
 * </p>
 * <p>
 * The second argument must be the {@link ModelEntity}. The second argument must
 * be a {@link OnEntityImportedListener}.
 * </p>
 */
public class ImportElement extends EditorAction {

	private OnEntityImportedListener listener;
	private ModelEntity target;

	public ImportElement() {
		super(true, false, ModelEntity.class, OnEntityImportedListener.class);
	}

	@Override
	public void perform(final Object... args) {
		target = (ModelEntity) args[0];
		listener = ((OnEntityImportedListener) args[1]);
		controller.getBackgroundExecutor().submit(importElemTask,
				importListener);
	}

	private final BackgroundTask<ModelEntity> importElemTask = new BackgroundTask<ModelEntity>() {

		@Override
		public ModelEntity call() throws Exception {
			return ((MockupController) controller).getRepositoryManager()
					.importElement(target, controller);
		}
	};

	private final BackgroundTaskListener<ModelEntity> importListener = new BackgroundTaskListener<ModelEntity>() {

		@Override
		public void completionPercentage(float percentage) {

		}

		@Override
		public void done(BackgroundExecutor backgroundExecutor,
				ModelEntity result) {
			listener.entityImported(result, controller);
		}

		@Override
		public void error(Throwable e) {
			listener.entityImported(null, controller);
		}

	};
}
