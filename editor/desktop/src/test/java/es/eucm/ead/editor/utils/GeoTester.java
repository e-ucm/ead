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
package es.eucm.ead.editor.utils;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

/**
 * Simple test class to play with geometry in GDX.
 * 
 * @author mfreire
 */
public class GeoTester {

	public static Random rand = new Random(42);

	public static Pixmap randomPixmap(int width, int height, Pixmap.Format fmt) {
		if (fmt == null) {
			fmt = Pixmap.Format.RGBA8888;
		}
		Pixmap p = new Pixmap(width, height, fmt);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				p.drawPixel(x, y,
						Color.rgba8888(r(0, 256), r(0, 256), r(0, 256), 128));
			}
		}
		return p;
	}

	/**
	 * Random int. Utility, intended only for testing
	 * 
	 * @param min
	 *            (included)
	 * @param max
	 *            (excluded).
	 * @return an int in the range [min, max[
	 */
	public static int r(int min, int max) {
		return rand.nextInt(max - min) + min;
	}

	/**
	 * Creates a string of semi-random polygons roughly aligned across the
	 * x-axis
	 * 
	 * @param n
	 *            the number of polygons to create
	 * @param distance
	 *            between polygon centers
	 * @param size
	 *            of polygons (will vary by 10% up/down)
	 * @param offset
	 *            initial polygon located here
	 * @param target
	 *            array where they will be written
	 */
	public static void randomPolys(int n, int distance, int size,
			Vector2 offset, Array<Polygon> target) {
		Vector2 v = new Vector2();
		int minSize = size - (size / 10);
		int maxSize = size + (size / 10);
		Vector2 o = new Vector2(offset);
		for (int i = 0; i < n; i++) {
			int inner = r(minSize, maxSize) / 2;
			int outer = inner * r(2, 4);
			// target.add(GeometryUtils.createStar(r(5, 6), inner, outer, o));
			// o));
			target.add(GeometryUtils.createPoly(r(5, 6), inner, o));
			v.set(distance, 0);
			v.setAngle(r(-30, 30));
			o.add(v);
		}
	}

	/**
	 * Utility class that displays a canvas, mouse coordinates, and allows
	 * subclasses to place & paint objects on this canvas.
	 * 
	 * Intended to test geometric operations with polygons and such
	 */
	public abstract static class GeoViewer implements ApplicationListener {

		protected int width;
		protected int height;

		protected ShapeRenderer shapeRenderer;
		protected PolygonSprite poly;
		protected SpriteBatch sb;
		protected BitmapFont font;
		protected PolygonSpriteBatch polyBatch;

		@Override
		public void create() {
			sb = new SpriteBatch();
			font = new BitmapFont();
			polyBatch = new PolygonSpriteBatch();
			shapeRenderer = new ShapeRenderer();

			Gdx.gl.glClearColor(1f, 1f, 1f, 1.0f);
		}

		public void renderPolygonSprite() {
			polyBatch.begin();
			poly.draw(polyBatch);
			polyBatch.end();
			poly.rotate(1.1f);
		}

		@Override
		public void resize(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public void renderPoint(float x, float y, Color color) {
			shapeRenderer.setColor(color);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			shapeRenderer.line(x - 5, y - 5, x + 5, y + 5);
			shapeRenderer.line(x - 5, y + 5, x + 5, y - 5);
			shapeRenderer.end();
		}

		public void renderPolygonShapes(Array<Polygon> polys, Color color,
				float dx, float dy) {
			shapeRenderer.setColor(color);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			for (Polygon p : polys) {
				p.translate(dx, dy);
				shapeRenderer.polygon(p.getTransformedVertices());
				p.translate(-dx, -dy);
			}
			shapeRenderer.end();
			sb.begin();
			font.setColor(color);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			for (Polygon p : polys) {
				float v[] = p.getTransformedVertices();
				for (int i = 0; i < v.length; i += 2) {
					shapeRenderer.circle(v[i] + dx, v[i + 1] + dy, 2);
					font.draw(sb, "" + i / 2, v[i], v[i + 1]
							+ (color == Color.BLUE ? 5 : -5));
				}
			}
			shapeRenderer.end();
			sb.end();
		}

		private void incColor(Color c) {
			c.g += 0.1f;
			if (c.g > 1) {
				c.g = 0.1f;
			}
		}

		public void renderTriangles(Polygon p, short[] t) {
			float v[] = p.getVertices();
			Color c = Color.valueOf("00aa00bb");
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			for (int i = 0; i < t.length; i += 3) {
				shapeRenderer.setColor(c);
				int t0 = t[i + 0] * 2;
				int t1 = t[i + 1] * 2;
				int t2 = t[i + 2] * 2;
				shapeRenderer.triangle(v[t0], v[t0 + 1], v[t1], v[t1 + 1],
						v[t2], v[t2 + 1]);
				incColor(c);
			}
			shapeRenderer.end();
		}

		@Override
		public void render() {
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			int mouseX = Gdx.input.getX();
			int mouseY = height - Gdx.input.getY();
			renderMouseCoords(mouseX, mouseY);
		}

		protected String generatePointerString(int x, int y) {
			return "(" + x + ", " + y + ") - "
					+ Gdx.graphics.getFramesPerSecond() + " fps";
		}

		private void renderMouseCoords(int x, int y) {
			renderText(0, height, generatePointerString(x, y));
		}

		/**
		 * Write a string of text
		 * 
		 * @param x
		 *            x-coord of text to draw
		 * @param y
		 *            y-coord of text to draw
		 * @param text
		 */
		protected void renderText(int x, int y, String text) {
			sb.begin();
			font.setColor(Color.DARK_GRAY);
			font.draw(sb, text, x + 10, y - 10);
			sb.end();
		}

		@Override
		public void pause() {
		}

		@Override
		public void resume() {
		}

		@Override
		public void dispose() {
			shapeRenderer.dispose();
		}
	}
}
