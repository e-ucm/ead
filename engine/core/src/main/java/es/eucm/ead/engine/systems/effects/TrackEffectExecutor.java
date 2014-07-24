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

import java.util.HashMap;
import java.util.Map;

import ashley.core.Entity;

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.systems.effects.effecttotween.EffectToTween;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Timer;
import es.eucm.ead.schema.components.tweens.Tween;
import es.eucm.ead.schema.effects.AddComponent;
import es.eucm.ead.schema.effects.AnimationEffect;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.TimedEffect;
import es.eucm.ead.schema.effects.TrackEffect;

public class TrackEffectExecutor extends EffectExecutor<TrackEffect> {

	private EffectsSystem system;

	private Map<Class<? extends AnimationEffect>, EffectToTween<AnimationEffect>> tweensMap;

	public TrackEffectExecutor(EffectsSystem system) {
		super();
		this.system = system;
		this.tweensMap = new HashMap<Class<? extends AnimationEffect>, EffectToTween<AnimationEffect>>();
	}

	public void registerTween(Class<? extends AnimationEffect> effectClass,
			EffectToTween effectToTween) {
		tweensMap.put(effectClass, effectToTween);
	}

	@Override
	public void execute(Entity target, TrackEffect track) {
		Array array = new Array();

		for (TimedEffect timedEffect : track.getEffects()) {

			Behavior behavior = new Behavior();

			Timer event = new Timer();
			event.setTime(timedEffect.getTime());
			event.setRepeat(1);
			event.setCondition("btrue");

			behavior.setEvent(event);

			Array effectsArray = behavior.getEffects();

			Effect effect = timedEffect.getEffect();

			if (effect instanceof AnimationEffect) {
				AddComponent addComponent = new AddComponent();

				Tween tween = tweensMap.get(effect.getClass()).effectToTween(
						(AnimationEffect) effect);
				addComponent.setComponent(tween);
				addComponent.setTarget(effect.getTarget());

				effectsArray.add(addComponent);
			} else {
				effectsArray.add(effect);
			}

			AddComponent addBehavior = new AddComponent();
			addBehavior.setComponent(behavior);
			array.add(addBehavior);

		}
		system.executeEffectList(array);
	}

}
