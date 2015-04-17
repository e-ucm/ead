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
package es.eucm.ead.editor.view.builders.scene.groupeditor.input;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.view.builders.scene.groupeditor.inputstatemachine.InputState;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;

public class ScaleState extends InputState {

	private EditStateMachine stateMachine;

	public static float INIT_ROTATE_ANGLE = 15.0f;

	public static float DISTANCE_CANCEL = WidgetBuilder
			.dpToPixels(WidgetBuilder.UNIT_SIZE);

	public static float TRIGONOMETRIC_TOLERANCE = MathUtils
			.cosDeg(INIT_ROTATE_ANGLE);

	public enum Scale {
		HORIZONTAL, VERTICAL, DIAGONAL
	}

	private Scale scale;

	private Vector2 temp = new Vector2();

	private boolean rotationCancelled;

	private float distance1;

	private float initialPointerRotation;

	private Array<ActorTransformation> transformations = new Array<ActorTransformation>();

	public ScaleState(EditStateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}

	@Override
	public void enter() {
		rotationCancelled = false;
		if (stateMachine.getSelection().size == 0
				|| stateMachine.isOnlySelection()) {
			stateMachine.setState(CameraState.class);
		} else {
			initialPointerRotation = temp.set(stateMachine.initialPointer1)
					.sub(stateMachine.initialPointer2).angle();
			scale = calculateState(stateMachine.initialPointer2,
					stateMachine.initialPointer1);
			switch (scale) {
			case HORIZONTAL:
				distance1 = Math.abs(stateMachine.initialPointer2.x
						- stateMachine.initialPointer1.x);
				break;
			case VERTICAL:
				distance1 = Math.abs(stateMachine.initialPointer2.y
						- stateMachine.initialPointer1.y);
				break;
			case DIAGONAL:
				this.distance1 = temp.set(stateMachine.initialPointer2)
						.sub(stateMachine.initialPointer1).len();
				break;
			}
			transformations.clear();
			for (Actor actor : stateMachine.getSelection()) {
				transformations.add(new ActorTransformation(actor));
			}
		}
	}

	@Override
	public void exit() {
		stateMachine.fireTransformed();
	}

	@Override
	public void touchDown(InputEvent event, float x, float y, int pointer) {
		enter();
	}

	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer) {
		if (stateMachine.pointers <= 0) {
			stateMachine.setState(NoPointersState.class);
		}
	}

	@Override
	public void drag(InputEvent event, float x, float y, int pointer) {
		Vector2 pointer1 = stateMachine.pointer1;
		Vector2 pointer2 = stateMachine.pointer2;
		if (Math.abs(distance1 - temp.set(pointer1).sub(pointer2).len()) > DISTANCE_CANCEL) {
			rotationCancelled = true;
		}

		if (!rotationCancelled) {
			float diffRotation = Math.abs(temp.set(pointer1).sub(pointer2)
					.angle()
					- initialPointerRotation);
			if (diffRotation > INIT_ROTATE_ANGLE) {
				for (ActorTransformation actorTransformation : transformations) {
					actorTransformation.resetRotation();
				}
				stateMachine.setState(RotateState.class);
				return;
			}
		}

		for (ActorTransformation actorTransformation : transformations) {
			actorTransformation.updateTransformation();
		}

	}

	private Scale calculateState(Vector2 initalPointer2, Vector2 initialPointer1) {
		float angle = temp.set(initalPointer2).sub(initialPointer1).angle();

		if (Math.abs(MathUtils.cosDeg(angle)) > TRIGONOMETRIC_TOLERANCE) {
			return Scale.HORIZONTAL;
		} else if (Math.abs(MathUtils.sinDeg(angle)) > TRIGONOMETRIC_TOLERANCE) {
			return Scale.VERTICAL;
		}
		return Scale.DIAGONAL;
	}

	public class ActorTransformation {

		private Actor actor;

		private float signumX;

		private float signumY;

		private float scale1X;

		private float scale1Y;

		private float size;

		private float aspectRatio;

		private float denominator;

		private float rotation;

		public ActorTransformation(Actor actor) {
			this.actor = actor;
			this.rotation = actor.getRotation();
			float scaleX = actor.getScaleX();
			float scaleY = actor.getScaleY();
			float width = actor.getWidth();
			float height = actor.getHeight();

			switch (scale) {
			case HORIZONTAL:
				this.signumX = Math.signum(scaleX);
				this.scale1X = Math.abs(scaleX);
				this.size = width;
				break;
			case VERTICAL:
				this.signumY = Math.signum(scaleY);
				this.scale1Y = Math.abs(scaleY);
				this.size = height;
				break;
			case DIAGONAL:
				this.signumX = Math.signum(scaleX);
				this.signumY = Math.signum(scaleY);
				size = temp.set(width * scaleX, height * scaleY).len();
				aspectRatio = Math.abs(scaleX / scaleY);
				denominator = (float) Math.sqrt(width * width * aspectRatio
						* aspectRatio + height * height);
				break;
			}
		}

		public void updateTransformation() {
			Vector2 pointer1 = stateMachine.pointer1;
			Vector2 pointer2 = stateMachine.pointer2;
			float zoom = stateMachine.getZoom();
			switch (scale) {
			case HORIZONTAL:
				actor.setScaleX(signumX
						* ((Math.abs(pointer2.x - pointer1.x) - distance1)
								/ zoom + size * scale1X) / size);
				break;
			case VERTICAL:
				actor.setScaleY(signumY
						* ((Math.abs(pointer2.y - pointer1.y) - distance1)
								/ zoom + size * scale1Y) / size);
				break;
			case DIAGONAL:
				float scaleY = Math.abs((size + (temp.set(pointer1)
						.sub(pointer2).len() - distance1)
						/ zoom)
						/ denominator);
				float scaleX = aspectRatio * scaleY;
				actor.setScale(scaleX * signumX, scaleY * signumY);
				break;
			}
		}

		public void resetRotation() {
			actor.setRotation(rotation);
		}
	}

}
