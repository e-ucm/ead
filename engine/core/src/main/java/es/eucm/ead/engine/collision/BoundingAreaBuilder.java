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

import ashley.core.Component;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.ConvexHull;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SnapshotArray;
import es.eucm.ead.engine.DefaultGameView;
import es.eucm.ead.engine.components.renderers.RendererComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schemax.Layer;

/**
 * This class holds methods to build bounding areas for a given entity. The
 * bounding areas calculated are always in {@link Layer#SCENE_CONTENT}
 * coordinates, which avoids camera's transformations interfere in distance
 * calculations.
 * 
 * Created by Javier Torrente on 3/07/14.
 */
public class BoundingAreaBuilder {

	private static final ConvexHull convexHull = new ConvexHull();

	// ////////////////////////////////////////
	// Methods for building bounding areas
	// ////////////////////////////////////////

	/**
	 * Creates a minimum rectangle that contains the given entity. The algorithm
	 * takes into account renderers' colliders (if available), and children. The
	 * algorithm is simple: search for min and max x and y coordinates to build
	 * the rectangle.
	 * 
	 * @param entity
	 *            The entity to build a bounding rectangle for.
	 * @return The bounding rectangle, in coordinates of the
	 *         {@link Layer#SCENE_CONTENT} layer. May return {@code null} if
	 *         this entity is not a descendant of {@link Layer#SCENE_CONTENT}.
	 */
	public static RectangleWrapper getBoundingRectangle(EngineEntity entity) {
		RectangleWrapper rectangleWrapper = Pools
				.obtain(RectangleWrapper.class);
		Rectangle rectangle = Pools.obtain(Rectangle.class);
		rectangleWrapper.set(rectangle);
		/*
		 * Vectors x and y will hold min and max coordinates: x.x = minX, x.y =
		 * maxX y.x = minY, y.y = maxY
		 */
		Vector2 x = Pools.obtain(Vector2.class);
		Vector2 y = Pools.obtain(Vector2.class);
		x.set(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY);
		y.set(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY);
		Group sceneContentGroup = getSceneContentAncestor(entity).getGroup();
		if (sceneContentGroup == null) {
			return null;
		}

		for (Component component : entity.getComponents()) {
			if (component instanceof RendererComponent) {
				processRenderer((RendererComponent) component,
						sceneContentGroup, entity.getGroup(), x, y);
			}
		}

		for (Actor actor : entity.getGroup().getChildren()) {
			if (actor.getUserObject() != null
					&& actor.getUserObject() instanceof EngineEntity) {
				EngineEntity child = (EngineEntity) actor.getUserObject();
				RectangleWrapper childWrapper = getBoundingRectangle(child);
				Rectangle childRect = childWrapper.rectangle;
				Vector2 bottomLeft = Pools.obtain(Vector2.class);
				Vector2 topRight = Pools.obtain(Vector2.class);
				bottomLeft.set(childRect.getX(), childRect.getY());
				topRight.set(childRect.getX() + childRect.getWidth(),
						childRect.getY() + childRect.getHeight());
				entity.getGroup().localToAscendantCoordinates(
						sceneContentGroup, bottomLeft);
				entity.getGroup().localToAscendantCoordinates(
						sceneContentGroup, topRight);
				x.set(Math.min(bottomLeft.x, x.x), Math.max(topRight.x, x.y));
				y.set(Math.min(bottomLeft.y, y.x), Math.max(topRight.y, y.y));
				Pools.free(bottomLeft);
				Pools.free(topRight);
				Pools.free(childWrapper);
			}
		}

		rectangle.x = x.x;
		rectangle.y = y.x;
		rectangle.width = x.y - x.x;
		rectangle.height = y.y - y.x;
		Pools.free(x);
		Pools.free(y);
		return rectangleWrapper;
	}

