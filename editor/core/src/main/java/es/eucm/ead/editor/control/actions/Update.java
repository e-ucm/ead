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

/**
 * This action launches the system's browser and shows the URL given as a
 * parameter, which is expected to hold the installer required for this OS and
 * release type
 * 
 * Arguments expected: Expects one argument (args[0]) with the given URL to
 * open. This argument should be a String. Otherwise an exception is thrown
 * 
 * Created by Javier Torrente on 17/03/14.
 */
public class Update extends EditorAction {
	@Override
	public void perform(Object... args) {
		if (args.length == 0) {
			throw new EditorActionException("Error in "
					+ this.getClass().getCanonicalName()
					+ ": At least one String argument is needed");
		}

		if (!(args[0] instanceof String)) {
			throw new EditorActionException(
					"Error in "
							+ this.getClass().getCanonicalName()
							+ ": First argument should be a String pointing to a valid URL");
		}

		String urlToOpen = (String) args[0];

		controller.getPlatform().browseURL(urlToOpen);
	}
}
