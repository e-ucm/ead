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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.vividsolutions.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Edits shapes. Use left-click to add area, right-click to erase an area
 */
public class ShapeEditor extends GeoTester.GeoViewer {

	private AsyncExecutor executor = null;
	private AsyncResult<PolygonRegion> updatedRegion = null;
	private final ConcurrentLinkedQueue<Polygon> pendingMerges = new ConcurrentLinkedQueue<Polygon>();
	private final ConcurrentLinkedQueue<Polygon> pendingRemoves = new ConcurrentLinkedQueue<Polygon>();
	private short[] triangles;

	private final Array<Geometry> geo = new Array<Geometry>();

	/**
	 * whatever is added here will be painted in blue
	 */
	protected final ArrayList<Polygon> blue = new ArrayList<Polygon>();
	/**
	 * whatever is added here will be painted in red, on top of the blue
	 */
	protected final ArrayList<Polygon> red = new ArrayList<Polygon>();

	private Texture textureSolid;

	public static void main(String args[]) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1000;
		config.height = 800;
		new LwjglApplication(new ShapeEditor(), config);
	}

	@Override
	public void create() {
		super.create();

		executor = new AsyncExecutor(1);

		// create a string of generally-overlapping polygons, will draw in
		// blue
		GeoTester.randomPolys(3, 40, 80, new Vector2(100, 300), blue);

		// merge them into a single polygon, will draw in red
		for (Polygon bp : blue) {
			GeometryUtils.merge(geo, bp);
		}
		Geometry collapsed = GeometryUtils.collapse(geo);
		Polygon p = GeometryUtils.jtsCoordsToGdx(collapsed.getCoordinates());
		red.add(p);

		triangles = GeometryUtils.triangulate(collapsed);
		Gdx.app.error("GeoTester", "ready to display triangles worth "
				+ triangles.length + " vertices");

		// use the polygon to clip a randomly-generated texture
		textureSolid = new Texture(GeoTester.randomPixmap(100, 100, null),
				false);

		PolygonRegion polyReg = new PolygonRegion(new TextureRegion(
				textureSolid), p.getVertices(), triangles);
		poly = new PolygonSprite(polyReg);
		poly.setOrigin(p.getVertices()[0], p.getVertices()[1]);
		polyBatch = new PolygonSpriteBatch();

		// prepare rendering aids
		shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void render() {
		super.render();
		renderPolygonSprite();

		renderTriangles(red.get(0), triangles);
		renderPolygonShapes(blue, Color.BLUE);
		renderPolygonShapes(red, Color.RED);

		if (Gdx.input.isTouched()) {
			int mouseX = Gdx.input.getX();
			int mouseY = height - Gdx.input.getY();
			if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
				pendingMerges.offer(GeometryUtils.createPoly(6, 80,
						new Vector2(mouseX, mouseY)));
			} else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
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
							new TextureRegion(textureSolid), p.getVertices(),
							ts);
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
					if (GeoTester.r(0, 10) < 4) {
						GeometryUtils.simplify(geo, 3);
					}
					Polygon p = GeometryUtils.jtsCoordsToGdx(collapsed
							.getCoordinates());
					short[] ts = GeometryUtils.triangulate(collapsed);
					PolygonRegion polyReg = new PolygonRegion(
							new TextureRegion(textureSolid), p.getVertices(),
							ts);
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
}
