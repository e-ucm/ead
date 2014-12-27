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

	/*
	 * Convenience values to be passed as second argument in {@link
	 * #SpinningLogo(Skin skin, float frameDuration, float size, float x, float
	 * y)}
	 */
	public static final float FRAME_DURATION_FAST = 0.012F;
	public static final float FRAME_DURATION_NORMAL = 0.025F;
	public static final float FRAME_DURATION_SLOW = 0.05F;
	public static final float FRAME_DURATION_VERYSLOW = 0.1F;
	public static final float FRAME_DURATION_DEFAULT = FRAME_DURATION_NORMAL;

	/*
	 * Convenience values to be passed as third argument in {@link
	 * #SpinningLogo(Skin skin, float frameDuration, float size, float x, float
	 * y)}
	 */
	public static final float AUTO_SIZE = -1;

	/*
	 * Convenience values to be passed as fourth or fifth arguments in {@link
	 * #SpinningLogo(Skin skin, float frameDuration, float size, float x, float
	 * y)}
	 */
	public static final float DEFAULT_XY = 32;

	private SpinningAnimation spinningAnimation;

	// Init configuration
	private float frameDuration;
	private float x;
	private float y;
	private float size;

	/**
	 * Creates a default 'spinning logo' animation with the next settings:
	 * <ul>
	 * <li>Default frame duration ({@link #FRAME_DURATION_DEFAULT})</li>
	 * <li>Auto size (will look small)</li>
	 * <li>Location ({@value #DEFAULT_XY}DP,{@value #DEFAULT_XY}DP) on screen</li>
	 * </ul>
	 * 
	 * @param skin
	 */
	public SpinningLogo(Skin skin) {
		this(skin, FRAME_DURATION_DEFAULT, AUTO_SIZE, DEFAULT_XY, DEFAULT_XY);
	}

	/**
	 * Creates a custom version of the 'spinning logo' animation.
	 * 
	 * @param skin
	 *            Skin used to load drawables
	 * @param frameDuration
	 *            The time, in seconds, each frame is displayed on screen. The
	 *            smaller this number is, the faster the logo spins. Convenience
	 *            values are provided: {@link #FRAME_DURATION_NORMAL},
	 *            {@link #FRAME_DURATION_FAST}, {@link #FRAME_DURATION_SLOW},
	 *            {@link #FRAME_DURATION_VERYSLOW}
	 * @param size
	 *            The size, in density pixels, this widget should have. If
	 *            {@link #AUTO_SIZE} is provided, then the widget will calculate
	 *            its own size (small).
	 * @param x
	 *            The x position on screen, in density pixels. If
	 *            {@link #DEFAULT_XY} is passed, then the logo will be placed
	 *            {@value #DEFAULT_XY}DP to the left edge of the window.
	 * @param y
	 *            The y position on screen, in density pixels. If
	 *            {@link #DEFAULT_XY} is passed, then the logo will be placed
	 *            {@value #DEFAULT_XY}DP to the bottom edge of the window.
	 */
	public SpinningLogo(Skin skin, float frameDuration, float size, float x,
			float y) {
		this.frameDuration = frameDuration;
		this.x = WidgetBuilder.dpToPixels(x);
		this.y = WidgetBuilder.dpToPixels(y);
		this.size = size;
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
				delta = elapsedTime - frameDuration;
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
			if (size == AUTO_SIZE) {
				SpinningLogo.this.setSize(SpinningLogo.this.getPrefWidth(),
						SpinningLogo.this.getPrefHeight());
			} else {
				SpinningLogo.this
						.setSize(
								WidgetBuilder.dpToPixels(size),
								WidgetBuilder.dpToPixels(size) * 0.99601593625498007968127490039841F);
			}
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
			boolean large = size != AUTO_SIZE;
			frames = new Array<Drawable>();
			for (int i = 1; i <= N_FRAMES; i++) {
				String drawableString = (large ? SkinConstants.DRAWABLE_LOGO_SPINNING_LARGE
						: SkinConstants.DRAWABLE_LOGO_SPINNING)
						+ (i < 10 ? "0" : "") + i + "a";
				Drawable drawable = skin.getDrawable(drawableString);
				frames.add(drawable);
			}
		}

	}
}
