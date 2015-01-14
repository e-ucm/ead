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

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.engine.EngineTest;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.mock.schema.MockEffect;
import es.eucm.ead.engine.mock.schema.MockEffect.MockEffectListener;
import es.eucm.ead.engine.mock.schema.MockEffectExecutor;
import es.eucm.ead.engine.processors.AnimationProcessor;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.engine.processors.TagsProcessor;
import es.eucm.ead.engine.processors.behaviors.BehaviorsProcessor;
import es.eucm.ead.engine.processors.tweens.TweensProcessor;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.systems.behaviors.TimersSystem;
import es.eucm.ead.engine.systems.effects.AddComponentExecutor;
import es.eucm.ead.engine.systems.effects.TrackEffectExecutor;
import es.eucm.ead.engine.systems.effects.effecttotween.AlphaEffectToTween;
import es.eucm.ead.engine.systems.effects.effecttotween.MoveEffectToTween;
import es.eucm.ead.engine.systems.effects.effecttotween.RotateEffectToTween;
import es.eucm.ead.engine.systems.effects.effecttotween.ScaleEffectToTween;
import es.eucm.ead.engine.systems.tweens.TweenSystem;
import es.eucm.ead.engine.systems.tweens.tweencreators.AlphaTweenCreator;
import es.eucm.ead.engine.systems.tweens.tweencreators.EffectTweenCreator;
import es.eucm.ead.engine.systems.tweens.tweencreators.MoveTweenCreator;
import es.eucm.ead.engine.systems.tweens.tweencreators.RotateTweenCreator;
import es.eucm.ead.engine.systems.tweens.tweencreators.ScaleTweenCreator;
import es.eucm.ead.schema.components.Animation;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.BaseTween;
import es.eucm.ead.schema.components.tweens.EffectTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.RotateTween;
import es.eucm.ead.schema.components.tweens.ScaleTween;
import es.eucm.ead.schema.effects.AddComponent;
import es.eucm.ead.schema.effects.AlphaEffect;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.MoveEffect;
import es.eucm.ead.schema.effects.RotateEffect;
import es.eucm.ead.schema.effects.ScaleEffect;
import es.eucm.ead.schema.effects.TimedEffect;
import es.eucm.ead.schema.effects.TrackEffect;

public class AnimationTest extends EngineTest implements MockEffectListener {

	private int executed;

	protected TweenSystem tweenSystem;

	private TweensProcessor tweensProcessor;

	private EffectsSystem effectsSystem;

	private TrackEffectExecutor timelineExecutor;

	private AnimationProcessor animationProcessor;

