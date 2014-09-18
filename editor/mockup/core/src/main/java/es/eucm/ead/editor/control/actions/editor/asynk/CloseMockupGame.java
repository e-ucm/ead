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
package es.eucm.ead.editor.control.actions.editor.asynk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;

import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.actions.editor.ChangeMockupView;
import es.eucm.ead.editor.control.actions.editor.ForceSave;
import es.eucm.ead.editor.view.builders.gallery.ProjectsView;

/**
 * Close the current edited game. It receives no arguments.
 */
public class CloseMockupGame extends BackgroundExecutorAction<Boolean> {

	public CloseMockupGame() {
		super(new Class[] { String.class }, new Class[] {});
	}

	@Override
	protected String getProcessingI18N() {
		return "closeGame";
	}

	@Override
	protected String getErrorProcessingI18N() {
		return "closeGame.error";
	}

	@Override
	protected Boolean doInBackground() {
		Group rootComponent = ((MockupController) controller)
				.getRootComponent();
		rootComponent.clearActions();
		controller.getPreferences().putString(Preferences.LAST_OPENED_GAME, "");
		controller.getPreferences().flush();
		try {
			controller.action(ForceSave.class);
		} catch (Exception ex) {
			Gdx.app.error("CloseMockupGame",
					"Error saving game before closing", ex);
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		controller.getEditorGameAssets().setLoadingPath("");
		controller.action(ChangeMockupView.class, ProjectsView.class);
		if (args.length > 0) {
			((MockupViews) controller.getViews()).getToasts().showNotification(
					args[0].toString(), ERROR_TIMEOUT);
		}
	}
}