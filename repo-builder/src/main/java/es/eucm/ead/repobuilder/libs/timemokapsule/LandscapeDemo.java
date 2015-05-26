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
package es.eucm.ead.repobuilder.libs.timemokapsule;

import es.eucm.ead.engine.demobuilder.ExecutableDemoBuilder;
import es.eucm.ead.schema.components.tweens.*;
import es.eucm.ead.schema.entities.ModelEntity;

public class LandscapeDemo extends ExecutableDemoBuilder {

	public LandscapeDemo() {
		super("landscape");
	}

	@Override
	public String[] assetPaths() {
		return new String[] { "images/Sky.png", "images/Field.png",
				"images/Cloud_white.png", "images/Cloud_blue.png",
				"images/Cloud_small.png", "images/Sky_dust.png",
				"images/Skylines1.png", "images/Skylines2.png",
				"images/Skylines3.png", "images/Tower_launcher.png",
				"images/Space_shuttle.png", "images/Launcher_platform.png",
				"images/Shadow_launcher_platform.png",
				"images/Shadow_space_shuttle.png" };
	}

	@Override
	protected void doBuild() {
		ModelEntity scene = game(1800, 1280).scene().getLastScene();

		ModelEntity background = entity(scene, 0, 0).getLastEntity();

		// sky
		entity(background, assets[0], -1, 300);
		ModelEntity skyDust = entity(background, assets[5], 35, 900)
				.getLastEntity();
		ModelEntity skyline1 = entity(background, assets[6], -20, 860)
				.getLastEntity();
		ModelEntity skyline2 = entity(background, assets[7], -20, 550)
				.getLastEntity();
		ModelEntity skyline3 = entity(background, assets[8], -20, 340)
				.getLastEntity();

		// clouds
		entity(background, assets[3], 1080, 150);
		entity(background, assets[2], -230, 240);
		ModelEntity cloud3 = entity(background, assets[4], 600, 800)
				.scale(0.3f).getLastEntity();
		ModelEntity cloud4 = entity(background, assets[4], 50, 740).scale(0.5f)
				.getLastEntity();
		ModelEntity cloud5 = entity(background, assets[2], 1600, 860).scale(
				0.5f).getLastEntity();
		ModelEntity cloud6 = entity(background, assets[3], 300, 1000).scale(
				0.4f).getLastEntity();
		moveCloud(cloud3, 2F, 7F, -500);
		moveCloud(cloud4, 4F, 10F, -500);
		moveCloud(cloud5, 3F, 13F, -500);
		moveCloud(cloud6, 5F, 16F, -500);

		// Field
		entity(background, assets[1], -1, 0);

		// Dust
		entity(background, assets[4], 550, 400);
		entity(background, assets[4], 920, 470).scale(0.3f);

		// Shadows
		entity(background, assets[12], 315, 25);
		ModelEntity spaceShuttleShadow = entity(background, assets[13], 435,
				135).getLastEntity();
		spaceShuttleShadowTrip(spaceShuttleShadow);

		// tower of launcher
		entity(background, assets[9], 395, 350);

		ModelEntity spaceShuttle = entity(background, assets[10], 425, 355)
				.getLastEntity();

		spaceShuttleTrip(spaceShuttle);

		// launcher
		entity(background, assets[11], 335, 325);

		moveAndAlpha(skyline1, 0F, -1, 6F, true, 0, -50, 0,
				Tween.EaseEquation.LINEAR);
		moveAndAlpha(skyline2, 0F, -1, 7F, true, 0, -50, 0,
				Tween.EaseEquation.LINEAR);
		moveAndAlpha(skyline3, 0F, -1, 8F, true, 0, -50, 0,
				Tween.EaseEquation.LINEAR);

		tween(skyDust, AlphaTween.class, 0.5F, -1, 0F, true, 3F, false,
				Tween.EaseEquation.LINEAR, Tween.EaseType.IN, 0.5F, null, null,
				null);

	}

