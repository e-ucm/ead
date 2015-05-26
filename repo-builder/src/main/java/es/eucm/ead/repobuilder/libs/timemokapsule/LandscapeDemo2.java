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

public class LandscapeDemo2 extends ExecutableDemoBuilder {

	public LandscapeDemo2() {
		super("landscape2");
	}

	@Override
	public String[] assetPaths() {
		return new String[] { "images/Space.png", "images/Space_lines.png",
				"images/Dust.png", "images/Planet.png", "images/Ring.png",
				"images/Ring2.png", "images/Moon.png", "images/Star.png" };
	}

	@Override
	protected void doBuild() {
		ModelEntity scene = game(1800, 1200).scene().getLastScene();

		ModelEntity background = entity(scene, 0, 0).getLastEntity();

		entity(background, assets[0], -1, -1);
		ModelEntity spaceLines = entity(background, assets[1], -1, 200)
				.getLastEntity();

		ModelEntity star1 = entity(background, assets[7], 50, 1100)
				.getLastEntity();
		star1.setScaleX(0.3F);
		star1.setScaleY(0.3F);
		ModelEntity star2 = entity(background, assets[7], 1300, 1000)
				.getLastEntity();
		star2.setScaleX(0.5F);
		star2.setScaleY(0.5F);
		ModelEntity star3 = entity(background, assets[7], 300, 50)
				.getLastEntity();
		star3.setScaleX(0.8F);
		star3.setScaleY(0.8F);
		ModelEntity star4 = entity(background, assets[7], 900, 500)
				.getLastEntity();

		ModelEntity dust1 = entity(background, assets[2], -1300, 860)
				.getLastEntity();
		ModelEntity dust2 = entity(background, assets[2], -3000, -100)
				.getLastEntity();
		ModelEntity dust3 = entity(background, assets[2], 190, 0)
				.getLastEntity();

		ModelEntity moon = entity(background, assets[6], 1300, 150)
				.getLastEntity();

		ModelEntity backRing = entity(background, assets[5], 363, 735)
				.getLastEntity();
		ModelEntity planet = entity(background, assets[3], 429, 480)
				.getLastEntity();
		ModelEntity frontRing = entity(background, assets[4], 146, 572)
				.getLastEntity();

		tween(spaceLines, MoveTween.class, 0F, -1, 0F, true, 5F, true,
				Tween.EaseEquation.LINEAR, Tween.EaseType.IN, 0F, -50F, null,
				null);
		tween(spaceLines, MoveTween.class, 0F, -1, 0F, true, 0.5F, true,
				Tween.EaseEquation.LINEAR, Tween.EaseType.IN, -10F, 0F, null,
				null);
		tween(spaceLines, AlphaTween.class, 0F, -1, 0F, true, 3F, false,
				Tween.EaseEquation.LINEAR, Tween.EaseType.IN, 0.5F, null, null,
				null);

		tween(dust2, MoveTween.class, 0F, -1, 0F, false, 90F, true,
				Tween.EaseEquation.LINEAR, Tween.EaseType.IN, 5000F, 1200F,
				null, null);

		moveObject(dust1, 14, 6, 14F);
		moveObject(dust3, -20, 9, 16F);
		moveObject(moon, -40, 20, 10F);

		animStar(star1, -0.2F, 0.2F, 1F);
		animStar(star2, -0.2F, 0.4F, 2F);
		animStar(star3, -0.2F, 0.6F, 3F);
		animStar(star4, -0.2F, 0.8F, 4F);

		moveObject(planet, -10, 5, 8F);
		tween(backRing, MoveTween.class, 0F, -1, 0F, true, 3F, true,
				Tween.EaseEquation.LINEAR, Tween.EaseType.IN, 8F, -30F, null,
				null);
		tween(frontRing, MoveTween.class, 0F, -1, 0F, true, 3F, true,
				Tween.EaseEquation.LINEAR, Tween.EaseType.IN, 8F, -30F, null,
				null);

	}

	public void animStar(ModelEntity entity, float scale, float alpha,
			float time) {
		centerOrigin(entity);
		Timeline timeline = new Timeline();
		timeline.setRepeat(-1);
		timeline.setYoyo(true);
		timeline.setDelay(0);
		timeline.setMode(Timeline.Mode.PARALLEL);
		timeline.getChildren().add(
				makeTween(ScaleTween.class, 0F, 0, 0F, false, time, false,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, scale,
						scale, null, null));
		timeline.getChildren().add(
				makeTween(AlphaTween.class, 0F, 0, 0F, false, time, false,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, alpha,
						null, null, null));

		entity.getComponents().add(timeline);
		tween(entity, RotateTween.class, 0F, -1, 0F, false, 3F, true,
				Tween.EaseEquation.LINEAR, Tween.EaseType.IN, 360F, null, null,
				null);
	}

	public void moveObject(ModelEntity entity, float n, float m, float time) {
		float t = time / 8;
		Timeline timeline = new Timeline();
		timeline.setRepeat(-1);
		timeline.setYoyo(false);
		timeline.setDelay(0);
		timeline.setMode(Timeline.Mode.SEQUENCE);
		timeline.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, t, true,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, -n, -m,
						null, null));
		timeline.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, t, true,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, -m, -n,
						null, null));
		timeline.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, t, true,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, m, -n,
						null, null));
		timeline.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, t, true,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, n, -m,
						null, null));
		timeline.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, t, true,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, n, m,
						null, null));
		timeline.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, t, true,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, m, n,
						null, null));
		timeline.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, t, true,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, -m, n,
						null, null));
		timeline.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, t, true,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, -n, m,
						null, null));
		entity.getComponents().add(timeline);
	}

	public static void main(String[] args) {
		LandscapeDemo2 landscapeDemo = new LandscapeDemo2();
		landscapeDemo.run();
	}

}