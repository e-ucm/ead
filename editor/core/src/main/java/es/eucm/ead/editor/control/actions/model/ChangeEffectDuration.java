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
package es.eucm.ead.editor.control.actions.model;

import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.schema.effects.AnimationEffect;
import es.eucm.ead.schema.effects.TimedEffect;
import es.eucm.ead.schema.effects.TrackEffect;
import es.eucm.ead.schemax.FieldName;

/**
 * 
 * Changes the duration time of {@link TimedEffect}
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link TrackEffect}</em></dd> track that
 * contains the effect
 * <dd><strong>args[1]</strong> <em>{@link TimedEffect}</em></dd>
 * <dd><strong>args[2]</strong> <em>{@link Float}</em>
 * <dd><strong>args[3]</strong> <em>{@link Integer}</em> <strong>1</strong> if
 * the <strong>args[1]</strong> is a new finish time, <strong>-1</strong> if is
 * a new delay, <strong>0</strong> if is the new value</dd>
 * </dl>
 */
public class ChangeEffectDuration extends ModelAction {

	public ChangeEffectDuration() {
		super(true, false, TrackEffect.class, TimedEffect.class, Float.class,
				Integer.class);
	}

	@Override
	public Command perform(Object... args) {

		CompositeCommand command = new CompositeCommand();

		AnimationEffect animationEffect = null;

		TrackEffect track = (TrackEffect) args[0];

		TimedEffect effect = (TimedEffect) args[1];

		if (effect.getEffect() instanceof AnimationEffect) {
			animationEffect = (AnimationEffect) effect.getEffect();
		} else {
			return null;
		}

		float newValue = 0f;
		if ((Integer) args[3] == 0) {
			newValue = (Float) args[2];
		} else if ((Integer) args[3] == 1) {
			newValue = (Float) args[2] - effect.getTime();
			int next = 1;
			float plus = 0;
			for (TimedEffect e : track.getEffects()) {
				if (plus > 0 && e.getEffect() instanceof AnimationEffect) {
					command.addCommand(controller.getActions()
							.getAction(ChangeTimedEffectTime.class)
							.perform(e, e.getTime() + plus));
				} else if (plus < 0) {
					break;
				}
				if (e == effect && next < track.getEffects().size) {
					plus = (Float) args[2]
							- track.getEffects().get(next).getTime();
				}
				next++;
			}
		} else {
			float newTime = (Float) args[2];
			if (newTime >= 0) {
				newValue = animationEffect.getDuration() + effect.getTime()
						- (Float) args[2];
			} else {
				newValue = animationEffect.getDuration() + effect.getTime();
				newTime = 0;
			}

			if (newValue < 0) {
				newValue = 0f;
				newTime = effect.getTime() + animationEffect.getDuration();
			} else if (newTime > 0) {
				boolean found = false;
				track.getEffects().reverse();
				for (TimedEffect e : track.getEffects()) {
					if (found && e.getEffect() instanceof AnimationEffect) {
						if ((Float) args[2] < e.getTime()
								+ ((AnimationEffect) e.getEffect())
										.getDuration()) {
							newTime = e.getTime()
									+ ((AnimationEffect) e.getEffect())
											.getDuration();
							newValue = animationEffect.getDuration()
									+ effect.getTime()
									- (e.getTime() + ((AnimationEffect) e
											.getEffect()).getDuration());
						}
						break;
					}
					if (e == effect) {
						found = true;
					}
				}
				track.getEffects().reverse();

				command.addCommand(controller.getActions()
						.getAction(ChangeTimedEffectTime.class)
						.perform(effect, newTime));
			}
		}

		command.getCommandList().reverse(); // Is necessary that the undo is
											// done from the front of the track
											// and not from the end.

		command.addCommand(new FieldCommand(animationEffect,
				FieldName.DURATION, newValue, false));
		return command;
	}
}