	/*
	 * Returns the EngineLayer corresponding to layer {@link
	 * Layer#SCENE_CONTENT}, if it is either this {@code entity} or any of its
	 * ancestors. Returns {@code null} if {@link Layer#SCENE_CONTENT} is a
	 * descendant of {@code entity}.
	 */
	private static EngineEntity getSceneContentAncestor(EngineEntity entity) {
		if (entity instanceof DefaultGameView.EngineLayer) {
			DefaultGameView.EngineLayer engineLayer = (DefaultGameView.EngineLayer) entity;
			if (engineLayer.getLayer() == Layer.SCENE_CONTENT) {
				return engineLayer;
			}
		}

		if (entity.getGroup() != null && entity.getGroup().getParent() != null
				&& entity.getGroup().getParent() instanceof Group) {
			Group parentGroup = entity.getGroup().getParent();
			if (parentGroup.getUserObject() != null
					&& parentGroup.getUserObject() instanceof EngineEntity) {
				return getSceneContentAncestor((EngineEntity) parentGroup
						.getUserObject());
			}
		}
		return null;
	}

	/*
	 * Updates x and y vectors (see getBoundingRectangle()) taking into account
	 * the given renderer's dimensions.
	 */
	private static boolean processRenderer(RendererComponent rendererComponent,
			Group sceneContentGroup, Group group, Vector2 x, Vector2 y) {
		boolean hasRenderer = false;
		Vector2 tmp = Pools.obtain(Vector2.class);
		if (rendererComponent.getCollider() != null) {
			for (Polygon polygon : rendererComponent.getCollider()) {
				for (int i = 0; i < polygon.getVertices().length; i += 2) {
					tmp.set(polygon.getVertices()[i],
							polygon.getVertices()[i + 1]);
					// group.localToParentCoordinates(tmp);
					// group.localToStageCoordinates(tmp);
					group.localToAscendantCoordinates(sceneContentGroup, tmp);
					x.set(Math.min(tmp.x, x.x), Math.max(tmp.x, x.y));
					y.set(Math.min(tmp.y, y.x), Math.max(tmp.y, y.y));
					hasRenderer = true;
				}
			}
		}
		Pools.free(tmp);
		return hasRenderer;
	}

	/**
	 * Creates a minimum convex polygon that contains the given entity. The
	 * algorithm takes into account renderers' colliders (if available), and
	 * children. The algorithm gets different polygons coming from renderers,
	 * children, etc. and uses {@link ConvexHull} to determine the minimum
	 * convex polygon that contains all of them.
	 * 
	 * @param entity
	 *            The entity to build a bounding polygon for.
	 * @return The bounding convex polygon, in coordinates of the
	 *         {@link Layer#SCENE_CONTENT} layer. May return {@code null} if
	 *         this entity is not a descendant of {@link Layer#SCENE_CONTENT}.
	 */
	static Polygon getBoundingPolygon(EngineEntity entity) {
		Array<Vector2> points = new Array<Vector2>();
		int count = 0;
		Group sceneContentGroup = getSceneContentAncestor(entity).getGroup();
		if (sceneContentGroup == null) {
			return null;
		}

		for (Component component : entity.getComponents()) {
			if (component instanceof RendererComponent) {
				Polygon polygon = getBoundingPolygon(
						(RendererComponent) component, sceneContentGroup,
						entity.getGroup());
				toVector2Array(polygon.getVertices(), points);
				count++;
			}
		}

		for (Actor actor : entity.getGroup().getChildren()) {
			if (actor.getUserObject() != null
					&& actor.getUserObject() instanceof EngineEntity) {
				EngineEntity child = (EngineEntity) actor.getUserObject();
				Polygon childPolygon = getBoundingPolygon(child);
				toVector2Array(childPolygon.getVertices(), points);
				count++;
			}
		}

		Polygon polygon = new Polygon();
		if (count > 1) {
			FloatArray polygonPoints = convexHull.computePolygon(
					toSimpleArray(points), false);
			// Remove last point (duplicate)
			polygonPoints.removeRange(polygonPoints.size - 2,
					polygonPoints.size - 1);
			polygon.setVertices(polygonPoints.toArray());
		} else {
			polygon.setVertices(toSimpleArray(points));
		}
		for (Vector2 point : points) {
			Pools.free(point);
		}
		return polygon;
	}

