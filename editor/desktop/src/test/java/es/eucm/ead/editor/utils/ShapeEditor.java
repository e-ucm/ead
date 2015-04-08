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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.vividsolutions.jts.geom.Geometry;
import es.eucm.ead.engine.utils.GeometryUtils;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Edits shapes. Use left-click to add area, right-click to erase an area
 */
public class ShapeEditor extends GeoTester.GeoViewer implements InputProcessor {

	private AsyncExecutor executor = null;
	private AsyncResult<Array<Geometry>> updatedGeometry = null;
	private final ConcurrentLinkedQueue<Polygon> pendingMerges = new ConcurrentLinkedQueue<Polygon>();
	private final ConcurrentLinkedQueue<Polygon> pendingRemoves = new ConcurrentLinkedQueue<Polygon>();

	private final Array<Geometry> geo = new Array<Geometry>();

	/**
	 * whatever is added here will be painted in blue
	 */
	protected final Array<Polygon> blue = new Array<Polygon>();
	/**
	 * whatever is added here will be painted in red, on top of the blue
	 */
	protected final Array<Polygon> red = new Array<Polygon>();

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

		// prepare rendering aids
		shapeRenderer = new ShapeRenderer();

		Gdx.input.setInputProcessor(this);
	}

	private float polySize = 30;
	private int polySides = 6;
	private int lastX, lastY;

	protected String generatePointerString(int x, int y) {
		lastX = x;
		lastY = y;
		return super.generatePointerString(x, y);
	}

	@Override
	public void render() {
		super.render();
		renderPolygonShapes(blue, Color.BLUE, 0, 0);
		renderPolygonShapes(red, Color.RED, 0, 0);

		Array<Polygon> a = new Array<Polygon>();
		a.add(GeometryUtils.createPoly(polySides, polySize, new Vector2(lastX,
				lastY)));
		renderPolygonShapes(a, Color.DARK_GRAY, 0, 0);

		if (Gdx.input.isTouched()) {
			int mouseX = Gdx.input.getX();
			int mouseY = height - Gdx.input.getY();
			if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
				pendingMerges.offer(GeometryUtils.createPoly(polySides,
						polySize, new Vector2(mouseX, mouseY)));
			} else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
				pendingRemoves.offer(GeometryUtils.createPoly(polySides,
						polySize, new Vector2(mouseX, mouseY)));
			}
		}

		if (updatedGeometry != null && updatedGeometry.isDone()) {
			Gdx.app.error("GeoTester", "merging in...");
			red.clear();
			for (Geometry g : updatedGeometry.get()) {
				red.add(GeometryUtils.jtsCoordsToGdx(g.getCoordinates()));
			}
			geo.clear();
			geo.addAll(updatedGeometry.get());
			updatedGeometry = null;
		} else if (!pendingMerges.isEmpty() && updatedGeometry == null) {
			updatedGeometry = executor.submit(new AsyncTask<Array<Geometry>>() {
				@Override
				public Array<Geometry> call() throws Exception {
					Array<Geometry> ga = new Array<Geometry>();
					for (Geometry g : geo) {
						ga.add((Geometry) g.clone());
					}
					while (!pendingMerges.isEmpty()) {
						GeometryUtils.merge(ga, pendingMerges.poll());
					}
					if (GeoTester.r(0, 10) < 4) {
						GeometryUtils.simplify(ga, 5);
					}
					return ga;
				}
			});
		} else if (!pendingRemoves.isEmpty() && updatedGeometry == null) {
			updatedGeometry = executor.submit(new AsyncTask<Array<Geometry>>() {
				@Override
				public Array<Geometry> call() throws Exception {
					Array<Geometry> ga = new Array<Geometry>();
					for (Geometry g : geo) {
						ga.add((Geometry) g.clone());
					}
					while (!pendingRemoves.isEmpty()) {
						GeometryUtils.subtract(ga, pendingRemoves.poll());
					}
					if (GeoTester.r(0, 10) < 4) {
						GeometryUtils.simplify(ga, 5);
					}
					return ga;
				}
			});
		}
	}

	@Override
	public boolean keyDown(int i) {
		return false;
	}

	@Override
	public boolean keyUp(int i) {
		return false;
	}

	@Override
	public boolean keyTyped(char c) {
		return false;
	}

	@Override
	public boolean touchDown(int i, int i2, int i3, int i4) {
		return false;
	}

	@Override
	public boolean touchUp(int i, int i2, int i3, int i4) {
		return false;
	}

	@Override
	public boolean touchDragged(int i, int i2, int i3) {
		return false;
	}

	@Override
	public boolean mouseMoved(int i, int i2) {
		return false;
	}

	@Override
	public boolean scrolled(int i) {
		if (i > 0) {
			polySize = Math.min(polySize * 1.2f, 100);
		} else if (i < 0) {
			polySize = Math.max(polySize * 0.8f, 5);
		}
		polySides = Math.max(6, (int) polySize / 5);
		System.err.println("poly size: " + polySize);
		return false;
	}
}
