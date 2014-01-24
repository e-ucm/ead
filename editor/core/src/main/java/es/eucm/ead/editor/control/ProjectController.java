/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.control;

/**
 * Interface for the controller of the system. This controller is in charge of
 * keeping the project in scope.
 */
public class ProjectController {

	/**
	 * Load a project from an URL
	 * 
	 * @param projectURL
	 *            The URL for the project
	 */
	void load(String projectURL) {

	}

	/**
	 * Load a project from an URL
	 * 
	 * @param sourceURL
	 *            The URL for the old project
	 * @param projectURL
	 *            The URL for the new project
	 */
	void doImport(String sourceURL, String projectURL) {

	}

	/**
	 * Save the current project
	 */
	void save() {
		throw new UnsupportedOperationException("not yet implemented");
	}

	/**
	 * Save the project in a new location
	 * 
	 * @param projectURL
	 *            The new URL location for the project
	 */
	void saveAs(String projectURL) {
		throw new UnsupportedOperationException("not yet implemented");
	}

	/**
	 * Create a new project
	 */
	void newProject() {
		throw new UnsupportedOperationException("not yet implemented");
	}

	/**
	 * Launches current game
	 */
	void doRun() {
		throw new UnsupportedOperationException("not yet implemented");
	}
}
