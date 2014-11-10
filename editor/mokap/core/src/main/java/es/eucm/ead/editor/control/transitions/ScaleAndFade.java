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
package es.eucm.ead.editor.control.transitions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

import es.eucm.ead.editor.control.transitions.TransitionManager.Transition;

/**
 * Scale and Fade transition between the current screen and the next screen.
 */
public class ScaleAndFade implements Transition {

	private float duration;
	private boolean out;

	public ScaleAndFade(float duration, boolean in) {
		this.out = in;
		this.duration = duration;
	}

	public ScaleAndFade() {
		this(MathUtils.random(.4f, .6f), MathUtils.randomBoolean());
	}

	@Override
	public float getDuration() {
		return duration;
	}

	@Override
	public void render(Batch batch, TextureRegion currScreen,
			Region currScreenRegion, TextureRegion nextScreen,
			Region nextScreenRegion, float completion) {
		if (!out) {
			TextureRegion temp = currScreen;
			currScreen = nextScreen;
			nextScreen = temp;
		}

		completion = out ? Interpolation.exp5Out.apply(completion)
				: Interpolation.exp5In.apply(1 - completion);

		batch.draw(currScreen, currScreenRegion.x, currScreenRegion.y,
				currScreenRegion.w, currScreenRegion.h);

		Color color = batch.getColor();
		batch.setColor(1, 1, 1, completion);
		float width = nextScreenRegion.w, height = nextScreenRegion.h;
		batch.draw(nextScreen, nextScreenRegion.x, nextScreenRegion.y,
				width * .5f, height * .5f, width, height, completion,
				completion, 0f);
		batch.setColor(color);
	}

	@Override
	public void end() {

	}
}