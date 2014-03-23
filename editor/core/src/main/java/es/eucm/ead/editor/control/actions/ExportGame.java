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

import es.eucm.ead.editor.control.EditorIO;
import es.eucm.ead.editor.platform.Platform;

/**
 * Simple action that exports the current model to the given destiny file
 * (args[0]), which is expected to be a string.
 * 
 * If args[0] is not present, this action asks for the destiny of the jar file.
 * If its present but its not a string, an exception is thrown.
 * 
 * This action also admits two additional arguments, which are optional:
 * 
 * args[1]: A String path pointing to the location of the engine library path
 * used for exportation. This is typically a JAR file with dependencies produced
 * by Maven. If this argument is not provided or if it is null, the action tries
 * to get it from the controller, since this path should be specified in the
 * release.json file. If this argument is present but is not a String, an
 * exception is thrown
 * 
 * args[2]: A callback, defined as an
 * {@link es.eucm.ead.editor.control.EditorIO.ExportCallback} object. This
 * callback gets updates on the exportation process. If this argument is
 * provided, but it is not an instance of
 * {@link es.eucm.ead.editor.control.EditorIO.ExportCallback}, an exception is
 * thrown.
 * 
 * Created by Javier Torrente on 20/03/14.
 */
public class ExportGame extends EditorAction {

	private String jarPath = null;
	private String engineLibraryPath = null;
	private EditorIO.ExportCallback callback = null;

	@Override
	// Destiny, engine jar file, callback
	public void perform(Object... args) {
		// First argument, if present, should be the destiny path
		if (args != null && args.length > 0 && args[0] != null
				&& !(args[0] instanceof String)) {
			throw new EditorActionException(
					"Error in action ExportGame"
							+ ": The action requires one String argument that points to the path were the game must be exported to");
		} else if (args != null && args.length > 0) {
			jarPath = (String) args[0];
		}

		// Second argument, if present, should be the path of the engine library
		// that must be used during the exportation
		if (args != null && args.length > 1 && args[1] != null
				&& !(args[1] instanceof String)) {
			throw new EditorActionException(
					"Error in action ExportGame"
							+ ": The second argument of this action, if present, should be a path pointing to the version of the engine library to be used for exportation.");
		} else if (args != null && args.length > 1) {
			engineLibraryPath = (String) args[1];
		}

		// Third argument, if present, should be a callback that gets updates on
		// the exportation process
		if (args != null && args.length > 2 && args[2] != null
				&& !(args[2] instanceof EditorIO.ExportCallback)) {
			throw new EditorActionException(
					"Error in action ExportGame"
							+ ": The third argument of this action, if present, should be of type EditorIO.ExportCallback");
		} else if (args != null && args.length > 2) {
			callback = (EditorIO.ExportCallback) args[2];
		}

		// Retrieve those arguments that were not specified
		// If the destiny path is not specified, ask the user.
		if (jarPath == null) {
			controller.getPlatform().askForFile(
					new Platform.FileChooserListener() {
						@Override
						public void fileChosen(String path) {
							jarPath = path;
							doPerform();
						}
					});
		}
		// If the engine library path is not specified, retrieve it from the
		// controller (should be specified in the
		// release.json file)
		if (engineLibraryPath == null) {
			engineLibraryPath = controller.getEngineLibPath();
		}

		doPerform();

	}

	private void doPerform() {
		controller.getEditorIO().exportAsJar(jarPath, engineLibraryPath,
				controller.getModel(), callback);
	}
}
