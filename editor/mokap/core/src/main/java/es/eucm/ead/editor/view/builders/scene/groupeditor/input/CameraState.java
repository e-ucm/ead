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

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import es.eucm.ead.editor.view.builders.scene.groupeditor.inputstatemachine.InputState;

public class CameraState extends InputState {

	public static final Vector2 tmp1 = new Vector2(), tmp2 = new Vector2();

	private EditStateMachine stateMachine;

	private float initialDistance;

	private float initialZoom;

	public CameraState(EditStateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}

	@Override
	public void enter() {
		stateMachine.enterFullScreen();
		touchDown(null, 0, 0, 0);
	}

	@Override
	public void touchDown(InputEvent event, float x, float y, int pointer) {
		if (stateMachine.pointers == 2) {
			reset();
		}
	}

	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer) {
		if (stateMachine.pointers == 0) {
			stateMachine.setState(NoPointersState.class);
		}
	}

	@Override
	public void exit() {
		stateMachine.exitPan();
	}

	@Override
	public void drag1(InputEvent event, float x, float y) {
		stateMachine.pan1();
	}

	@Override
	public void drag2(InputEvent event, float x, float y) {
		stateMachine.pan2();
	}

	@Override
	public void drag(InputEvent event, float x, float y, int pointer) {
		if (stateMachine.pointers == 2) {
			tmp1.set(stateMachine.pointer1);
			tmp2.set(stateMachine.pointer2);
			Vector2 center = tmp1.interpolate(tmp2, 0.5f, Interpolation.linear);
			System.out.println(stateMachine.pointer1 + ";"
					+ stateMachine.pointer2);
			System.out.println("Distance "
					+ stateMachine.pointer1.dst(stateMachine.pointer2));
			stateMachine.zoom(center.x, center.y,
					stateMachine.pointer1.dst(stateMachine.pointer2)
							* initialZoom / initialDistance);
		}
	}

	private void reset() {
		initialDistance = stateMachine.pointer1.dst(stateMachine.pointer2);
		System.out.println(stateMachine.pointer1 + ";" + stateMachine.pointer2);
		System.out.println("Initial distance "
				+ stateMachine.pointer1.dst(stateMachine.pointer2));
		initialZoom = stateMachine.getZoom();
	}
}
