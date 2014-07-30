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
package es.eucm.ead.editor.view.widgets.timeline;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.actions.model.AddTimedEffectInTrack;
import es.eucm.ead.editor.control.actions.model.ChangeAnimationEffectDuration;
import es.eucm.ead.editor.control.actions.model.ChangeTimedEffectTime;
import es.eucm.ead.editor.control.actions.model.RemoveTimedEffectInTrack;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.view.widgets.StretchableButton;
import es.eucm.ead.editor.view.widgets.layouts.TrackLayout;
import es.eucm.ead.schema.effects.AnimationEffect;
import es.eucm.ead.schema.effects.TimedEffect;
import es.eucm.ead.schema.effects.TrackEffect;
import es.eucm.ead.schemax.FieldName;

/**
 * 
 * {@link StretchableButton} with {@link TimedEffect} that changes the duration
 * and time delay of this effect. Its parent have to be a {@link TrackLayout}
 * 
 */
public class GradualTrackEffectButton extends StretchableButton implements
		EffectButton, FieldListener {

	private float pixelsPerSecond;

	private TimedEffect effect;

	private Controller controller;

	public GradualTrackEffectButton(Actor actor, Float width,
			Drawable drawable, Controller controller, TimedEffect effect,
			float pixelsPerSecond) {
		super(actor, width, drawable, controller.getApplicationAssets()
				.getSkin());
		this.effect = effect;
		this.controller = controller;
		this.pixelsPerSecond = pixelsPerSecond;
		this.addListener(changeListener());
		setTotalWidth(((AnimationEffect) effect.getEffect()).getDuration() != 0 ? ((AnimationEffect) effect
				.getEffect()).getDuration() * pixelsPerSecond
				: right.getWidth() + left.getWidth());
		controller.getModel().addFieldListener(effect.getEffect(), this);
		controller.getModel().addFieldListener(effect, this);
	}

	public TimedEffect getEffect() {
		return effect;
	}

	public void setEffect(TimedEffect effect) {
		this.effect = effect;
	}

	private InputListener changeListener() {

		return new InputListener() {

			private TrackEffect old;

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				old = ((TrackEffectLayout) getParent()).getTrackEffect();
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				CompositeCommand c = new CompositeCommand();
				for (Actor actor : ((TrackLayout) getParent()).getChildren()) {
					if (actor instanceof EffectButton) {
						((EffectButton) actor).changeEffectValues(c);
					}
				}
				if (old != ((TrackEffectLayout) getParent()).getTrackEffect()) {
					changeTrackEffect(c, old,
							((TrackEffectLayout) getParent()).getTrackEffect());
				}
				c.getCommandList().reverse(); // In undo command, is necessary
												// to reposition actors starting
												// by the beginning of the track
												// to avoid collisions between
												// buttons.
				controller.command(c);
			}
		};
	}

	@Override
	public void modelChanged(FieldEvent event) {
		setTotalWidth(((AnimationEffect) effect.getEffect()).getDuration() != 0 ? ((AnimationEffect) effect
				.getEffect()).getDuration() * pixelsPerSecond
				: right.getWidth() + left.getWidth());
		if (getParent() instanceof TrackLayout) {
			((TrackLayout) getParent()).setLeftMargin(this, getEffect()
					.getTime() * pixelsPerSecond);
			((TrackLayout) getParent()).layout();
		}
	}

	@Override
	public boolean listenToField(String fieldName) {
		return (fieldName == FieldName.DURATION || fieldName == FieldName.TIME);
	}

	@Override
	public void changeEffectValues(CompositeCommand command) {
		if (((AnimationEffect) getEffect().getEffect()).getDuration() != getWidth()
				/ pixelsPerSecond) {
			ModelAction action = new ChangeAnimationEffectDuration();
			Command c = action.perform((AnimationEffect) getEffect()
					.getEffect(), getWidth() / pixelsPerSecond);

			command.addCommand(c);
		}

		if (getEffect().getTime() != ((TrackLayout) getParent())
				.getLeftMargin(this) / pixelsPerSecond) {
			ModelAction action2 = new ChangeTimedEffectTime();
			Command c2 = action2.perform(getEffect(),
					((TrackLayout) getParent()).getLeftMargin(this)
							/ pixelsPerSecond);

			command.addCommand(c2);
		}
	}

	public void changeTrackEffect(CompositeCommand command,
			TrackEffect oldValue, TrackEffect newValue) {
		ModelAction action = new RemoveTimedEffectInTrack();
		Command c = action.perform(oldValue, getEffect());

		ModelAction action2 = new AddTimedEffectInTrack();
		Command c2 = action2.perform(newValue, getEffect());

		command.addCommand(c);
		command.addCommand(c2);
	}
}
