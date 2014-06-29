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
import es.eucm.ead.editor.control.actions.editor.NewGame;
import es.eucm.ead.editor.control.actions.model.Rename;
import es.eucm.ead.editor.control.actions.model.RenameScene;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.entities.ModelEntity;

import es.eucm.ead.schemax.entities.ModelEntityCategory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Javier Torrente on 8/03/14.
 */
public class RenameTest extends ActionTest {
	private ModelEntity scene;
	private String oldName;
	private String newName;
	private boolean changed = false;

	@Test
	/**
	 * Tests whether a scene entity is properly renamed when
	 * the {@link Rename} action receives
	 * a {@link ModelEntity} object representing a scene that exists,
	 * plus a new Value that is well formed (not null String)
	 */
	public void testSceneRenamingBySceneObject() {
		testRenamingFullObject(true, "trying to rename a scene using Rename");
	}

	@Test
	/**
	 * Tests the {@link Rename} action when it receives a
	 * {@link ModelEntity} representing a scene that exists,
	 * plus a new Value that is either null or missing
	 */
	public void testSceneRenamingBySceneObjectNotValidNewValue() {
		testRenamingFullObject(false, null);
		testRenamingFullObject(false);
	}

	@Test
	/**
	 * Tests that when the {@link Rename} action gets zero
	 * arguments an exception is thrown
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
	 * Tests {@link RenameScene#findObjectById(String)}.
	 * When a {@link Rename} action gets a String as a first argument
	 * instead of an object, then it assumes that string is the id for
	 * the object that must be renamed, and tries to find it.
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
	 *            2 if the full {@link ModelEntity} scene should be passed, 1 if
	 *            only its id (e.g. "scene0") should be passed, 0 if nothing
	 *            must be passed.
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
		model.putEntity(ModelEntityCategory.GAME.getCategoryPrefix(),
				new ModelEntity());
		controller.action(NewGame.class, projectFile.file().getAbsolutePath(),
				model.getGame());

		// Initialize the new value that must be used
		String newNameToUse;
		boolean useNewName = false;
		if (args != null && args.length > 0) {
			newNameToUse = args[0];
			useNewName = true;
		} else
			newNameToUse = null;

		// Add a scene to be renamed
		controller.action(AddScene.class);

		// Get the just created scene:
		scene = model.getEditScene();
		// Add a listener that reacts to changes in scene data. This is
		// given as a parameter
		controller.getModel().addFieldListener(
				Model.getComponent(scene, Documentation.class),
				new RenameFieldListener() {
					@Override
					public void makeAssertions(FieldEvent event) {
						assertTrue(changeExpected);
						assertTrue((Model.getComponent(scene,
								Documentation.class).getName() == null && newName == null)
								|| !Model
										.getComponent(scene,
												Documentation.class).getName()
										.equals(oldName));
						assertEquals(
								Model.getComponent(scene, Documentation.class)
										.getName(), newName);
					}
				});

		// Execute the action. Needs to retrieve the old name first
		oldName = Model.getComponent(scene, Documentation.class).getName();

		if (useNewName) {
			newName = newNameToUse;
			// Rename the sceneMetadata, accessed by the object and providing
			// the newName (may be null)
			if (passFullObject == 2) {
				controller
						.action(RenameTestAction.class,
								Model.getComponent(scene, Documentation.class),
								newName);
			}
			// If passFullObject is 1, then pass the id of the scene and use
			// RenameScene id
			else if (passFullObject == 1) {
				controller.action(RenameScene.class,
						Model.getComponent(model.getGame(), EditState.class)
								.getEditScene(), newName);
			}
			// If passFullObject is 0, then pass not the scene
			else {
				controller.action(RenameTestAction.class, newName);
			}

		} else {
			newName = null;
			// Rename the sceneMetadata, accessed by the object, without any
			// newName.
			if (passFullObject == 2) {
				controller.action(RenameTestAction.class,
						Model.getComponent(scene, Documentation.class));
			}
			// If passFullObject is 1, then pass the id of the scene and use
			// RenameScene id
			else if (passFullObject == 1) {
				controller.action(RenameScene.class,
						Model.getComponent(model.getGame(), EditState.class)
								.getEditScene());
			}
			// If passFullObject is 0, then pass not the scene nor the newName
			else {
				controller.action(RenameTestAction.class);
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

	abstract class RenameFieldListener implements Model.FieldListener {

		@Override
		public boolean listenToField(FieldName fieldName) {
			return FieldName.NAME == fieldName;
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

	public static class RenameTestAction extends Rename {
		public RenameTestAction() {
			super();
		}
	}
}
