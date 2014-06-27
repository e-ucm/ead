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

import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.control.actions.editor.NewGame;
import es.eucm.ead.editor.control.actions.model.AddScene;
import es.eucm.ead.editor.control.actions.model.ChangeNote;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.view.listeners.ChangeNoteFieldListener;
import es.eucm.ead.schema.editor.components.Note;

import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ModelEntityCategory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Cristian Rotaru on 20/03/14.
 */
public class ChangeSceneNoteTest extends ActionTest {

	private boolean somethingChanged;

	@Test
	public void testDescriptionAndTitle() {
		// Create a new project
		FileHandle projectFile = FileHandle
				.tempDirectory("eadtest-changescenenotes");
		model.putEntity(ModelEntityCategory.GAME.getCategoryPrefix(),
				new ModelEntity());
		controller.action(NewGame.class, projectFile.file().getAbsolutePath(),
				model.getGame());

		// Initialize the new value that must be used
		final String newTitle = "MY_NEW_TITLE";
		final String newDescription = "MY_NEW_DESCRIPTION";

		// Add a scene to be renamed
		controller.action(AddScene.class);

		// Get the sceneMetadata & the notes
		final ModelEntity editScene = model.getEditScene();
		final Note changingNotes = Model.getComponent(editScene, Note.class);

		// Add a listener that reacts to changes in scene data. This is
		// given as a parameter
		controller.getModel().addFieldListener(changingNotes,
				new ChangeNoteFieldListener() {

					@Override
					public void modelChanged(FieldEvent event) {
						ChangeSceneNoteTest.this.somethingChanged = true;
						super.modelChanged(event);
					}

					@Override
					public void descriptionChanged(FieldEvent event) {
						assertEquals(changingNotes.getDescription(),
								newDescription);
					}

					@Override
					public void titleChanged(FieldEvent event) {
						assertEquals(changingNotes.getTitle(), newTitle);
					}
				});

		this.somethingChanged = false;
		controller.action(ChangeNote.class, editScene, FieldName.NOTE_TITLE,
				newTitle);
		// Check something actually changed (if the model is not changed the
		// listener is not notified and then no assertions are made)
		assertTrue(this.somethingChanged);

		this.somethingChanged = false;
		controller.action(ChangeNote.class, editScene,
				FieldName.NOTE_DESCRIPTION, newDescription);
		assertTrue(this.somethingChanged);

		// Release resources
		projectFile.deleteDirectory();
	}
}
