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

import ashley.core.Entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.GameView;
import es.eucm.ead.engine.components.RemoveEntityComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.systems.effects.transitions.Fade;
import es.eucm.ead.engine.systems.effects.transitions.None;
import es.eucm.ead.engine.systems.effects.transitions.ScaleAndFade;
import es.eucm.ead.engine.systems.effects.transitions.Slice;
import es.eucm.ead.engine.systems.effects.transitions.Slide;
import es.eucm.ead.engine.systems.effects.transitions.TransitionManager;
import es.eucm.ead.engine.systems.effects.transitions.TransitionManager.EndListener;
import es.eucm.ead.engine.systems.effects.transitions.TransitionManager.Transition;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schemax.Layer;

public class GoSceneExecutor extends EffectExecutor<GoScene> {

	private TransitionManager transitionManager;

	private EntitiesLoader entitiesLoader;

	private GameView gameView;

	private EngineEntity nextScreen;

	/*
	 * If set to false (default), gameView's world dimensions are used to
	 * determine width and height of screen captures during transitions. If
	 * true, stage's viewport is used instead.
	 */
	protected boolean useViewport = false;

	public GoSceneExecutor(EntitiesLoader entitiesLoader, GameView gameView) {
		this(entitiesLoader, gameView, false);
	}

	public GoSceneExecutor(EntitiesLoader entitiesLoader, GameView gameView,
			boolean useViewport) {
		this.transitionManager = new TransitionManager();
		this.entitiesLoader = entitiesLoader;
		this.gameView = gameView;
		this.useViewport = useViewport;
		transitionManager.addListener(new EndListener() {
			@Override
			public void transitionFinished() {
				gameLoop.setPlaying(true);
				Group layer = GoSceneExecutor.this.gameView.getLayer(
						Layer.SCENE_CONTENT).getGroup();
				layer.setVisible(true);
			}
		});
	}

	protected int getScreenWidth(Group layer) {
		return useViewport ? layer.getStage().getViewport().getScreenWidth()
				: gameView.getWorldWidth();
	}

	protected int getScreenHeight(Group layer) {
		return useViewport ? layer.getStage().getViewport().getScreenHeight()
				: gameView.getWorldHeight();
	}

	@Override
	public void execute(Entity target, final GoScene effect) {
		final Group layer = gameView.getLayer(Layer.SCENE_CONTENT).getGroup();

		if (layer.getChildren().size == 1) {
			Actor actor = layer.getChildren().get(0);
			transitionManager.takeCurrentScreenPicture(getScreenWidth(layer),
					getScreenHeight(layer), layer.getStage(), actor);
			Object o = actor.getUserObject();
			if (o instanceof EngineEntity) {
				((EngineEntity) o).add(gameLoop
						.createComponent(RemoveEntityComponent.class));
			} else {
				Gdx.app.error("GoSceneExecutor",
						"Scene layer doesn't have an entity as first child");
			}
		}

		if (effect.getSceneId() == null) {
			Gdx.app.error("GoSceneExecutor",
					"Scene id set to null. Effect was skipped");
			return;
		}

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				entitiesLoader.loadEntity(effect.getSceneId(),
						new EntitiesLoader.EntityLoadedCallback() {
							@Override
							public void loaded(String path,
									EngineEntity engineEntity) {
								nextScreen = engineEntity;
								gameLoop.update(0);
								gameView.clearLayer(Layer.SCENE_CONTENT, true);
								gameView.addEntityToLayer(Layer.SCENE_CONTENT,
										nextScreen);
								layer.setVisible(false);

								transitionManager.takeNextScreenPicture(
										getScreenWidth(layer),
										getScreenHeight(layer),
										engineEntity.getGroup());
								gameLoop.setPlaying(false);

								Group sceneLayer = gameView.getLayer(
										Layer.SCENE).getGroup();
								sceneLayer.addActor(transitionManager);
								transitionManager
										.startTransition(getTransition(
												effect.getTransition(),
												effect.getDuration(), false));
							}

							@Override
							public void pathNotFound(String path) {
								gameLoop.setPlaying(true);
								sceneNotFound(path);
							}
						});
			}
		});
	}

	protected void sceneNotFound(String path) {
		Gdx.app.error("GoSceneExecutor", "No scene found in " + path);
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
