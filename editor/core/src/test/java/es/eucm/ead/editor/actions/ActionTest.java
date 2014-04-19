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
package es.eucm.ead.editor.actions;

import es.eucm.ead.editor.EditorTest;
import es.eucm.ead.editor.control.actions.editor.OpenGame;
import org.junit.Before;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Parent class for all tests related to {@link es.eucm.ead.editor.actions}. It
 * creates a mock controller and platform.
 * 
 * Provides convenient methods to test actions. Also, provides
 * {@link es.eucm.ead.editor.actions.ActionTest#openEmpty()} to load an empty
 * games for those actions that need it.
 * 
 */
public abstract class ActionTest extends EditorTest {

	/**
	 * Loads an empty game. The game has the following structure: - A game.json
	 * file with only one scene: scene0.json, which is empty, stored in subpath
	 * /scenes/scene0.json - A game.json file that only specifies the scene
	 * being edited (editScene): scene0
	 * 
	 * Subclasses of ActionTest may want to call this method the first in set up
	 */
	protected void openEmpty() {
		File emptyGame = null;
		URL url = ClassLoader.getSystemResource("projects/empty/game.json");
		try {
			emptyGame = new File(url.toURI()).getParentFile();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		mockController.action(OpenGame.class, emptyGame.getAbsolutePath());
		mockController.getEditorGameAssets().finishLoading();
	}

	@Before
	public void setUp() {
		mockController.getModel().reset();
	}

	protected void loadAllPendingAssets() {
		mockController.getEditorGameAssets().finishLoading();
	}

}
