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
import es.eucm.ead.editor.actions.ActionTest;
import es.eucm.ead.editor.control.actions.model.scene.ActorTransformToEntity;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActorTransformToEntityTest extends ActionTest {

	@Test
	public void testReadTransformation() {

		ModelEntity modelEntity = new ModelEntity();
		EngineEntity engineEntity = new EngineEntity(controller.getEngine()
				.getGameLoop());
		engineEntity.setModelEntity(modelEntity);

		Actor actor = new Actor();
		int i = 0;
		actor.setPosition(i++, i++);
		actor.setRotation(i++);
		actor.setOrigin(i++, i++);
		actor.setScale(i++, i);
		actor.setUserObject(engineEntity);

		controller.action(ActorTransformToEntity.class, actor);

		i = 0;

		float t = 0.00001f;
		assertEquals(modelEntity.getX(), i++, t);
		assertEquals(modelEntity.getY(), i++, t);
		assertEquals(modelEntity.getRotation(), i++, t);
		assertEquals(modelEntity.getOriginX(), i++, t);
		assertEquals(modelEntity.getOriginY(), i++, t);
		assertEquals(modelEntity.getScaleX(), i++, t);
		assertEquals(modelEntity.getScaleY(), i, t);
	}
}
