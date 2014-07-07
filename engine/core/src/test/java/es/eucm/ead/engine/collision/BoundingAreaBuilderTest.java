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
package es.eucm.ead.engine.collision;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.EngineTest;
import es.eucm.ead.engine.components.physics.BoundingAreaComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.processors.renderers.ShapeRendererProcessor;
import es.eucm.ead.schema.data.shape.Circle;
import es.eucm.ead.schema.data.shape.Rectangle;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.ShapeRenderer;
import es.eucm.ead.schemax.Layer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Javier Torrente on 1/07/14.
 */
public class BoundingAreaBuilderTest extends EngineTest {

	@Before
	public void setup() {
		componentLoader.registerComponentProcessor(ShapeRenderer.class,
				new ShapeRendererProcessor(gameLoop));
	}

	@Test
	public void testRectangleDistances() {
		Vector2 intersection = new Vector2();
		BoundingAreaComponent boundingArea1 = new BoundingAreaComponent();
		boundingArea1.set(true);
		BoundingAreaComponent boundingArea2 = new BoundingAreaComponent();
		boundingArea2.set(true);

		// Vertical rectangles
		EngineEntity rect1a = buildRectEntity(10, 10, 100, 100);
		EngineEntity rect1b = buildRectEntity(10, 310, 100, 100);
		boundingArea1.update(rect1a);
		boundingArea2.update(rect1b);
		assertEquals(200,
				boundingArea1.distanceTo(boundingArea2, intersection, true),
				0.00F);
		assertEquals("The direction of distance is not right!", 200,
				intersection.y, 0.00F);
		assertEquals(300,
				boundingArea1.distanceTo(boundingArea2, intersection, false),
				0.00F);
		assertEquals(0, intersection.x, 0.0F);
		assertEquals(300, intersection.y, 0.0F);

		// Horizontal rectangles
		EngineEntity rect2a = buildRectEntity(10, 10, 100, 100);
		EngineEntity rect2b = buildRectEntity(310, 10, 100, 100);
		boundingArea1.update(rect2a);
		boundingArea2.update(rect2b);
		assertEquals(200,
				boundingArea1.distanceTo(boundingArea2, intersection, true),
				0.00F);
		assertEquals("The direction of distance is not right!", 200,
				intersection.x, 0.00F);
		assertEquals(300,
				boundingArea1.distanceTo(boundingArea2, intersection, false),
				0.00F);
		assertEquals(300, intersection.x, 0.0F);
		assertEquals(0, intersection.y, 0.0F);

		// Tangential rectangles
		EngineEntity rect3a = buildRectEntity(10, 50, 50, 50);
		EngineEntity rect3b = buildRectEntity(60, 0, 50, 50);
		boundingArea1.update(rect3a);
		boundingArea2.update(rect3b);
		assertEquals(0,
				boundingArea1.distanceTo(boundingArea2, intersection, true),
				0.00F);
		assertEquals(Math.sqrt(2) * 50,
				boundingArea1.distanceTo(boundingArea2, intersection, false),
				0.01F);
		assertEquals(50, intersection.x, 0.0F);
		assertEquals(-50, intersection.y, 0.0F);

		// Internal rectangle (centered)
		EngineEntity rect4a = buildRectEntity(0, 0, 100, 100);
		EngineEntity rect4b = buildRectEntity(10, 10, 80, 80);
		boundingArea1.update(rect4a);
		boundingArea2.update(rect4b);
		assertEquals(-1,
				boundingArea1.distanceTo(boundingArea2, intersection, true),
				0.00F);
		assertEquals(0,
				boundingArea1.distanceTo(boundingArea2, intersection, false),
				0.00F);

		// Overlapped rectangles
		EngineEntity rect5a = buildRectEntity(0, 0, 100, 100);
		EngineEntity rect5b = buildRectEntity(40, 40, 100, 100);
		boundingArea1.update(rect5a);
		boundingArea2.update(rect5b);
		assertEquals(-1,
				boundingArea1.distanceTo(boundingArea2, intersection, true),
				0.00F);
		assertEquals(Math.sqrt(2) * 40,
				boundingArea1.distanceTo(boundingArea2, intersection, false),
				0.01F);
	}

