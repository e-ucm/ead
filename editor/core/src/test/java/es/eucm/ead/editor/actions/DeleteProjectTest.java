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

import es.eucm.ead.editor.control.actions.editor.DeleteProject;
import es.eucm.ead.editor.control.actions.editor.DeleteProject.DeleteProjectListener;
import es.eucm.ead.editor.control.actions.editor.NewGame;

import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeleteProjectTest extends ActionTest {

	private boolean success = false;

	@Test
	public void test() {
		final File file = platform.createTempFile(true);
		ModelEntity game = new ModelEntity();
		EditState editState = new EditState();
		editState.setEditScene("scene0");
		game.getComponents().add(editState);
		controller.action(NewGame.class, file.getAbsolutePath(), game,
				new ModelEntity());

		controller.action(DeleteProject.class, file.getAbsolutePath(),
				new DeleteProjectListener() {
					@Override
					public void projectDeleted(boolean succeeded) {
						success = succeeded;
					}
				});

		assertTrue(
				"Project deletion failed (listener invoked with succeeded=false)",
				success);
		assertFalse("Project deletion failed (project file still exists)",
				file.exists());
	}
}
