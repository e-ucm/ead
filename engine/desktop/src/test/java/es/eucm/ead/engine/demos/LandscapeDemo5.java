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

import es.eucm.ead.engine.demobuilder.ExecutableDemoBuilder;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.tweens.*;
import es.eucm.ead.schema.data.Dimension;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Renderer;

public class LandscapeDemo5 extends ExecutableDemoBuilder {

	public LandscapeDemo5() {
		super("landscape5");
	}

	@Override
	public String[] assetPaths() {
		return new String[] { "images/Sky.png", "images/Sun.png",
				"images/Field.png", "images/Tree1.png", "images/Tree2.png",
				"images/Grass.png", "images/Leaf1.png", "images/Leaf2.png",
				"images/Leaf3.png", "images/Leaf4.png", "images/Cloud1.png",
				"images/Cloud2.png", "images/Cloud3.png", "images/Cloud4.png",
				"images/Cloud5.png", "images/Birds.png", };
	}

	@Override
	protected void doBuild() {
		ModelEntity scene = game(1800, 1200).scene().getLastScene();

		ModelEntity background = entity(scene, 0, 0).getLastEntity();

		entity(background, assets[0], -1, -1);

		ModelEntity sun = entity(background, assets[1], -220, -900)
				.getLastEntity();
		centerOrigin(sun);
		tween(sun, RotateTween.class, 0F, -1, 0F, false, 20F, true,
				Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT, 360F, null,
				null, null);

		entity(background, assets[2], -1, -1);

		// clouds
		ModelEntity cloud1 = entity(background, assets[10], -500, 950)
				.getLastEntity();
		ModelEntity cloud2 = entity(background, assets[11], -500, 880)
				.getLastEntity();
		ModelEntity cloud3 = entity(background, assets[12], -500, 760)
				.getLastEntity();
		ModelEntity cloud4 = entity(background, assets[13], -500, 700)
				.getLastEntity();
		ModelEntity cloud5 = entity(background, assets[14], -500, 640)
				.getLastEntity();
		ModelEntity cloud6 = entity(background, assets[13], -500, 680)
				.getLastEntity();
		cloud6.setScaleX(-0.8F);
		cloud6.setScaleY(0.8F);
		ModelEntity cloud7 = entity(background, assets[14], -50, 720)
				.getLastEntity();
		cloud7.setScaleX(-0.8F);
		cloud7.setScaleY(0.8F);

		moveCloud(cloud1, 2F, 5F, 25F, -500);
		moveCloud(cloud2, 9F, 3F, 23F, -500);
		moveCloud(cloud3, 15F, 6F, 20F, -500);
		moveCloud(cloud4, 12F, 1F, 17F, -500);
		moveCloud(cloud5, 5F, 9F, 15F, -500);
		moveCloud(cloud6, 7F, 7F, 16F, -500);
		moveCloud(cloud7, 9F, 8F, 14F, -500);

		// trees
		entity(background, assets[3], 900, 120);
		entity(background, assets[4], -5, 315);

		entity(background, assets[5], -5, -5);

		ModelEntity leaf1 = entity(background, assets[6], -120, -100)
				.getLastEntity();
		ModelEntity leaf2 = entity(background, assets[8], 220, -100)
				.getLastEntity();
		ModelEntity leaf3 = entity(background, assets[7], 125, -10)
				.getLastEntity();
		ModelEntity leaf4 = entity(background, assets[9], -20, 700)
				.getLastEntity();
		ModelEntity leaf5 = entity(background, assets[9], -50, 850)
				.getLastEntity();
		leaf5.setRotation(-30F);

		wind(leaf1, 0.5F, 3.1F, 8);
		wind(leaf2, 0F, 3F, 7);
		wind(leaf3, 0.1F, 3.2F, 9);
		wind(leaf4, 0.3F, 3F, 6);
		wind(leaf5, 0.3F, 3.5F, 7);

	}

	public void moveCloud(ModelEntity entity, float initDelay, float delay,
			float duration, float initPos) {
		Timeline timeline = new Timeline();
		timeline.setRepeat(-1);
		timeline.setYoyo(false);
		timeline.setDelay(initDelay);
		timeline.setRepeatDelay(delay);
		timeline.setMode(Timeline.Mode.SEQUENCE);
		timeline.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, duration, true,
						Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT, 2400F,
						0F, null, null));
		timeline.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, 0F, false,
						Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT,
						initPos, Float.NaN, null, null));

		entity.getComponents().add(timeline);
	}

	public void wind(ModelEntity entity, float originX, float duration,
			float angle) {
		Dimension actualDim = null;
		for (ModelComponent component : entity.getComponents()) {
			if (component instanceof Renderer) {
				actualDim = getRendererDimension((Renderer) component);
				break;
			}
		}

		entity.setOriginX(actualDim.getWidth() * originX);
		entity.setOriginY(0);

		Timeline timeline = new Timeline();
		timeline.setRepeat(-1);
		timeline.setYoyo(false);
		timeline.setMode(Timeline.Mode.SEQUENCE);
		float time = duration / 2;
		timeline.getChildren().add(
				makeTween(RotateTween.class, 0F, 0, 0F, false, time, true,
						Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT, angle,
						null, null, null));
		timeline.getChildren().add(
				makeTween(RotateTween.class, 0F, 0, 0F, false, time, true,
						Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT,
						-angle, null, null, null));

		entity.getComponents().add(timeline);
	}

	public static void main(String[] args) {
		LandscapeDemo5 landscapeDemo = new LandscapeDemo5();
		landscapeDemo.run();
	}

}