	@Test
	public void testComplexEntity() {
		ModelEntity child1 = buildModelRectEntity(10, 20, 60, 70);
		ModelEntity child2 = buildModelRectEntity(50, 50, 30, 20);
		ModelEntity parent = new ModelEntity();
		parent.getChildren().add(child1);
		parent.getChildren().add(child2);

		EngineEntity entity = entitiesLoader.toEngineEntity(parent);
		gameView.addEntityToLayer(Layer.SCENE_CONTENT, entity);

		RectangleWrapper rectangleWrapper = BoundingAreaBuilder
				.getBoundingRectangle(entity);
		assertEquals(10, rectangleWrapper.rectangle.getX(), 0.00F);
		assertEquals(20, rectangleWrapper.rectangle.getY(), 0.00F);
		assertEquals(70, rectangleWrapper.rectangle.getWidth(), 0.00F);
		assertEquals(70, rectangleWrapper.rectangle.getHeight(), 0.00F);

		Polygon polygon = BoundingAreaBuilder.getBoundingPolygon(entity);
		CircleWrapper circleWrapper = BoundingAreaBuilder
				.getBoundingCircle(entity);
		assertEquals(12, polygon.getVertices().length);
		Array<Vector2> points = new Array<Vector2>();
		BoundingAreaBuilder.toVector2Array(polygon.getVertices(), points);
		Array<Vector2> expectedPoints = new Array<Vector2>();
		expectedPoints.add(new Vector2(10, 90));
		expectedPoints.add(new Vector2(10, 20));
		expectedPoints.add(new Vector2(70, 20));
		expectedPoints.add(new Vector2(80, 50));
		expectedPoints.add(new Vector2(80, 70));
		expectedPoints.add(new Vector2(70, 90));
		for (int i = 0; i < points.size; i++) {
			Vector2 point = points.get(i);
			assertTrue(point.epsilonEquals(expectedPoints.get(i), 0.0F));
			assertTrue(circleWrapper.circle.contains(point));
		}

		assertEquals(40, circleWrapper.circle.x, 0.1F);
		assertEquals(55, circleWrapper.circle.y, 0.1F);
		assertEquals(Math.sqrt(60 * 60 + 70 * 70) / 2.0,
				circleWrapper.circle.radius, 0.1F);
	}

	@Test
	public void testCircles() {
		Vector2 intersection = new Vector2();
		BoundingAreaComponent boundingArea1 = new BoundingAreaComponent();
		boundingArea1.set(false);
		BoundingAreaComponent boundingArea2 = new BoundingAreaComponent();
		boundingArea2.set(false);

		// Not overlapping circles
		EngineEntity circle1 = buildCircleEntity(10, 20, 100);
		boundingArea1.update(circle1);
		com.badlogic.gdx.math.Circle collisionCircle1 = BoundingAreaBuilder
				.getBoundingCircle(circle1).circle;
		assertEquals(110, collisionCircle1.x, 0.2F);
		assertEquals(120, collisionCircle1.y, 0.2F);
		assertEquals(100, collisionCircle1.radius, 0.2F);

		EngineEntity circle2 = buildCircleEntity(-400, 400, 200);
		boundingArea2.update(circle2);
		assertEquals(Math.sqrt(310 * 310 + 480 * 480),
				boundingArea1.distanceTo(boundingArea2, intersection, false),
				0.1F);
		assertEquals(-310, intersection.x, 0.1F);
		assertEquals(480, intersection.y, 0.1F);
		assertEquals(Math.sqrt(310 * 310 + 480 * 480) - 300,
				boundingArea1.distanceTo(boundingArea2, intersection, true),
				0.7F);

		// Circle inside other circle
		EngineEntity circle3 = buildCircleEntity(-50, -50, 50);
		EngineEntity circle4 = buildCircleEntity(-20, -20, 20);
		boundingArea1.update(circle3);
		boundingArea2.update(circle4);
		assertEquals(-1,
				boundingArea1.distanceTo(boundingArea2, intersection, true),
				0.0F);
		assertEquals(0,
				boundingArea1.distanceTo(boundingArea2, intersection, false),
				0.1F);

		// Overlapping rectangles
		EngineEntity rect1 = buildRectEntity(-20, -20, 40, 40);
		EngineEntity rect2 = buildRectEntity(0, -20, 40, 40);
		BoundingAreaComponent r1 = new BoundingAreaComponent();
		r1.set(true);
		r1.update(rect1);
		BoundingAreaComponent r2 = new BoundingAreaComponent();
		r2.set(true);
		r2.update(rect2);
		assertEquals(20, r1.distanceTo(r2, intersection, true), 0.1F);

		// Overlapping circles
		EngineEntity circle5 = buildCircleEntity(-90, -50, 50);
		EngineEntity circle6 = buildCircleEntity(0, -50, 50);
		boundingArea1.update(circle5);
		boundingArea2.update(circle6);
		assertEquals(10,
				boundingArea1.distanceTo(boundingArea2, intersection, true),
				0.2F);
		assertEquals(90,
				boundingArea1.distanceTo(boundingArea2, intersection, false),
				0.0F);
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

	private EngineEntity buildRectEntity(float x, float y, float width,
			float height) {
		ModelEntity modelEntity = buildModelRectEntity(x, y, width, height);
		EngineEntity entity = entitiesLoader.toEngineEntity(modelEntity);
		gameView.addEntityToLayer(Layer.SCENE_CONTENT, entity);
		return entity;
	}

	private EngineEntity buildCircleEntity(float x, float y, float radius) {
		ModelEntity modelEntity = new ModelEntity();
		modelEntity.setX(x);
		modelEntity.setY(y);
		Circle circle = new Circle();
		circle.setRadius((int) radius);

		ShapeRenderer shapeRenderer = new ShapeRenderer();
		shapeRenderer.setShape(circle);
		shapeRenderer.setPaint("FFFFFF");
		modelEntity.getComponents().add(shapeRenderer);
		EngineEntity entity = entitiesLoader.toEngineEntity(modelEntity);
		gameView.addEntityToLayer(Layer.SCENE_CONTENT, entity);
		return entity;
	}
}
