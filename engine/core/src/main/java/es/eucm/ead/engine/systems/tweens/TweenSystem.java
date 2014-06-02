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
package es.eucm.ead.engine.systems.tweens;

import java.util.HashMap;
import java.util.Map;

import ashley.core.Engine;
import ashley.core.Entity;
import ashley.core.EntityListener;
import ashley.core.Family;
import ashley.systems.IteratingSystem;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.scenes.scene2d.Group;

import es.eucm.ead.engine.components.TweensComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.systems.tweens.FieldAccessor.FieldWrapper;
import es.eucm.ead.engine.systems.tweens.tweencreators.BaseTweenCreator;
import es.eucm.ead.schema.components.tweens.BaseTween;
import es.eucm.ead.schema.components.tweens.Timeline;

/**
 * Deals with tweens components in entities. Relies in {@link TweenManager} to
 * do actual tweens
 */
public class TweenSystem extends IteratingSystem implements EntityListener {

	private TweenManager tweenManager;

	private Map<Class, BaseTweenCreator> baseTweenCreators;

	public TweenSystem() {
		super(Family.getFamilyFor(TweensComponent.class));
		tweenManager = new TweenManager();
		Tween.registerAccessor(Group.class, new GroupAccessor());
		Tween.registerAccessor(FieldWrapper.class, new FieldAccessor());
		baseTweenCreators = new HashMap<Class, BaseTweenCreator>();
	}

	@Override
	// Overriden so this object can register itself to listen to entity removal
	// notifications - so it can actually kill animations for entities that are
	// gone
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(this);
	}

	/**
	 * Register a tween creator for the given clazz.
	 */
	public <T extends BaseTween> void registerBaseTweenCreator(Class<T> clazz,
			BaseTweenCreator<T> tweenCreator) {
		baseTweenCreators.put(clazz, tweenCreator);
	}

	public Map<Class, BaseTweenCreator> getBaseTweenCreators() {
		return baseTweenCreators;
	}

	@Override
	public void update(float deltaTime) {
		// First call super, so processEntity is executed and possible new
		// tweens are added
		super.update(deltaTime);
		// Update tweens
		tweenManager.update(deltaTime);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TweensComponent tweens = entity.getComponent(TweensComponent.class);
		for (es.eucm.ead.schema.components.tweens.BaseTween t : tweens
				.getTweens()) {
			BaseTweenCreator tweenCreator = baseTweenCreators.get(t.getClass());
			if (tweenCreator != null) {
				tweenManager.add(tweenCreator.createTween(
						(EngineEntity) entity, t));
			}
		}
		entity.remove(TweensComponent.class);
	}

	@Override
	public void entityAdded(Entity entity) {
		// Nothing needed
	}

	@Override
	// Just kill all animations related to this entity
	public void entityRemoved(Entity entity) {
		if (entity instanceof EngineEntity) {
			tweenManager.killTarget(((EngineEntity) entity).getGroup());
		} else {
			tweenManager.killTarget(entity);
		}
	}

	/**
	 * Returns the complete duration, including initial delay and repetitions.
	 * The formula is as follows:
	 * 
	 * <pre>
	 * fullDuration = delay + duration + (repeatDelay + duration) * repeatCnt
	 * </pre>
	 */
	public static float getAnimationFullDuration(BaseTween baseTween) {
		int repeatCnt = baseTween.getRepeat();
		float delay = 0;
		if (repeatCnt < 0)
			return -1;

		if (baseTween instanceof es.eucm.ead.schema.components.tweens.Tween) {
			es.eucm.ead.schema.components.tweens.Tween tween = (es.eucm.ead.schema.components.tweens.Tween) baseTween;
			return tween.getDelay() + tween.getDuration()
					+ (tween.getRepeatDelay() + tween.getDuration())
					* repeatCnt;
		} else if (baseTween instanceof Timeline) {
			Timeline animation = (Timeline) baseTween;
			float duration = 0;

			for (int i = 0; i < animation.getChildren().size(); i++) {
				BaseTween obj = animation.getChildren().get(i);

				if (obj.getRepeat() < 0)
					throw new RuntimeException(
							"You can't push an object with infinite repetitions in a timeline");

				switch (animation.getMode()) {
				case SEQUENCE:
					duration += getAnimationFullDuration(obj);
					break;

				case PARALLEL:
					duration = Math
							.max(duration, getAnimationFullDuration(obj));
					break;
				}
			}
			return delay + duration + (animation.getRepeatDelay() + duration)
					* repeatCnt;
		}

		return 0;
	}

}
