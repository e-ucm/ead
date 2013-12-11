package es.eucm.ead.engine.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import es.eucm.ead.schema.components.Bounds;
import es.eucm.ead.schema.renderers.Circle;
import es.eucm.ead.schema.renderers.Polygon;
import es.eucm.ead.schema.renderers.Rectangle;
import es.eucm.ead.schema.renderers.Shape;

public class ShapeRenderer extends AbstractRenderer<Shape> {

	public static final ShapeFactory shapeFactory = new ShapeFactory();

	private float originX;

	private float originY;

	private int width;

	private int height;

	private Texture texture;

	@Override
	public void initialize(Shape schemaObject) {
		Pixmap pixmap = shapeFactory.createShape(schemaObject);
		width = pixmap.getWidth();
		height = pixmap.getHeight();
		originX = shapeFactory.getOriginX();
		originY = shapeFactory.getOriginY();
		texture = new Texture(pixmap);
		pixmap.dispose();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(texture, originX, originY);
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public void free() {
		super.free();
		// dispose texture, since it was created from a pixmap and it is not
		// managed by Engine.assets
		texture.dispose();
	}

	private static class ShapeFactory {

		private static final String borderSeparator = ";";
		private static final String gradientSeparator = ":";

		private boolean useGradient;

		private boolean hasBorder;

		private Color borderColor;

		private Color color1, color2;

		private float x0, y0, x1, y1;

		private float originX;

		private float originY;

		public void readPaint(String paint) {
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
					x1 = Float.parseFloat(gradientParts[4]);
					y1 = Float.parseFloat(gradientParts[5]);

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
				Gdx.app.error("ShapeFactory", "Invalid paint " + paint, e);
			}
		}

		public Pixmap createShape(Shape shape) {
			readPaint(shape.getPaint());
			if (shape instanceof Rectangle) {
				return createRectangle((Rectangle) shape);
			} else if (shape instanceof Circle) {
				return createCircle((Circle) shape);
			} else if (shape instanceof Polygon) {
				return createPolygon((Polygon) shape);
			}
			Gdx.app.error("ShapeFactory", "Unsupported shape type "
					+ shape.getClass());
			return null;
		}

		private Pixmap createRectangle(Rectangle rectangle) {
			Bounds bounds = rectangle.getSize();
			originX = bounds.getLeft();
			originY = bounds.getBottom();
			int width = bounds.getRight() - bounds.getLeft();
			int height = bounds.getTop() - bounds.getBottom();
			Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
			if (useGradient) {

			} else {
				pixmap.setColor(color1);
				pixmap.fill();
			}

			if (hasBorder) {
				pixmap.setColor(borderColor);
				pixmap.drawRectangle(0, 0, width, height);
			}
			return pixmap;
		}

		private Pixmap createCircle(Circle circle) {
			int radius = circle.getRadius();
			int size = radius * 2;
			originX = circle.getCx() - size;
			originY = circle.getCy() - size;
			Pixmap pixmap = new Pixmap(size, size, Format.RGBA8888);
			if (useGradient) {

			} else {
				pixmap.setColor(color1);
				pixmap.fillCircle(radius, radius, radius);
			}

			if (hasBorder) {
				pixmap.setColor(borderColor);
				pixmap.drawCircle(radius, radius, radius);
			}
			return pixmap;
		}

		private Pixmap createPolygon(Polygon schemaObject) {
			return null;
		}

		public float getOriginX() {
			return originX;
		}

		public float getOriginY() {
			return originY;
		}

	}

}
