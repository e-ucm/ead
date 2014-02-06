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

import com.badlogic.gdx.scenes.scene2d.Group;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.OpenGame;
import es.eucm.ead.editor.platform.MockPlatform;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockFiles;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class EditorActionTest {

	protected static Controller controller;

	protected static MockPlatform platform;

	protected String action;

	@BeforeClass
	public static void setUpClass() {
		MockApplication.initStatics();
		platform = new MockPlatform();
		controller = new Controller(platform, new MockFiles(), new Group());
	}

	public void openEmpty() {
		File emptyProject = null;
		URL url = ClassLoader.getSystemResource("projects/empty/project.json");
		try {
			emptyProject = new File(url.toURI()).getParentFile();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		controller.action(OpenGame.NAME, emptyProject.getAbsolutePath());
		controller.getProjectAssets().finishLoading();
	}

	@Before
	public void setUp() {
		controller.getModel().clear();
		action = getEditorAction();
	}

	public void loadAllPendingAssets() {
		controller.getProjectAssets().finishLoading();
	}

	protected abstract String getEditorAction();

	@AfterClass
	public static void tearDownClass() {
		platform.removeTempFiles();
	}
}
