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
package es.eucm.ead.engine.paths;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Path-finding for polygons. Instead of creating a mesh and running A* on it,
 * we rely on line-tracing and intersections until the path is reached. Yes, we
 * also have A*. But for common cases, this should be quicker than building or
 * using a traditional mesh.
 */
public class PathFinder {

	/**
	 * A polygon defining the paths' bounds
	 */
	private Polygon pathBoundary;
	/**
	 * An easier to work-with presentation: polygon points, circular
	 */
	private Vector2[] boundaryPoints;
	/**
	 * Perspective transformation matrix: from view (game) to world
	 * (path-finding)
	 */
	private Matrix3 viewToWorld;
	/**
	 * Perspective transformation matrix: from world (path-finding) to view
	 * (game)
	 */
	private Matrix3 worldToView;

	/**
	 * to test for line-to-segment intersections that ignore start-point; must
	 * be significantly larger than PathUtils.DEFAULT_EPSILON
	 */
	private static final float MINIMAL_DISPLACEMENT = 0.01f;

	/** used for scale computation */
	private Vector2 requestedScalePoint = new Vector2();
	/** used for scale computation */
	private Vector2 siblingScalePoint = new Vector2();

	/**
	 * Used internally for calculating minimal paths, within an A* look-alike
	 */
	static class PathPoint implements Comparable<PathPoint> {
		int id;
		Vector2 pos;
		PathPoint parent;
		float dist; // distance through shortest-path to start-of-path
		float parentDist; // distance to parent (straight-line)
		float targetDist; // distance to target (lower bound, straight-line)

		public PathPoint(int id, Vector2 pos, PathPoint parent, Vector2 target) {
			this.id = id;
			this.pos = pos;
			this.parent = parent;
			this.parentDist = (parent == null) ? 0 : pos.dst(parent.pos);
			this.dist = (parent == null) ? 0 : parent.dist + parentDist;
			this.targetDist = pos.dst(target);
		}

		@Override
		public int compareTo(PathPoint other) {
			return Float.compare((dist - targetDist),
					(other.dist - other.targetDist));
		}
	}

	/**
	 * @return the projection matrix that transforms points from the game view
	 *         to their actual positions. Applied internally before
	 *         path-finding.
	 */
	public Matrix3 getViewToWorld() {
		return viewToWorld;
	}

	/**
	 * @return the projection matrix that transforms points from the internal
	 *         actual-positions coordinate space to view coordinates. Inverse of
	 *         viewToWorld.
	 */
	public Matrix3 getWorldToView() {
		return worldToView;
	}

	/**
	 * Finds paths within a given polygon.
	 * 
	 * @param pathBoundary
	 *            the polygon that defines the boundaries of admissible paths
	 * @param viewToWorld
	 *            projection matrix to use. If null, no projection is used. See
	 *            PathUtils.CENTRAL_ONE_QUARTER_SQUARE for an example
	 *            perspective.
	 */
	public PathFinder(Polygon pathBoundary, Matrix3 viewToWorld) {

		// setup projection
		viewToWorld = viewToWorld != null ? new Matrix3(viewToWorld)
				: new Matrix3().idt();
		this.viewToWorld = viewToWorld;
		this.worldToView = new Matrix3(viewToWorld).inv();

		// store a world-transformed copy of pathBoundary, keep all
		// boundaryPoints
		// for segment match
		pathBoundary = new Polygon(PathUtils.clone(pathBoundary.getVertices()));
		this.pathBoundary = pathBoundary;
		PathUtils.transformPolygons(viewToWorld, pathBoundary);
		this.boundaryPoints = PathUtils.polygonToPointsCircular(pathBoundary);
	}

	/**
	 * Finds a path from start to finish. The path is guaranteed to be minimal
	 * and to fall entirely within the original pathBoundary polygon. Each time
	 * a new step is requested, a point along the path (not necessarily a
	 * vertex) exactly "step" distance from the target will be returned. The
	 * last one may be a bit closer, though. If either start or finish are
	 * outside the polygon, the closest inside points will be used instead.
	 * 
	 * @param start
	 *            starting point, in original view coordinates
	 * @param finish
	 *            end point, in original view coordinates
	 * @param step
	 *            default step size; can be changed later via
	 *            PathIterator.setStepSize()
	 */
	public PathIterator findPath(Vector2 start, Vector2 finish, float step) {
		Array<PathPoint> pps = findPath(start, finish);
		return new PathIterator(pps, worldToView, step);
	}

