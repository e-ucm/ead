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
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.components.TweensComponent;
import es.eucm.ead.engine.components.behaviors.TimersComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.systems.tweens.TweenSystem;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.effects.AddAnimation;
import es.eucm.ead.schema.effects.AddEntity;
import es.eucm.ead.schema.effects.RemoveEntity;

/**
 * Adds a new entity referenced from a separate file.
 * 
 * This executor also creates other components over the entities added as
 * needed: - Uses {@link TweensComponent} to launch in animation - Uses
 * {@link TimersComponent} with {@link AddAnimation} and {@link RemoveEntity}
 * effects to execute out animation and also to get the entity removed after
 * duration elapses.
 * 
 * Created by Javier Torrente on 3/05/14.
 */
public class AddEntityExecutor extends EffectExecutor<AddEntity> {

	private EntitiesLoader entitiesLoader;

	private VariablesManager variablesManager;

	public AddEntityExecutor(EntitiesLoader entitiesLoader,
			VariablesManager variablesManager) {
		this.entitiesLoader = entitiesLoader;
		this.variablesManager = variablesManager;
	}

	@Override
	public void execute(final Entity target, final AddEntity effect) {
		// Add entity to engine
		if (effect.getEntityUri() != null) {
			entitiesLoader.loadEntity(effect.getEntityUri(),
					new EntitiesLoader.EntityLoadedCallback() {
						@Override
						public void loaded(String path,
								EngineEntity engineEntity) {
							addEngineEntity(target, engineEntity, effect);
						}
					});
			entitiesLoader.finishLoading();
		} else {
			// Just log that the effect will be skipped
			Gdx.app.debug("AddEntityExecutor", "Effect: " + effect
					+ " was skipped 'cause no entity to add was defined.");
		}
	}

	private void addEngineEntity(Entity target, EngineEntity entityToAdd,
			AddEntity effect) {
		// Add entity to parent (if applicable)
		if (target instanceof EngineEntity) {
			EngineEntity ownerEngineEntity = (EngineEntity) target;
			ownerEngineEntity.getGroup().addActor(entityToAdd.getGroup());

			// Update newest entity var
			variablesManager.globalNewestEntityVar(entityToAdd);

			// If the entity has in-animation, add it
			if (effect.getAnimationIn() != null) {
				if (!entityToAdd.hasComponent(TweensComponent.class)) {
					entityToAdd.add(gameLoop
							.createComponent(TweensComponent.class));
				}
				entityToAdd.getComponent(TweensComponent.class).addTween(
						effect.getAnimationIn());
			}

			// Setup out animation and entity removal (if needed)
			if (effect.getDuration() >= 0) {
				// If the entity has out animation, add a timer that will add
				// the animation when it is needed
				float inAnimationDuration = effect.getAnimationIn() != null ? TweenSystem
						.getAnimationFullDuration(effect.getAnimationIn()) : 0;
				float effectDuration = effect.getDuration();
				float outAnimationDelay = inAnimationDuration + effectDuration;
				if (effect.getAnimationOut() != null) {
					AddAnimation addAnimationEffect = new AddAnimation();
					addAnimationEffect.setAnimation(effect.getAnimationOut());
					EffectsSystem.launchDelayedEffect(gameLoop,
							addAnimationEffect, outAnimationDelay, entityToAdd);
				}

				// If the entity has to be removed, add a timer with remove
				// entity effect
				float removalDelay = outAnimationDelay
						+ (effect.getAnimationOut() != null ? TweenSystem
								.getAnimationFullDuration(effect
										.getAnimationOut()) : 0);
				RemoveEntity removeEntityEffect = new RemoveEntity();
				EffectsSystem.launchDelayedEffect(gameLoop, removeEntityEffect,
						removalDelay, entityToAdd);
			}
		}
	}
}
