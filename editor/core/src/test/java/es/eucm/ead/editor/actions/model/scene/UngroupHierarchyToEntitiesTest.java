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
import es.eucm.ead.editor.control.actions.model.scene.UngroupHierarchyToEntities;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class UngroupHierarchyToEntitiesTest extends ActionTest {

	@Test
	public void testUngroup() {
		EngineEntity parentEntity = createEntity();

		EngineEntity oldGroup = createEntity();

		EngineEntity child1 = createEntity();
		EngineEntity child2 = createEntity();

		oldGroup.getModelEntity().getChildren().add(child1.getModelEntity());
		oldGroup.getModelEntity().getChildren().add(child2.getModelEntity());

		parentEntity.getModelEntity().getChildren()
				.add(oldGroup.getModelEntity());

		Array<Actor> actors = new Array<Actor>();
		actors.add(child1.getGroup());
		actors.add(child2.getGroup());

		controller.action(UngroupHierarchyToEntities.class,
				parentEntity.getGroup(), oldGroup.getGroup(), actors);

		assertFalse(oldGroup.getModelEntity().getChildren()
				.contains(child1.getModelEntity()));
		assertFalse(oldGroup.getModelEntity().getChildren()
				.contains(child2.getModelEntity()));

		assertFalse(parentEntity.getModelEntity().getChildren()
				.contains(oldGroup.getModelEntity()));
		assertTrue(parentEntity.getModelEntity().getChildren()
				.contains(child1.getModelEntity()));
		assertTrue(parentEntity.getModelEntity().getChildren()
				.contains(child2.getModelEntity()));

		assertSame(Model.getComponent(child1.getModelEntity(), Parent.class)
				.getParent(), parentEntity.getModelEntity());
		assertSame(Model.getComponent(child2.getModelEntity(), Parent.class)
				.getParent(), parentEntity.getModelEntity());
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
