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

import es.eucm.ead.editor.demobuilder.DemoBuilder;
import es.eucm.ead.engine.variables.VarsContext;
import es.eucm.ead.schema.components.positiontracking.ChaseEntity;
import es.eucm.ead.schema.components.positiontracking.MoveByEntity;
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.ScaleTween;
import es.eucm.ead.schema.data.Script;
import es.eucm.ead.schema.effects.AddComponent;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.controlstructures.If;
import es.eucm.ead.schema.effects.controlstructures.IfThenElseIf;
import es.eucm.ead.schema.effects.controlstructures.ScriptCall;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Created by Javier Torrente on 2/07/14.
 */
public class PlanesDemo extends DemoBuilder {

	private static final String chasedPlaneVar = "chasedEntity";
	private static final String chasedPlaneVarRef = "$" + chasedPlaneVar;
	private static final String newestEntityRef = "$"
			+ VarsContext.RESERVED_NEWEST_ENTITY_VAR;

	public PlanesDemo() {
		super("planes-demo");
	}

	@Override
	public String[] assetPaths() {
		return new String[] { "images/background.png", "images/rocks_down.png",
				"images/rocks_up.png", "images/starGold.png", "plane/red.json" };
	}

	@Override
	protected void doBuild() {
		// Build game with one scene with background image
		ModelEntity firstScene = singleSceneGame(assets[0]).getLastScene();
		// Add parallax to background
		parallax(firstScene.getChildren().get(0), 0F);
		// Create up and down rocks
		entity(firstScene, assets[1], VerticalAlign.DOWN, HorizontalAlign.LEFT)
				.parallax(0.5f);
		entity(firstScene, assets[2], VerticalAlign.UP, HorizontalAlign.LEFT)
				.parallax(0.5f);

		/*
		 * Add init behavior to the scene with two effects: 1) Register var that
		 * points to plane being tracked 2) Add component to the camera for
		 * chasing the plane being tracked
		 */
		initBehavior(firstScene);
		changeVar(chasedPlaneVar, newestEntityRef, ChangeVar.Context.GLOBAL);
		addComponent("(layer scamera)", makeCameraTracking());

		// Create script running all the time that creates a plane per second
		infiniteTimer(firstScene, 1, makeAddPlaneToSceneScript());

		// Create the star (chases the tracked plane)
		entity(firstScene, assets[3], 20, 400).tags("star");
	}

	@Override
	public String getDescription() {
		return "This demo provides a single scene where different planes come from both sides of the screen. Touch any of the planes to track it across the screen!\n"
				+ "The demo demonstrates the following aspects:\n"
				+ "- Animation: planes are animated through frames and moveTweens.\n"
				+ "- Randomness: four parameters of the planes are generated randomly:\n"
				+ "\t* Color (red, green, blue, yellow)\n"
				+ "\t* Duration (how long it takes the plane to cross the scene)\n"
				+ "\t* Y coordinate\n"
				+ "\t* Move from left to right or from right to left\n"
				+ "- Components used for modifying one entity's position according to other entity's movement:\n"
				+ "\t* Parallax: background image and rocks have parallax components to give a sense of z-depth\n"
				+ "\t* MoveByEntity: camera moves following the tracked plane\n"
				+ "\t* ChaseEntity: star follows the tracked plane, trying to keep at a minimum distance.";
	}

	/*
	 * Creates the component that makes the camera follow the tracked plane
	 */
	private MoveByEntity makeCameraTracking() {
		MoveByEntity moveByEntity = new MoveByEntity();
		moveByEntity.setTarget(chasedPlaneVarRef);
		moveByEntity.setSpeedX(-1.0F);
		moveByEntity.setSpeedY(0.0F);
		return moveByEntity;
	}

