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
package es.eucm.ead.engine.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.Vector2;

import es.eucm.ead.schema.components.Bounds;
import es.eucm.ead.schema.renderers.Circle;
import es.eucm.ead.schema.renderers.Polygon;
import es.eucm.ead.schema.renderers.Rectangle;
import es.eucm.ead.schema.renderers.Shape;

/**
 * <p>
 * Factory to generate pixmaps (images) from schema shapes. Shapes contains a
 * paint, represented by strings, representing the paint mode for the border and
 * the fill of the shape. Paints follow the next syntax:
 * </p>
 * <div class="highlight">
 * 
 * <pre>
 * <span class="n">fill</span><span class="p">;</span><span class="n">border</span>
 * </pre>
 * 
 * </div> If the string doesn't contain <code>;</code>, the whole string is
 * interpreted as fill. There are two types of fills:
 * 
 * <ul>
 * <li>
 * <strong>Color</strong>: represented with a string following the hex format
 * <code>RRGGBBAA</code> or <code>RRGGBB</code> is alpha is <code>FF</code>.</li>
 * <li>
 * <strong>Linear gradient</strong>: with two colors associated to two points,
 * represented with a string following the format
 * <code>RRGGBBAA:RRGGBBAA:x0:y0:x1:y1</code>.</li>
 * </ul>
 * <p>
 * The param <code>border</code> only supports color.
 * </p>
 */
public class ShapesFactory {

	/**
	 * Constant to separate border and the fill in a string defining a shape
	 * paint. Paints follows the format fill;border
	 */
	private static final String borderSeparator = ";";

	/**
	 * Constant to separate parameters in a string defining a gradient fill.
	 * Gradients follows the format RRGGBBAA:RRGGBBAA:x0:y0:x1:y1
	 */
	private static final String gradientSeparator = ":";

	private boolean useGradient;

	private boolean hasBorder;

	private Color borderColor;

	private Color color1, color2;

	private float x0, y0;

	private int pixmapHeight;

	private Vector2 gradientVector;

	private float gradientLength;

	private float originX;

	private float originY;

	// Aux variables to avoid new instances in recurring methods
	private Vector2 auxVector = new Vector2();
	private Color auxColor = new Color();

	/**
	 * Reads the paint and sets the proper values before the fill phase
	 * 
	 * @param paint
	 *            the string representing the paint. <a href=
	 *            "https://github.com/e-ucm/ead/wiki/Renderers#shapes">More info
	 *            about paint format</a>
	 */
	private void readPaint(String paint) {
		try {
			String parts[] = paint.split(borderSeparator);
			// Fill
			useGradient = parts[0].contains(gradientSeparator);
			if (useGradient) {
				String gradientParts[] = parts[0].split(gradientSeparator);
				color1 = Color.valueOf(gradientParts[0]);
				color2 = Color.valueOf(gradientParts[1]);
				x0 = Float.parseFloat(gradientParts[2]);
				y0 = Float.parseFloat(gradientParts[3]);
				float x1 = Float.parseFloat(gradientParts[4]);
				float y1 = Float.parseFloat(gradientParts[5]);
				gradientVector = new Vector2(x1 - x0, y1 - y0);
				gradientLength = gradientVector.len();
			} else {
				color1 = Color.valueOf(parts[0]);
			}

			// Border
			hasBorder = parts.length > 1;
			if (hasBorder) {
				borderColor = Color.valueOf(parts[1]);
			}
		} catch (Exception e) {
			hasBorder = false;
			useGradient = false;
			color1 = Color.PINK;
			Gdx.app.error("ShapeFactory", "Invalid paint " + paint
					+ ". Paint set to pink.", e);
		}
	}

	/**
	 * Create a pixmap containing the given shape
	 * 
	 * @param shape
	 *            the shape to draw in the pixmap
	 * @return the pixmap. Returns null if the shape is invalid
	 */
	public Pixmap createShape(Shape shape) {
		readPaint(shape.getPaint());
		if (shape instanceof Rectangle) {
			return createRectangle((Rectangle) shape);
		} else if (shape instanceof Circle) {
			return createCircle((Circle) shape);
		} else if (shape instanceof Polygon) {
			return createPolygon((Polygon) shape);
		}
		Gdx.app.error("ShapeFactory",
				"Unsupported shape type " + shape.getClass());
		return null;
	}

