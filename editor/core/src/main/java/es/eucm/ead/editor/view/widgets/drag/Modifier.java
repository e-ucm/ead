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
package es.eucm.ead.editor.view.widgets.drag;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Contains all the logic to transform actors. The modifier contains 10 handles:
 * 8 handles to resize, 1 handles to change the origin and 1 handles to rotate.
 * Handles are distributed as follows:
 * 
 * <pre>
 *           R
 *           |
 *     6 --- 7 --- 8
 *     |     |     |
 *     3 --- 4 --- 5
 *     |     |     |
 *     0 --- 1 --- 2
 * </pre>
 * 
 * <p>
 * </p>
 * Where R is the rotation point and 4 the origin point. Each number represent
 * its index in {@link #handles}</p>
 * <p>
 * All transformations are based on the position of this 10 handles.
 * </p>
 * <p>
 * The method {@link #readActorTransformation(Actor)} reads the transformation
 * of an actor and redistribute the handles to match its transformation.
 * </p>
 * <p>
 * The method {@link #applyHandleTransformation(Actor)} performs the inverse
 * operation: applies the transformation represented by the handles to the given
 * actor.
 * </p>
 */
public class Modifier extends Group {

	public static final int ORIGIN_HANDLE_INDEX = 4;

	public static final int HANDLE_SQUARE_SIZE = 8;

	public static final int HANDLE_CIRCLE_SIZE = 6;

	public static final int ROTATION_HANDLE_OFFSET = 20;

	public final Vector2 tmp1 = new Vector2(), tmp2 = new Vector2(),
			tmp3 = new Vector2(), tmp4 = new Vector2(), tmp5 = new Vector2();

	private ShapeRenderer shapeRenderer;

	private Handle[] handles;

	private RotationHandle rotationHandle;

	private float rotationStep = 1.0f;

	private final Matrix3 tmpMatrix = new Matrix3();

	public Modifier(ShapeRenderer shapeRenderer) {
		this.shapeRenderer = shapeRenderer;
		handles = new Handle[9];
		for (int i = 0; i < 9; i++) {
			if (i == ORIGIN_HANDLE_INDEX) {
				handles[i] = new OriginHandle(shapeRenderer,
						HANDLE_CIRCLE_SIZE, Color.LIGHT_GRAY);
			} else {
				handles[i] = new Handle(shapeRenderer, HANDLE_SQUARE_SIZE,
						Color.BLACK);
			}
			addActor(handles[i]);
		}

		/*
		 * Set move constrains between handles. E.g., handle[0] is attached to
		 * handle[2] in the x axis and to handle[6] in the y axis.
		 * 
		 * These constrains are based on that only 3 handles are required to
		 * specify the transformation: 0 (origin), 2 (width) and 6 (height).
		 * What the rest actually do is move one or more of these 3 handles.
		 * 
		 * So each point only need to specify its constrains with these 3 main
		 * points.
		 */
		handles[0].setAlignedX(handles[2]);
		handles[0].setAlignedY(handles[6]);

		handles[2].setAlignedX(handles[0]);

		handles[6].setAlignedY(handles[0]);

		handles[8].setAlignedX(handles[6]);
		handles[8].setAlignedY(handles[2]);

		handles[1].setAlignedX(handles[0], handles[2]);
		handles[3].setAlignedY(handles[0], handles[6]);

		handles[7].setAlignedX(handles[6]);
		handles[5].setAlignedY(handles[2]);

		rotationHandle = new RotationHandle(shapeRenderer, HANDLE_CIRCLE_SIZE,
				Color.BLACK);
		addActor(rotationHandle);

	}

	/**
	 * Sets the rotation step for the modifier. When rotating, angle will only
	 * vary the amount set by this step
	 */
	public void setRotationStep(float rotationStep) {
		this.rotationStep = rotationStep;
	}

	/**
	 * Reads the transformation from the given actor and redistributes the
	 * handles to match it
	 */
	public void readActorTransformation(Actor actor) {
		for (int i = 0; i <= 2; i++) {
			for (int j = 0; j <= 2; j++) {
				if (i == 1 && j == 1) {
					tmp1.set(actor.getOriginX(), actor.getOriginY());
				} else {
					float x = actor.getWidth() / 2.0f * j;
					float y = actor.getHeight() / 2.0f * i;
					tmp1.set(x, y);
				}
				actor.localToParentCoordinates(tmp1);
				handles[i * 3 + j].setPosition(tmp1.x, tmp1.y);
			}
		}

		// Set rotation handle
		float scale = actor.getScaleY() == 0.0f ? 0.01f : actor.getScaleY();
		tmp1.set(actor.getWidth() / 2.0f, (scale > 0.0f ? actor.getHeight()
				: 0.0f) + ROTATION_HANDLE_OFFSET / actor.getScaleY());
		actor.localToParentCoordinates(tmp1);
		rotationHandle.setPosition(tmp1.x, tmp1.y);

	}

	/**
	 * Applies the current transformation represented by the handles to given
	 * actor
	 */
	public void applyHandleTransformation(Actor actor) {
		/*
		 * We are going to calculate the affine transformation for the actor to
		 * fit the bounds represented by the handles. The affine transformation
		 * is defined as follows:
		 */
		// |a b tx|
		// |c d ty|=|Translation Matrix| x |Scale Matrix| x |Rotation Matrix|
		// |0 0 1 |
		/*
		 * More info about affine transformations:
		 * https://people.gnome.org/~mathieu
		 * /libart/libart-affine-transformation-matrices.html, To obtain the
		 * matrix, we want to resolve the following equation system:
		 */
		// | a b tx| |0| |o.x|
		// | c d ty|*|0|=|o.y|
		// | 0 0 1 | |1| | 1 |
		//
		// | a b tx| |w| |t.x|
		// | c d ty|*|0|=|t.y|
		// | 0 0 1 | |1| | 1 |
		//
		// | a b tx| |0| |n.x|
		// | c d ty|*|h|=|n.y|
		// | 0 0 1 | |1| | 1 |
		/*
		 * where o is handles[0] (origin), t is handles[2] (tangent) and n is
		 * handles[6] (normal), w is actor.getWidth() and h is
		 * actor.getHeight().
		 * 
		 * This matrix defines that the 3 points defining actor bounds are
		 * transformed to the 3 points defining modifier bounds. E.g., we want
		 * that actor origin (0,0) is transformed to (handles[0].x,
		 * handles[0].y), and that is expressed in the first equation.
		 * 
		 * Resolving these equations is obtained:
		 */
		// a = (t.x - o.y) / w
		// b = (t.y - o.y) / w
		// c = (n.x - o.x) / h
		// d = (n.y - o.y) / h
		/*
		 * Values for translation, scale and rotation contained by the matrix
		 * can be obtained directly making operations over a, b, c and d:
		 */
		// tx = o.x
		// ty = o.y
		// sx = sqrt(a^2+b^2)
		// sy = sqrt(c^2+d^2)
		// rotation = atan(c/d)
		// or
		// rotation = atan(-b/a)
		/*
		 * Rotation can give two different values (this happens when there is
		 * more than one way of obtaining the same transformation). To avoid
		 * that, we ignore the rotation to obtain the final values.
		 */

		Vector2 o = tmp1.set(handles[0].getX(), handles[0].getY());
		Vector2 t = tmp2.set(handles[2].getX(), handles[2].getY());
		Vector2 n = tmp3.set(handles[6].getX(), handles[6].getY());

		Vector2 vt = tmp4.set(t).sub(o);
		Vector2 vn = tmp5.set(n).sub(o);

		// Ignore rotation
		float rotation = actor.getRotation();
		vt.rotate(-rotation);
		vn.rotate(-rotation);

		t.set(vt).add(o);
		n.set(vn).add(o);

		float a = (t.x - o.x) / actor.getWidth();
		float c = (t.y - o.y) / actor.getWidth();
		float b = (n.x - o.x) / actor.getHeight();
		float d = (n.y - o.y) / actor.getHeight();

		// Math.sqrt gives a positive value, but it also have a negatives. The
		// signum is calculated computing the current rotation
		float signumX = vt.angle() > 90.0f && vt.angle() < 270.0f ? -1.0f
				: 1.0f;
		float signumY = vn.angle() > 180.0f ? -1.0f : 1.0f;

		float scaleX = (float) Math.sqrt(a * a + b * b) * signumX;
		float scaleY = (float) Math.sqrt(c * c + d * d) * signumY;

		actor.setScale(scaleX, scaleY);

		/*
		 * To obtain the correct translation value we need to subtract the
		 * amount of translation due to the origin.
		 */
		tmpMatrix.setToTranslation(actor.getOriginX(), actor.getOriginY());
		tmpMatrix.rotate(actor.getRotation());
		tmpMatrix.scale(actor.getScaleX(), actor.getScaleY());
		tmpMatrix.translate(-actor.getOriginX(), -actor.getOriginY());

		/*
		 * Now, the matrix has how much translation is due to the origin
		 * involved in the rotation and scaling operations
		 */
		float x = o.x - tmpMatrix.getValues()[Matrix3.M02];
		float y = o.y - tmpMatrix.getValues()[Matrix3.M12];
		actor.setPosition(x, y);
		readActorTransformation(actor);
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		batch.end();
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.DARK_GRAY);
		shapeRenderer.line(handles[0].getX(), handles[0].getY(),
				handles[2].getX(), handles[2].getY());
		shapeRenderer.line(handles[0].getX(), handles[0].getY(),
				handles[6].getX(), handles[6].getY());
		shapeRenderer.line(handles[2].getX(), handles[2].getY(),
				handles[8].getX(), handles[8].getY());
		shapeRenderer.line(handles[8].getX(), handles[8].getY(),
				handles[6].getX(), handles[6].getY());
		shapeRenderer.end();
		super.drawChildren(batch, parentAlpha);
		batch.begin();
	}

	public class Handle extends Group {

		private ShapeRenderer shapeRenderer;

		private float size;

		private Color color;

		private Handle[] alignedX;

		private Handle[] alignedY;

		public Handle(ShapeRenderer shapeRenderer, float size, Color color) {
			this.shapeRenderer = shapeRenderer;
			this.size = size;
			this.color = color;
		}

		/**
		 * Sets which handles are aligned to this handle in the x axis. They
		 * will be updated when the position of this handle changes to keep it
		 * aligned.
		 */
		public void setAlignedX(Handle... alignedX) {
			this.alignedX = alignedX;
		}

		/**
		 * Sets which handles are aligned to this handle in the y axis. They
		 * will be updated when the position of this handle changes to keep it
		 * aligned.
		 */
		public void setAlignedY(Handle... alignedY) {
			this.alignedY = alignedY;
		}

		/**
		 * Updates the position of this handle, automatically updating the
		 * handles attached to it
		 * 
		 * @param actor
		 *            the actor of the transformation
		 * @param x
		 *            new x for the handle
		 * @param y
		 *            new y for the handle
		 */
		public void updatePosition(Actor actor, float x, float y) {
			float rotation = actor.getRotation();
			setPosition(x, y);
			updateAligned(alignedX, rotation, rotation + 90);
			updateAligned(alignedY, rotation + 90, rotation);

		}

		@Override
		public Actor hit(float x, float y, boolean touchable) {
			float scale = 1.0f / getParent().getParent().getScaleX();
			return tmp1.set(x, y).len() < size * scale ? this : null;
		}

		/**
		 * Aligns all the given handles with this handle
		 * 
		 * @param aligned
		 *            the handles to align
		 * @param originAngle
		 *            the angle of the line that pass for this handle
		 * @param targetAngle
		 *            the angle of the line that pass for the given handles
		 *            (perpendicular to originAngle)
		 */
		private void updateAligned(Handle[] aligned, float originAngle,
				float targetAngle) {
			if (aligned != null) {
				for (Handle handle : aligned) {
					Vector2 p1 = tmp1.set(getX(), getY());
					Vector2 p2 = tmp2.set(1, 0).rotate(originAngle).add(p1);

					Vector2 q1 = tmp3.set(handle.getX(), handle.getY());
					Vector2 q2 = tmp4.set(1, 0).rotate(targetAngle).add(q1);

					Intersector.intersectLines(p1, p2, q1, q2, tmp5);
					handle.setPosition(tmp5.x, tmp5.y);
				}
			}
		}

		@Override
		protected void drawChildren(Batch batch, float parentAlpha) {
			float scale = 1.0f / getParent().getParent().getScaleX();
			shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
			shapeRenderer.begin(ShapeType.Filled);
			float realSize = size * scale;
			shapeRenderer.setColor(Color.WHITE);
			drawShape(shapeRenderer, realSize + 1);
			shapeRenderer.setColor(color);
			drawShape(shapeRenderer, realSize);
			shapeRenderer.end();
		}

		protected void drawShape(ShapeRenderer shapeRenderer, float size) {
			shapeRenderer.rect(-size / 2.0f, -size / 2.0f, size, size);
		}
	}

	public class OriginHandle extends Handle {

		public OriginHandle(ShapeRenderer shapeRenderer, float size, Color color) {
			super(shapeRenderer, size, color);
		}

		@Override
		protected void drawShape(ShapeRenderer shapeRenderer, float size) {
			shapeRenderer.circle(0, 0, size);
		}

		/**
		 * Updates the origin of the given actor to match x and y
		 */
		public void updateOrigin(Actor actor, float x, float y) {
			super.setPosition(x, y);
			float rotation = actor.getRotation();
			Vector2 o = tmp1.set(handles[0].getX(), handles[0].getY());
			Vector2 t = tmp2.set(handles[2].getX(), handles[2].getY());
			Vector2 n = tmp3.set(handles[6].getX(), handles[6].getY());
			Vector2 origin = tmp4.set(handles[ORIGIN_HANDLE_INDEX].getX(),
					handles[ORIGIN_HANDLE_INDEX].getY()).sub(o);

			Vector2 vt = t.sub(o);
			Vector2 vn = n.sub(o);

			vt.rotate(-rotation);
			vn.rotate(-rotation);
			origin.rotate(-rotation);
			actor.setOrigin((origin.x / vt.x) * actor.getWidth(),
					(origin.y / vn.y) * actor.getHeight());
		}
	}

	public class RotationHandle extends Handle {

		private float startingAngle;

		private float originalRotation;

		public RotationHandle(ShapeRenderer shapeRenderer, float size,
				Color color) {
			super(shapeRenderer, size, color);
		}

		protected void drawShape(ShapeRenderer shapeRenderer, float size) {
			shapeRenderer.circle(0, 0, size);
		}

		/**
		 * Initialize the rotation
		 * 
		 * @param actor
		 *            the actor selected
		 */
		public void startRotation(Actor actor) {
			originalRotation = actor.getRotation();
			startingAngle = tmp1
					.set(getX(), getY())
					.sub(handles[ORIGIN_HANDLE_INDEX].getX(),
							handles[ORIGIN_HANDLE_INDEX].getY()).angle();
		}

		/**
		 * Sets the intended position for the handle and updates the rotation
		 * 
		 * @param actor
		 *            the actor selected
		 * @param x
		 *            new position for the handle
		 * @param y
		 *            new position for the handle
		 */
		public void updateRotation(Actor actor, float x, float y) {
			float rotation = tmp1
					.set(x, y)
					.sub(handles[ORIGIN_HANDLE_INDEX].getX(),
							handles[ORIGIN_HANDLE_INDEX].getY()).angle()
					+ originalRotation - startingAngle;
			actor.setRotation(rotation
					- (rotationStep > 0.0f ? rotation % rotationStep : 0.0f));
		}

	}
}
