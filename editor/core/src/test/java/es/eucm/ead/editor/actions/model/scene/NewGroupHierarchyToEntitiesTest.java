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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.actions.ActionTest;
import es.eucm.ead.editor.control.actions.model.scene.NewGroupHierarchyToEntities;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class NewGroupHierarchyToEntitiesTest extends ActionTest {

	@Test
	public void testCreateGroup() {
		EngineEntity parentEntity = createEntity();

		EngineEntity child1 = createEntity();
		EngineEntity child2 = createEntity();

		// Create structure
		parentEntity.getModelEntity().getChildren()
				.add(child1.getModelEntity());
		parentEntity.getModelEntity().getChildren()
				.add(child2.getModelEntity());

		Array<Actor> grouped = new Array<Actor>();
		grouped.add(child1.getGroup());
		grouped.add(child2.getGroup());

		Group newGroup = new Group();

		controller.action(NewGroupHierarchyToEntities.class,
				parentEntity.getGroup(), newGroup, grouped);

		assertFalse(parentEntity.getModelEntity().getChildren()
				.contains(child1.getModelEntity()));
		assertFalse(parentEntity.getModelEntity().getChildren()
				.contains(child2.getModelEntity()));

		ModelEntity newGroupEntity = Model.getModelEntity(newGroup);

		assertNotNull(newGroupEntity);

		assertTrue(parentEntity.getModelEntity().getChildren()
				.contains(newGroupEntity));
		assertTrue(newGroupEntity.getChildren().contains(
				child1.getModelEntity()));
		assertTrue(newGroupEntity.getChildren().contains(
				child2.getModelEntity()));

		assertSame(Model.getComponent(child1.getModelEntity(), Parent.class)
				.getParent(), newGroupEntity);
		assertSame(Model.getComponent(child2.getModelEntity(), Parent.class)
				.getParent(), newGroupEntity);

	}

	private EngineEntity createEntity() {
		EngineEntity engineEntity = new EngineEntity(controller.getEngine()
				.getGameLoop());
		ModelEntity modelEntity = new ModelEntity();
		engineEntity.setModelEntity(modelEntity);
		engineEntity.setGroup(new Group());
		return engineEntity;
	}
}
