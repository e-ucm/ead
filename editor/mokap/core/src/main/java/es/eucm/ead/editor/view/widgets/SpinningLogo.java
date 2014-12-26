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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.view.SkinConstants;

/**
 * Simple widget that displays an (absolutely amazing) animation of mokap's logo
 * spinning, as if someone was mixing the coffee using the pencil.
 * 
 * Used for displaying loading status.
 * 
 * Created by jtorrente on 25/12/2014.
 */
public class SpinningLogo extends Image {

	private SpinningAnimation spinningAnimation;

	public SpinningLogo(Skin skin) {
		setTouchable(Touchable.disabled);
		spinningAnimation = new SpinningAnimation(skin);
	}

	public void reset() {
		spinningAnimation.resetAnimation();
		clearActions();
		addAction(spinningAnimation);
	}

	/*
	 * Inner class that controls the animation. Is responsible for updating the
	 * current frame according to the timing.
	 */
	private class SpinningAnimation extends Action {

		// Total amount of frames. This number should be as high as resources
		// matching the pattern logo_spinning_XXa.png are in assets
		public static final int N_FRAMES = 30;

		public static final float DEFAULT_FRAME_DURATION = 0.025F;

		protected Array<Drawable> frames;

		// State is determined by these three params
		private float elapsedTime;
		private int currentFrameIndex;
		private Drawable currentFrame;

		private SpinningAnimation(Skin skin) {
			initFrames(skin);
			resetAnimation();
		}

		private void resetAnimation() {
			updateCurrentFrame(0);
			elapsedTime = 0.0F;
		}

		@Override
		/**
		 * Logic inspired by {@link es.eucm.ead.engine.components.renderers.frames.FramesComponent#act(float delta)}.
		 */
		public boolean act(float delta) {
			while (delta > 0 && currentFrame != null) {
				elapsedTime += delta;
				delta = elapsedTime - DEFAULT_FRAME_DURATION;
				if (delta >= 0) {
					elapsedTime = 0;
					updateCurrentFrame((currentFrameIndex + 1) % frames.size);
				}
			}
			return false;
		}

		private void updateCurrentFrame(int newIndex) {
			currentFrameIndex = newIndex;
			currentFrame = frames.get(currentFrameIndex);
			SpinningLogo.this.setDrawable(currentFrame);
			SpinningLogo.this.setSize(SpinningLogo.this.getPrefWidth(),
					SpinningLogo.this.getPrefHeight());
			SpinningLogo.this.setOrigin(
					SpinningLogo.this.getPrefWidth() * 0.5f,
					SpinningLogo.this.getPrefHeight() * 0.5f);
			float coordinate = WidgetBuilder.dpToPixels(32);
			SpinningLogo.this.setPosition(coordinate, coordinate);
		}

		// Load all drawables matching pattern logo_spinning_XXa
		// (resources end with an 'a' because otherwise the packer interprets
		// the _number termination as a 9 patch or something.
		private void initFrames(Skin skin) {
			frames = new Array<Drawable>();
			for (int i = 1; i <= N_FRAMES; i++) {
				String drawableString = SkinConstants.DRAWABLE_LOGO_SPINNING
						+ (i < 10 ? "0" : "") + i + "a";
				Drawable drawable = skin.getDrawable(drawableString);
				frames.add(drawable);
			}
		}

	}
}
