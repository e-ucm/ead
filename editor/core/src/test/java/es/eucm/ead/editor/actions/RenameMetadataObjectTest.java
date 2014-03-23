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
import es.eucm.ead.editor.control.actions.model.AddScene;
import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.actions.model.NewGame;
import es.eucm.ead.editor.control.actions.model.RenameMetadataObject;
import es.eucm.ead.editor.control.actions.model.RenameScene;
import es.eucm.ead.editor.model.FieldNames;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.schema.editor.actors.EditorScene;
import es.eucm.ead.schema.editor.game.EditorGame;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Javier Torrente on 8/03/14.
 */
public class RenameMetadataObjectTest extends EditorActionTest {
	private EditorScene scene;
	private String oldName;
	private String newName;
	private boolean changed = false;

	@Test
	/**
	 * Tests whether a scene text attribute is properly renamed when the {@link es.eucm.ead.editor.control.actions.model.RenameMetadataObject} action receives a {@link es.eucm.ead.schema.actors.Scene} object that exists, plus a new Value that is well formed (not null String)
	 */
	public void testSceneRenamingBySceneObject() {
		testRenamingFullObject(true,
				"trying to rename a scene using RenameMetadataObject");
	}

	@Test
	/**
	 * Tests the {@link es.eucm.ead.editor.control.actions.model.RenameMetadataObject} action when it receives a {@link es.eucm.ead.schema.actors.Scene} object that exists, plus a new Value that is either null or missing
	 */
	public void testSceneRenamingBySceneObjectNotValidNewValue() {
		testRenamingFullObject(false, null);
		testRenamingFullObject(false);
	}

	@Test
	/**
	 * Tests that when the {@link es.eucm.ead.editor.control.actions.model.RenameMetadataObject} action gets zero arguments an exception is thrown
	 */
	public void testZeroArguments() {
		try {
			testRenaming(0, false);
		} catch (EditorActionException e) {
			// An exception must be thrown
			assertTrue(true);
		}
	}

	@Test
	/**
	 * Tests {@link es.eucm.ead.editor.control.actions.model.RenameScene#findObjectById(String)}. When a {@link es.eucm.ead.editor.control.actions.model.RenameMetadataObject} action gets a String as a first argument instead of an object, then it assumes that string is the id for the object that must be renamed, and tries to find it.
	 */
	public void testSceneRenamingBySceneId() {
		testRenaming(1, true, "new name");
	}

	protected void testRenamingFullObject(boolean changeExpected,
			String... args) {
		testRenaming(2, changeExpected, args);
	}

	/**
	 * 1) Creates a new project 2) Adds a new scene to the project. This scene
	 * will be renamed 3) Renames the scene in the following way: If
	 * {@code passFullObject} is 2, the recently created sceneMetadata is passed
	 * to the Rename action. If it is 1, only the string id is passed (e.g.
	 * "scene0"). If it is 0, nothing is passed If {@code args} has a String
	 * object, this will be the new name for the sceneMetadata object. If
	 * args[0] is not present or it is null, then the Rename action does not get
	 * the param. 4) The correct behaviour is tested by doing the following:
	 * Checking if the model is actually modified when it is supposed to be
	 * modified (i.e. if {@code changeExpected} is true) Checking if the model
	 * is modified in the appropriate way (newValue!=oldValue)
	 * 
	 * @param passFullObject
	 *            2 if the full {@link es.eucm.ead.schema.actors.Scene} should
	 *            be passed, 1 if only its id (e.g. "scene0") should be passed,
	 *            0 if nothing must be passed.
	 * @param changeExpected
	 *            True if this rename action should actually modify the model,
	 *            false otherwise
	 * @param args
	 *            The new value to be used. Can be null and also can be {null}
	 *            (length==1)
	 */
	protected void testRenaming(final int passFullObject,
			final boolean changeExpected, String... args) {
		// Restore default values for test attributes. This is necessary since
		// previous test methods could have modified these attributes
		reset();

		// Create a new project
		FileHandle projectFile = FileHandle
				.tempDirectory("eadtest-renameattributeobject");
		mockModel.setGame(new EditorGame());
		// FIXME NewGame should create GameMetaData and Game instead of
		// receiving them as arguments!!!
		mockController.action(NewGame.class, projectFile.file()
				.getAbsolutePath(), mockModel.getGame(), mockModel.getGame());

		// Initialize the new value that must be used
		String newNameToUse;
		boolean useNewName = false;
		if (args != null && args.length > 0) {
			newNameToUse = args[0];
			useNewName = true;
		} else
			newNameToUse = null;

		// Get scenes
		Map<String, EditorScene> scenes = mockModel.getScenes();
		// scenes.clear();

		// Add a scene to be renamed
		mockController.action(AddScene.class);

		// Get the sceneMetadata:
		scene = mockModel.getEditScene();
		// Add a listener that reacts to changes in scene data. This is
		// given as a parameter
		mockController.getModel().addFieldListener(scene,
				new RenameFieldListener() {
					@Override
					public void makeAssertions(FieldEvent event) {
						assertTrue(changeExpected);
						assertTrue((scene.getName() == null && newName == null)
								|| !scene.getName().equals(oldName));
						assertEquals(scene.getName(), newName);
					}
				});

		// Execute the action. Needs to retrieve the old name first
		oldName = scene.getName();

		if (useNewName) {
			newName = newNameToUse;
			// Rename the sceneMetadata, accessed by the object and providing
			// the newName (may be null)
			if (passFullObject == 2) {
				mockController.action(action, scene, newName);
			}
			// If passFullObject is 1, then pass the id of the scene and use
			// RenameScene id
			else if (passFullObject == 1) {
				mockController.action(RenameScene.class, mockModel.getGame()
						.getEditScene(), newName);
			}
			// If passFullObject is 0, then pass not the scene
			else {
				mockController.action(action, newName);
			}

		} else {
			newName = null;
			// Rename the sceneMetadata, accessed by the object, without any
			// newName.
			if (passFullObject == 2) {
				mockController.action(action, scene);
			}
			// If passFullObject is 1, then pass the id of the scene and use
			// RenameScene id
			else if (passFullObject == 1) {
				mockController.action(RenameScene.class, mockModel.getGame()
						.getEditScene());
			}
			// If passFullObject is 0, then pass not the scene nor the newName
			else {
				mockController.action(action);
			}
		}

		// Check something actually changed (if the model is not changed the
		// listener is not notified and then no assertions are made)
		assertTrue(changed == changeExpected);

		// Release resources
		projectFile.deleteDirectory();
	}

	// ///////////////////////////////////////////////////////////
	// Private methods. Nothing especial.
	// //////////////////////////////////////////////////////////

	/**
	 * Resets all test attributes. This method should be called the first thing
	 * on each test method.
	 */
	private void reset() {
		this.newName = null;
		this.oldName = null;
		this.changed = false;
		this.scene = null;
	}

	@Override
	protected Class getEditorAction() {
		return RenameMetadataObject.class;
	}

	abstract class RenameFieldListener implements Model.FieldListener {

		@Override
		public boolean listenToField(FieldNames fieldName) {
			return FieldNames.NAME == fieldName;
		}

		@Override
		public void modelChanged(FieldEvent event) {
			changed = true;
			makeAssertions(event);
		}

		/**
		 * This is the method that tests if the
		 * 
		 * @param event
		 */
		public abstract void makeAssertions(FieldEvent event);
	}
}
