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
package es.eucm.ead.editor.view.widgets.selectors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

import es.eucm.ead.engine.systems.effects.GoSceneExecutor;
import es.eucm.ead.engine.systems.effects.transitions.Region;
import es.eucm.ead.schema.effects.GoScene.Transition;

public class TransitionDrawable extends BaseDrawable {

	private boolean update = true;

	private TextureRegion currScreen = new TextureRegion();
	private Region region = new Region(0, 0, 0, 0);
	private TextureRegion nextScreen = new TextureRegion();

	private es.eucm.ead.engine.systems.effects.transitions.TransitionManager.Transition transition;

	private float percentageCompletion = 0f, time = 0f;

	public TransitionDrawable() {

	}

	public void setTransition(Transition transition, float duration) {
		this.transition = GoSceneExecutor.getTransition(transition, duration,
				true);
	}

	public void setCurrentTexture(Texture current) {
		currScreen.setRegion(current);
	}

	public void setNextTexture(Texture next) {
		nextScreen.setRegion(next);
	}

	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {

		if (currScreen.getTexture() != null && nextScreen.getTexture() != null
				&& transition != null) {
			region.x = (int) x;
			region.y = (int) y;
			region.w = (int) width;
			region.h = (int) height;
			if (update) {
				// ongoing transition
				float duration = transition.getDuration();
				// updateUI progress of ongoing transition
				time += Gdx.graphics.getDeltaTime();
				if (time > duration) {
					percentageCompletion = 1f;
					float tempDuration = duration * 1.5f;
					if (time > tempDuration) {
						endTransition();
					}
				} else {
					// render transition effect to screen
					percentageCompletion = time / duration;

				}
			} else {
				percentageCompletion = 0f;
				time = 0f;
			}
			this.transition.render(batch, currScreen, region, nextScreen,
					region, percentageCompletion);
			Gdx.graphics.requestRendering();
		}

	}

	private void endTransition() {
		transition.end();
		time = 0f;
		percentageCompletion = 0f;
	}

	@Override
	public float getMinWidth() {
		return region.w;
	}

	@Override
	public float getMinHeight() {
		return region.h;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

}
