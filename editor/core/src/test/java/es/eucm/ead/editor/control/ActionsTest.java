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
package es.eucm.ead.editor.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.SerializationException;
import es.eucm.ead.editor.EditorTest;
import es.eucm.ead.editor.control.actions.ArgumentsValidationException;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.editor.NewGame;
import es.eucm.ead.editor.control.actions.model.AddScene;
import es.eucm.ead.editor.control.actions.model.DeleteScene;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ActionsTest extends EditorTest {

	private Actions actions;

	private static int resultPerformMethod;

	public static class MockEditorAction extends EditorAction {

		public MockEditorAction() {
			super(true, false, Number.class);
		}

		@Override
		public void perform(Object... args) {
			resultPerformMethod = ((Number) args[0]).intValue();
		}

	}

	/**
	 * Action with no parameters
	 * 
	 */
	public static class EmptyValidArguments extends EditorAction {

		public EmptyValidArguments() {
			super(true, false);
		}

		@Override
		public void perform(Object... args) {
		}

	}

	/**
	 * Action with one parameter: an array list of a String and Boolean
	 */
	public static class TwoArgumentsAction extends EditorAction {

		public TwoArgumentsAction() {
			super(true, false, String.class, Boolean.class);
		}

		@Override
		public void perform(Object... args) {

		}
	}

	/**
	 * An action with primitive types
	 */
	public static class MultipleValidArguments extends EditorAction {

		public MultipleValidArguments() {
			super(true, false, new Class[][] { { String.class, Boolean.class },
					{}, { Integer.class } });
		}

		@Override
		public void perform(Object... args) {

		}
	}

	@Before
	public void setUp() {
		super.setUp();
		actions = new Actions(controller);
	}

	@Test
	public void testValidationOfSingleAndMultipleArguments() {
		try {
			actions.perform(EmptyValidArguments.class);

			actions.perform(TwoArgumentsAction.class, "", true);

			// the three different possibilities for MultipleValidArguments
			actions.perform(MultipleValidArguments.class, "", true);
			actions.perform(MultipleValidArguments.class);
			actions.perform(MultipleValidArguments.class, 50);

		} catch (ArgumentsValidationException e) {
			fail("Error testing validation of the list of arguments "
					+ e.getMessage());
		}

	}

	@Test
	public void testAction() {
		try {
			actions.perform(MockEditorAction.class, 50);
			assertEquals(resultPerformMethod, 50);
		} catch (ArgumentsValidationException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testInvalidArguments() {
		try {
			actions.perform(MockEditorAction.class, "ñor");
			fail("Exception should be launched");
		} catch (ArgumentsValidationException e) {

		}
	}

	@Test
	/**
	 * Tests the serialization of actions for bug reporting purposes.
	 * {@link Actions#getEditorActionsLog()}
	 */
	public void testActionSerialization() {
		File file = platform.createTempFile(true);
		controller.action(NewGame.class, file.getAbsolutePath(),
				new ModelEntity());
		controller.action(AddScene.class);
		controller.action(AddScene.class);
		controller.action(AddScene.class);
		controller.action(DeleteScene.class, "scene2");
		controller.action(EditScene.class, "scene3");
		try {
			String json = controller.getApplicationAssets()
					.toJson(controller.getActions().getLoggedActions(
							Integer.MAX_VALUE));
			Gdx.app.debug(this.getClass().getCanonicalName(),
					"Stack of serialized actions: " + json);
			assertNotNull(json);
		} catch (SerializationException e) {
			fail("The stack of actions could not be serialized");
		}
	}

}
