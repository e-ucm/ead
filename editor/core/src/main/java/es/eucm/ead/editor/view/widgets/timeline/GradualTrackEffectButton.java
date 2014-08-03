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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.ChangeEffectDuration;
import es.eucm.ead.editor.control.actions.model.ChangeTrackEffect;
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
		FieldListener {

	private static final Vector2 v = new Vector2();

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

			private int side;

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				old = ((TrackEffectLayout) getParent()).getTrackEffect();
				if (isDragLeft()) {
					side = -1;
				} else if (isDragRight()) {
					side = 1;
				} else {
					side = 0;
				}
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				v.set(x, y);
				localToParentCoordinates(v);

				if (old != ((TrackEffectLayout) getParent()).getTrackEffect()) {
					controller.action(ChangeTrackEffect.class, getEffect(),
							((TrackEffectLayout) getParent()).getTrackEffect(),
							old, v.x / pixelsPerSecond);

				} else if (side != 0) {
					controller.action(ChangeEffectDuration.class,
							((TrackEffectLayout) getParent()).getTrackEffect(),
							getEffect(), v.x / pixelsPerSecond, side);
				}
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
}
