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

import es.eucm.ead.editor.control.commands.RootEntityCommand.AddRootEntityCommand;
import es.eucm.ead.editor.control.commands.RootEntityCommand.RemoveRootEntityCommand;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ModelEntityCategory;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Created by angel on 22/05/14.
 */
public class RootEntityCommandTest extends CommandTest {

	@Test
	public void testAddEntity() {
		ModelEntity modelEntity = new ModelEntity();
		AddRootEntityCommand addEntityCommand = new AddRootEntityCommand(model,
				"scene", modelEntity, ModelEntityCategory.SCENE);
		addEntityCommand.doCommand();
		assertSame(model.getEntity("scene", ModelEntityCategory.SCENE),
				modelEntity);
		addEntityCommand.undoCommand();
		assertNull(model.getEntity("scene", ModelEntityCategory.SCENE));
	}

	@Test
	public void testRemoveEntity() {
		ModelEntity modelEntity = new ModelEntity();

		model.putEntity("scene", ModelEntityCategory.SCENE, modelEntity);

		RemoveRootEntityCommand removeEntityCommand = new RemoveRootEntityCommand(
				model, "scene", modelEntity, ModelEntityCategory.SCENE);
		removeEntityCommand.doCommand();
		assertNull(model.getEntity("scene", ModelEntityCategory.SCENE));
		removeEntityCommand.undoCommand();
		assertSame(model.getEntity("scene", ModelEntityCategory.SCENE),
				modelEntity);
	}

}
