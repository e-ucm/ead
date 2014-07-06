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
package es.eucm.ead.engine.tests.systems.tweens;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.mock.schema.MockEffect;
import es.eucm.ead.engine.mock.schema.MockEffect.MockEffectListener;
import es.eucm.ead.engine.mock.schema.MockEffectExecutor;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.engine.processors.TagsProcessor;
import es.eucm.ead.engine.processors.behaviors.BehaviorsProcessor;
import es.eucm.ead.engine.processors.tweens.TweensProcessor;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.systems.tweens.tweencreators.EffectTweenCreator;
import es.eucm.ead.engine.systems.tweens.tweencreators.TweenCreator;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.tweens.BaseTween;
import es.eucm.ead.schema.components.tweens.EffectTween;
import es.eucm.ead.schema.effects.Effect;

public class EffectTweenTest extends TweenTest implements MockEffectListener {

	private int executed;
	private EffectsSystem effectsSystem;
	private TweensProcessor tweensProcessor;

	@Override
	@Before
	public void setUp() {
		super.setUp();

		gameLoop.addSystem(effectsSystem);

		Map<Class<? extends ModelComponent>, ComponentProcessor> componentProcessors = new HashMap<Class<? extends ModelComponent>, ComponentProcessor>();
		registerComponentProcessors(gameLoop, componentProcessors);
		for (Entry<Class<? extends ModelComponent>, ComponentProcessor> e : componentProcessors
				.entrySet()) {
			componentLoader
					.registerComponentProcessor(e.getKey(), e.getValue());
		}
		tweensProcessor = new TweensProcessor(gameLoop);

		executed = 0;
	}

	/**
	 * Adds the require component processors for the test
	 */
	protected void registerComponentProcessors(
			GameLoop gameLoop,
			Map<Class<? extends ModelComponent>, ComponentProcessor> componentProcessors) {
		componentProcessors.put(Behavior.class,
				new BehaviorsProcessor(gameLoop));
		componentProcessors.put(Tags.class, new TagsProcessor(gameLoop));
	}

	@Test
	public void test() {

		EffectTween timer = new EffectTween();
		timer.setDelay(0.5f);

		Array<Effect> a = new Array<Effect>();
		Effect e = new MockEffect(this);
		a.add(e);
		timer.setEffects(a);

		EngineEntity owner = addEntityWithTweens(timer);
		variablesManager.localOwnerVar(owner);

		gameLoop.update(1);
		gameLoop.update(0);
		assertTrue("Effect wasn't executed", executed == 1);

	}

	@Override
	public void executed() {
		executed++;
	}

	protected EngineEntity addEntityWithTweens(BaseTween... tweens) {
		EngineEntity entity = gameLoop.createEntity();
		entity.setGroup(new Group());
		for (BaseTween tween : tweens) {
			entity.add(tweensProcessor.getComponent(tween));
		}
		gameLoop.addEntity(entity);
		return entity;
	}

	@Override
	public Class getTweenClass() {
		return EffectTween.class;
	}

	@Override
	public TweenCreator getTweenCreator() {
		effectsSystem = new EffectsSystem(gameLoop, variablesManager);
		effectsSystem.registerEffectExecutor(MockEffect.class,
				new MockEffectExecutor());

		return new EffectTweenCreator(effectsSystem);
	}
}
