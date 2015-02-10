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
package es.eucm.ead.editor.view.builders.scene.templates;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.data.Parameter;
import es.eucm.ead.schema.editor.components.templates.Attribute;
import es.eucm.ead.schemax.FieldName;

public class TemplateBar extends LinearLayout implements Model.FieldListener {

	private final float PAD = WidgetBuilder.dpToPixels(16);
	private final float PERCENTAGE_WIDTH = 0.25f;

	private Controller controller;

	private Label title;
	private Container<Label> textContainer;

	private LinearLayout topBar;

	private Parameter parameter;

	private Slider slider;

	private float auxWalk;

	public TemplateBar(Controller controller) {

		super(false);

		Skin skin = controller.getApplicationAssets().getSkin();
		I18N i18N = controller.getApplicationAssets().getI18N();
		this.controller = controller;

		auxWalk = 0;

		slider = new Slider(0, 1, 1, false, skin, SkinConstants.STYLE_TEMPLATE);

		topBar = new LinearLayout(true,
				skin.getDrawable(SkinConstants.DRAWABLE_TOOLBAR));
		topBar.backgroundColor(skin.getColor(SkinConstants.COLOR_BROWN_MOKA));

		title = new Label("", skin, SkinConstants.STYLE_TOOLBAR);

		IconButton save = WidgetBuilder.toolbarIcon(SkinConstants.IC_CHECK,
				i18N.m("accept"));
		IconButton close = WidgetBuilder.toolbarIcon(SkinConstants.IC_CLOSE,
				i18N.m("cancel"));

		textContainer = new Container<Label>();
		textContainer.setActor(title);
		textContainer.fillX();

		topBar.add(save);
		topBar.add(textContainer).marginLeft(PAD).marginRight(PAD);
		topBar.add(slider).expandX();
		topBar.addSpace();
		topBar.add(close);

		add(topBar).top().expandX().left();

	}

	public void changeWidget(Attribute attr, Parameter parameter) {
		if (this.parameter != null) {
			controller.getModel().removeListener(this.parameter, this);
		}
		this.parameter = parameter;
		controller.getModel().addFieldListener(this.parameter, this);

		title.setText(attr.getName());
		slider.setRange(attr.getMin(), attr.getMax());
		slider.setValue((Float) parameter.getValue());
	}

	public void horizontalMove(float max, float amount) {
		float stepsSize = max * 0.8f
				/ (slider.getMaxValue() - slider.getMinValue());

		float precision = slider.getStepSize();
		if (precision == Math.ceil(precision)) {
			precision = 1;
		} else {
			precision = 10;
		}

		float walk = auxWalk + amount / stepsSize;
		float stepPlus = 0;
		if (walk < 0) {
			stepPlus = MathUtils.ceil(walk * precision) / precision;
		} else {
			stepPlus = MathUtils.floor(walk * precision) / precision;
		}

		auxWalk = walk - stepPlus;

		float newValue = slider.getValue() + stepPlus;
		if (newValue > slider.getMaxValue()) {
			newValue = slider.getMaxValue();
		} else if (newValue < slider.getMinValue()) {
			newValue = slider.getMinValue();
		}
		controller.action(SetField.class, parameter, FieldName.VALUE, newValue);
	}

	public void stopMove() {
		auxWalk = 0;
	}

	@Override
	public void layout() {
		super.layout();
		setHeight(getPrefHeight());
		setX(0);
		if (getParent() != null) {
			setWidth(getParent().getWidth());
			setY(getParent().getHeight() - getPrefHeight());
			textContainer.width(getParent().getWidth() * PERCENTAGE_WIDTH);
		}

	}

	@Override
	public boolean listenToField(String fieldName) {
		return fieldName.equals(FieldName.VALUE);
	}

	@Override
	public void modelChanged(FieldEvent event) {
		slider.setValue((Float) parameter.getValue());
	}

}
