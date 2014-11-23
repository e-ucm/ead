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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Manages a {@link Transition} between the current screen and the next screen.
 */
public class TransitionManager extends Actor implements Disposable {

	private Region currentScreenRegion, nextScreenRegion;
	private FrameBuffer currFbo, nextFbo;
	private Transition screenTransition;
	private TextureRegion currTex;
	private TextureRegion nexTex;

	private float percentageCompletion;
	private float time;

	public TransitionManager() {
		nexTex = new TextureRegion();
		currTex = new TextureRegion();
		currentScreenRegion = new Region(0, 0, 0, 0);
		nextScreenRegion = currentScreenRegion;
	}

	public void act(float delta) {

		// ongoing transition
		float duration = screenTransition.getDuration();
		// update progress of ongoing transition
		time += delta;
		if (time > duration) {
			endTransition();
		} else {
			// render transition effect to screen
			percentageCompletion = time / duration;
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.setColor(1f, 1f, 1f, 1f);
		screenTransition.render(batch, currTex, currentScreenRegion, nexTex,
				nextScreenRegion, percentageCompletion);
	}

	public void takeCurrentScreenPicture(Stage stage) {
		takeScreenPicture(currFbo, currTex, stage, null);
	}

	public void takeCurrentScreenPicture(Stage stage, Actor currentLayer) {
		takeScreenPicture(currFbo, currTex, stage, currentLayer);
	}

	public void takeNextScreenPicture(Stage stage) {
		takeScreenPicture(nextFbo, nexTex, stage, null);
	}

	public void takeNextScreenPicture(Stage stage, Actor nextLayer) {
		takeScreenPicture(nextFbo, nexTex, stage, nextLayer);
	}

	private void takeScreenPicture(FrameBuffer fbo, TextureRegion region,
			Stage stage, Actor actor) {
		if (stage == null) {
			stage = getStage();
		}
		if (hasParent()) {
			endTransition();
		}
		Viewport viewport = stage.getViewport();
		if (fbo == null) {
			int w = viewport.getScreenWidth();
			int h = viewport.getScreenHeight();
			fbo = new FrameBuffer(Format.RGB888, w, h, false);
			currentScreenRegion.w = w;
			currentScreenRegion.h = h;
		}

		fbo.begin();
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (actor != null) {
			Batch batch = stage.getBatch();
			batch.begin();
			actor.draw(batch, 1f);
			batch.end();
		} else {
			stage.draw();
		}
		fbo.end(viewport.getScreenX(), viewport.getScreenY(),
				viewport.getScreenWidth(), viewport.getScreenHeight());
		region.setRegion(fbo.getColorBufferTexture());
		region.flip(false, true);
	}

	public void startTransition(Transition screenTransition) {
		this.screenTransition = screenTransition;
		time = 0f;
		percentageCompletion = 0;
	}

	private void endTransition() {
		remove();
		screenTransition.end();
		dispose();
		// transition has just finished
		// switch screens
		EndEvent event = Pools.obtain(EndEvent.class);
		fire(event);
		Pools.free(event);
	}

	@Override
	public void dispose() {
		if (currFbo != null) {
			currFbo.dispose();
			currFbo = null;
		}
		if (nextFbo != null) {
			nextFbo.dispose();
			nextFbo = null;
		}
	}

	/**
	 * Defines a way to change between the current screen and the next screen.
	 */
	public interface Transition {

		float getDuration();

		void render(Batch batch, TextureRegion currScreen,
				Region currScreenRegion, TextureRegion nextScreen,
				Region nextScreenRegion, float percentageCompletion);

		void end();
	}

	/**
	 * Base class to listen to {@link EndEvent}s produced by
	 * {@link TransitionManager}.
	 */
	public static class EndListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			if (event instanceof EndEvent) {
				transitionFinished();
			}
			return true;
		}

		/**
		 * The transition has finished.
		 */
		public void transitionFinished() {

		}
	}

	public static class EndEvent extends Event {

	}
}