	@Before
	public void setUp() {
		super.setUp();

		executed = 0;

		tweenSystem = new TweenSystem();
		gameLoop.addSystem(tweenSystem);

		tweensProcessor = new TweensProcessor(gameLoop);
		animationProcessor = new AnimationProcessor(gameLoop);

		effectsSystem = new EffectsSystem(gameLoop, variablesManager,
				gameAssets);

		timelineExecutor = new TrackEffectExecutor(effectsSystem);

		effectsSystem.registerEffectExecutor(MockEffect.class,
				new MockEffectExecutor());
		effectsSystem.registerEffectExecutor(AddComponent.class,
				new AddComponentExecutor(componentLoader));
		effectsSystem.registerEffectExecutor(TrackEffect.class,
				timelineExecutor);

		timelineExecutor.registerTween(MoveEffect.class,
				new MoveEffectToTween());
		timelineExecutor.registerTween(ScaleEffect.class,
				new ScaleEffectToTween());
		timelineExecutor.registerTween(AlphaEffect.class,
				new AlphaEffectToTween());
		timelineExecutor.registerTween(RotateEffect.class,
				new RotateEffectToTween());

		tweenSystem.registerBaseTweenCreator(EffectTween.class,
				new EffectTweenCreator(gameLoop, effectsSystem));
		tweenSystem.registerBaseTweenCreator(MoveTween.class,
				new MoveTweenCreator());
		tweenSystem.registerBaseTweenCreator(ScaleTween.class,
				new ScaleTweenCreator());
		tweenSystem.registerBaseTweenCreator(AlphaTween.class,
				new AlphaTweenCreator());
		tweenSystem.registerBaseTweenCreator(RotateTween.class,
				new RotateTweenCreator());

		componentLoader.registerComponentProcessor(MoveTween.class,
				tweensProcessor);
		componentLoader.registerComponentProcessor(ScaleTween.class,
				tweensProcessor);
		componentLoader.registerComponentProcessor(AlphaTween.class,
				tweensProcessor);
		componentLoader.registerComponentProcessor(RotateTween.class,
				tweensProcessor);
		componentLoader.registerComponentProcessor(Animation.class,
				animationProcessor);

		Map<Class<? extends ModelComponent>, ComponentProcessor> componentProcessors = new HashMap<Class<? extends ModelComponent>, ComponentProcessor>();
		registerComponentProcessors(gameLoop, componentProcessors);
		for (Entry<Class<? extends ModelComponent>, ComponentProcessor> e : componentProcessors
				.entrySet()) {
			componentLoader
					.registerComponentProcessor(e.getKey(), e.getValue());
		}
		gameLoop.addSystem(new TimersSystem(gameLoop, variablesManager));

		gameLoop.addSystem(effectsSystem);
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

	@Test
	public void testTimeline() {

		MoveEffect moveEffect = new MoveEffect();
		moveEffect.setDuration(0.5f);
		moveEffect.setX(10f);
		moveEffect.setY(20f);

		RotateEffect rotateEffect = new RotateEffect();
		rotateEffect.setDuration(1f);
		rotateEffect.setRotation(45f);

		AlphaEffect alphaEffect = new AlphaEffect();
		alphaEffect.setDuration(0.5f);
		alphaEffect.setAlpha(0f);

		ScaleEffect scaleEffect = new ScaleEffect();
		scaleEffect.setDuration(2f);
		scaleEffect.setScaleX(2f);
		scaleEffect.setScaleY(1f);
		scaleEffect.setRelative(true);

		// Creates the effects with delay used in tracks
		TimedEffect effect1 = createTimedEffect(0.5f, moveEffect);
		TimedEffect effect2 = createTimedEffect(0.1f, new MockEffect(this));
		TimedEffect effect3 = createTimedEffect(0.5f, scaleEffect);
		TimedEffect effect4 = createTimedEffect(0.1f, rotateEffect);
		TimedEffect effect5 = createTimedEffect(1.2f, alphaEffect);

		// Creates a first track with effect1, effect2 and effect5
		TrackEffect line1 = new TrackEffect();
		Array<TimedEffect> effects1 = new Array<TimedEffect>();
		effects1.add(effect1);
		effects1.add(effect2);
		effects1.add(effect5);
		line1.setEffects(effects1);

		// Creates a second track with effect3 and effect4
		TrackEffect line2 = new TrackEffect();
		Array<TimedEffect> effects2 = new Array<TimedEffect>();
		effects2.add(effect3);
		effects2.add(effect4);
		line2.setEffects(effects2);

		// Creates a complete Animation with two tracks
		Animation animation = new Animation();
		Array<TrackEffect> tracks = new Array<TrackEffect>();
		tracks.add(line1);
		tracks.add(line2);

		animation.setEffects(tracks);

		EngineEntity owner = createEntityWithAnimation(animation);

		gameLoop.update(0f); // process the animation with tracks

		gameLoop.update(0.1f); // start MockEffect and RotateEffect (also
								// MockEffect finish)
		assertTrue("Effect wasn't executed", executed == 1);
		assertTrue("Entity x position is " + owner.getGroup().getX()
				+ ". Should be 0", owner.getGroup().getX() == 0);
		assertTrue("Rotation is " + owner.getGroup().getRotation()
				+ ". Should be 0", owner.getGroup().getRotation() == 0);
		assertTrue("Alpha is " + owner.getGroup().getColor().a
				+ ". Should be 1", owner.getGroup().getColor().a == 1);
		assertTrue(
				"Scale is " + owner.getGroup().getScaleX() + ". Should be 1",
				owner.getGroup().getScaleX() == 1);

		gameLoop.update(0.4f); // start MoveEffect and ScaleEffect1

		gameLoop.update(0.5f); // finish MoveEffect
		assertTrue("Effect wasn't executed", executed == 1);
		assertTrue("Entity x position is " + owner.getGroup().getX()
				+ ". Should be 10", owner.getGroup().getX() == 10);
		assertTrue("Rotation is " + owner.getGroup().getRotation()
				+ ". Should be between 45 and 0", owner.getGroup()
				.getRotation() > 0 && owner.getGroup().getRotation() < 45);
		assertTrue("Scale is " + owner.getGroup().getScaleX()
				+ ". Should be between 1 and 3",
				owner.getGroup().getScaleX() > 1
						&& owner.getGroup().getScaleX() < 3);

		gameLoop.update(0.1f); // finish RotateEffect
		assertTrue("Rotation is " + owner.getGroup().getRotation()
				+ ". Should be 45", owner.getGroup().getRotation() == 45);
		assertTrue("Alpha is " + owner.getGroup().getColor().a
				+ ". Should be 1", owner.getGroup().getColor().a == 1);
		assertTrue("Scale is " + owner.getGroup().getScaleX()
				+ ". Should be between 1 and 3",
				owner.getGroup().getScaleX() > 1
						&& owner.getGroup().getScaleX() < 3);

		gameLoop.update(0.2f); // start AlphaEffect

		gameLoop.update(0.5f); // finish AlphaEffect
		assertTrue("Alpha is " + owner.getGroup().getColor().a
				+ ". Should be 0", owner.getGroup().getColor().a == 0);
		assertTrue("Scale is " + owner.getGroup().getScaleX()
				+ ". Should be between 1 and 3",
				owner.getGroup().getScaleX() > 1
						&& owner.getGroup().getScaleX() < 3);

		gameLoop.update(0.8f); // finish ScaleEffect
		assertTrue("Effect wasn't executed", executed == 1);
		assertTrue(
				"Scale is " + owner.getGroup().getScaleX() + ". Should be 3",
				owner.getGroup().getScaleX() == 3);

	}

	public EngineEntity createEntityWithAnimation(Animation animation) {
		EngineEntity owner = gameLoop.createEntity();
		owner.setGroup(new Group());
		owner.add(animationProcessor.getComponent(animation));
		gameLoop.addEntity(owner);
		variablesManager.localOwnerVar(owner);

		return owner;
	}

	public TimedEffect createTimedEffect(float time, Effect effect) {
		TimedEffect timerEffect = new TimedEffect();
		timerEffect.setEffect(effect);
		timerEffect.setTime(time);

		return timerEffect;
	}

}