	/** Creates a rectangle **/
	private Pixmap createRectangle(Rectangle rectangle) {
		Bounds bounds = rectangle.getBounds();
		originX = bounds.getLeft();
		originY = bounds.getBottom();
		int width = bounds.getRight() - bounds.getLeft();
		pixmapHeight = bounds.getTop() - bounds.getBottom();

		if (width <= 0 || pixmapHeight <= 0) {
			Gdx.app.error("ShapeFactory",
					"Rectangles can't have negative or zero dimensions: ("
							+ width + ", " + pixmapHeight + ")");
		}

		Pixmap pixmap = new Pixmap(width, pixmapHeight, Format.RGBA8888);
		if (useGradient) {
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < pixmapHeight; j++) {
					setGradientColor(pixmap, i, j);
					pixmap.drawPixel(i, j);
				}
			}
		} else {
			pixmap.setColor(color1);
			pixmap.fill();
		}

		if (hasBorder) {
			pixmap.setColor(borderColor);
			pixmap.drawRectangle(0, 0, width, pixmapHeight);
		}
		return pixmap;
	}

	/** Creates a circle **/
	private Pixmap createCircle(Circle circle) {
		int radius = circle.getRadius();
		int size = radius * 2;
		pixmapHeight = size;
		originX = 0;
		originY = 0;
		Pixmap pixmap = new Pixmap(size, size, Format.RGBA8888);
		if (useGradient) {
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < pixmapHeight; j++) {
					if (auxVector.set(i - radius, j - radius).len() <= radius - 1) {
						setGradientColor(pixmap, i, j);
						pixmap.drawPixel(i, j);
					}
				}
			}
		} else {
			pixmap.setColor(color1);
			pixmap.fillCircle(radius, radius, radius - 1);
		}

		if (hasBorder) {
			pixmap.setColor(borderColor);
			pixmap.drawCircle(radius, radius, radius - 1);
		}
		return pixmap;
	}

	/** Creates a polygon **/
	private Pixmap createPolygon(Polygon schemaPolygon) {
		if (schemaPolygon.getPoints().size() < 6) {
			Gdx.app.error("ShapeFactory",
					"Invalid polygon. It contains less than 3 points.");
			return null;
		}

		float[] points = new float[schemaPolygon.getPoints().size()];
		for (int i = 0; i < schemaPolygon.getPoints().size(); i++) {
			points[i] = schemaPolygon.getPoints().get(i);
			// See comment in setGradientColor to understand this
			if (i % 2 != 0) {
				points[i] = pixmapHeight - points[i];
			}
		}

		com.badlogic.gdx.math.Polygon polygon = new com.badlogic.gdx.math.Polygon(
				points);
		com.badlogic.gdx.math.Rectangle rectangle = polygon
				.getBoundingRectangle();

		Pixmap pixmap = new Pixmap((int) rectangle.getWidth(),
				(int) rectangle.getHeight(), Format.RGBA8888);

		// Fill
		pixmap.setColor(color1);
		for (int i = 0; i < rectangle.getWidth(); i++) {
			for (int j = 0; j < rectangle.getHeight(); j++) {
				if (polygon.contains(i, j)) {
					if (useGradient) {
						setGradientColor(pixmap, i, j);
					}
					pixmap.drawPixel(i, j);
				}
			}
		}
		// Border
		if (hasBorder) {
			pixmap.setColor(borderColor);
			int prevX = (int) points[0];
			int prevY = (int) points[1];
			for (int i = 2; i < points.length; i += 2) {
				int x = (int) points[i];
				int y = (int) points[i + 1];
				pixmap.drawLine(prevX, prevY, x, y);
				prevX = x;
				prevY = y;
			}
		}
		return pixmap;
	}

	public float getOriginX() {
		return originX;
	}

	public float getOriginY() {
		return originY;
	}

	/**
	 * Sets the gradient color in the pixmap according the x and y coordinates
	 * and the paint set through {@link ShapesFactory#readPaint(String)}
	 * 
	 * @param pixmap
	 *            the pixmap
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 */
	private void setGradientColor(Pixmap pixmap, int x, int y) {
		// Pixmaps have a different coordinates system than the stage. In
		// pixmaps (0, 0) is the top-left corner, and for the stage, (0, 0)
		// is the bottom-left corner, so we need to invert the value of y to
		// transform from one system to another
		auxVector.set(x - x0, pixmapHeight - 1 - y - y0);
		float cos = gradientVector.dot(auxVector) / gradientLength;
		// Normalize
		float proj = cos / gradientLength;
		if (proj <= 0) {
			pixmap.setColor(color1);
		} else if (proj >= 1) {
			pixmap.setColor(color2);
		} else {
			auxColor.set(color1);
			auxColor.lerp(color2, proj);
			pixmap.setColor(auxColor);
		}
	}

}
