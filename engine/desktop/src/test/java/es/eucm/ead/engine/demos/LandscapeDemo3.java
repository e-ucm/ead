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

public class LandscapeDemo3 extends ExecutableDemoBuilder {

	public LandscapeDemo3() {
		super("landscape3");
	}

	@Override
	public String[] assetPaths() {
		return new String[] { "images/Background.png", "images/City.png",
				"images/Bottom_gate.png", "images/Top_gate.png",
				"images/Rocket.png", "images/Space_ships.png" };
	}

	@Override
	protected void doBuild() {
		ModelEntity scene = game(1800, 1200).scene().getLastScene();

		ModelEntity background = entity(scene, 0, 0).getLastEntity();

		// field and sky
		entity(background, assets[0], -1, -1);

		ModelEntity rocket = entity(background, assets[4], 700, 500)
				.getLastEntity();
		tween(rocket, MoveTween.class, 0F, -1, 10F, false, 10F, true,
				Tween.EaseEquation.QUART, Tween.EaseType.IN, 1000F, 1000F,
				null, null);

		ModelEntity rocket2 = entity(background, assets[4], 700, 500)
				.getLastEntity();
		rocket2.setScaleX(-1F);
		tween(rocket2, MoveTween.class, 5F, -1, 10F, false, 10F, true,
				Tween.EaseEquation.QUART, Tween.EaseType.IN, -1000F, 1000F,
				null, null);

		// city
		entity(background, assets[1], 90, 20);
		ModelEntity gate1 = entity(background, assets[2], 755, 43)
				.getLastEntity();
		ModelEntity gate2 = entity(background, assets[3], 755, 164)
				.getLastEntity();

		Dimension actualDim = null;
		for (ModelComponent component : gate2.getComponents()) {
			if (component instanceof Renderer) {
				actualDim = getRendererDimension((Renderer) component);
				break;
			}
		}

		gate2.setOriginY(actualDim.getHeight());

		openCloseDoor(gate1);
		openCloseDoor(gate2);

		ModelEntity ships = entity(background, assets[5], 1800, 500)
				.getLastEntity();
		Timeline timeline = new Timeline();
		timeline.setRepeat(-1);
		timeline.setYoyo(false);
		timeline.setDelay(0F);
		timeline.setRepeatDelay(0F);
		timeline.setMode(Timeline.Mode.SEQUENCE);
		timeline.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, 4F, true,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, -2000F,
						1000F, null, null));
		timeline.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, 1F, false,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, -200F,
						500F, null, null));
		timeline.getChildren().add(
				makeTween(ScaleTween.class, 0F, 0, 0F, false, 1F, false,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, -1F, 1F,
						null, null));
		timeline.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, 4F, true,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, 2100F,
						1000F, null, null));
		timeline.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, 1F, false,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, 2000F,
						500F, null, null));

		ships.getComponents().add(timeline);

	}

	public void openCloseDoor(ModelEntity entity) {
		Timeline timeline = new Timeline();
		timeline.setRepeat(-1);
		timeline.setYoyo(true);
		timeline.setDelay(4F);
		timeline.setRepeatDelay(8F);
		timeline.setMode(Timeline.Mode.SEQUENCE);
		timeline.getChildren().add(
				makeTween(ScaleTween.class, 0F, 0, 0F, false, 3F, false,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, 1F, 0.1F,
						null, null));
		timeline.getChildren().add(
				makeTween(ScaleTween.class, 0F, 0, 0F, false, 5F, false,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, 1F, 0.1F,
						null, null));

		entity.getComponents().add(timeline);
	}

	public static void main(String[] args) {
		LandscapeDemo2 landscapeDemo = new LandscapeDemo2();
		landscapeDemo.run();
	}

}