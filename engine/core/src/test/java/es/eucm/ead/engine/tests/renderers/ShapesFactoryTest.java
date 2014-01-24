/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.engine.tests.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import es.eucm.ead.engine.mock.MockGame;
import es.eucm.ead.engine.renderers.ShapesFactory;
import es.eucm.ead.schema.components.Bounds;
import es.eucm.ead.schema.renderers.Rectangle;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ShapesFactoryTest {

	private ShapesFactory shapesFactory;

	private Rectangle rectangle;

	private static final int size = 5;

	@Before
	public void setUp() {
		new MockGame();
		shapesFactory = new ShapesFactory();
		rectangle = new Rectangle();
		Bounds bounds = new Bounds();
		bounds.setTop(size);
		bounds.setRight(size);
		rectangle.setBounds(bounds);
	}

	@Test
	public void testColorPaint() {
		String paint = "FFFFFF";
		rectangle.setPaint(paint);
		Pixmap pixmap = shapesFactory.createShape(rectangle);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				assertEquals(Color.rgba8888(Color.WHITE), pixmap.getPixel(i, j));
			}
		}
		pixmap.dispose();
	}

	@Test
	public void testBorder() {
		String paint = "FFFFFF;000000";
		rectangle.setPaint(paint);
		Pixmap pixmap = shapesFactory.createShape(rectangle);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (i == 0 || j == 0 || i == size - 1 || j == size - 1) {
					if (Color.rgba8888(Color.BLACK) != pixmap.getPixel(i, j)) {
						fail("Incorrect border color in (" + i + ", " + j + ")");
					}
				} else {
					if (Color.rgba8888(Color.WHITE) != pixmap.getPixel(i, j)) {
						fail("Incorrect fill color in (" + i + ", " + j + ")");
					}
				}
			}
		}
		pixmap.dispose();
	}

	@Test
	public void testGradient() {
		String paint = "FFFFFF:000000:0:0:0:" + (size - 1);
		rectangle.setPaint(paint);
		Pixmap pixmap = shapesFactory.createShape(rectangle);
		// Remember: black is in 0, 0 because pixmap and stage has y coordinate
		// reversed
		assertEquals(Color.rgba8888(Color.BLACK), pixmap.getPixel(0, 0));
		assertEquals(Color.rgba8888(Color.WHITE), pixmap.getPixel(0, size - 1));
		pixmap.dispose();
	}

	@Test
	public void testInvalidPaint() {
		String paint = "ñor";
		rectangle.setPaint(paint);
		Pixmap pixmap = shapesFactory.createShape(rectangle);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				assertEquals(Color.rgba8888(Color.PINK), pixmap.getPixel(i, j));
			}
		}
		pixmap.dispose();
	}
}
