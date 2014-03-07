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
package es.eucm.ead.editor.actions;

import es.eucm.ead.editor.EditorTest;
import es.eucm.ead.editor.control.actions.OpenGame;
import org.junit.Before;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Parent class for all tests related to {@link es.eucm.ead.editor.actions}. It
 * creates a mock controller and platform.
 * 
 * Any test classes extending
 * {@link es.eucm.ead.editor.actions.EditorActionTest} should:
 * 
 * 1) Implement {@link #getEditorAction()}. This method should return the name
 * of the action that is to be tested (e.g. AddSceneElement.NAME) 2) Create any
 * test methods as usual. Each test method may want to call {@link #openEmpty()}
 * , which loads an empty test game on the controller. (See
 * {@link es.eucm.ead.editor.actions.AddSceneElementTest} for an example)
 */
public abstract class EditorActionTest extends EditorTest {

	protected Class action;

	/**
	 * Loads an empty game project.
	 * 
	 * Subclasses of EditorActionTest may want to call this method the first
	 * thing on each @Test method.
	 */
	protected void openEmpty() {
		File emptyProject = null;
		URL url = ClassLoader.getSystemResource("projects/empty/project.json");
		try {
			emptyProject = new File(url.toURI()).getParentFile();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		mockController.action(OpenGame.class, emptyProject.getAbsolutePath());
		mockController.getProjectAssets().finishLoading();
	}

	@Before
	public void setUp() {
		mockController.getModel().clearListeners();
		action = getEditorAction();
	}

	public void loadAllPendingAssets() {
		mockController.getProjectAssets().finishLoading();
	}

	/**
	 * Subclasses of EditorActionTest should implement this method, which
	 * returns the name of the action to be tested (e.g. AddSceneElement.NAME)
	 * 
	 * @return The name of the action to be tested
	 */
	protected abstract Class getEditorAction();

}
