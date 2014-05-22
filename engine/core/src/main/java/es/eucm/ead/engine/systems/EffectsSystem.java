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
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.GameLoop;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.components.EffectsComponent;
import es.eucm.ead.engine.systems.effects.EffectExecutor;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.engine.variables.VarsContext;
import es.eucm.ead.schema.effects.Effect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EffectsSystem extends ConditionalSystem {

	// Options for selecting targets
	public static final String ALL = "all";
	public static final String THIS = VarsContext.THIS_VAR;
	public static final String EACH = "each " + VarsContext.RESERVED_ENTITY_VAR;

	private Map<Class, EffectExecutor> effectExecutorMap;

	public EffectsSystem(GameLoop engine, VariablesManager variablesManager) {
		super(engine, variablesManager, Family
				.getFamilyFor(EffectsComponent.class));
		effectExecutorMap = new HashMap<Class, EffectExecutor>();
	}

	public void registerEffectExecutor(Class<? extends Effect> effectClass,
			EffectExecutor effectExecutor) {
		effectExecutorMap.put(effectClass, effectExecutor);
		effectExecutor.initialize(engine);
	}

	@Override
	public void doProcessEntity(Entity entity, float delta) {
		EffectsComponent effectsComponent = entity
				.getComponent(EffectsComponent.class);

		executeEffectList(entity, effectsComponent.getEffectList());

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
	public void executeEffectList(Entity entity, Iterable<Effect> effectList) {
		for (Effect effect : effectList) {
			if (evaluateCondition(effect.getCondition())) {
				EffectExecutor effectExecutor = effectExecutorMap.get(effect
						.getClass());
				if (effectExecutor != null) {
					// Find target entities
					Array<Entity> targets = findTargets(entity,
							effect.getTarget());
					for (Entity target : targets) {
						// Setup target var
						variablesManager.push().localEntityVar(target);
						effectExecutor.execute(target, effect);
						variablesManager.pop();
					}
					targets.clear();
					Pools.free(targets);
				} else {
					Gdx.app.error("EffectsSystem", "No executor for effect "
							+ effect.getClass());
				}
			}
		}
	}

	/**
	 * Returns the entities that match the given {@code target}.
	 * 
	 * @param owner
	 *            The entity that owns the effect. Needed if {@code target} is
	 *            "this".
	 * @param target
	 *            The target. For details on supported target values, see
	 *            {@link Effect#target}.
	 * @return An array with the entity targets
	 */
	protected Array<Entity> findTargets(Entity owner, String target) {
		Array targetsFound = Pools.obtain(Array.class);
		targetsFound.clear();
		variablesManager.push();
		// Default option: the effect's owner
		if (target == null || THIS.equals(target)) {
			targetsFound.add(owner);
		} else if (target != null && target.equals(ALL)) {
			Iterator<Entity> allEntities = engine
					.getEntitiesFor(Family.getFamilyFor()).values().iterator();
			while (allEntities.hasNext()) {
				targetsFound.add(allEntities.next());
			}
		} else if (target != null && target.startsWith(EACH)) {
			if (!target.contains("{") || !target.contains("}")) {
				Gdx.app.error("EffectsSystem",
						"Invalid syntax for target. Should match: each _target {expression}");
			} else {
				String expression = target.substring(target.indexOf("{") + 1,
						target.lastIndexOf("}"));
				// Iterate through entities
				Iterator<Entity> allEntities = engine
						.getEntitiesFor(Family.getFamilyFor()).values()
						.iterator();
				try {
					while (allEntities.hasNext()) {
						Entity otherEntity = allEntities.next();
						if (variablesManager.localEntityVar(otherEntity)
								.evaluateCondition(expression, false)) {
							targetsFound.add(otherEntity);
						}
					}
				} catch (IllegalArgumentException e) {
					Gdx.app.error(
							"EffectsSystem",
							"Bloody hell! The expression was not well formed and therefore it was not possible to determine targets for this effect. The effect will be skipped. Target was = "
									+ target);
				}

			}
		} else {
			Gdx.app.error(
					"EffectsSystem",
					"No valid target for effect. Accepted targets are \"all\", \"this\" and \"each _target {expression}\". Target found = "
							+ target + ". The effect will not be launched");
		}
		variablesManager.pop();
		return targetsFound;
	}
}
