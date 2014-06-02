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
package es.eucm.ead.engine.tests.systems.effects;

import ashley.core.Entity;
import ashley.core.EntityListener;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import es.eucm.ead.engine.GameLayers;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.systems.RemoveEntitiesSystem;
import es.eucm.ead.engine.mock.MockEntitiesLoader;
import es.eucm.ead.engine.processors.tweens.TweensProcessor;
import es.eucm.ead.engine.systems.RemoveEntitiesSystem;
import es.eucm.ead.engine.systems.effects.RemoveEntityExecutor;
import es.eucm.ead.engine.systems.tweens.TweenSystem;
import es.eucm.ead.engine.systems.tweens.tweencreators.ScaleTweenCreator;
import es.eucm.ead.schema.components.tweens.ScaleTween;
import es.eucm.ead.schema.components.tweens.Tweens;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.effects.RemoveEntity;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by angel on 30/04/14.
 */
public class RemoveEntityTest {

	private boolean removed;

	@Test
	public void testRemoveEntity() {
		removed = false;
		MockEntitiesLoader mockEntitiesLoader = new MockEntitiesLoader();
		GameLoop gameLoop = mockEntitiesLoader.getGameLoop();
		GameLayers gameLayers = new GameLayers(gameLoop);
		gameLoop.addSystem(new RemoveEntitiesSystem(gameLoop,
				new VariablesManager(gameLoop, mockEntitiesLoader
						.getComponentLoader(), gameLayers)));
		RemoveEntityExecutor executor = new RemoveEntityExecutor();
		executor.initialize(gameLoop);

		final EngineEntity engineEntity = gameLoop.createEntity();
		gameLoop.addEntity(engineEntity);
		gameLoop.addEntityListener(new EntityListener() {
			@Override
			public void entityAdded(Entity entity) {
			}

			@Override
			public void entityRemoved(Entity entity) {
				removed = entity == engineEntity;
			}
		});
		executor.execute(engineEntity, new RemoveEntity());
		gameLoop.update(0);
		assertTrue(removed);
	}

	@Test
	public void testTweensKilledAfterRemoval() {
		removed = false;
		MockEntitiesLoader mockEntitiesLoader = new MockEntitiesLoader();
		GameLoop gameLoop = mockEntitiesLoader.getGameLoop();

		// Init tweens
		mockEntitiesLoader.getComponentLoader().registerComponentProcessor(
				Tweens.class, new TweensProcessor(gameLoop));
		final TweenSystem tweenSystem = new TweenSystem();
		tweenSystem.registerBaseTweenCreator(ScaleTween.class,
				new ScaleTweenCreator());
		gameLoop.addSystem(tweenSystem);

		// Create and add entity
		ModelEntity entityWithTweens = new ModelEntity();
		Tweens tweens = new Tweens();
		ScaleTween scaleTween = new ScaleTween();
		scaleTween.setDuration(100); // To see it is not automatically removed
		scaleTween.setRepeat(-1);
		scaleTween.setScaleX(0.5F);
		scaleTween.setScaleY(0.5F);
		entityWithTweens.getComponents().add(tweens);
		tweens.getTweens().add(scaleTween);
		final EngineEntity engineEntity = mockEntitiesLoader
				.toEngineEntity(entityWithTweens);
		gameLoop.update(0);
		// Check tween system has 1 tween
		assertEquals("The system should have 1 tween", 1,
				getNRuntimeTweens(tweenSystem));

		// Remove entity
		gameLoop.removeEntity(engineEntity);
		gameLoop.update(0);
		assertEquals("The system should have 0 tweens", 0,
				getNRuntimeTweens(tweenSystem));
	}

	/*
	 * Returns the number of tweens running via reflection
	 */
	private int getNRuntimeTweens(TweenSystem tweenSystem) {
		try {
			Field tweenManagerField = ClassReflection.getDeclaredField(
					TweenSystem.class, "tweenManager");
			tweenManagerField.setAccessible(true);
			TweenManager tweenManager = (TweenManager) tweenManagerField
					.get(tweenSystem);
			return tweenManager.getRunningTweensCount();
		} catch (Exception e) {
			Gdx.app.debug(
					"RemoveEntityTest",
					"An exception occurred checking ig tweens are automatically killed when an entity is removed",
					e);
			fail("An unexpected exception occurred");
		}
		return -1;
	}
}
