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
package es.eucm.ead.editor.view.builders.scene.groupeditor.inputstatemachine;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class InputStateMachine extends InputListener {

	public static final float LONG_PRESS_TIME = 1.1f;

	private float tapSquareSize = 14;

	private InputState currentState;

	private ObjectMap<Class, InputState> states = new ObjectMap<Class, InputState>();

	public Vector2 initialPointer1 = new Vector2();

	public Vector2 initialPointer2 = new Vector2();

	public Vector2 pointer1 = new Vector2();

	public Vector2 pointer2 = new Vector2();

	public int pointers;

	private boolean dragging1;

	private boolean dragging2;

	private float deltaX1;

	private float deltaY1;

	private float deltaX2;

	private float deltaY2;

	private InputState nextState;

	private final Task longPressTask = new Task() {
		@Override
		public void run() {
			currentState.longPress(initialPointer1.x, initialPointer1.y);
		}
	};

	public void addState(InputState state) {
		states.put(state.getClass(), state);
	}

	public void setState(Class nextStateClass) {
		nextState = states.get(nextStateClass);
	}

	private void updateState() {
		InputState previousState = currentState;
		currentState = nextState;
		nextState = null;
		if (previousState != null) {
			previousState.exit();
		}
		currentState.enter();
	}

	@Override
	public boolean handle(Event e) {
		if (nextState != null) {
			updateState();
		}
		return super.handle(e);
	}

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer,
			int button) {
		pointers++;
		if (pointer == 0) {
			initialPointer1.set(x, y);
			pointer1.set(x, y);
			Timer.schedule(longPressTask, LONG_PRESS_TIME);
			currentState.touchDown1(event, x, y);
		} else {
			longPressTask.cancel();
			if (pointer == 1) {
				initialPointer2.set(x, y);
				pointer2.set(x, y);
				currentState.touchDown2(event, x, y);
			}
		}
		currentState.touchDown(event, x, y, pointer);
		return pointer < 2;
	}

	@Override
	public void touchDragged(InputEvent event, float x, float y, int pointer) {
		if (pointer == 0) {
			pointer1.set(x, y);
		} else if (pointer == 1) {
			pointer2.set(x, y);
		}

		if (pointer == 0) {
			if (!dragging1
					&& (Math.abs(initialPointer1.x - x) > tapSquareSize || Math
							.abs(initialPointer1.y - y) > tapSquareSize)) {
				dragging1 = true;
				dragStart(event, x, y, pointer);
				deltaX1 = x;
				deltaY1 = y;
			}

			if (dragging1) {
				deltaX1 -= x;
				deltaY1 -= y;
				drag(event, x, y, pointer);
				deltaX1 = x;
				deltaY1 = y;
			}
		} else if (pointer == 1) {
			if (!dragging2
					&& (Math.abs(initialPointer2.x - x) > tapSquareSize || Math
							.abs(initialPointer2.y - y) > tapSquareSize)) {
				dragging2 = true;
				dragStart(event, x, y, pointer);
				deltaX2 = x;
				deltaY2 = y;
			}

			if (dragging2) {
				deltaX2 -= x;
				deltaY2 -= y;
				drag(event, x, y, pointer);
				deltaX2 = x;
				deltaY2 = y;
			}
		}
	}

	public float getDeltaX1() {
		return deltaX1;
	}

	public float getDeltaY1() {
		return deltaY1;
	}

	public float getDeltaX2() {
		return deltaX2;
	}

	public float getDeltaY2() {
		return deltaY2;
	}

	public void drag(InputEvent event, float x, float y, int pointer) {
		currentState.drag(event, x, y, pointer);
		if (pointer == 0) {
			currentState.drag1(event, x, y);
		} else if (pointer == 1) {
			currentState.drag2(event, x, y);
		}
	}

	public void dragStart(InputEvent event, float x, float y, int pointer) {
		longPressTask.cancel();
		currentState.dragStart(event, x, y, pointer);
		if (pointer == 0) {
			currentState.dragStart1(event, x, y);
		} else if (pointer == 1) {
			currentState.dragStart2(event, x, y);
		}
	}

	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer,
			int button) {
		pointers--;
		longPressTask.cancel();
		currentState.touchUp(event, x, y, pointer);
		if (pointer == 0) {
			dragging1 = false;
			currentState.touchUp1(event, x, y);
		} else if (pointer == 1) {
			dragging2 = false;
			currentState.touchUp2(event, x, y);
		}
		super.touchUp(event, x, y, pointer, button);
	}
}
