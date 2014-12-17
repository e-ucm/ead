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
package es.eucm.ead.engine.systems.effects.transitions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.engine.systems.effects.transitions.TransitionManager.Transition;

/**
 * Slices between two screens.
 */
public class Slice implements Transition {

	public static final int RANDOM = 0;
	public static final int UP = 1;
	public static final int DOWN = 2;
	public static final int UP_DOWN = 3;

	private static Slice instance;

	private boolean horizontal;
	private float duration;
	private int direction;
	private final Interpolation easing = Interpolation.exp5;
	private Array<Integer> sliceIndex;

	public static Slice init(float duration, boolean horizontal, int direction,
			int numSlices) {
		if (instance == null) {
			instance = new Slice(duration, horizontal, direction, numSlices);
		} else {
			instance.initialize(duration, horizontal, direction, numSlices);
		}
		return instance;
	}

	public Slice() {
		this(MathUtils.random(.4f, .7f), MathUtils.randomBoolean(), RANDOM,
				MathUtils.random(5, 10));
	}

	public Slice(float duration, boolean horizontal, int direction,
			int numSlices) {
		initialize(duration, horizontal, direction, numSlices);
	}

	private void initialize(float duration, boolean horizontal, int direction,
			int numSlices) {
		this.duration = duration;
		this.horizontal = horizontal;
		this.direction = direction == RANDOM ? MathUtils.random(UP, UP_DOWN)
				: direction;
		// create shuffled list of slice indices which determines
		// the order of slice animation
		if (sliceIndex == null) {
			sliceIndex = new Array<Integer>(numSlices);
		}
		sliceIndex.clear();
		for (int i = 0; i < numSlices; i++)
			sliceIndex.add(i);
		sliceIndex.shuffle();
	}

	@Override
	public float getDuration() {
		return duration;
	}

	@Override
	public void render(Batch batch, TextureRegion currScreen,
			Region currScreenRegion, TextureRegion nextScreen,
			Region nextScreenRegion, float completion) {
		batch.draw(currScreen, currScreenRegion.x, currScreenRegion.y,
				currScreenRegion.w, currScreenRegion.h);
		float w = nextScreenRegion.w;
		float h = nextScreenRegion.h;
		float x = nextScreenRegion.x;
		float y = nextScreenRegion.y;
		int numSlices = sliceIndex.size;

		Texture nextTex = nextScreen.getTexture();
		completion = easing.apply(completion);

		if (horizontal) {

			int sliceTexelHeight = (int) (nextTex.getHeight() / numSlices);
			float sliceHeight = h
					* ((float) sliceTexelHeight / nextTex.getHeight());
			float yOverflow = h - numSlices * sliceHeight;
			for (int i = 0; i < numSlices; ++i) {
				// current slice/column
				int offsetIdx;
				float offsetY;
				boolean flipY;
				if (nextScreen.isFlipY()) {
					flipY = true;
					offsetIdx = i;
					offsetY = 0;
				} else {
					flipY = false;
					offsetIdx = numSlices - i - 1;
					offsetY = yOverflow;
				}
				y = offsetIdx * sliceHeight + nextScreenRegion.y + offsetY;
				// horizontal displacement using randomized
				// list of slice indices
				float offsetX = w * (1 + sliceIndex.get(i) / (float) numSlices);
				switch (direction) {
				case UP:
					x = nextScreenRegion.x - offsetX + offsetX * completion;
					break;
				case DOWN:
					x = nextScreenRegion.x + offsetX - offsetX * completion;
					break;
				case UP_DOWN:
					if (i % 2 == 0) {
						x = nextScreenRegion.x - offsetX + offsetX * completion;
					} else {
						x = nextScreenRegion.x + offsetX - offsetX * completion;
					}
					break;
				}
				batch.draw(nextTex, x, y, 0, 0, w, sliceHeight, 1, 1, 0, 0, i
						* sliceTexelHeight, nextTex.getWidth(),
						sliceTexelHeight, false, flipY);
			}
		} else {

			int sliceTexelWidth = (int) (nextTex.getWidth() / numSlices);
			float sliceWidth = w
					* ((float) sliceTexelWidth / nextTex.getWidth());
			for (int i = 0; i < numSlices; ++i) {
				// current slice/column
				x = i * sliceWidth + nextScreenRegion.x;
				// vertical displacement using randomized
				// list of slice indices
				float offsetY = h * (1 + sliceIndex.get(i) / (float) numSlices);
				switch (direction) {
				case UP:
					y = nextScreenRegion.y - offsetY + offsetY * completion;
					break;
				case DOWN:
					y = nextScreenRegion.y + offsetY - offsetY * completion;
					break;
				case UP_DOWN:
					if (i % 2 == 0) {
						y = nextScreenRegion.y - offsetY + offsetY * completion;
					} else {
						y = nextScreenRegion.y + offsetY - offsetY * completion;
					}
					break;
				}

				batch.draw(nextTex, x, y, 0, 0, sliceWidth, h, 1, 1, 0, i
						* sliceTexelWidth, 0, sliceTexelWidth, nextTex
						.getHeight(), false, nextScreen.isFlipY() ? true
						: false);
			}
		}
	}

	@Override
	public void end() {

	}
}
