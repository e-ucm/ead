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
package es.eucm.ead.engine.demos;

import com.badlogic.gdx.Input;
import es.eucm.ead.engine.demobuilder.ExecutableDemoBuilder;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.components.physics.Acceleration;
import es.eucm.ead.schema.components.physics.Gravity;
import es.eucm.ead.schema.components.physics.Mass;
import es.eucm.ead.schema.components.tweens.RotateTween;
import es.eucm.ead.schema.components.tweens.Tween;
import es.eucm.ead.schema.effects.*;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Created by jtorrente on 22/05/2015.
 */
public class GravityDemo extends ExecutableDemoBuilder {

	public GravityDemo() {
		super("gravity");
	}

	@Override
	public String getDescription() {
		return "This demo shows how gravity and acceleration components work.\n"
				+ "\nINSTRUCTIONS:\n\n"
				+ "Press the space bar to set the rocket's accelerator.\n"
				+ "The more acceleration, the faster it will take off.\n"
				+ "After 3 seconds, the rocket stops accelerating and a Mass\n"
				+ "component is added, so it becomes affected by gravity.\n"
				+ "It will start to fall infinitely!.";
	}

	@Override
	public String getName() {
		return "Gravity and Acceleration";
	}

	@Override
	protected void doBuild() {

		// Create the scene and the rocket.
		// Init var "launch" used to determine if ignition has started - has to
		// be declared as it is constantly read through Visibility comp.
		ModelEntity rocket = singleSceneGame("background.png", 1024, 1024)
				.initBehavior(
						makeChangeVar("launch", "bfalse",
								ChangeVar.Context.GLOBAL))
				.entity((String) null, 398, 221).tags("rocket").getLastEntity();
		// Gravity affects the scene
		getLastScene().getComponents().add(new Gravity());

		// The rocket is only visible once launched
		entity(rocket, "rocket_flame.png", 55, -300).visibility("$launch");
		entity(rocket, "rocket.png", 0, 0);

		// Create the joystick and make it rotate
		ModelEntity joystick = entity(getLastScene(), null, 135, 141)
				.getLastEntity();
		ModelEntity handle = entity(joystick, "joystick_handle.png", 34.5F, 10)
				.origin(18, 0)
				.tween(RotateTween.class, 0F, -1, 0F, true, 1.2F, true,
						Tween.EaseEquation.SINE, Tween.EaseType.INOUT, -90F,
						null, null, null).getLastEntity();
		entity(joystick, "joystick_base.png", 0, 0);

		/*
		 * Create the acceleration component that will launch the rocket. The Y
		 * value of the component is dynamically determined as a function of the
		 * angle the handle had when stopped
		 */
		Acceleration acceleration = new Acceleration();
		acceleration.setY(10);
		int max = 1000;
		int min = 40;
		// y = [(max-min)/maxAngle]*(maxAngle+angle)+min
		// This assumes angle goes from 0 to -maxAngle (maxAngle is positive).
		// Max acceleration is achieved when angle=0
		String exp = "(+ (* (/ (- i" + max + " i" + min
				+ ") i90) (+ i90 $angle)) i" + min + ")";
		acceleration.getParameters().add(param("y", exp));

		/*
		 * Create the key behavior that ignites the rocket: 1) Stops handle
		 * oscillation 2) Sets angle variable (handle's rotation) 3) Sets launch
		 * variable to true 4) Adds the acceleration component to the rocket 5)
		 * Adds a timer that will remove the acceleration after 5 seconds, and
		 * add a mass component so the rocket becomes affected by gravity
		 */
		String rocketTag = "(get (collection sentity (hastag $entity srocket)) i0)";
		simpleKeyBehavior(
				handle,
				Input.Keys.SPACE,
				makeRemoveComponent(RotateTween.class),
				makeChangeVar("angle", "(prop $_this sgroup.rotation)",
						ChangeVar.Context.GLOBAL),
				makeChangeVar("launch", "btrue", ChangeVar.Context.GLOBAL),
				makeAddComponent(rocketTag, acceleration),
				makeAddComponent(
						rocketTag,
						makeSimpleTimer(
								3,
								makeRemoveComponent(rocketTag,
										Acceleration.class),
								makeAddComponent(rocketTag, new Mass()))));

		Label label = new Label();
		label.setText("Press space bar to set the rocket's accelerator!");
		entity(getLastScene(), null, 50, 900).getLastEntity().getComponents()
				.add(label);
		scale(0.4F);
	}

}
