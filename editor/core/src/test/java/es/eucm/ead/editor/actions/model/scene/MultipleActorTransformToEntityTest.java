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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.actions.ActionTest;
import es.eucm.ead.editor.control.actions.model.scene.MultipleActorTransformToEntity;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by angel on 20/05/14.
 */
public class MultipleActorTransformToEntityTest extends ActionTest {

	@Test
	public void testReadTransformation() {

		Array<ModelEntity> entities = new Array<ModelEntity>();
		Array<Actor> actors = new Array<Actor>();

		int i = 0;
		for (int j = 0; j < 10; j++) {
			ModelEntity modelEntity = new ModelEntity();
			EngineEntity engineEntity = new EngineEntity();
			engineEntity.setModelEntity(modelEntity);

			Actor actor = new Actor();
			actor.setPosition(i++, i++);
			actor.setRotation(i++);
			actor.setOrigin(i++, i++);
			actor.setScale(i++, i);
			actor.setUserObject(engineEntity);

			entities.add(modelEntity);
			actors.add(actor);
		}

		mockController.action(MultipleActorTransformToEntity.class, actors);

		i = 0;

		float t = 0.00001f;
		for (ModelEntity modelEntity : entities) {
			assertTrue(MathUtils.isEqual(modelEntity.getX(), i++, t));
			assertTrue(MathUtils.isEqual(modelEntity.getY(), i++, t));
			assertTrue(MathUtils.isEqual(modelEntity.getRotation(), i++, t));
			assertTrue(MathUtils.isEqual(modelEntity.getOriginX(), i++, t));
			assertTrue(MathUtils.isEqual(modelEntity.getOriginY(), i++, t));
			assertTrue(MathUtils.isEqual(modelEntity.getScaleX(), i++, t));
			assertTrue(MathUtils.isEqual(modelEntity.getScaleY(), i, t));
		}
	}
}
