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
package es.eucm.ead.editor.view.listeners;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

/**
 * Listens to long press
 */
public class LongPressListener extends DragListener {

	public static final float LONG_PRESS_TIME = 1.1f;

	private Vector2 initialPointer = new Vector2();

	private final Task longPressTask = new Task() {
		@Override
		public void run() {
			longPress(initialPointer.x, initialPointer.y);
		}
	};

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer,
			int button) {
		if (pointer == 0) {
			initialPointer.set(x, y);
			if (!longPressTask.isScheduled()) {
				Timer.schedule(longPressTask, LONG_PRESS_TIME);
			}
		}
		return super.touchDown(event, x, y, pointer, button);
	}

	@Override
	public void dragStart(InputEvent event, float x, float y, int pointer) {
		longPressTask.cancel();
	}

	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer,
			int button) {
		longPressTask.cancel();
		super.touchUp(event, x, y, pointer, button);
	}

	public void longPress(float x, float y) {
	}
}
