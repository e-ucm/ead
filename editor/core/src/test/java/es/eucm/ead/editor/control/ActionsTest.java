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
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.model.AddScene;
import es.eucm.ead.editor.control.actions.model.DeleteScene;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.editor.control.actions.model.NewGame;
import es.eucm.ead.schema.editor.game.EditorGame;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class ActionsTest extends EditorTest {

	private static Actions actions;

	private static int result;

	public static class MockEditorAction extends EditorAction {

		public MockEditorAction() {
			super(true);
		}

		@Override
		public boolean validate(Object... args) {
			return args.length == 1 && args[0] instanceof Integer;
		}

		@Override
		public void perform(Object... args) {
			result = (Integer) args[0];
		}
	}

	@BeforeClass
	public static void setUpClass() {
		EditorTest.setUpClass();
		actions = new Actions(mockController);
	}

	@Test
	public void testAction() {
		actions.perform(MockEditorAction.class, 50);
		assertEquals(result, 50);
	}

	@Test
	public void testInvalidArguments(){
		actions.perform(MockEditorAction.class, "ñor");
		assertEquals(result, 0);
	}

	@Test
	/**
	 * Tests the serialization of actions for bug reporting purposes.
	 * {@link Actions#getEditorActionsLog()}
	 */
	public void testActionSerialization() {
		File file = mockPlatform.createTempFile(true);
		mockController.action(NewGame.class, file.getAbsolutePath(),
				new EditorGame());
		mockController.action(AddScene.class);
		mockController.action(AddScene.class);
		mockController.action(AddScene.class);
		mockController.action(DeleteScene.class, "scene2");
		mockController.action(EditScene.class, "scene3");
		try {
			String json = mockController.getApplicationAssets().toJson(
					mockController.getActions().getModelActions()
							.getLoggedActions(Integer.MAX_VALUE));
			Gdx.app.debug(this.getClass().getCanonicalName(),
					"Stack of serialized actions: " + json);
			assertNotNull(json);
		} catch (SerializationException e) {
			fail("The stack of actions could not be serialized");
		}
	}

}
