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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.List;

import org.junit.Test;

import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.actions.model.ReplaceEntity;
import es.eucm.ead.schema.entities.ModelEntity;

public class ReplaceEntityTest extends ActionTest {

	@Test
	public void testAddSceneElement() throws URISyntaxException {
		openEmpty();

		ModelEntity replacedEntity = new ModelEntity();
		controller.action(AddSceneElement.class, replacedEntity);

		ModelEntity newEntity = new ModelEntity();
		controller.action(ReplaceEntity.class, replacedEntity, newEntity);

		ModelEntity scene = controller.getModel().getEditScene();
		List<ModelEntity> parentChildren = scene.getChildren();

		assertFalse("Failed to remove the replaced entity.",
				parentChildren.contains(replacedEntity));
		assertTrue("Failed to add the new entity",
				parentChildren.contains(newEntity));

		clearEmpty();
	}
}
