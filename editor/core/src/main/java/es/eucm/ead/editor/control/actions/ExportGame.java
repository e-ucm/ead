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
package es.eucm.ead.editor.control.actions;

import es.eucm.ead.editor.platform.Platform;

/**
 * Simple action that exports the current model to the given destiny file
 * (args[0]), which is expected to be a string.
 * 
 * If args[0] is not present, this action asks for the destiny of the jar file.
 * If its present but its not a string, an exception is thrown.
 * 
 * Created by Javier Torrente on 20/03/14.
 */
public class ExportGame extends EditorAction {

	String jarPath = null;

	@Override
	public void perform(Object... args) {

		if (args != null && args.length > 0 && !(args[0] instanceof String)) {
			throw new EditorActionException(
					"Error in action "
							+ this.getClass().getCanonicalName()
							+ ": The action requires one String argument that points to the path were the game must be exported to");
		} else if (args != null && args.length > 0) {
			jarPath = (String) args[0];
		}

		if (jarPath == null) {
			controller.getPlatform().askForFile(
					new Platform.FileChooserListener() {
						@Override
						public void fileChosen(String path) {
							jarPath = path;
							doPerform();
						}
					});
		} else {
			doPerform();
		}

	}

	private void doPerform() {
		// TODO Solve callback issues
		controller.getEditorIO().exportAsJar(jarPath, controller.getModel(),
				null);
	}
}
