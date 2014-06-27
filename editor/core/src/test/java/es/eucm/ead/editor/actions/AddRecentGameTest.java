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

import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.actions.editor.AddRecentGame;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class AddRecentGameTest extends ActionTest {

	@Test
	public void testAdd() {
		controller.getPreferences().putString(Preferences.RECENT_GAMES, "");
		int maxRecents = AddRecentGame.MAX_RECENT_GAMES;
		File[] file = new File[maxRecents];
		for (int i = 0; i < maxRecents; i++) {
			file[i] = platform.createTempFile(true);
			controller.action(AddRecentGame.class, file[i].getAbsolutePath());
		}
		String preference = controller.getPreferences().getString(
				Preferences.RECENT_GAMES);
		String[] recents = preference.split(";");
		assertEquals(recents.length, maxRecents);
		for (int i = 0; i < maxRecents; i++) {
			assertEquals(
					controller.getEditorGameAssets().toCanonicalPath(
							file[maxRecents - 1 - i].getAbsolutePath()),
					recents[i]);
		}
		// Overflow the recent project list
		String newProject = controller.getEditorGameAssets().toCanonicalPath(
				platform.createTempFile(true).getAbsolutePath());
		controller.action(AddRecentGame.class, newProject);
		preference = controller.getPreferences().getString(
				Preferences.RECENT_GAMES);
		recents = preference.split(";");
		assertEquals(recents.length, maxRecents);
		assertEquals(newProject, recents[0]);
		for (int i = 1; i < maxRecents; i++) {
			assertEquals(
					controller.getEditorGameAssets().toCanonicalPath(
							file[maxRecents - i].getAbsolutePath()), recents[i]);
		}
	}
}
