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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

import es.eucm.ead.engine.systems.effects.transitions.TransitionManager.Transition;

/**
 * Slides between two screens.
 */
public class Slide implements Transition {

	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int UP = 3;
	public static final int DOWN = 4;
	public static final int RANDOM = 0;

	private static Slide instance;

	private float duration;
	private int direction;
	private boolean slideOut, over;
	private Interpolation easing;

	public static Slide init(float duration, int direction, boolean slideOut,
			boolean over) {
		if (instance == null) {
			instance = new Slide(duration, direction, slideOut, over);
		} else {
			instance.initialize(duration, direction, slideOut, over);
		}
		return instance;
	}

	public Slide() {
		this(MathUtils.random(.6f, .9f), RANDOM, MathUtils.randomBoolean(),
				MathUtils.randomBoolean());
	}

	public Slide(float duration, int direction, boolean slideOut, boolean over) {
		initialize(duration, direction, slideOut, over);
	}

	private void initialize(float duration, int direction, boolean slideOut,
			boolean over) {
		this.duration = duration;
		this.direction = direction == RANDOM ? MathUtils.random(LEFT, DOWN)
				: direction;
		this.slideOut = slideOut;
		this.easing = slideOut ? Interpolation.pow2Out : Interpolation.pow2In;
		this.over = over;
	}

	@Override
	public float getDuration() {
		return duration;
	}

	public void render(Batch batch, TextureRegion currScreen,
			Region currScreenRegion, TextureRegion nextScreen,
			Region nextScreenRegion, float alpha) {
		float w = currScreenRegion.w;
		float h = currScreenRegion.h;
		float x = currScreenRegion.x;
		float y = currScreenRegion.y;
		alpha = easing.apply(alpha);
		// calculate position
		switch (direction) {
		case LEFT:
			x = currScreenRegion.x - w * alpha;
			if (!slideOut)
				x += w;
			break;
		case RIGHT:
			x = currScreenRegion.x + w * alpha;
			if (!slideOut)
				x -= w;
			break;
		case UP:
			y = currScreenRegion.y + h * alpha;
			if (!slideOut)
				y -= h;
			break;
		case DOWN:
			y = currScreenRegion.y - h * alpha;
			if (!slideOut)
				y += h;
			break;
		}
		// drawing order depends on slide type ('in' or 'out')
		TextureRegion texBottom = null;
		TextureRegion texTop = null;
		Region bottomRegion = null;
		Region topRegion = null;
		if (slideOut) {
			texBottom = nextScreen;
			bottomRegion = nextScreenRegion;

			texTop = currScreen;
			topRegion = currScreenRegion;
		} else {
			texBottom = currScreen;
			bottomRegion = currScreenRegion;

			texTop = nextScreen;
			topRegion = nextScreenRegion;
		}

		// finally, draw both screens
		if (!over) {
			w = bottomRegion.w;
			h = bottomRegion.h;
			float x2 = bottomRegion.x, y2 = bottomRegion.y;
			switch (direction) {
			case LEFT:
				x2 = x - w;
				if (slideOut) {
					x2 += 2 * w;
				}
				break;
			case RIGHT:
				x2 = x + w;
				if (slideOut) {
					x2 -= 2 * w;
				}
				break;
			case UP:
				y2 = y + h;
				if (slideOut) {
					y2 -= 2 * h;
				}
				break;
			case DOWN:
				y2 = y - h;
				if (slideOut) {
					y2 += 2 * h;
				}
				break;
			}
			batch.draw(texBottom, x2, y2, w, h);
		} else {
			batch.draw(texBottom, bottomRegion.x, bottomRegion.y,
					bottomRegion.w, bottomRegion.h);
		}
		batch.draw(texTop, x, y, topRegion.w, topRegion.h);
	}

	@Override
	public void end() {

	}
}
