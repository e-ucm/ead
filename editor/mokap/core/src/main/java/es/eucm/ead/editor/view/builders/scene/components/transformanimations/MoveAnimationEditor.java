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
package es.eucm.ead.editor.view.builders.scene.components.transformanimations;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Align;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ShowSpinner;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.Slider;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.editor.view.widgets.modals.SpinnerModal.SpinnerModalListener;
import es.eucm.ead.schema.editor.components.animations.MoveAnimation;
import es.eucm.ead.schemax.ComponentIds;
import es.eucm.ead.schemax.FieldName;

public class MoveAnimationEditor extends
		TransformAnimationEditor<MoveAnimation> {

	private Slider amplitude;

	private IconButton direction;

	private Container<Image> segmentSize;

	private SpinnerModalListener listener = new SpinnerModalListener() {
		@Override
		public void value(int value) {
			controller.action(SetField.class, animation, FieldName.DIRECTION,
					(float) value);
		}

		@Override
		public void cancelled() {
		}
	};

	public MoveAnimationEditor(Controller c) {
		super(c, ComponentIds.MOVE, "move");
		Skin skin = controller.getApplicationAssets().getSkin();

		buttons.add(
				0,
				direction = WidgetBuilder.icon(SkinConstants.IC_ARROW,
						SkinConstants.STYLE_ORANGE));
		direction.pack();
		direction.setOrigin(Align.center);
		direction.setTransform(true);
		direction.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(ShowSpinner.class, "Direction ", "º",
						(int) animation.getDirection(), 0, 359, listener);
			}
		});

		LinearLayout amplitudeRow = new LinearLayout(true);
		amplitude = new Slider(0.1f, 1.0f, 0.01f, false, skin);

		LinearLayout segment = new LinearLayout(true);
		segment.background(skin
				.getDrawable(SkinConstants.DRAWABLE_TRANSPARENT_48));
		segment.add(WidgetBuilder.image(SkinConstants.IC_SEGMENT_END,
				SkinConstants.COLOR_GRAY));
		segment.add(segmentSize = new Container<Image>(WidgetBuilder.image(
				SkinConstants.IC_SEGMENT_MIDDLE, SkinConstants.COLOR_GRAY)));
		segment.add(WidgetBuilder.image(SkinConstants.IC_SEGMENT_END,
				SkinConstants.COLOR_GRAY));

		amplitudeRow.add(segment).centerX().centerY();
		amplitudeRow.add(amplitude).expandX();
		addBodyRow(amplitudeRow);
		amplitude.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				controller.action(SetField.class, animation,
						FieldName.AMPLITUDE, amplitude.getValue());
			}
		});
	}

	@Override
	public void read(MoveAnimation animation) {
		super.read(animation);
		setAmplitude(animation.getAmplitude());
		setDirection(animation.getDirection());
	}

	@Override
	public void modelChanged(FieldEvent event) {
		if (FieldName.AMPLITUDE.equals(event.getField())) {
			setAmplitude((Float) event.getValue());
		} else if (FieldName.DIRECTION.equals(event.getField())) {
			setDirection((Float) event.getValue());
		} else {
			super.modelChanged(event);
		}
	}

	private void setAmplitude(float value) {
		amplitude.setValue(value);
		float width = segmentSize.getActor().getPrefWidth();
		segmentSize
				.width(Math.round((value / (amplitude.getMaxValue() - amplitude
						.getMinValue())) * width));
		((Layout) segmentSize.getParent()).invalidate();
	}

	private void setDirection(float value) {
		direction.setRotation(value);
	}

	@Override
	protected MoveAnimation newComponent() {
		return new MoveAnimation();
	}
}
