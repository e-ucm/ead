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

import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.editor.asynk.ImportMockupProject;
import es.eucm.ead.editor.control.actions.editor.asynk.OpenMockupGameAsynk;
import es.eucm.ead.editor.platform.MockupPlatform;
import es.eucm.ead.editor.view.builders.gallery.ProjectsView;

/**
 * <p>
 * Tries to open the last opened game or
 * {@link MockupPlatform#getImportProjectPath()} if any.
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>None</strong></dd>
 * </dl>
 */
public class OpenApplication extends EditorAction {

	public OpenApplication() {
		super(true, false);
	}

	@Override
	public void perform(Object... args) {

		MockupPlatform platform = (MockupPlatform) controller.getPlatform();
		String importProjectPath = platform.getImportProjectPath();

		if (importProjectPath != null
				&& !importProjectPath.isEmpty()
				&& importProjectPath
						.endsWith(MockupController.EXPORT_EXTENSION)) {
			controller.action(ImportMockupProject.class);
		} else {

			String projectToOpenPath = controller.getPreferences().getString(
					Preferences.LAST_OPENED_GAME);

			if (projectToOpenPath != null && !"".equals(projectToOpenPath)) {
				controller.action(OpenMockupGameAsynk.class, projectToOpenPath);
			} else {
				controller.action(ChangeMockupView.class, ProjectsView.class);
			}
		}
	}
}
