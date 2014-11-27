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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.view.builders.scene.groupeditor.inputstatemachine.InputState;

public class RotateState extends InputState {

	private EditStateMachine stateMachine;

	private Vector2 temp = new Vector2();

	private float initialPointerRotation;

	private Array<ActorRotation> actorRotations = new Array<ActorRotation>();

	public RotateState(EditStateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}

	@Override
	public void enter() {
		initialPointerRotation = temp.set(stateMachine.initialPointer1)
				.sub(stateMachine.initialPointer2).angle();

		actorRotations.clear();
		for (Actor actor : stateMachine.getSelection()) {
			actorRotations.add(new ActorRotation(actor));
		}
	}

	@Override
	public void exit() {
		stateMachine.fireTransformed();
	}

	@Override
	public void touchDown2(InputEvent event, float x, float y) {
		enter();
	}

	@Override
	public void drag1(InputEvent event, float x, float y) {
		drag();
	}

	@Override
	public void dragStart2(InputEvent event, float x, float y) {
		drag();
	}

	public void drag() {
		float angle = temp.set(stateMachine.pointer1)
				.sub(stateMachine.pointer2).angle()
				- initialPointerRotation;
		for (ActorRotation actorRotation : actorRotations) {
			actorRotation.update(angle);
		}
	}

	@Override
	public void touchUp1(InputEvent event, float x, float y) {
		stateMachine.setState(NoPointersState.class);
	}

	public class ActorRotation {

		private Actor actor;

		private float rotation;

		public ActorRotation(Actor actor) {
			this.actor = actor;
			this.rotation = actor.getRotation();
		}

		public void update(float angle) {
			actor.setRotation(rotation + angle);
		}
	}
}
