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
package es.eucm.ead.engine.components.behaviors;

import ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.engine.components.behaviors.events.RuntimeBehavior;
import es.eucm.ead.schema.components.behaviors.Event;
import es.eucm.ead.schema.effects.Effect;

public abstract class BehaviorComponent<S extends Event, T extends RuntimeBehavior>
		extends Component implements Poolable {

	private SnapshotArray<T> runtimeBehaviors = new SnapshotArray<T>();

	public SnapshotArray<T> getBehaviors() {
		return runtimeBehaviors;
	}

	@Override
	public void reset() {
		for (T runtimeEvent : runtimeBehaviors) {
			Pools.free(runtimeEvent);
		}
		runtimeBehaviors.clear();
	}

	public abstract Class<T> getRuntimeBehaviorClass();

	@Override
	public boolean combine(Component component) {
		if (component.getClass() == getClass()) {
			BehaviorComponent<S, T> other = (BehaviorComponent<S, T>) component;
			runtimeBehaviors.addAll(other.getBehaviors());
			return true;
		}
		return false;
	}

	public void addBehavior(S event, Iterable<Effect> effects) {
		T runtimeBehavior = Pools.obtain(getRuntimeBehaviorClass());
		runtimeBehavior.setEffects(effects);
		initializeBehavior(event, runtimeBehavior);
		runtimeBehaviors.add(runtimeBehavior);
	}

	/**
	 * Reads attributes from {@code event} and set them to
	 * {@code runtimeBehavior}
	 */
	protected void initializeBehavior(S event, T runtimeBehavior) {
		initializeBehavior(event, event.getClass(), runtimeBehavior);
	}

	/**
	 * Default implementation relies in reflection to set the attributes
	 */
	protected void initializeBehavior(S event, Class eventClass,
			T runtimeBehavior) {
		Method[] methods = ClassReflection.getDeclaredMethods(eventClass);
		for (Method get : methods) {
			String getPrefix = get.getName().startsWith("get") ? "get" : "is";
			if (get.getName().startsWith("get")
					|| get.getName().startsWith("is")) {
				// Search equivalent set method in runtimeBehavior
				String setMethodName = get.getName().replace(getPrefix, "set");
				Class returningType = get.getReturnType();
				try {
					Method set = ClassReflection.getDeclaredMethod(
							runtimeBehavior.getClass(), setMethodName,
							returningType);
					if (set != null) {
						set.invoke(runtimeBehavior, get.invoke(event));
					}
				} catch (ReflectionException e) {
					Gdx.app.error("BehaviorComponent",
							"Error initializing behavior", e);
				}
			}
		}

		if (eventClass.getSuperclass() != null
				&& eventClass.getSuperclass() != Object.class) {
			initializeBehavior(event, eventClass.getSuperclass(),
					runtimeBehavior);
		}
	}

}
