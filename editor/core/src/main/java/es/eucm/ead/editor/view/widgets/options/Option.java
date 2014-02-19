/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.view.widgets.options;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.view.widgets.AbstractWidget;

public abstract class Option extends AbstractWidget implements FieldListener {

	private float margin;

	protected Controller controller;

	private final String field;

	private Object target;

	private Label label;

	private Actor option;

	public Option(Controller controller, String label, Object target,
			String field) {
		this.controller = controller;
		this.field = field;
		Skin skin = controller.getEditorAssets().getSkin();
		this.label = new Label(label, skin);
		addActor(this.label);
		margin = this.label.getWidth();
	}

	public void retarget(Object target) {
		controller.getModel().retargetListener(this.target, target, this);
		this.target = target;
	}

	@Override
	public boolean listenToField(String fieldName) {
		return field.equals(fieldName);
	}

	@Override
	public float getPrefWidth() {
		return margin + getPrefWidth(option);
	}

	@Override
	public float getPrefHeight() {
		return label.getHeight();
	}

	@Override
	public void layout() {
		label.setPosition(margin - label.getWidth(), 0);
		option.setBounds(margin, 0, getWidth() - margin, getHeight());
	}

	public void setMargin(float margin) {
		this.margin = margin;
	}

	public Label getLabel() {
		return label;
	}

	protected abstract Actor getOption(Skin skin);

	public void initialize() {
		option = getOption(controller.getEditorAssets().getSkin());
		addActor(this.option);
	}
}
