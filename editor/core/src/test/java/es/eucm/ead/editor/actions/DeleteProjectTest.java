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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.Test;

import es.eucm.ead.editor.control.actions.model.DeleteProject;
import es.eucm.ead.editor.control.actions.model.DeleteProject.DeleteProjectListener;
import es.eucm.ead.editor.control.actions.model.NewGame;
import es.eucm.ead.schema.editor.game.EditorGame;
import es.eucm.ead.schema.game.Game;

public class DeleteProjectTest extends EditorActionTest {

	@Override
	protected Class<?> getEditorAction() {
		return DeleteProject.class;
	}

	@Test
	public void test() {
		final File file = mockPlatform.createTempFile(true);
		EditorGame game = new EditorGame();
		game.setEditScene("scene0");
		mockController.action(NewGame.class, file.getAbsolutePath(), game,
				new Game());

		mockController.action(action, file.getAbsoluteFile(),
				new DeleteProjectListener() {
					@Override
					public void projectDeleted(boolean succeeded) {
						assertTrue(
								"Project deletion failed (listener invoked with succeeded=false)",
								succeeded);
						assertFalse(
								"Project deletion failed (project file still exists)",
								file.exists());
					}
				});
	}
}
