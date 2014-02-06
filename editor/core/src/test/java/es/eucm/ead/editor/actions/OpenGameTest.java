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

import com.badlogic.gdx.utils.GdxRuntimeException;
import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.actions.OpenGame;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.LoadEvent;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OpenGameTest extends EditorActionTest implements
		ModelListener<LoadEvent> {

	private int count;

	private File emptyProject;

	@Override
	protected String getEditorAction() {
		return OpenGame.NAME;
	}

	@Before
	public void setUp() {
		super.setUp();
		controller.getModel().addLoadListener(this);
		count = 0;
		URL url = ClassLoader.getSystemResource("projects/empty/project.json");
		try {
			emptyProject = new File(url.toURI()).getParentFile();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testNoArgs() {
		platform.pushPath(emptyProject.getAbsolutePath());
		controller.action(action);
		loadAllPendingAssets();
		assertEquals(emptyProject.getAbsolutePath() + "/",
				controller.getLoadingPath());
		assertEquals(count, 1);
	}

	@Test
	public void testWithPath() {
		controller.action(action, emptyProject.getAbsolutePath());
		loadAllPendingAssets();
		assertEquals(emptyProject.getAbsolutePath() + "/",
				controller.getLoadingPath());
		assertEquals(count, 1);
	}

	@Test
	public void testWithInvalidPath() {
		try {
			controller.action(action, "ñor/ñor");
			fail("An exception should be thrown");
		} catch (EditorActionException e) {

		}
	}

	@Test
	public void testWithNullPath() {
		// When user cancels file chooser, a null is returned
		platform.pushPath(null);
		controller.action(action);
	}

	@Test
	public void testInvalidProject() {
		URL url = ClassLoader
				.getSystemResource("projects/invalid/project.json");
		File project = null;
		try {
			project = new File(url.toURI()).getParentFile();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		controller.action(action, project.getAbsolutePath());

		try {
			controller.getProjectAssets().finishLoading();
			fail("An exception must be thrown");
		} catch (GdxRuntimeException e) {

		}

	}

	@Override
	public void modelChanged(LoadEvent event) {
		Model model = event.getModel();
		assertEquals(model.getProject().getEditScene(), "scene0");
		assertEquals(model.getGame().getTitle(), "");
		count++;
	}
}
