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
package es.eucm.ead.engine.systems;

import ashley.core.Entity;
import ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.EffectsComponent;
import es.eucm.ead.engine.components.behaviors.TimersComponent;
import es.eucm.ead.engine.systems.effects.EffectExecutor;
import es.eucm.ead.engine.utils.EngineUtils;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.components.behaviors.events.Timer;
import es.eucm.ead.schema.effects.Effect;

import java.util.HashMap;
import java.util.Map;

/**
 * This system has the lowest priority possible, since we always want the
 * effects to be executed last, once any possible effect has been added
 */
public class EffectsSystem extends ConditionalSystem {

	private Map<Class, EffectExecutor> effectExecutorMap;

	private GameAssets gameAssets;

	public EffectsSystem(GameLoop engine, VariablesManager variablesManager,
			GameAssets gameAssets) {
		super(engine, variablesManager, Family
				.getFamilyFor(EffectsComponent.class), Integer.MAX_VALUE);
		effectExecutorMap = new HashMap<Class, EffectExecutor>();
		this.gameAssets = gameAssets;
	}

	public void registerEffectExecutor(Class<? extends Effect> effectClass,
			EffectExecutor effectExecutor) {
		effectExecutorMap.put(effectClass, effectExecutor);
		effectExecutor.initialize(gameLoop);
	}

	@Override
	public void doProcessEntity(Entity entity, float delta) {
		EffectsComponent effectsComponent = entity
				.getComponent(EffectsComponent.class);
		executeEffectList(effectsComponent.getEffectList());
		entity.remove(EffectsComponent.class);
	}

	/**
	 * Executes a list of effects. This method makes a recursive call for each
	 * ScriptEffect found.
	 * 
	 * This method is public since other classes may need to launch effects
	 * immediately, but generally it's a better choice to queue them into an
	 * {@link EffectsComponent} to be executed in the next loop.
	 */
	public void executeEffectList(Iterable<Effect> effectList) {
		for (Effect effect : effectList) {
			EffectExecutor effectExecutor = effectExecutorMap.get(effect
					.getClass());
			if (effectExecutor != null) {

				effect = EngineUtils.buildWithParameters(gameAssets,
						variablesManager, effect);

				// Find target entities
				Object expResult = variablesManager.evaluateExpression(effect
						.getTarget());
				// Accepted results: Entity or Iterable<Entity>
				if (expResult instanceof Entity) {
					processTarget((Entity) expResult, effect, effectExecutor);
				} else if (expResult instanceof Iterable) {
					Iterable targets = (Iterable) expResult;
					for (Object maybeATarget : targets) {
						if (!(maybeATarget instanceof Entity)) {
							Gdx.app.error(
									"EffectsSystem",
									"An object returned after expression evaluation is not an Entity. It will be skipped. "
											+ effect.getClass());

						} else {
							Entity target = (Entity) maybeATarget;
							processTarget(target, effect, effectExecutor);
						}
					}
				}
			} else {
				Gdx.app.error("EffectsSystem", "No executor for effect "
						+ effect.getClass());
			}
		}
	}

	private void processTarget(Entity target, Effect effect,
			EffectExecutor effectExecutor) {
		// Setup target var. It is registered in current context, which is the
		// local context effects system creates for each EffectsComponent
		// that is processed
		variablesManager.localEntityVar(target);
		effectExecutor.execute(target, effect);
	}

	/**
	 * Schedules the given {@code effect} to be executed on the given
	 * {@code entity} after {@code time} has elapsed.
	 * 
	 * It makes use of {@link TimersComponent} underneath.
	 * 
	 * @param engine
	 *            The gameLoop used to create components
	 * @param effect
	 *            The effect to launch
	 * @param time
	 *            The delay, in seconds
	 * @param entity
	 *            The entity that "owns" the effect.
	 */
	public static void launchDelayedEffect(GameLoop engine, Effect effect,
			float time, Entity entity) {
		TimersComponent timersComponent = engine.addAndGetComponent(entity,
				TimersComponent.class);

		Timer timer = new Timer();
		timer.setCondition("btrue");
		timer.setRepeat(1);
		timer.setTime(time);

		Array<Effect> effects = new Array<Effect>();
		effects.add(effect);
		timersComponent.addBehavior(timer, effects);
	}
}
