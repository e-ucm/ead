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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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

	// Scale

	private float signumX;

	private float signumY;

	private float scale1X;

	private float scale1Y;

	private float distance1;

	private float size;

	private float aspectRatio;

	private float denominator;

	private float initialPointerRotation;

	private int pointers = 0;

	public ScaleState(EditStateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}

	@Override
	public void enter() {
		pointers = 2;
		rotationCancelled = false;
		if (stateMachine.getSelection().size == 0) {
			stateMachine.setState(NoPointersState.class);
		} else {
			Actor actor = stateMachine.getSelection().get(0);
			float scaleX = actor.getScaleX();
			float scaleY = actor.getScaleY();
			float width = actor.getWidth();
			float height = actor.getHeight();

			initialPointerRotation = temp.set(stateMachine.initialPointer1)
					.sub(stateMachine.initialPointer2).angle();
			scale = calculateState(stateMachine.initialPointer2,
					stateMachine.initialPointer1);
			switch (scale) {
			case HORIZONTAL:
				this.signumX = Math.signum(scaleX);
				this.scale1X = Math.abs(scaleX);
				distance1 = Math.abs(stateMachine.initialPointer2.x
						- stateMachine.initialPointer1.x);
				this.size = width;
				break;
			case VERTICAL:
				this.signumY = Math.signum(scaleY);
				this.scale1Y = Math.abs(scaleY);
				distance1 = Math.abs(stateMachine.initialPointer2.y
						- stateMachine.initialPointer1.y);
				this.size = height;
				break;
			case DIAGONAL:
				this.signumX = Math.signum(scaleX);
				this.signumY = Math.signum(scaleY);
				this.distance1 = temp.set(stateMachine.initialPointer2)
						.sub(stateMachine.initialPointer1).len();
				size = temp.set(width * scaleX, height * scaleY).len();
				aspectRatio = Math.abs(scaleX / scaleY);
				denominator = (float) Math.sqrt(width * width * aspectRatio
						* aspectRatio + height * height);
				break;
			}
			Gdx.app.debug("State", scale + "");

		}
	}

	@Override
	public void exit() {
		stateMachine.fireTransformed();
	}

	@Override
	public void drag1(InputEvent event, float x, float y) {
		updateTransformation();
	}

	@Override
	public void drag2(InputEvent event, float x, float y) {
		updateTransformation();
	}

	@Override
	public void touchUp1(InputEvent event, float x, float y) {
		touchUp();
	}

	@Override
	public void touchUp2(InputEvent event, float x, float y) {
		touchUp();
	}

	@Override
	public void touchDown1(InputEvent event, float x, float y) {
		enter();
	}

	@Override
	public void touchDown2(InputEvent event, float x, float y) {
		enter();
	}

	private void touchUp() {
		pointers--;
		if (pointers <= 0) {
			stateMachine.setState(NoPointersState.class);
		}
	}

	public void updateTransformation() {
		Vector2 pointer1 = stateMachine.pointer1;
		Vector2 pointer2 = stateMachine.pointer2;
		Actor actor = stateMachine.getSelection().get(0);
		if (Math.abs(distance1 - temp.set(pointer1).sub(pointer2).len()) > DISTANCE_CANCEL) {
			rotationCancelled = true;
		}

		if (!rotationCancelled) {
			float diffRotation = Math.abs(temp.set(pointer1).sub(pointer2)
					.angle()
					- initialPointerRotation);
			if (diffRotation > INIT_ROTATE_ANGLE) {
				stateMachine.setState(RotateState.class);
				return;
			}
		}

		switch (scale) {
		case HORIZONTAL:
			actor.setScaleX(signumX
					* (Math.abs(pointer2.x - pointer1.x) - distance1 + size
							* scale1X) / size);
			break;
		case VERTICAL:
			actor.setScaleY(signumY
					* (Math.abs(pointer2.y - pointer1.y) - distance1 + size
							* scale1Y) / size);
			break;
		case DIAGONAL:
			float scaleY = Math.abs((size
					+ temp.set(pointer1).sub(pointer2).len() - distance1)
					/ denominator);
			float scaleX = aspectRatio * scaleY;
			actor.setScale(scaleX * signumX, scaleY * signumY);
			break;
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

}
