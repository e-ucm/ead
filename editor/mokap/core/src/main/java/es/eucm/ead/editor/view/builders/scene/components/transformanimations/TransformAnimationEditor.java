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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ShowSpinner;
import es.eucm.ead.editor.control.actions.model.generic.RemoveFromArray;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.control.actions.model.scene.AddComponent;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.Slider;
import es.eucm.ead.editor.view.widgets.StatesButton;
import es.eucm.ead.editor.view.widgets.SwitchDropDownPane;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.editor.view.widgets.layouts.StackContainer;
import es.eucm.ead.editor.view.widgets.modals.SpinnerModal.SpinnerModalListener;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.editor.components.animations.TransformAnimation;
import es.eucm.ead.schema.editor.components.animations.TransformAnimation.Ease;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;

public abstract class TransformAnimationEditor<T extends TransformAnimation>
		extends SwitchDropDownPane implements FieldListener, ModelView,
		SpinnerModalListener {

	protected Controller controller;

	protected ModelEntity entity;

	protected T animation;

	private String componentId;

	protected LinearLayout buttons;

	protected StatesButton easeButton;

	protected StatesButton yoyoButton;

	protected StatesButton repeatButton;

	protected Label repeats;

	private Slider speed;

	private Image speedPointer;

	public TransformAnimationEditor(Controller c, String componentId,
			String titleI18nKey) {
		super(c.getApplicationAssets().getSkin());
		this.controller = c;
		this.componentId = componentId;

		switchWidget.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (switchWidget.isChecked()) {
					controller.action(AddComponent.class, entity,
							buildComponent());
				} else {
					removeComponent();
				}
			}
		});

		Skin skin = controller.getApplicationAssets().getSkin();
		setTitle(controller.getApplicationAssets().getI18N().m(titleI18nKey));
		buttons = new LinearLayout(true);
		addBodyRow(buttons);

		buttons.add(easeButton = new StatesButton(SkinConstants.STYLE_CONTEXT,
				skin, SkinConstants.IC_EASE_LINEAR,
				SkinConstants.IC_EASE_IN_CUBIC,
				SkinConstants.IC_EASE_OUT_CUBIC,
				SkinConstants.IC_EASE_IN_OUT_CUBIC,
				SkinConstants.IC_EASE_IN_BOUNCE,
				SkinConstants.IC_EASE_OUT_BOUNCE));

		easeButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				controller.action(SetField.class, animation, FieldName.EASE,
						getSelectedEase());
			}
		});

		buttons.add(yoyoButton = new StatesButton(SkinConstants.STYLE_CONTEXT,
				skin, SkinConstants.IC_YOYO, SkinConstants.IC_NO_YOYO));

		yoyoButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				controller.action(SetField.class, animation, FieldName.YOYO,
						yoyoButton.getSelectedIndex() == 0);
			}
		});

		buttons.add(repeatButton = new StatesButton(
				SkinConstants.STYLE_CONTEXT, skin, SkinConstants.IC_REPLAY,
				SkinConstants.IC_REPEAT));

		repeatButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (repeatButton.getSelectedIndex() == 0) {
					controller.action(SetField.class, animation,
							FieldName.REPEAT, -1);
				} else {
					controller.action(SetField.class, animation,
							FieldName.REPEAT, 0);
				}
			}
		});

		buttons.add(repeats = new Label("100", skin)).expandX();
		buttons.setComputeInvisibles(true);
		repeats.setAlignment(Align.center);
		repeats.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(ShowSpinner.class, "Repeat", "times",
						animation.getRepeat(), 0, 100,
						TransformAnimationEditor.this);
			}
		});

		LinearLayout speedRow = new LinearLayout(true);

		Image speedometer = new Image(skin, SkinConstants.IC_SPEED);
		speedometer.setColor(skin.getColor(SkinConstants.COLOR_GRAY));
		speedPointer = new Image(skin, SkinConstants.IC_SPEED_POINTER);
		speedPointer.pack();
		speedPointer.setOrigin(speedPointer.getWidth() / 2,
				speedPointer.getHeight() / 4);
		speedPointer.setColor(skin.getColor(SkinConstants.COLOR_GRAY));
		speedRow.add(new StackContainer(speedometer, speedPointer))
				.margin(WidgetBuilder.dpToPixels(12)).centerY();

		speedRow.add(speed = new Slider(0.0f, 1.0f, 0.01f, false, skin))
				.expandX();
		speed.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				controller.action(SetField.class, animation, FieldName.SPEED,
						speed.getValue());
			}
		});

		addBodyRow(speedRow);
	}

	public String getComponentId() {
		return componentId;
	}

	public T buildComponent() {
		T component = newComponent();
		component.setId(componentId);
		read(component);
		return component;
	}

	public void removeComponent() {
		ModelComponent component = Q.getComponentById(entity, componentId);
		if (component != null) {
			controller.action(RemoveFromArray.class, entity,
					entity.getComponents(), component);
		}
	}

	protected abstract T newComponent();

	public void setEntity(ModelEntity entity) {
		this.entity = entity;
	}

	public void read(T animation) {
		release();
		controller.getModel().addFieldListener(animation, this);
		this.animation = animation;
		setSelectedEase(animation.getEase());
		yoyoButton.setState(animation.isYoyo() ? 0 : 1);
		setRepeat(animation.getRepeat());
		setSpeed(animation.getSpeed());
	}

	@Override
	public boolean listenToField(String fieldName) {
		return true;
	}

	@Override
	public void modelChanged(FieldEvent event) {
		String field = event.getField();
		if (FieldName.EASE.equals(field)) {
			setSelectedEase((Ease) event.getValue());
		} else if (FieldName.YOYO.equals(field)) {
			yoyoButton.setState((Boolean) event.getValue() ? 0 : 1);
		} else if (FieldName.REPEAT.equals(field)) {
			setRepeat((Integer) event.getValue());
		} else if (FieldName.SPEED.equals(field)) {
			setSpeed((Float) event.getValue());
		}
	}

	@Override
	public void prepare() {
	}

	@Override
	public void release() {
		animation = null;
		controller.getModel().removeListenerFromAllTargets(this);
	}

	@Override
	public void value(int value) {
		controller.action(SetField.class, animation, FieldName.REPEAT, value);
	}

	@Override
	public void cancelled() {
	}

	private Ease getSelectedEase() {
		switch (easeButton.getSelectedIndex()) {
		case 0:
			return Ease.LINEAR;
		case 1:
			return Ease.CUBIC_IN;
		case 2:
			return Ease.CUBIC_OUT;
		case 3:
			return Ease.CUBIC_IN_OUT;
		case 4:
			return Ease.BOUNCE_IN;
		case 5:
			return Ease.BOUNCE_OUT;
		default:
			return Ease.LINEAR;
		}
	}

	private void setSpeed(float value) {
		speed.setValue(value);
		speedPointer.setRotation(360.0f - (value / (speed.getMaxValue() - speed
				.getMinValue())) * 180.0f);
	}

	private void setSelectedEase(Ease ease) {
		easeButton.setState(ease.ordinal());
	}

	private void setRepeat(int repeat) {
		if (repeat == -1) {
			repeatButton.setState(0);
			repeats.setVisible(false);
		} else {
			repeatButton.setState(1);
			repeats.setText(repeat + "");
			repeats.setVisible(true);
		}
	}
}
