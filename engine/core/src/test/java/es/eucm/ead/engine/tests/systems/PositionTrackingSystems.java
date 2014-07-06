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
package es.eucm.ead.engine.tests.systems;

import es.eucm.ead.engine.EngineTest;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.processors.TagsProcessor;
import es.eucm.ead.engine.processors.positiontracking.ChaseEntityProcessor;
import es.eucm.ead.engine.processors.positiontracking.MoveByEntityProcessor;
import es.eucm.ead.engine.processors.positiontracking.ParallaxProcessor;
import es.eucm.ead.engine.processors.renderers.ShapeRendererProcessor;
import es.eucm.ead.engine.systems.positiontracking.ChaseEntitySystem;
import es.eucm.ead.engine.systems.positiontracking.MoveByEntitySystem;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.components.positiontracking.ChaseEntity;
import es.eucm.ead.schema.components.positiontracking.MoveByEntity;
import es.eucm.ead.schema.components.positiontracking.Parallax;
import es.eucm.ead.schema.data.shape.Rectangle;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.ShapeRenderer;
import es.eucm.ead.schemax.Layer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link ChaseEntitySystem} and {@link MoveByEntitySystem} Created by
 * Javier Torrente on 6/07/14.
 */
public class PositionTrackingSystems extends EngineTest {

	private EngineEntity targetEntity;

	@Before
	public void setup() {
		componentLoader.registerComponentProcessor(MoveByEntity.class,
				new MoveByEntityProcessor(gameLoop));
		componentLoader.registerComponentProcessor(ChaseEntity.class,
				new ChaseEntityProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Parallax.class,
				new ParallaxProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Tags.class,
				new TagsProcessor(gameLoop));
		componentLoader.registerComponentProcessor(ShapeRenderer.class,
				new ShapeRendererProcessor(gameLoop));

		gameLoop.addSystem(new MoveByEntitySystem(gameLoop, variablesManager));
		gameLoop.addSystem(new ChaseEntitySystem(gameLoop, variablesManager));
	}

	@Test
	public void test() {
		ModelEntity target = buildModelRectEntity(-5, -5, 10, 10);
		Tags tags = new Tags();
		tags.getTags().add("targetObject");
		target.getComponents().add(tags);

		ModelEntity chasing = buildModelRectEntity(-30, -5, 10, 10);
		ChaseEntity chaseEntity = new ChaseEntity();
		chaseEntity.setCenterDistance(true);
		chaseEntity.setSpeedX(2.0F);
		chaseEntity.setSpeedY(0.0F);
		chaseEntity.setTarget("(collection (hastag $entity stargetObject))");
		chaseEntity.setRelativeSpeed(true);
		chaseEntity.setMaxDistance(20.0F);
		chaseEntity.setMinDistance(10.0F);
		chasing.getComponents().add(chaseEntity);

		ModelEntity background = buildModelRectEntity(-5, -5, 10, 10);
		Parallax parallax = new Parallax();
		parallax.setD(0);
		background.getComponents().add(parallax);

		MoveByEntity camera = new MoveByEntity();
		camera.setSpeedX(1.0F);
		camera.setSpeedY(0);
		camera.setTarget("(collection (hastag $entity stargetObject))");
		gameView.getLayer(Layer.CAMERA).add(
				componentLoader.toEngineComponent(camera));

		targetEntity = entitiesLoader.toEngineEntity(target);
		EngineEntity chasingEntity = entitiesLoader.toEngineEntity(chasing);
		EngineEntity backgroundEntity = entitiesLoader
				.toEngineEntity(background);
		EngineEntity cameraEntity = gameView.getLayer(Layer.CAMERA);

		gameView.addEntityToLayer(Layer.SCENE_CONTENT, targetEntity);
		gameView.addEntityToLayer(Layer.SCENE_CONTENT, chasingEntity);
		gameView.addEntityToLayer(Layer.SCENE_CONTENT, backgroundEntity);

		gameLoop.update(0);
		move(5);
		checkPos(-18, chasingEntity);
		checkPos(5, cameraEntity);
		checkPos(-10, backgroundEntity);
	}

	private void move(float x) {
		targetEntity.getGroup().moveBy(x, 0);
		gameLoop.update(0);
	}

	private void checkPos(float expected, EngineEntity entity) {
		gameLoop.update(0);
		assertEquals(expected, entity.getGroup().getX(), 0.0F);
	}

	private ModelEntity buildModelRectEntity(float x, float y, float width,
			float height) {
		ModelEntity modelEntity = new ModelEntity();
		modelEntity.setX(x);
		modelEntity.setY(y);
		Rectangle rectangle = new Rectangle();
		rectangle.setWidth((int) width);
		rectangle.setHeight((int) height);
		ShapeRenderer shapeRenderer = new ShapeRenderer();
		shapeRenderer.setShape(rectangle);
		shapeRenderer.setPaint("FFFFFF");
		modelEntity.getComponents().add(shapeRenderer);
		return modelEntity;
	}
}
