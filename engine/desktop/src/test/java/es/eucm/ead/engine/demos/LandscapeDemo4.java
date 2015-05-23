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

public class LandscapeDemo4 extends ExecutableDemoBuilder {

	public LandscapeDemo4() {
		super("landscape4");
	}

	@Override
	public String[] assetPaths() {
		return new String[] { "images/Sky.png", "images/Skylines1.png",
				"images/Skylines2.png", "images/Skylines3.png",
				"images/Water.png", "images/Jungle.png", "images/Mountain.png",
				"images/Birds.png", "images/Branch1.png", "images/Branch2.png",
				"images/Flowers.png", "images/Grass.png", "images/Leaf1.png",
				"images/Leaf2.png", "images/Leaf3.png", "images/Leaf4.png",
				"images/Leaf5.png", "images/Reflection1.png",
				"images/Reflection2.png", "images/Reflection3.png",
				"images/Tree.png" };
	}

	@Override
	protected void doBuild() {
		ModelEntity scene = game(1900, 1300).scene().getLastScene();

		ModelEntity background = entity(scene, 0, 0).getLastEntity();

		// Sky
		entity(background, assets[0], -1, 1300 - getImageDimension(assets[0])
				.getHeight());

		ModelEntity skylines1 = entity(background, assets[2], 385, 1100)
				.getLastEntity();
		ModelEntity skylines2 = entity(background, assets[1], 790, 1150)
				.getLastEntity();
		ModelEntity skylines3 = entity(background, assets[3], 450, 1060)
				.getLastEntity();

		tween(skylines1, MoveTween.class, 0F, -1, 2F, true, 10F, true,
				Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT, -50F, 0F,
				null, null);
		tween(skylines2, MoveTween.class, 0F, -1, 2F, true, 10F, true,
				Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT, -40F, 0F,
				null, null);
		tween(skylines3, MoveTween.class, 0F, -1, 2F, true, 10F, true,
				Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT, -30F, 0F,
				null, null);

		ModelEntity birds = entity(background, assets[7], 1080, 850)
				.getLastEntity();
		centerOrigin(birds);
		Timeline timeline = new Timeline();
		timeline.setRepeat(-1);
		timeline.setYoyo(false);
		timeline.setDelay(2F);
		timeline.setRepeatDelay(6F);
		timeline.setMode(Timeline.Mode.PARALLEL);
		timeline.getChildren().add(
				makeTween(ScaleTween.class, 0F, 0, 0F, false, 5F, true,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, 1.4F,
						1.4F, null, null));
		timeline.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, 5F, true,
						Tween.EaseEquation.LINEAR, Tween.EaseType.IN, 200F,
						500F, null, null));

		birds.getComponents().add(timeline);

		entity(background, assets[6], 730, 730);

		entity(background, assets[4], -1, -10);

		// water reflections
		ModelEntity reflection1 = entity(background, assets[17], 375, -1)
				.getLastEntity();
		ModelEntity reflection2 = entity(background, assets[18], 375, -1)
				.getLastEntity();
		ModelEntity reflection3 = entity(background, assets[19], 845, 470)
				.getLastEntity();

		tween(reflection1, AlphaTween.class, 0F, -1, 2F, true, 2F, false,
				Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT, 0F, null,
				null, null);
		tween(reflection2, AlphaTween.class, 2F, -1, 2F, true, 2F, false,
				Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT, 0F, null,
				null, null);
		tween(reflection3, MoveTween.class, 0F, -1, 0F, true, 5F, true,
				Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT, 0F, -20F,
				null, null);

		// Plants
		entity(background, assets[5], -1, 0);

		ModelEntity branch1 = entity(background, assets[8], 10, 10)
				.getLastEntity();
		entity(background, assets[20], -10, 20);
		ModelEntity branch2 = entity(background, assets[9], 260, 0)
				.getLastEntity();
		ModelEntity flowers = entity(background, assets[10], 85, 45)
				.getLastEntity();

		wind(branch1, 1F, 3F, 16);
		wind(branch2, 0F, 3F, 15);
		wind(flowers, 0.5F, 2.5F, 18);

		ModelEntity leaf1 = entity(background, assets[12], 1175, -5)
				.getLastEntity();
		ModelEntity leaf2 = entity(background, assets[13], 1200, -5)
				.getLastEntity();
		ModelEntity leaf3 = entity(background, assets[14], 1620, -5)
				.getLastEntity();
		ModelEntity leaf4 = entity(background, assets[15], 1755, -5)
				.getLastEntity();
		ModelEntity leaf5 = entity(background, assets[16], 1775, -5)
				.getLastEntity();

		wind(leaf1, 1F, 2.5F, 8);
		wind(leaf2, 0.8F, 2.5F, 6);
		wind(leaf3, 0.3F, 2.6F, 7);
		wind(leaf4, 0F, 2.8F, 9);
		wind(leaf5, 0F, 2.9F, 5);

		entity(background, assets[11], -1, -1).getLastEntity();

	}

	public void wind(ModelEntity entity, float originX, float duration, int str) {
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
		timeline.setDelay(4F);
		timeline.setMode(Timeline.Mode.SEQUENCE);
		float rotation = str + 1;
		float time = duration / rotation * 2;
		timeline.setRepeatDelay(30 - duration * 8);
		for (int i = 0; i < rotation; i++) {
			timeline.getChildren().add(
					makeTween(RotateTween.class, 0F, 0, 0F, false, time, true,
							Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT,
							(float) i, null, null, null));
			timeline.getChildren().add(
					makeTween(RotateTween.class, 0F, 0, 0F, false, time, true,
							Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT,
							-(float) i, null, null, null));
		}
		for (int i = (int) rotation - 1; i >= 0; i--) {
			timeline.getChildren().add(
					makeTween(RotateTween.class, 0F, 0, 0F, false, time, true,
							Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT,
							(float) i, null, null, null));
			timeline.getChildren().add(
					makeTween(RotateTween.class, 0F, 0, 0F, false, time, true,
							Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT,
							-(float) i, null, null, null));
		}

		entity.getComponents().add(timeline);
	}

	public static void main(String[] args) {
		LandscapeDemo4 landscapeDemo = new LandscapeDemo4();
		landscapeDemo.run();
	}

}