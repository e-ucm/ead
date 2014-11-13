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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

import es.eucm.ead.editor.control.Controller;

/**
 * Manages a {@link Transition} between the current screen and the next screen.
 */
public class TransitionManager extends Actor implements Disposable {

	private Actor nextScreen;
	private FrameBuffer currFbo, nextFbo;
	private Transition screenTransition;
	private Group viewsContainer, modalsContainer;
	private TextureRegion currTex;
	private TextureRegion nexTex;
	private float percentageCompletion;
	private Region currentScreenRegion, nextScreenRegion;
	private float time;

	public TransitionManager(Controller controller, Group viewsContainer,
			Group modalsContainer) {
		this.viewsContainer = viewsContainer;
		this.modalsContainer = modalsContainer;
	}

	public void prepateTransition(Transition screenTransition, Actor next) {
		nextScreen = next;

		// start new transition
		Gdx.input.setInputProcessor(null); // disable input
		this.screenTransition = screenTransition;
		time = 0f;
		Gdx.app.postRunnable(startTransition);
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

	private void endTransition() {
		screenTransition.end();
		nextScreen = null;
		// transition has just finished
		// enable input for next screen
		Stage stage = getStage();
		Gdx.input.setInputProcessor(stage);
		// switch screens
		remove();
		stage.addActor(viewsContainer);
		stage.addActor(modalsContainer);
		Gdx.graphics.setContinuousRendering(false);
		Gdx.graphics.requestRendering();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.setColor(1f, 1f, 1f, 1f);
		screenTransition.render(batch, currTex, currentScreenRegion, nexTex,
				nextScreenRegion, percentageCompletion);
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

	private final Runnable startTransition = new Runnable() {

		@Override
		public void run() {
			Gdx.graphics.setContinuousRendering(true);
			Stage stage = viewsContainer.getStage();
			if (stage == null) {
				stage = getStage();
				stage.getRoot().clearChildren();
				stage.addActor(viewsContainer);
				stage.addActor(modalsContainer);
			}
			if (nextFbo == null) {
				Viewport viewport = stage.getViewport();
				int w = viewport.getScreenWidth();
				int h = viewport.getScreenHeight();
				nextFbo = new FrameBuffer(Format.RGB888, w, h, false);
				currFbo = new FrameBuffer(Format.RGB888, w, h, false);
				currentScreenRegion = new Region(0, 0, w, h);
				nextScreenRegion = currentScreenRegion;
				nexTex = new TextureRegion();
				currTex = new TextureRegion();
			}

			Gdx.gl20.glClearColor(1f, 1f, 1f, 1f);
			currFbo.begin();
			Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
			stage.draw();
			currFbo.end();
			currTex.setRegion(currFbo.getColorBufferTexture());
			currTex.flip(false, true);

			viewsContainer.clearChildren();
			viewsContainer.addActor(nextScreen);
			if (nextScreen instanceof Layout) {
				Layout layout = (Layout) nextScreen;
				layout.invalidateHierarchy();
				layout.setFillParent(true);
			}

			nextFbo.begin();
			Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
			stage.draw();
			nextFbo.end();
			nexTex.setRegion(nextFbo.getColorBufferTexture());
			nexTex.flip(false, true);

			stage.getRoot().clearChildren();
			stage.addActor(TransitionManager.this);
		}
	};

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

}