	public void spaceShuttleTrip(ModelEntity spaceShuttle) {
		Timeline trip = new Timeline();
		trip.setRepeat(-1);
		trip.setDelay(2F);
		trip.setRepeatDelay(10F);
		trip.setYoyo(false);
		trip.setMode(Timeline.Mode.SEQUENCE);
		trip.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, 6F, true,
						Tween.EaseEquation.QUART, Tween.EaseType.IN, 0F, 1500F,
						null, null));
		trip.getChildren().add(
				makeTween(MoveTween.class, 15F, 0, 0F, false, 6F, true,
						Tween.EaseEquation.QUART, Tween.EaseType.OUT, 0F,
						-1500F, null, null));

		spaceShuttle.getComponents().add(trip);
	}

	public void spaceShuttleShadowTrip(ModelEntity spaceShuttleShadow) {
		Timeline base = new Timeline();
		base.setRepeat(-1);
		base.setYoyo(false);
		base.setDelay(2F);
		base.setRepeatDelay(10F);
		base.setMode(Timeline.Mode.SEQUENCE);

		Timeline going = new Timeline();
		going.setRepeat(0);
		going.setYoyo(false);
		going.setRepeatDelay(0F);
		going.setDelay(0F);
		going.setMode(Timeline.Mode.PARALLEL);
		going.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, 6F, true,
						Tween.EaseEquation.QUART, Tween.EaseType.IN, 450F,
						-550F, null, null));
		going.getChildren().add(
				makeTween(AlphaTween.class, 0F, 0, 0F, false, 6F, false,
						Tween.EaseEquation.QUART, Tween.EaseType.IN, 0F, null,
						null, null));

		base.getChildren().add(going);

		Timeline back = new Timeline();
		back.setRepeat(0);
		back.setYoyo(false);
		back.setRepeatDelay(0F);
		back.setDelay(15F);
		back.setMode(Timeline.Mode.PARALLEL);
		back.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, 6F, true,
						Tween.EaseEquation.QUART, Tween.EaseType.OUT, -450F,
						550F, null, null));
		back.getChildren().add(
				makeTween(AlphaTween.class, 0F, 0, 0F, false, 6F, false,
						Tween.EaseEquation.QUART, Tween.EaseType.OUT, 1F, null,
						null, null));

		base.getChildren().add(back);

		spaceShuttleShadow.getComponents().add(base);
	}

	public void moveCloud(ModelEntity entity, float delay, float duration,
			float initPos) {
		tween(entity, MoveTween.class, 0F, 0, 0F, false, duration * 3 / 4,
				true, Tween.EaseEquation.LINEAR, Tween.EaseType.IN, 1800F, 0F,
				null, null);
		tween(entity, MoveTween.class, duration * 3 / 4, 0, 0F, false, 0F,
				false, Tween.EaseEquation.LINEAR, Tween.EaseType.IN, initPos,
				Float.NaN, null, null);
		tween(entity, MoveTween.class, duration * 3 / 4 + 1F, -1, delay, false,
				duration, true, Tween.EaseEquation.LINEAR, Tween.EaseType.IN,
				2400F, 0F, null, null);
	}

	public void moveAndAlpha(ModelEntity entity, float delay, int repeats,
			float time, boolean yoyo, float x, float y, float alpha,
			Tween.EaseEquation equation) {
		Timeline timeline = new Timeline();
		timeline.setRepeat(repeats);
		timeline.setYoyo(yoyo);
		timeline.setDelay(delay);
		timeline.setMode(Timeline.Mode.PARALLEL);
		timeline.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, time, true,
						equation, Tween.EaseType.IN, x, y, null, null));
		timeline.getChildren().add(
				makeTween(AlphaTween.class, 0F, 0, 0F, false, time, false,
						equation, Tween.EaseType.IN, alpha, null, null, null));
		entity.getComponents().add(timeline);
	}

	public static void main(String[] args) {
		LandscapeDemo landscapeDemo = new LandscapeDemo();
		landscapeDemo.run();
	}

}
