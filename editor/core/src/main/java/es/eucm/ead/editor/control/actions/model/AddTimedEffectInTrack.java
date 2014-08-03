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
import es.eucm.ead.editor.control.commands.ListCommand.AddToListCommand;
import es.eucm.ead.schema.effects.AnimationEffect;
import es.eucm.ead.schema.effects.TimedEffect;
import es.eucm.ead.schema.effects.TrackEffect;

/**
 * 
 * Adds a {@link TimedEffect} in {@link TrackEffect}
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link TrackEffect}</em></dd>
 * <dd><strong>args[1]</strong> <em>{@link TimedEffect}</em></dd>
 * </dl>
 */
public class AddTimedEffectInTrack extends ModelAction {

	public AddTimedEffectInTrack() {
		super(true, false, TrackEffect.class, TimedEffect.class);
	}

	@Override
	public Command perform(Object... args) {
		TrackEffect track = (TrackEffect) args[0];

		TimedEffect effect = (TimedEffect) args[1];

		CompositeCommand command = new CompositeCommand();
		int index = 0;
		float beforeX = 0;
		float lastW = 0;
		float leftX = 0;

		for (TimedEffect e : track.getEffects()) {
			float width = e.getEffect() instanceof AnimationEffect ? ((AnimationEffect) e
					.getEffect()).getDuration() : 0;

			float x = 0;

			float auxX = leftX + e.getTime();

			if (!(e.getEffect() instanceof AnimationEffect)) {
				x = e.getTime();
			} else {
				command.addCommand(controller.getActions()
						.getAction(ChangeEffectDuration.class)
						.perform(track, e, width, 0));
				x = leftX
						+ (auxX < beforeX + lastW ? beforeX + lastW : e
								.getTime());
			}

			beforeX = x;
			lastW = width;

			if (effect.getEffect() instanceof AnimationEffect
					&& effect.getTime() > beforeX
					&& effect.getTime() < beforeX + lastW / 2) {
				command.addCommand(controller
						.getActions()
						.getAction(ChangeTimedEffectTime.class)
						.perform(
								e,
								effect.getTime()
										+ ((AnimationEffect) effect.getEffect())
												.getDuration()));
			} else if (effect.getTime() > beforeX + lastW / 2) {
				index++;
			}

		}
		command.addCommand(new AddToListCommand(track, track.getEffects(),
				effect, index));

		return command;
	}

}
