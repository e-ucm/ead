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
package es.eucm.ead.editor.view.widgets.groupeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Contains all the logic to transform actors. It contains 10 handles: 8 handles
 * to resize, 1 handle to change the origin and 1 handle to rotate. Handles are
 * distributed as follows:
 * 
 * <pre>
 *           9
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
 * Where 9 is the rotation point and 4 the origin point. Each number represent
 * its index in {@link #handles}</p>
 * <p>
 * All transformations are based on the position of this 10 handles.
 * </p>
 * <p>
 * The method {@link #readActorTransformation()} reads the transformation of an
 * actor ({@link #setInfluencedActor(Actor)}) and redistribute the handles to
 * match its transformation.
 * </p>
 * <p>
 * The method {@link #applyHandleTransformation()} performs the inverse
 * operation: applies the transformation represented by the handles to the given
 * actor.
 * </p>
 */
public class Handles extends Group {

	public final Vector2 tmp1 = new Vector2(), tmp2 = new Vector2(),
			tmp3 = new Vector2(), tmp4 = new Vector2(), tmp5 = new Vector2();

	public static final float ROTATION_STEP = 15.0f;

	public static final int ORIGIN_HANDLE_INDEX = 4;

	public static final int ROTATION_HANDLE_INDEX = 9;

	private int handleSquareSize;

	private int handleCircleSize;

	private int rotationHandleOffset;

	private ShapeRenderer shapeRenderer;

	private Modifier modifier;

	private Handle[] handles;

	private Actor influencedActor;

	private boolean keepAspectRatio = false;

	private boolean alwaysKeepAspectRatio = false;

	private float aspectRatio = 1.0f;

	public Handles(ShapeRenderer shapeRenderer, Modifier modifier,
			GroupEditorConfiguration config) {

		rotationHandleOffset = config.rotationHandleOffset;
		handleCircleSize = config.handleCircleSize;
		handleSquareSize = config.handleSquareSize;

		this.shapeRenderer = shapeRenderer;
		this.modifier = modifier;
		handles = new Handle[10];
		for (int i = 0; i < 9; i++) {
			if (i == ORIGIN_HANDLE_INDEX) {
				handles[i] = new OriginHandle(shapeRenderer);
			} else {
				handles[i] = new Handle(shapeRenderer);
			}
			addActor(handles[i]);
		}

		handles[ROTATION_HANDLE_INDEX] = new RotationHandle(shapeRenderer);
		addActor(handles[ROTATION_HANDLE_INDEX]);

		/*
		 * Set move constraints between handles. E.g., handle[0] is attached to
		 * handle[2] in the x axis and to handle[6] in the y axis.
		 * 
		 * These constraints are based on that only 3 handles are required to
		 * specify the transformation: 0 (origin), 2 (width) and 6 (height).
		 * What the rest actually do is move one or more of these 3 handles.
		 * 
		 * So each point only need to specify its constraints with these 3 main
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
	}

	/**
	 * Sets if the transformation must keep the aspect ratio.
	 */
	public void setKeepAspectRatio(boolean keep) {
		this.keepAspectRatio = keep;
	}

	/**
	 * Sets the actor that the handles will influence. If the actor is a group
	 * with more than 1 child, the aspect ratio will be automatically kept, to
	 * enforce affine transformations.
	 */
	public void setInfluencedActor(Actor influencedActor) {
		this.influencedActor = influencedActor;
		if (influencedActor instanceof Group) {
			Group group = (Group) influencedActor;
			alwaysKeepAspectRatio = group.getChildren().size > 1;
		} else {
			alwaysKeepAspectRatio = false;
		}
		readActorTransformation();
		updateAspectRatio();
	}

	/**
	 * Reads the transformation of the current influenced actor and sets it to
	 * the handles
	 */
	public void readActorTransformation() {
		if (influencedActor != null) {
			for (int i = 0; i <= 2; i++) {
				for (int j = 0; j <= 2; j++) {
					if (i == 1 && j == 1) {
						tmp1.set(influencedActor.getOriginX(),
								influencedActor.getOriginY());
					} else {
						float x = influencedActor.getWidth() / 2.0f * j;
						float y = influencedActor.getHeight() / 2.0f * i;
						tmp1.set(x, y);
					}
					influencedActor.localToParentCoordinates(tmp1);
					Handle handle = handles[i * 3 + j];
					handle.setX(tmp1.x);
					handle.setY(tmp1.y);
				}
			}
			updateHandlesScale();
		}
	}

	/**
	 * Updates the aspect ratio, reading the current influenced actor
	 */
	public void updateAspectRatio() {
		aspectRatio = tmp1.set(handles[0].getX(), handles[0].getY())
				.sub(handles[2].getX(), handles[2].getY()).len()
				/ tmp1.set(handles[0].getX(), handles[0].getY())
						.sub(handles[6].getX(), handles[6].getY()).len();
	}

	/**
	 * Applies the current transformation represented by the handles to given
	 * actor
	 */
	public void applyHandleTransformation() {
		Vector2 o = tmp1.set(handles[0].getX(), handles[0].getY());
		Vector2 t = tmp2.set(handles[2].getX(), handles[2].getY());
		Vector2 n = tmp3.set(handles[6].getX(), handles[6].getY());
		modifier.applyTransformation(influencedActor, o, t, n);
		readActorTransformation();
	}

	/**
	 * Updates the handles scale to keep them at the same stage-relative size
	 */
	public void updateHandlesScale() {
		for (Handle handle : handles) {
			handle.stageToLocalCoordinates(tmp2.set(0, 0));
			if (handle instanceof OriginHandle
					|| handle instanceof RotationHandle) {
				handle.stageToLocalCoordinates(tmp3.set(handleCircleSize,
						handleCircleSize));
			} else {
				handle.stageToLocalCoordinates(tmp3.set(handleSquareSize,
						handleSquareSize));
			}
			tmp3.sub(tmp2);
			handle.setRadius(tmp3.len());
		}

		// Set rotation handle
		Vector2 o = localToStageCoordinates(tmp1.set(handles[0].getX(),
				handles[0].getY()));
		Vector2 n = localToStageCoordinates(
				tmp2.set(handles[6].getX(), handles[6].getY())).sub(o).nor();

		Vector2 top = localToStageCoordinates(tmp3.set(handles[7].getX(),
				handles[7].getY()));

		top.add(n.scl(rotationHandleOffset));

		stageToLocalCoordinates(top);
		handles[ROTATION_HANDLE_INDEX].setX(top.x);
		handles[ROTATION_HANDLE_INDEX].setY(top.y);
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_DST_COLOR);
		Gdx.gl.glBlendEquation(GL20.GL_FUNC_SUBTRACT);
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.WHITE);
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
		Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		// Handles gorup can NEVER be hit
		Actor actor = super.hit(x, y, touchable);
		return actor == this ? null : actor;
	}

	public class Handle extends Group {

		private ShapeRenderer shapeRenderer;

		private float radius;

		private Handle[] alignedX;

		private Handle[] alignedY;

		public Handle(ShapeRenderer shapeRenderer) {
			this.shapeRenderer = shapeRenderer;
		}

		/**
		 * Radius of the handle (use in drawing)
		 */
		public void setRadius(float radius) {
			this.radius = radius;
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
		 * @param x
		 *            new x for the handle
		 * @param y
		 *            new y for the handle
		 */
		public void setPosition(float x, float y) {
			super.setPosition(x, y);
			float rotation = influencedActor.getRotation();
			updateAligned(alignedX, rotation, rotation + 90);
			updateAligned(alignedY, rotation + 90, rotation);
			if (keepAspectRatio || alwaysKeepAspectRatio) {
				keepAspectRatio(rotation);
			}
			applyHandleTransformation();
			readActorTransformation();
		}

		private void keepAspectRatio(float rotation) {
			/*
			 * FIXME this can be done much better. I just don't know how. Yet.
			 */
			Vector2 t = tmp2.set(handles[2].getX(), handles[2].getY()).sub(
					handles[0].getX(), handles[0].getY());
			Vector2 n = tmp3.set(handles[6].getX(), handles[6].getY()).sub(
					handles[0].getX(), handles[0].getY());

			if (handles[1] == this || handles[7] == this) {
				Vector2 center = tmp1.set(handles[0].getX(), handles[0].getY())
						.add(t.scl(0.5f));
				n.scl(aspectRatio).scl(0.5f).rotate90(1);
				handles[0].setX(center.x + n.x);
				handles[0].setY(center.y + n.y);
				handles[2].setX(center.x - n.x);
				handles[2].setY(center.y - n.y);
			} else if (handles[5] == this || handles[3] == this) {
				Vector2 center = tmp1.set(handles[0].getX(), handles[0].getY())
						.add(n.scl(0.5f));
				t.scl(1.f / aspectRatio).scl(0.5f).rotate90(1);
				handles[6].setX(center.x + t.x);
				handles[6].setY(center.y + t.y);
				handles[0].setX(center.x - t.x);
				handles[0].setY(center.y - t.y);
			} else if (handles[8] == this) {
				if (t.len() * aspectRatio >= n.len()) {
					n.set(-t.y / aspectRatio, t.x / aspectRatio);
				} else {
					t.set(n.y * aspectRatio, -n.x * aspectRatio);
				}
				handles[2].setX(handles[0].getX() + t.x);
				handles[2].setY(handles[0].getY() + t.y);
				handles[6].setX(handles[0].getX() + n.x);
				handles[6].setY(handles[0].getY() + n.y);
				handles[8].setX(handles[6].getX() + t.x);
				handles[8].setY(handles[6].getY() + t.y);
			} else if (handles[0] == this) {
				if (n.len() * aspectRatio >= t.len()) {
					n.rotate90(1);
					n.scl(aspectRatio);
					n.add(handles[2].getX(), handles[2].getY());
					handles[0].setX(n.x);
					handles[0].setY(n.y);
				} else {
					t.rotate90(-1);
					t.scl(1.f / aspectRatio);
					t.add(handles[6].getX(), handles[6].getY());
					handles[0].setX(t.x);
					handles[0].setY(t.y);
				}
			} else if (handles[2] == this) {
				if (n.len() * aspectRatio >= t.len()) {
					n.rotate90(-1);
					n.scl(aspectRatio);
					n.add(handles[0].getX(), handles[0].getY());
					handles[2].setX(n.x);
					handles[2].setY(n.y);
				} else {
					float right = handles[2].getX() + n.x;
					float top = handles[2].getY() + n.y;
					t.rotate90(-1);
					t.scl(1.f / aspectRatio);
					t.add(right, top);
					handles[2].setX(t.x);
					handles[2].setY(t.y);
				}
			} else if (handles[6] == this) {
				if (n.len() * aspectRatio >= t.len()) {
					float right = handles[6].getX() + t.x;
					float top = handles[6].getY() + t.y;
					n.rotate90(1);
					n.scl(aspectRatio);
					n.add(right, top);
					handles[6].setX(n.x);
					handles[6].setY(n.y);
				} else {
					t.rotate90(1);
					t.scl(1.f / aspectRatio);
					t.add(handles[0].getX(), handles[0].getY());
					handles[6].setX(t.x);
					handles[6].setY(t.y);
				}
			}
			updateAligned(alignedX, rotation, rotation + 90);
			updateAligned(alignedY, rotation + 90, rotation);
		}

		@Override
		public Actor hit(float x, float y, boolean touchable) {
			return tmp1.set(x, y).len() < radius ? this : null;
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
					handle.setX(tmp5.x);
					handle.setY(tmp5.y);
				}
			}
		}

		@Override
		protected void drawChildren(Batch batch, float parentAlpha) {
			shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
			drawShape(shapeRenderer, radius);
		}

		protected void drawShape(ShapeRenderer shapeRenderer, float size) {
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.rect(-size / 2.0f, -size / 2.0f, size, size);
			shapeRenderer.end();
		}
	}

	public class OriginHandle extends Handle {

		public OriginHandle(ShapeRenderer shapeRenderer) {
			super(shapeRenderer);
		}

		@Override
		protected void drawShape(ShapeRenderer shapeRenderer, float size) {
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.x(0, 0, size);
			shapeRenderer.end();
		}

		/**
		 * Updates the origin of the given actor to match x and y
		 */
		public void setPosition(float x, float y) {
			float rotation = influencedActor.getRotation();
			Vector2 o = tmp1.set(handles[0].getX(), handles[0].getY());
			Vector2 t = tmp2.set(handles[2].getX(), handles[2].getY());
			Vector2 n = tmp3.set(handles[6].getX(), handles[6].getY());
			Vector2 origin = tmp4.set(x, y).sub(o);

			Vector2 vt = t.sub(o);
			Vector2 vn = n.sub(o);

			vt.rotate(-rotation);
			vn.rotate(-rotation);
			origin.rotate(-rotation);
			influencedActor.setOrigin(
					(origin.x / vt.x) * influencedActor.getWidth(),
					(origin.y / vn.y) * influencedActor.getHeight());
			super.setPosition(x, y);
		}
	}

	public class RotationHandle extends Handle {

		private float startingAngle;

		private float originalRotation;

		public RotationHandle(ShapeRenderer shapeRenderer) {
			super(shapeRenderer);
		}

		protected void drawShape(ShapeRenderer shapeRenderer, float size) {
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.circle(0, 0, size);
			shapeRenderer.end();
		}

		/**
		 * Initialize the rotation
		 * 
		 */
		public void startRotation() {
			originalRotation = influencedActor.getRotation();
			startingAngle = tmp1
					.set(getX(), getY())
					.sub(handles[ORIGIN_HANDLE_INDEX].getX(),
							handles[ORIGIN_HANDLE_INDEX].getY()).angle();
		}

		/**
		 * Sets the intended position for the handle and updates the rotation
		 * 
		 * @param x
		 *            new position for the handle
		 * @param y
		 *            new position for the handle
		 */
		public void setPosition(float x, float y) {
			float rotation = tmp1
					.set(x, y)
					.sub(handles[ORIGIN_HANDLE_INDEX].getX(),
							handles[ORIGIN_HANDLE_INDEX].getY()).angle()
					+ originalRotation - startingAngle;

			float rotationStep = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) ? ROTATION_STEP
					: 0.0f;

			influencedActor.setRotation(rotation
					- (rotationStep > 0.0f ? rotation % rotationStep : 0.0f));
			readActorTransformation();
		}

	}

	public Actor getInfluencedActor() {
		return influencedActor;
	}

}