	/**
	 * Finds a path from start to finish. The path is guaranteed to be minimal
	 * and to fall entirely within the original path polygon. If either start or
	 * finish are outside the polygon, the closest inside points will be chosen
	 * instead.
	 * 
	 * @param start
	 *            starting point, in original view coordinates
	 * @param finish
	 *            end point, in original view coordinates
	 */
	Array<PathPoint> findPath(Vector2 start, Vector2 finish) {

		if (start.equals(finish)) {
			Array<PathPoint> empty = new Array<PathPoint>();
			empty.add(new PathPoint(0, start, null, finish));
			return empty;
		}

		// transform to world-coordinates
		start = new Vector2(start);
		finish = new Vector2(finish);
		PathUtils.transformPoints(viewToWorld, start, finish);

		if (!pathBoundary.contains(start.x, start.y)) {
			start = PathUtils.closestInternalPoint(start, pathBoundary);
		}
		if (!pathBoundary.contains(finish.x, finish.y)) {
			finish = PathUtils.closestInternalPoint(finish, pathBoundary);
		}

		// set once goal is reached
		PathPoint goal = null;

		// A*
		PriorityQueue<PathPoint> pathPoints = new PriorityQueue<PathPoint>();
		HashMap<Vector2, PathPoint> expanded = new HashMap<Vector2, PathPoint>();
		pathPoints.add(new PathPoint(pathPoints.size(), start, null, finish));
		while (!pathPoints.isEmpty() && goal == null) {
			PathPoint current = pathPoints.poll();
			PathPoint prevExpanded = expanded.get(current.pos);
			if (prevExpanded != null && prevExpanded.dist <= current.dist) {
				// if already expanded, ignore
				continue;
			} else {
				expanded.put(current.pos, current);
			}
			Vector2[] endpoints = lineEndpoints(current.pos, finish, true);
			if (endpoints.length == 1) {
				goal = new PathPoint(pathPoints.size(), finish, current, finish);
			} else {
				for (Vector2 v : endpoints) {
					pathPoints.add(new PathPoint(pathPoints.size(), v, current,
							finish));
				}
			}
		}

		// check "did not reach"
		if (goal == null) {
			return null;
		}

		// reverse path & return
		Array<PathPoint> best = new Array<PathPoint>();
		do {
			best.add(goal);
			goal = goal.parent;
		} while (goal != null);
		best.reverse();
		return best;
	}

	/**
	 * Finds shortcut-endpoints (or direct hits if 'recursive' is set to false)
	 * between a source-to-target segment and the current polygon.
	 * 
	 * @param source
	 *            of the current segment
	 * @param target
	 *            of the current segment
	 * @param recursive
	 *            if the intersection point itself is not desired; instead, the
	 *            intersections of the polygon and the endpoints of the
	 *            intersected segment (1 level of recursion) will be returned.
	 * @return an array of points:
	 *         <ul>
	 *         <li>If 1 result is returned, then the segment does not cross the
	 *         polygon, and lies entirely within; or "recursive" was set to
	 *         false, and the first intersection of source-to-target is directly
	 *         returned.</li>
	 *         <li>
	 *         If more results are returned, then "recursive" was set to true,
	 *         and the results will contain the points returned by calling this
	 *         method recursively (with "recursive" set to false) for each
	 *         intersection.</li>
	 *         </ul>
	 */
	Vector2[] lineEndpoints(Vector2 source, Vector2 target, boolean recursive) {
		Vector2 moreThanEpsilon = new Vector2(target).sub(source).nor()
				.scl(MINIMAL_DISPLACEMENT);
		Vector2 justAfterSource = new Vector2(source).add(moreThanEpsilon);

		boolean startsByGoingOutside = !pathBoundary.contains(
				justAfterSource.x, justAfterSource.y);

		if (!startsByGoingOutside) {
			Vector2 justBeforeTarget = new Vector2(target).sub(moreThanEpsilon)
					.sub(moreThanEpsilon);
			if (!PathUtils.intersectSegmentPolygon(justAfterSource,
					justBeforeTarget, pathBoundary, new Vector2())) {
				return new Vector2[] { target };
			} else {
			}
		} else {
			// starts by going outside: source on segment, return endpoints
			for (int i = 0; i < boundaryPoints.length - 1; i++) {
				if (Intersector.distanceSegmentPoint(boundaryPoints[i],
						boundaryPoints[i + 1], source) < MINIMAL_DISPLACEMENT) {
					return new Vector2[] { boundaryPoints[i],
							boundaryPoints[i + 1] };
				}
			}
			throw new IllegalStateException(
					"Expected to find a very close segment");
		}

		// no shortcut possible, and source is not on a segment
		Vector2 crossOverPoint = new Vector2();
		float closest = Float.POSITIVE_INFINITY;
		Vector2 best = null;
		int firstSegment = -1;
		for (int i = 0; i < boundaryPoints.length - 1; i++) {
			// intersection only makes sense in-to-out; out-to-in would have had
			// to go out first
			int relative = Intersector.pointLineSide(justAfterSource,
					boundaryPoints[i], boundaryPoints[i + 1]);

			// Gdx.app.log("pf", "Lookup: relative pos is " + relative);
			if (relative > 0) {
				if (Intersector.intersectSegments(justAfterSource, target,
						boundaryPoints[i], boundaryPoints[i + 1],
						crossOverPoint)) {
					float dst2 = crossOverPoint.dst2(source);

					if (dst2 < closest) {
						closest = dst2;
						best = crossOverPoint;
						firstSegment = i;
					}
				}
			}
		}
		if (firstSegment == -1) {
			throw new IllegalStateException(
					"Expected to intersect something, since there was no direct path.");
		}

		Vector2[] result;
		if (!recursive) {
			// return the closest intersection, as requested
			result = new Vector2[] { best };
		} else {
			// recursion: endpoints from source to each intersected-segment
			Vector2[] r1 = lineEndpoints(source, boundaryPoints[firstSegment],
					false);
			Vector2[] r2 = lineEndpoints(source,
					boundaryPoints[firstSegment + 1], false);
			result = new Vector2[r1.length + r2.length];
			System.arraycopy(r1, 0, result, 0, r1.length);
			System.arraycopy(r2, 0, result, r1.length, r2.length);
		}
		return result;
	}

