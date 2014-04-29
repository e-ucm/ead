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
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.paths.PathFinder;
import es.eucm.ead.engine.paths.PathUtils;

/**
 * Edits shapes. Use left-click to add area, right-click to erase an area
 */
public class PathTester extends GeoTester.GeoViewer {

	public static void main(String args[]) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1000;
		config.height = 800;
		new LwjglApplication(new PathTester(), config);
	}

	protected Matrix3 viewToWorld;
	protected Matrix3 worldToView;

	protected Vector2 offsetBlue = new Vector2(100, 200);
	protected Vector2 offsetRed = new Vector2(50, 50);
	protected Vector2 offsetGreen = new Vector2(50, 50);

	/**
	 * whatever is added here will be painted in blue. Used for "world" stuff.
	 */
	protected final Array<Polygon> blue = new Array<Polygon>();
	/**
	 * whatever is added here will be painted in red, on top of the blue. Used
	 * for "view" stuff.
	 */
	protected final Array<Polygon> red = new Array<Polygon>();
	/**
	 * whatever is added here will be painted in green, on top of the red.
	 */
	protected final Array<Polygon> green = new Array<Polygon>();

	protected Pixmap pixmap = null;
	protected Texture texture = null;

	/**
	 * Enables path-finding on a polygon
	 */
	protected PathFinder pathFinder;
	/**
	 * Origin of path (in world-coordinates)
	 */
	protected Vector2 pathStart;
	/**
	 * End of path (in world-coordinates)
	 */
	protected Vector2 pathFinish;
	/**
	 * The result of path-finding
	 */
	protected PathFinder.PathIterator pathIterator;

	@Override
	public void create() {
		super.create();

		// create pixmap
		pixmap = new Pixmap(Gdx.files.classpath("path-test/bee.png"));
		texture = new Texture(pixmap);

		// create a world-path
		float s = 30;
		Polygon worldPath = new Polygon(new float[] {
				// north-west, low, north-east
				0, 3 * s, 0, 2 * s, 2 * s, 0, 2.5f * s, 0, 4.5f * s, 2 * s,
				6 * s, 0, 7 * s, 0, 9 * s, 2 * s, 9 * s, 3 * s,
				// north-east, high, north-west
				8 * s, 3 * s, 6.5f * s, 1 * s, 5 * s, 3 * s, 4 * s, 3 * s,
				2.5f * s, s, 1 * s, 3 * s });
		worldPath.setScale(3f, 6f);
		worldPath.setRotation(5f);
		worldPath = new Polygon(worldPath.getTransformedVertices());
		blue.add(worldPath);

		// set the perspective
		viewToWorld = PathUtils
				.getProjectionMatrix(PathUtils.CENTRAL_ONE_QUARTER_SQUARE);
		worldToView = new Matrix3(viewToWorld).inv();

		// build a view-path (you would normally do things the other way
		// around...)
		Polygon viewPath = new Polygon(worldPath.getVertices().clone());
		PathUtils.transformPolygons(worldToView, viewPath);
		red.add(viewPath);
		green.add(PathUtils.CENTRAL_ONE_QUARTER_SQUARE);

		// build a path-finder, attempt to find a path
		pathFinder = new PathFinder(viewPath, viewToWorld);
		Vector2[] viewPathVertices = PathUtils.polygonToPoints(viewPath);
		pathStart = viewPathVertices[0].add(.1f, -.1f);
		pathFinish = viewPathVertices[8].add(-.1f, -.1f);
		paintRoute();

		// store for translation
		viewToWorld = pathFinder.getViewToWorld();
		worldToView = pathFinder.getWorldToView();
	}

	private void paintRoute() {
		// find the route
		try {
			pathIterator = pathFinder.findPath(pathStart, pathFinish, 20f);
		} catch (IllegalArgumentException iae) {
			System.err.println(iae);
			return;
		}

		// paint the route
		Array<Float> all = new Array<Float>();
		while (pathIterator.hasNext()) {
			Vector2 next = pathIterator.next();
			all.add(next.x);
			all.add(next.y);
		}
		float[] coords = new float[all.size];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = all.get(i);
		}
		Polygon route = new Polygon(coords);
		if (green.size > 1) {
			green.removeIndex(1);
		}
		green.add(route);
	}

	private Vector2 blueEx = new Vector2();
	private Vector2 redEx = new Vector2();
	private Vector2 actual = new Vector2();
	private boolean withinRed = false;

	protected String generatePointerString(int x, int y) {
		Vector3 v3 = new Vector3();
		actual.set(x, y);

		v3.set(x, y, 1).sub(offsetRed.x, offsetRed.y, 0).mul(viewToWorld);
		blueEx.set(v3.x / v3.z, v3.y / v3.z);

		v3.set(x, y, 1).sub(offsetBlue.x, offsetBlue.y, 0).mul(worldToView);
		redEx.set(v3.x / v3.z, v3.y / v3.z);

		Polygon redPoly = red.get(0);
		withinRed = redPoly.contains(actual.x - offsetRed.x, actual.y
				- offsetRed.y);

		return super.generatePointerString(x, y) + " => actual: " + actual
				+ " view: " + blueEx + " <-> world: " + redEx;
	}

	private boolean processingLastInput = false;
	private boolean inPath = false;

	@Override
	public void render() {
		super.render();

		if (!processingLastInput) {
			processingLastInput = true;
			if (Gdx.input.isKeyPressed(Input.Keys.A)) {
				pathStart.set(redEx);
				paintRoute();
			} else if (Gdx.input.isKeyPressed(Input.Keys.B)) {
				pathFinish.set(redEx);
				paintRoute();
			} else if (Gdx.input.isKeyPressed(Input.Keys.P)) {
				pathIterator = pathFinder.findPath(pathStart, pathFinish, 20f);
				inPath = true;
			}
			processingLastInput = false;
		}

		if (inPath) {
			// we are in a path
			if (pathIterator.hasNext()) {
				pathIterator.setStepSize(Gdx.graphics.getDeltaTime() * 100);
				Vector2 next = pathIterator.next();
				next.add(offsetRed.x, offsetRed.y);
				float scale = 2 * 1 / pathFinder.scaleAt(next);
				float w = texture.getWidth() * scale;
				float h = texture.getHeight() * scale;
				sb.begin();
				sb.draw(texture, next.x - w / 2, next.y, w, h);
				sb.end();
			} else {
				// path finished
				inPath = false;
			}
		}

		if (withinRed) {
			float scale = 2 * 1 / pathFinder.scaleAt(actual);
			float w = texture.getWidth() * scale;
			float h = texture.getHeight() * scale;
			sb.begin();
			sb.draw(texture, actual.x - w / 2, actual.y, w, h);
			sb.end();
		}

		renderPolygonShapes(blue, Color.BLUE, offsetBlue.x, offsetBlue.y);
		renderPolygonShapes(red, Color.RED, offsetRed.x, offsetRed.y);
		renderPolygonShapes(green, Color.GREEN, offsetGreen.x, offsetGreen.y);
		renderPoint(blueEx.x + offsetBlue.x, blueEx.y + offsetBlue.y,
				Color.BLUE);
		renderPoint(redEx.x + offsetRed.x, redEx.y + offsetRed.y, Color.RED);
		renderText(0, 30, "Use the red X on the red polygon, and: "
				+ "press [A] to place start, [B] to place end, [P] to play.");
	}
}
