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
package es.eucm.ead.engine.systems.effects;

import com.badlogic.ashley.core.Entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import com.badlogic.gdx.scenes.scene2d.Stage;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.EntitiesLoader.EntityLoadedCallback;
import es.eucm.ead.engine.GameView;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.systems.GleanerSystem;
import es.eucm.ead.engine.systems.effects.transitions.Fade;
import es.eucm.ead.engine.systems.effects.transitions.None;
import es.eucm.ead.engine.systems.effects.transitions.ScaleAndFade;
import es.eucm.ead.engine.systems.effects.transitions.Slice;
import es.eucm.ead.engine.systems.effects.transitions.Slide;
import es.eucm.ead.engine.systems.effects.transitions.TransitionManager;
import es.eucm.ead.engine.systems.effects.transitions.TransitionManager.EndListener;
import es.eucm.ead.engine.systems.effects.transitions.TransitionManager.Transition;
import es.eucm.ead.engine.variables.ReservedVariableNames;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schemax.Layer;

public class GoSceneExecutor extends EffectExecutor<GoScene> implements
		EntityLoadedCallback {

	private String previousScene, nextScene;

	private TransitionManager transitionManager;

	private EntitiesLoader entitiesLoader;

	private GameView gameView;

	private GleanerSystem gleanerSystem;

	public GoSceneExecutor(EntitiesLoader entitiesLoader, GameView gameView,
			GameAssets gameAssets, GleanerSystem gleanerSystem) {
		this.transitionManager = new TransitionManager(gameAssets);
		this.entitiesLoader = entitiesLoader;
		this.gameView = gameView;
		this.gleanerSystem = gleanerSystem;
		transitionManager.addListener(new EndListener() {
			@Override
			public void transitionFinished() {
				finish();
			}
		});
	}

	@Override
	public void execute(Entity target, GoScene effect) {
		String previousScene = this.previousScene;
		this.previousScene = nextScene;
		if (effect.getSceneId() == null && previousScene == null) {
			Gdx.app.error("GoSceneExecutor",
					"Previous scene id set to null. Effect was skipped");
			return;
		}
		nextScene = effect.getSceneId() == null ? previousScene : effect
				.getSceneId();
		transitionManager.setVisible(this.previousScene != null);

		Group sceneLayer = gameView.getLayer(Layer.SCENE_CONTENT).getGroup();
		transitionManager.setViewport(gameView.getScreenX(),
				gameView.getScreenY(), gameView.getScreenWidth(),
				gameView.getScreenHeight(), gameView.getPixelsWidth(),
				gameView.getPixelsHeight(), gameView.getWorldWidth(),
				gameView.getWorldHeight());

		transitionManager.setTransition(
				effect.isWaitLoading(),
				getTransition(effect.getTransition(), effect.getDuration(),
						false));
		gameLoop.setPlaying(effect.isUpdateGameLoop());

		if (sceneLayer.getChildren().size >= 1) {
			Actor currentScene = sceneLayer.getChildren().get(0);
			transitionManager.setCurrentScene(
					((Stage) Gdx.input.getInputProcessor()).getBatch(),
					currentScene);
		}

		gameView.clearLayer(Layer.SCENE_CONTENT, true);
		sceneLayer.addActor(transitionManager);

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				entitiesLoader.loadEntity(nextScene, GoSceneExecutor.this);
			}
		});
	}

	@Override
	public void loaded(String path, EngineEntity nextScene) {
		gameView.addEntityToLayer(Layer.SCENE_CONTENT, nextScene);

		gleanerSystem.screen(this.nextScene);
		gleanerSystem.zone(this.nextScene);

		transitionManager.toFront();
		transitionManager.setNextScene(nextScene.getGroup());
	}

	@Override
	public void pathNotFound(String path) {
		Gdx.app.error("GoSceneExecutor", "No scene found in " + path);
		finish();
	}

	protected void finish() {
		transitionManager.remove();
		gameLoop.setPlaying(true);
	}

	public static Transition getTransition(GoScene.Transition modelTransition,
			float duration, boolean memAlloc) {
		Transition transition = null;
		switch (modelTransition) {
		case FADE_IN:
			transition = memAlloc ? new Fade(duration, false) : Fade.init(
					duration, false);
			break;
		case FADE_OUT:
			transition = memAlloc ? new Fade(duration, true) : Fade.init(
					duration, true);
			break;
		case NONE:
			transition = memAlloc ? new None(duration) : None.init(duration);
			break;
		case SCALE_DOWN:
			transition = memAlloc ? new ScaleAndFade(duration, false)
					: ScaleAndFade.init(duration, false);
			break;
		case SCALE_UP:
			transition = memAlloc ? new ScaleAndFade(duration, true)
					: ScaleAndFade.init(duration, true);
			break;
		case SLICE_HORIZONTAL:
			transition = memAlloc ? new Slice(duration, true, Slice.UP_DOWN, 6)
					: Slice.init(duration, true, Slice.UP_DOWN, 6);
			break;
		case SLICE_VERTICAL:
			transition = memAlloc ? new Slice(duration, false, Slice.UP_DOWN,
					10) : Slice.init(duration, false, Slice.UP_DOWN, 10);
			break;
		case SLIDE_DOWN:
			transition = memAlloc ? new Slide(duration, Slide.DOWN, false,
					false) : Slide.init(duration, Slide.DOWN, false, false);
			break;
		case SLIDE_LEFT:
			transition = memAlloc ? new Slide(duration, Slide.LEFT, false,
					false) : Slide.init(duration, Slide.LEFT, false, false);
			break;
		case SLIDE_RANDOM:
			transition = memAlloc ? new Slide(duration, Slide.RANDOM, false,
					false) : Slide.init(duration, Slide.RANDOM, false, false);
			break;
		case SLIDE_RIGHT:
			transition = memAlloc ? new Slide(duration, Slide.RIGHT, false,
					false) : Slide.init(duration, Slide.RIGHT, false, false);
			break;
		case SLIDE_UP:
			transition = memAlloc ? new Slide(duration, Slide.UP, false, false)
					: Slide.init(duration, Slide.UP, false, false);
			break;
		case SLIDE_OVER_DOWN_IN:
			transition = memAlloc ? new Slide(duration, Slide.DOWN, false, true)
					: Slide.init(duration, Slide.DOWN, false, true);
			break;
		case SLIDE_OVER_DOWN_OUT:
			transition = memAlloc ? new Slide(duration, Slide.DOWN, true, true)
					: Slide.init(duration, Slide.DOWN, true, true);
			break;
		case SLIDE_OVER_LEFT_IN:
			transition = memAlloc ? new Slide(duration, Slide.LEFT, false, true)
					: Slide.init(duration, Slide.LEFT, false, true);
			break;
		case SLIDE_OVER_LEFT_OUT:
			transition = memAlloc ? new Slide(duration, Slide.LEFT, true, true)
					: Slide.init(duration, Slide.LEFT, true, true);
			break;
		case SLIDE_OVER_RANDOM_IN:
			transition = memAlloc ? new Slide(duration, Slide.RANDOM, false,
					true) : Slide.init(duration, Slide.RANDOM, false, true);
			break;
		case SLIDE_OVER_RANDOM_OUT:
			transition = memAlloc ? new Slide(duration, Slide.RANDOM, true,
					true) : Slide.init(duration, Slide.RANDOM, true, true);
			break;
		case SLIDE_OVER_RIGHT_IN:
			transition = memAlloc ? new Slide(duration, Slide.RIGHT, false,
					true) : Slide.init(duration, Slide.RIGHT, false, true);
			break;
		case SLIDE_OVER_RIGHT_OUT:
			transition = memAlloc ? new Slide(duration, Slide.RIGHT, true, true)
					: Slide.init(duration, Slide.RIGHT, true, true);
			break;
		case SLIDE_OVER_UP_IN:
			transition = memAlloc ? new Slide(duration, Slide.UP, false, true)
					: Slide.init(duration, Slide.UP, false, true);
			break;
		case SLIDE_OVER_UP_OUT:
			transition = memAlloc ? new Slide(duration, Slide.UP, true, true)
					: Slide.init(duration, Slide.UP, true, true);
			break;
		default:
			transition = memAlloc ? new None(duration) : None.init(duration);
			break;
		}
		return transition;
	}
}