	/**
	 * Returns the scale for 1 unit of X increment at a given point.
	 * 
	 * @param point
	 *            the queried point. Points nearer to the viewer will be
	 *            generally larger, but details depend on exact perspective
	 *            transform used.
	 * @return the relative scale at the requested point.
	 */
	public float scaleAt(Vector2 point) {
		requestedScalePoint.set(point);
		siblingScalePoint.set(point.x + 1, point.y);
		PathUtils.transformPoints(viewToWorld, requestedScalePoint,
				siblingScalePoint);
		return requestedScalePoint.dst(siblingScalePoint);
	}

	/**
	 * Grants access to the points on a path, using the step-size that the path
	 * was created with.
	 */
	public static class PathIterator implements Iterator<Vector2>,
			Iterable<Vector2> {

		private Array<PathPoint> points;

		/** size of each successive step; can be changed durig iteration */
		private float stepSize;
		/** transform to apply to points before placing them */
		private Matrix3 worldToView;
		/** current pathBoundary-point */
		private int currentIndex;
		/** actual position between 'current' to 'next' (in 0..1) */
		private float partwayInterpolation;
		/** remaining size of last step (from lastPoint to the next pathPoint) */
		private float toNextPoint;
		/** last-returned point */
		private Vector2 prevPoint = new Vector2();

		PathIterator(Array<PathPoint> points, Matrix3 worldToView,
				float stepSize) {
			if (points == null || points.size == 0) {
				throw new IllegalArgumentException(
						"Need at least 1 point to iterate");
			}
			this.points = points;
			this.partwayInterpolation = 0;
			currentIndex = -1;
			this.worldToView = worldToView;
			this.stepSize = stepSize;
		}

		/**
		 * @return this, which is, as its name implies, Iterable.
		 */
		@Override
		public Iterator<Vector2> iterator() {
			return this;
		}

		/**
		 * changes the step-size; can be called mid-iteration
		 */
		public void setStepSize(float stepSize) {
			this.stepSize = stepSize;
		}

		/**
		 * @return the next point visited by this iterator.
		 */
		@Override
		public Vector2 next() {
			PathPoint current, next;
			Vector2 returnedPoint = new Vector2();

			if (currentIndex == -1) {
				// first step
				current = points.get(0);
				if (points.size > 1) {
					next = points.get(1);
					toNextPoint = next.parentDist;
					currentIndex = 0;
				} else {
					currentIndex = 1;
				}
				returnedPoint.set(current.pos);
			} else {
				// subsequent steps
				current = points.get(currentIndex);
				next = points.get(currentIndex + 1);

				for (float remaining = stepSize; remaining > 0; /**/) {

					if (toNextPoint > remaining) {
						// can finish within current-to-next segment:
						// interpolate
						float fractionLeft = partwayInterpolation + remaining
								/ next.parentDist;
						partwayInterpolation = fractionLeft;
						toNextPoint -= remaining;
						remaining = 0;
						returnedPoint.set(current.pos).lerp(next.pos,
								fractionLeft);

					} else {
						// must advance segment
						remaining -= toNextPoint;
						currentIndex++;
						current = next;

						if (currentIndex == points.size - 1) {
							// exit; we have reached the end
							currentIndex++;
							remaining = 0;
							returnedPoint.set(next.pos);
						} else {
							// step into new segment
							next = points.get(currentIndex + 1);
							toNextPoint = next.parentDist;
							partwayInterpolation = 0;
						}
					}
				}
			}

			prevPoint.set(returnedPoint);
			PathUtils.transformPoints(worldToView, returnedPoint);
			return returnedPoint;
		}

		/**
		 * @return true if this iterator is not yet finished iterating
		 */
		@Override
		public boolean hasNext() {
			return currentIndex < points.size;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"Path points cannot be removed");
		}
	}
}
