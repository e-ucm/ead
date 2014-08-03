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
import es.eucm.ead.schema.effects.TimedEffect;
import es.eucm.ead.schema.effects.TrackEffect;

/***
 * 
 * Move a {@link TimedEffect} in {@link TrackEffect} to another
 * {@link TrackEffect}
 * 
 */
public class ChangeTrackEffect extends ModelAction {

	public ChangeTrackEffect() {
		super(true, false, TimedEffect.class, TrackEffect.class,
				TrackEffect.class, Float.class);
	}

	@Override
	public Command perform(Object... args) {

		TimedEffect e = (TimedEffect) args[0];

		TrackEffect newTrack = (TrackEffect) args[1];

		TrackEffect oldTrack = (TrackEffect) args[2];

		float newTime = (Float) args[3];

		CompositeCommand command = new CompositeCommand();

		command.addCommand(controller.getActions()
				.getAction(ChangeTimedEffectTime.class).perform(e, newTime));

		command.addCommand(controller.getActions()
				.getAction(RemoveTimedEffectInTrack.class).perform(oldTrack, e));

		command.addCommand(controller.getActions()
				.getAction(AddTimedEffectInTrack.class).perform(newTrack, e));

		return command;
	}

}
