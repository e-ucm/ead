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
package es.eucm.ead.editor.commands;

import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FieldCommandTest extends CommandTest {

	@Test
	public void testNormal() {
		ModelEntity game = new ModelEntity();
		GameData gameData = Model.getComponent(game, GameData.class);
		gameData.setInitialScene("old");

		FieldCommand command = new FieldCommand(gameData,
				FieldName.INITIAL_SCENE, "new", false);

		FieldEvent event = (FieldEvent) command.doCommand();
		assertEquals(gameData.getInitialScene(), "new");
		// Test also event produced is correct
		assertEquals(event.getTarget(), gameData);
		assertEquals(event.getValue(), "new");
		assertTrue(event.getField().equals(FieldName.INITIAL_SCENE));
		command.undoCommand();
		assertEquals(gameData.getInitialScene(), "old");
		command.doCommand();
		assertEquals(gameData.getInitialScene(), "new");
	}

	@Test
	public void testFieldFromSuperClass() {
		ModelEntityChild childEntity = new ModelEntityChild();
		childEntity.setX(50);
		childEntity.setTestField("old test field");

		FieldCommand command = new FieldCommand(childEntity, FieldName.X, 100,
				false);

		command.doCommand();
		assertEquals((int) childEntity.getX(), 100);
		command.undoCommand();
		assertEquals((int) childEntity.getX(), 50);
		command.doCommand();
		assertEquals((int) childEntity.getX(), 100);
	}

	@Test
	public void testCombine() {
		ModelEntity game = new ModelEntity();
		GameData gameData = Model.getComponent(game, GameData.class);
		gameData.setInitialScene("old");

		FieldCommand command = new FieldCommand(gameData,
				FieldName.INITIAL_SCENE, "n", true);
		FieldCommand command2 = new FieldCommand(gameData,
				FieldName.INITIAL_SCENE, "ne", true);

		command.doCommand();
		assertEquals(gameData.getInitialScene(), "n");
		command2.doCommand();
		assertEquals(gameData.getInitialScene(), "ne");
		command.combine(command2);
		command.undoCommand();
		assertEquals(gameData.getInitialScene(), "old");
	}

	@Test
	public void testInvalidArguments() {
		ModelEntity game = new ModelEntity();
		GameData gameData = Model.getComponent(game, GameData.class);
		gameData.setInitialScene("old");

		FieldCommand command = new FieldCommand(gameData,
				FieldName.INITIAL_SCENE, 100, false);
		assertNull(
				"Commands should return null if the doCommand operation fails",
				command.doCommand());
		assertEquals("old", gameData.getInitialScene());

		FieldCommand command2 = new FieldCommand(gameData,
				FieldName.INITIAL_SCENE, null, false);
		command2.doCommand();
		assertEquals(null, gameData.getInitialScene());
		command2.undoCommand();
		assertEquals("old", gameData.getInitialScene());

		boolean expectedException = false;
		try {
			new FieldCommand(null, FieldName.INITIAL_SCENE, "new", false);
		} catch (NullPointerException e) {
			expectedException = true;
		}
		assertTrue(expectedException);
		assertEquals("old", gameData.getInitialScene());
	}

	/**
	 * Simple class used for {@link #testFieldFromSuperClass()}
	 */
	public static class ModelEntityChild extends ModelEntity {

		private String testField;

		public ModelEntityChild() {
			super();
		}

		public String getTestField() {
			return testField;
		}

		public void setTestField(String testField) {
			this.testField = testField;
		}
	}
}