	private static Polygon getBoundingPolygon(
			RendererComponent rendererComponent, Group sceneContentGroup,
			Group group) {
		SnapshotArray<Vector2> allPoints = new SnapshotArray<Vector2>();

		if (rendererComponent.getCollider() != null) {
			for (Polygon polygon : rendererComponent.getCollider()) {
				for (int i = 0; i < polygon.getVertices().length; i += 2) {
					Vector2 tmp = Pools.obtain(Vector2.class);
					tmp.set(polygon.getVertices()[i],
							polygon.getVertices()[i + 1]);
					group.localToAscendantCoordinates(sceneContentGroup, tmp);
					allPoints.add(tmp);
				}
			}
		}

		if (rendererComponent.getCollider() == null || allPoints.size == 0) {
			return null;
		}

		// Remove duplicates, if any. Algorithm works better this way
		Object[] pointsToIterate = allPoints.begin();
		int size = allPoints.size;
		for (int i = 0; i < size; i++) {
			Vector2 pointA = (Vector2) pointsToIterate[i];
			for (int j = 0; j < size; j++) {
				Vector2 pointB = (Vector2) pointsToIterate[j];
				if (j != i && pointA.equals(pointB)) {
					allPoints.removeValue(pointB, true);
				}
			}
		}
		allPoints.end();

		// To array
		float[] points = toSimpleArray(allPoints);
		FloatArray floatArray = convexHull.computePolygon(points, false);
		// Remove the last point, since its the first one (duplicate)
		floatArray.removeRange(floatArray.size - 2, floatArray.size - 1);
		Polygon polygon = new Polygon();
		polygon.setVertices(floatArray.toArray());
		return polygon;
	}

	/**
	 * Creates a minimum circle that contains the given entity. The algorithm
	 * takes into account renderers' colliders (if available) and children. The
	 * algorithm uses
	 * {@link #getBoundingPolygon(RendererComponent, Group, Group)} and then
	 * calculates radius and center by finding the pair of vertex with longest
	 * distance.
	 * 
	 * @param entity
	 *            The entity to build a bounding circle for.
	 * @return The bounding circle, in coordinates of the
	 *         {@link Layer#SCENE_CONTENT} layer. May return {@code null} if
	 *         this entity is not a descendant of {@link Layer#SCENE_CONTENT}.
	 */
	public static CircleWrapper getBoundingCircle(EngineEntity entity) {
		Polygon pol = getBoundingPolygon(entity);
		// Calculate pair of vertex with longest distance
		Vector2 center = Pools.obtain(Vector2.class);
		float maxDistance = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < pol.getVertices().length; i += 2) {
			Vector2 vertex1 = Pools.obtain(Vector2.class);
			Vector2 vertex2 = Pools.obtain(Vector2.class);

			Vector2 vertex2toVertex1 = Pools.obtain(Vector2.class);
			vertex1.set(pol.getVertices()[i], pol.getVertices()[i + 1]);
			for (int j = 0; j < pol.getVertices().length - 1; j += 2) {
				if (i == j) {
					continue;
				}
				vertex2.set(pol.getVertices()[j], pol.getVertices()[j + 1]);
				vertex2toVertex1.set(vertex1);
				float distance = vertex2toVertex1.sub(vertex2).len();
				if (distance > maxDistance) {
					maxDistance = distance;
					center.set(vertex2).add(vertex2toVertex1.scl(0.5f));
				}
			}
			Pools.free(vertex1);
			Pools.free(vertex2);
			Pools.free(vertex2toVertex1);
		}
		Circle circle = Pools.obtain(Circle.class);
		circle.set(center.x, center.y, maxDistance / 2.0F);
		Pools.free(center);
		CircleWrapper circleWrapper = Pools.obtain(CircleWrapper.class);
		circleWrapper.set(circle);
		return circleWrapper;
	}

	private static float[] toSimpleArray(Array<Vector2> allPoints) {
		float[] points = new float[allPoints.size * 2];
		toSimpleArray(allPoints, points, 0);
		return points;
	}

	private static void toSimpleArray(Array<Vector2> from, float[] to,
			int offset) {
		for (int i = 0; i < from.size; i++) {
			to[2 * i + offset] = from.get(i).x;
			to[2 * i + offset + 1] = from.get(i).y;
		}
	}

	static void toVector2Array(float[] from, Array<Vector2> to) {
		for (int i = 0; i < from.length; i += 2) {
			Vector2 vector = Pools.obtain(Vector2.class);
			vector.set(from[i], from[i + 1]);
			to.add(vector);
		}
	}
}
