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
package es.eucm.ead.engine.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

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
	private static int r(int min, int max) {
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
			Vector2 offset, ArrayList<Polygon> target) {
		Vector2 v = new Vector2();
		int minSize = size - (size / 10);
		int maxSize = size + (size / 10);
		Vector2 o = new Vector2(offset);
		for (int i = 0; i < n; i++) {
			int inner = r(minSize, maxSize) / 2;
			int outer = inner * r(2, 4);
			// target.add(GeometryUtils.createStar(r(5, 6), inner, outer, o));
			target.add(GeometryUtils.createPoly(r(5, 6), inner, o));
			v.set(distance, 0);
			v.setAngle(r(-30, 30));
			o.add(v);
		}
	}

	public static class GeoViewer implements ApplicationListener {

		private int width;
		private int height;

		/**
		 * whatever is added here will be painted in blue
		 */
		private final ArrayList<Polygon> blue = new ArrayList<Polygon>();
		/**
		 * whatever is added here will be painted in red, on top of the blue
		 */
		private final ArrayList<Polygon> red = new ArrayList<Polygon>();

		private final ArrayList<Geometry> geo = new ArrayList<Geometry>();

		ShapeRenderer shapeRenderer = null;
		PolygonSprite poly;

		PolygonSpriteBatch polyBatch;
		Texture textureSolid;
		SpriteBatch sb;
		BitmapFont font;

		AsyncExecutor executor;
		AsyncResult<PolygonRegion> updatedRegion = null;
		private final ConcurrentLinkedQueue<Polygon> pendingMerges = new ConcurrentLinkedQueue<Polygon>();
		private final ConcurrentLinkedQueue<Polygon> pendingRemoves = new ConcurrentLinkedQueue<Polygon>();

		short[] triangles;

		@Override
		public void create() {
			sb = new SpriteBatch();
			font = new BitmapFont();
			executor = new AsyncExecutor(2);

			Gdx.gl.glClearColor(1f, 1f, 1f, 1.0f);

			// create a string of generally-overlapping polygons, will draw in
			// blue
			randomPolys(3, 40, 80, new Vector2(100, 300), blue);

			// merge them into a single polygon, will draw in red
			for (Polygon bp : blue) {
				GeometryUtils.merge(geo, bp);
			}
			Geometry collapsed = GeometryUtils.collapse(geo);
			Polygon p = GeometryUtils
					.jtsCoordsToGdx(collapsed.getCoordinates());
			red.add(p);

			triangles = GeometryUtils.triangulate(collapsed);
			Gdx.app.error("GeoTester", "ready to display triangles worth "
					+ triangles.length + " vertices");

			// use the polygon to clip a randomly-generated texture
			textureSolid = new Texture(randomPixmap(1024, 1024, null), false);
			PolygonRegion polyReg = new PolygonRegion(new TextureRegion(
					textureSolid), p.getVertices(), triangles);
			poly = new PolygonSprite(polyReg);
			poly.setOrigin(p.getVertices()[0], p.getVertices()[1]);
			polyBatch = new PolygonSpriteBatch();

			// prepare rendering aids
			shapeRenderer = new ShapeRenderer();
		}

		void renderPolygonSprite() {
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

		public void renderPolygonShapes(ArrayList<Polygon> al, Color color) {
			shapeRenderer.setColor(color);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			for (Polygon p : al) {
				shapeRenderer.polygon(p.getVertices());
			}
			shapeRenderer.end();
			sb.begin();
			font.setColor(color);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			for (Polygon p : al) {
				float v[] = p.getVertices();
				for (int i = 0; i < v.length; i += 2) {
					shapeRenderer.circle(v[i], v[i + 1], 2);
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

			renderTriangles(red.get(0), triangles);
			renderPolygonShapes(blue, Color.BLUE);
			renderPolygonShapes(red, Color.RED);
			renderPolygonSprite();

			int mouseX = Gdx.input.getX();
			int mouseY = height - Gdx.input.getY();
			renderMouseCoords(mouseX, mouseY);
			if (Gdx.input.isTouched()) {
				if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
					pendingMerges.offer(GeometryUtils.createPoly(6, 80,
							new Vector2(mouseX, mouseY)));
				} else if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
					pendingRemoves.offer(GeometryUtils.createPoly(6, 80,
							new Vector2(mouseX, mouseY)));
				}
			}

			if (updatedRegion != null && updatedRegion.isDone()) {
				Gdx.app.error("GeoTester", "merging in...");
				poly.setRegion(updatedRegion.get());
				triangles = updatedRegion.get().getTriangles();
				red.clear();
				red.add(new Polygon(updatedRegion.get().getVertices()));
				updatedRegion = null;
			} else if (!pendingMerges.isEmpty() && updatedRegion == null) {
				updatedRegion = executor.submit(new AsyncTask<PolygonRegion>() {
					@Override
					public PolygonRegion call() throws Exception {
						long t0 = System.nanoTime();
						while (!pendingMerges.isEmpty()) {
							GeometryUtils.merge(geo, pendingMerges.poll());
						}
						Geometry collapsed = GeometryUtils.collapse(geo);
						GeometryUtils.simplify(geo, 3);
						Polygon p = GeometryUtils.jtsCoordsToGdx(collapsed
								.getCoordinates());
						short[] ts = GeometryUtils.triangulate(collapsed);
						PolygonRegion polyReg = new PolygonRegion(
								new TextureRegion(textureSolid), p
										.getVertices(), ts);
						long t1 = System.nanoTime() - t0;
						Gdx.app.error("GeoTester",
								"ready to display triangles worth " + ts.length
										+ " vertices after merge in "
										+ (t1 / 1000000) + " ms");
						return polyReg;
					}
				});
			} else if (!pendingRemoves.isEmpty() && updatedRegion == null) {
				updatedRegion = executor.submit(new AsyncTask<PolygonRegion>() {
					@Override
					public PolygonRegion call() throws Exception {
						long t0 = System.nanoTime();
						while (!pendingRemoves.isEmpty()) {
							GeometryUtils.subtract(geo, pendingRemoves.poll());
						}
						Geometry collapsed = GeometryUtils.collapse(geo);
						if (r(0, 10) < 4) {
							GeometryUtils.simplify(geo, 3);
						}
						Polygon p = GeometryUtils.jtsCoordsToGdx(collapsed
								.getCoordinates());
						short[] ts = GeometryUtils.triangulate(collapsed);
						PolygonRegion polyReg = new PolygonRegion(
								new TextureRegion(textureSolid), p
										.getVertices(), ts);
						long t1 = System.nanoTime() - t0;
						Gdx.app.error("GeoTester",
								"ready to display triangles worth " + ts.length
										+ " vertices after removal in "
										+ (t1 / 1000000) + " ms");
						return polyReg;
					}
				});
			}
		}

		private void renderMouseCoords(int x, int y) {
			sb.begin();
			font.setColor(Color.DARK_GRAY);
			font.draw(
					sb,
					"(" + x + ", " + y + ") - "
							+ Gdx.graphics.getFramesPerSecond() + " fps", 10,
					height - 10);
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

	public static void main(String args[]) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1000;
		config.height = 800;
		new LwjglApplication(new GeoViewer(), config);
	}
}
