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
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.assets.GameAssets;

/**
 * Manages a {@link Transition} between the current screen and the next screen.
 */
public class TransitionManager extends Actor implements Disposable {

	private GameAssets gameAssets;

	private TransitionFrame current;

	private TransitionFrame next;

	private Transition transition;

	private int screenX, screenY, screenWidth, screenHeight, worldWidth,
			worldHeight, worldX, worldY;

	private float percentageCompletion;

	private boolean waitLoading;

	private float time;

	public TransitionManager(GameAssets gameAssets) {
		this.gameAssets = gameAssets;
		current = new TransitionFrame();
		next = new TransitionFrame();
	}

	public void setViewport(int screenX, int screenY, int screenWidth,
			int screenHeight, int worldX, int worldY, int worldWidth,
			int worldHeight) {
		this.screenX = screenX;
		this.screenY = screenY;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
		this.worldX = worldX;
		this.worldY = worldY;
	}

	public void setTransition(boolean waitLoading, Transition transition) {
		this.waitLoading = waitLoading;
		this.transition = transition;
		time = 0f;
		percentageCompletion = 0;
	}

	public void setCurrentScene(Batch batch, Actor currentScene) {
		next.setScene(null);
		if (hasParent()) {
			endTransition();
		}
		current.setScene(currentScene);
		batch.begin();
		current.updateTexture(batch);
		batch.end();
	}

	@Override
	public void act(float delta) {
		if (next.scene != null && (!waitLoading || gameAssets.isDoneLoading())) {
			time += delta;
			if (time > transition.getDuration()) {
				endTransition();
			} else {
				percentageCompletion = time / transition.getDuration();
			}
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (next.scene != null) {
			next.updateTexture(batch);
			transition.render(batch, current.textureRegion, current.region,
					next.textureRegion, next.region, percentageCompletion);
		} else {
			batch.draw(current.textureRegion, current.region.x,
					current.region.y, current.region.w, current.region.h);
		}
	}

	public void setNextScene(Actor nextScene) {
		next.setScene(nextScene);
	}

	private void endTransition() {
		remove();
		transition.end();
		dispose();
		EndEvent event = Pools.obtain(EndEvent.class);
		fire(event);
		Pools.free(event);
	}

	@Override
	public void dispose() {
		current.dispose();
		next.dispose();
	}

	public class TransitionFrame {
		Region region = new Region(0, 0, 0, 0);
		TextureRegion textureRegion = new TextureRegion();
		FrameBuffer frameBuffer;
		Actor scene;

		void updateTexture(Batch batch) {
			if (scene != null) {
				frameBuffer.begin();
				Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
				scene.draw(batch, 1f);
				frameBuffer.end(screenX, screenY, screenWidth, screenHeight);
			}
		}

		public void setScene(Actor scene) {
			this.scene = scene;
			region.x = worldX;
			region.y = worldY;
			region.w = worldWidth;
			region.h = worldHeight;
			if (frameBuffer == null || frameBuffer.getHeight() != screenHeight
					|| frameBuffer.getWidth() != screenWidth) {
				dispose();
				frameBuffer = new FrameBuffer(Format.RGB888, screenWidth,
						screenHeight, false);
				textureRegion.setRegion(frameBuffer.getColorBufferTexture());
				textureRegion.flip(false, true);
			}
		}

		public void dispose() {
			if (frameBuffer != null) {
				frameBuffer.dispose();
			}
			frameBuffer = null;
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
