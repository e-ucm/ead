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
package es.eucm.ead.engine.tests;

import com.badlogic.ashley.core.Family;
import es.eucm.ead.engine.components.renderers.RendererComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.processors.renderers.EmptyRendererProcessor;
import org.junit.Test;

import es.eucm.ead.engine.EngineTest;
import es.eucm.ead.schema.data.shape.Circle;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.EmptyRenderer;

import static org.junit.Assert.assertEquals;

public class RemoveEntityTest extends EngineTest {

	@Test
	public void testRemove() {
		ModelEntity scene = new ModelEntity();
		ModelEntity entity = new ModelEntity();
		scene.getChildren().add(entity);
		ModelEntity child = new ModelEntity();
		entity.getChildren().add(child);
		ModelEntity child2 = new ModelEntity();
		child.getChildren().add(child2);

		scene.setName("scene");
		entity.setName("entity");
		child.setName("child");
		child2.setName("child2");

		EmptyRenderer renderer = new EmptyRenderer();
		Circle circle = new Circle();
		circle.setRadius(1);
		renderer.setShape(circle);
		child2.getComponents().add(renderer);

		componentLoader.registerComponentProcessor(EmptyRenderer.class,
				new EmptyRendererProcessor(gameLoop, gameAssets));

		int baseEntities = gameLoop.getEntities().size();

		EngineEntity engineEntity = entitiesLoader.toEngineEntity(scene);

		assertEquals(4 + baseEntities, gameLoop.getEntities().size());
		assertEquals(
				1,
				gameLoop.getEntitiesFor(
						Family.all(RendererComponent.class).get()).size());

		gameLoop.removeEntity(engineEntity);
		assertEquals(baseEntities, gameLoop.getEntities().size());
		assertEquals(
				0,
				gameLoop.getEntitiesFor(
						Family.all(RendererComponent.class).get()).size());
	}
}
