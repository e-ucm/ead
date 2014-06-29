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
package es.eucm.ead.editor.actions.model.scene;

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.actions.ActionTest;
import es.eucm.ead.editor.control.actions.model.scene.RemoveChildrenFromEntity;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RemoveChildrenFromEntityTest extends ActionTest {

	@Test
	public void testRemoveChildrenFromEntity() {
		ModelEntity modelEntity = new ModelEntity();

		ModelEntity child1 = new ModelEntity();
		ModelEntity child2 = new ModelEntity();
		ModelEntity child3 = new ModelEntity();

		modelEntity.getChildren().add(child1);
		modelEntity.getChildren().add(child2);
		modelEntity.getChildren().add(child3);

		Array<ModelEntity> children = new Array<ModelEntity>();

		children.add(child1);
		children.add(child2);
		children.add(child3);

		controller
				.action(RemoveChildrenFromEntity.class, modelEntity, children);

		assertEquals(modelEntity.getChildren().size(), 0);

	}
}
