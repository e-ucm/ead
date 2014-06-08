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

import com.badlogic.gdx.math.Polygon;
import es.eucm.ead.engine.utils.ShapeToCollider;
import es.eucm.ead.schema.data.shape.Circle;
import es.eucm.ead.schema.data.shape.Rectangle;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Javier Torrente on 8/06/14.
 */
public class ShapeToColliderTest {

	@Test
	public void test() {
		// Test rectangle
		Rectangle rectangle = new Rectangle();
		rectangle.setHeight(2);
		rectangle.setWidth(3);
		assertPolygon(ShapeToCollider.buildShapeCollider(rectangle),
				new float[] { 0, 0, 3, 0, 3, 2, 0, 2 });

		// Test polygon
		es.eucm.ead.schema.data.shape.Polygon polygon = new es.eucm.ead.schema.data.shape.Polygon();
		polygon.getPoints().add(-1.0F);
		polygon.getPoints().add(-1.0F);
		polygon.getPoints().add(1.0F);
		polygon.getPoints().add(-1.0F);
		polygon.getPoints().add(0.0F);
		polygon.getPoints().add(1.0F);
		assertPolygon(ShapeToCollider.buildShapeCollider(polygon), new float[] {
				-1, -1, 1, -1, 0, 1 });

		// Test circle
		Circle circle = new Circle();
		circle.setRadius(2);
		assertPolygon(ShapeToCollider.buildShapeCollider(circle, 4),
				new float[] { 0, 4, 0, 0, 4, 0, 4, 4 });
	}

	private void assertPolygon(Polygon result, float[] expected) {
		assertEquals(expected.length, result.getVertices().length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], result.getVertices()[i], 0.001F);
		}
	}
}