	/*
	 * Creates the script that creates a random plane, loaded from disk
	 * (reusable entity). Random parameters: - Color: red, blue, green, yellow -
	 * Orientation (position to start from): left (screen start) / right (screen
	 * end) - Y: the vertical coordinate to move along, from 80 to 270 -
	 * duration: how long it takes to cross the whole scene (from 15 to 30
	 * seconds)
	 */
	private ScriptCall makeAddPlaneToSceneScript() {
		ScriptCall scriptCall = new ScriptCall();
		scriptCall.getInputArgumentValues().add("(rand i0 i4)");
		scriptCall.getInputArgumentValues().add("(rand i0 i2)");
		scriptCall.getInputArgumentValues().add("(rand i80 i270)");
		scriptCall.getInputArgumentValues().add("(rand i15 i30)");

		Script script = new Script();
		script.getInputArguments().add("color");
		script.getInputArguments().add("coin");
		script.getInputArguments().add("y");
		script.getInputArguments().add("duration");

		// Create the entities that represent the planes and save them as
		// reusable entities
		String[] planeUris = new String[] { makeReusablePlane("Red"),
				makeReusablePlane("Blue"), makeReusablePlane("Green"),
				makeReusablePlane("Yellow") };

		// Create if-then-else structure to create blocks that create either a
		// red, a blue, a green or a yellow plane.
		IfThenElseIf ifThenElseIf = new IfThenElseIf();
		effect(script, ifThenElseIf);
		scriptCall.setScript(script);

		int i = 0;
		for (String uri : planeUris) {
			If ifEffect = null;
			if (ifThenElseIf.getEffects().size == 0) {
				ifEffect = ifThenElseIf;
			} else {
				ifEffect = new If();
				ifThenElseIf.getElseIfList().add(ifEffect);
			}

			// Calculate the color of the plane and add it to the scene. Setup
			// "left" var to determine plane orientation.
			ifEffect.setCondition("(eq $color i0)".replace("0", "" + i));
			effect(ifEffect, makeChangeVar("left", "(eq $coin i0)"));
			effect(ifEffect, makeAddEntity(uri));

			// Flip plane if it starts from right to left
			If flipIf = makeControlStructure(If.class, "(not $left)");
			AddComponent setShipOrientation = makeAddComponent(newestEntityRef,
			// makeTween(ScaleTween.class, null, null, null, null, 0F,
			// true, null, null, -2.0F, null, null, null));
					makeMirrorEntityTween(1));
			effect(flipIf, setShipOrientation);
			effect(ifEffect, flipIf);

			// Place plane either left or right depending on the orientation
			MoveTween positionSpaceship = makeTween(MoveTween.class);
			parameter(positionSpaceship, "x", "(? $left i-100 i3700)");
			parameter(positionSpaceship, "y", "$y");
			addComponent(ifEffect, newestEntityRef, positionSpaceship);

			// Animation that makes the plane move across the scene
			MoveTween spaceMove = makeTween(MoveTween.class);
			spaceMove.setRelative(true);
			spaceMove.setDelay(0.1f);
			parameter(spaceMove, "x", "(? $left i3000 i-3000)");
			parameter(spaceMove, "duration", "$duration");
			addComponent(ifEffect, newestEntityRef, spaceMove);

			// Cool fade-out effect that makes the plane disappear from scene
			// when it has reached the end of the scene
			AlphaTween fadeOutEffect = new AlphaTween();
			fadeOutEffect.setRelative(false);
			fadeOutEffect.setAlpha(0.0F);
			fadeOutEffect.setDuration(0.5f);
			parameter(fadeOutEffect, "delay", "$duration");
			addComponent(ifEffect, newestEntityRef, fadeOutEffect);

			i++;
		}

		return scriptCall;
	}

	/*
	 * Make plane and save it to disk
	 */
	private String makeReusablePlane(String color) {
		String uri = "plane/red.json".replace("red", color.toLowerCase());
		reusableEntity(uri, "images/planeRed1.png".replace("Red", color), -100,
				0).frame("images/planeRed2.png".replace("Red", color), 0.2F)
				.frame("images/planeRed3.png".replace("Red", color), 0.2F);

		touchBehavior().changeVar(chasedPlaneVar, "$" + VarsContext.THIS_VAR)
				.addComponent(makeEntitiesWithTagExp("star"),
						makeStarChaseAfterSpaceship());
		return uri;
	}

	/*
	 * Makes the component that forces the star to follow the chased entity
	 */
	private ChaseEntity makeStarChaseAfterSpaceship() {
		ChaseEntity chaseEntity = new ChaseEntity();
		chaseEntity.setRelativeSpeed(false);
		chaseEntity.setSpeedX(25);
		chaseEntity.setSpeedY(25);
		chaseEntity.setMinDistance(28);
		chaseEntity.setMaxDistance(30);
		chaseEntity.setTarget(chasedPlaneVarRef);
		return chaseEntity;
	}

	public static void main(String[] args) {
		PlanesDemo planesDemo = new PlanesDemo();
		planesDemo.run();
	}
}
