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
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.BaseTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.RotateTween;
import es.eucm.ead.schema.components.tweens.ScaleTween;
import es.eucm.ead.schema.components.tweens.Timeline;
import es.eucm.ead.schema.components.tweens.Tween;
import es.eucm.ead.schemax.FieldName;

/**
 * <p>
 * Multiplies the time or the numerical fields of the all {@link BaseTweens} in
 * a {@link Timeline}
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>Timeline</em></dd>
 * <dd><strong>args[1]</strong> <em>Float</em> the multiplier</dd>
 * <dd><strong>args[2]</strong> <em>Boolean</em> if true multiplies the time, if
 * false multiplies the numerical fields</dd>
 * </dl>
 */
public class MultiplyTimeline extends ModelAction {

	public MultiplyTimeline() {
		super(true, false, Timeline.class, Float.class, Boolean.class);
	}

	@Override
	public Command perform(Object... args) {

		CompositeCommand command = new CompositeCommand();
		Timeline timeline = (Timeline) args[0];
		float multiplier = (Float) args[1];
		boolean time = (Boolean) args[2];
		
		for (BaseTween baseTween : timeline.getChildren()) {
			if (baseTween instanceof Tween) {
				if (time) {
					Tween tween = (Tween) baseTween;
					command.addCommand(new FieldCommand(tween,
							FieldName.DURATION, tween.getDuration()
									* multiplier));
				} else {
					if (baseTween instanceof AlphaTween) {
						command.addCommand(new FieldCommand(baseTween,
								FieldName.ALPHA, ((AlphaTween) baseTween)
										.getAlpha() * multiplier));
					} else if (baseTween instanceof MoveTween) {
						command.addCommand(new FieldCommand(baseTween,
								FieldName.X, ((MoveTween) baseTween).getX()
										* multiplier));
						command.addCommand(new FieldCommand(baseTween,
								FieldName.Y, ((MoveTween) baseTween).getY()
										* multiplier));
					} else if (baseTween instanceof ScaleTween) {
						command.addCommand(new FieldCommand(baseTween,
								FieldName.SCALE_X, ((ScaleTween) baseTween)
										.getScaleX() * multiplier));
						command.addCommand(new FieldCommand(baseTween,
								FieldName.SCALE_Y, ((ScaleTween) baseTween)
										.getScaleY() * multiplier));
					} else if (baseTween instanceof RotateTween) {
						command.addCommand(new FieldCommand(baseTween,
								FieldName.ROTATION, ((RotateTween) baseTween)
										.getRotation() * multiplier));
					}
				}
			} else {
				perform(baseTween, command, multiplier, time);
			}
		}
		
		return command;
	}

}
