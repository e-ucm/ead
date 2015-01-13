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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;

import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.editor.OpenLastProject.ErrorCallback.Result;

/**
 * Open the last known opened game. Used when the application is initiated.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>Class</em> Class of the view to show if
 * there is no last known game.</dd>
 * <dd><strong>args[1]</strong> Optional <em>ErrorCallback</em> A callback
 * notified in case of error.</dd>
 * </dl>
 */
public class OpenLastProject extends EditorAction {

	public OpenLastProject() {
		super(true, true, new Class[] { Class.class }, new Class[] {
				Class.class, ErrorCallback.class });
	}

	@Override
	public void perform(Object... args) {
		Class elseView = (Class) args[0];
		ErrorCallback callback = args.length == 2 ? (ErrorCallback) args[1]
				: null;

		String projectToOpenPath = controller.getPreferences().getString(
				Preferences.LAST_OPENED_GAME);

		if (projectToOpenPath != null && !"".equals(projectToOpenPath)) {
			if (!controller.getApplicationAssets().absolute(projectToOpenPath)
					.exists()) {
				// the project was deleted
				handleError(callback, Result.PROJECT_NOT_FOUND, elseView);
			} else if (!controller.action(OpenProject.class, projectToOpenPath)) {
				// the project is probably corrupt; complain but
				// continue
				Gdx.app.error("OpenLastProject", "Error opening '"
						+ projectToOpenPath + "'; ignoring request");
				handleError(callback, Result.PROJECT_CORRUPTED, elseView);

			}
		} else {
			controller.action(ChangeView.class, elseView);
		}
	}

	private void handleError(ErrorCallback callback, Result result,
			Class elseView) {
		controller.action(ChangeView.class, elseView);
		if (callback != null) {
			callback.error(result);
		}
	}

	public interface ErrorCallback {

		public static enum Result {
			PROJECT_NOT_FOUND, PROJECT_CORRUPTED;
		}

		void error(Result result);
	}
}
