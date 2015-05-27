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
package es.eucm.ead.editor.view.ui.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.view.controllers.ClassOptionsController;
import es.eucm.ead.editor.view.controllers.ParameterOptionsController;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schemax.FieldName;
import es.eucm.i18n.I18N;

public abstract class EffectWidget<T extends Effect> extends LinearLayout
		implements FieldListener {

	public static final String EDIT_BUTTON = "editButton";

	public static final Array<String> IGNORED_FIELDS = new Array<String>(
			new String[] { FieldName.TARGET });

	public static float MARGIN = 5.0f;

	protected Controller controller;

	protected Model model;

	protected I18N i18N;

	protected Skin skin;

	private EffectWidgetStyle style;

	private LinearLayout editor;

	private ClassOptionsController<T> optionsController;

	private Label stringRepresentation;

	public EffectWidget() {
		super(false);
		setComputeInvisibles(false);
	}

	public void initialize(Controller controller) {
		this.controller = controller;
		this.model = controller.getModel();
		this.i18N = controller.getApplicationAssets().getI18N();
		this.skin = controller.getApplicationAssets().getSkin();
		style = skin.get(EffectWidgetStyle.class);
		init();
	}

	protected abstract String getIcon();

	protected abstract String effectToString();

	protected Actor getShortRepresenation() {
		stringRepresentation = new Label("", skin);
		return stringRepresentation;
	}

	protected T getEffect() {
		return optionsController.getObjectRepresented();
	}

	protected abstract boolean hasTarget();

	protected abstract Color getBackgroundColor();

	protected abstract Class<T> getEffectClass();

	public void setEffect(T effect) {
		optionsController.read(effect);
		model.removeListenerFromAllTargets(this);
		model.addFieldListener(effect, this);
		updateShortRepresentation();
	}

	private void init() {
		setColor(getBackgroundColor());
		LinearLayout shortRepresentation = new LinearLayout(true);
		shortRepresentation.background(style.background)
				.backgroundColor(getBackgroundColor()).pad(style.padding);

		shortRepresentation.add(new Image(skin.getDrawable(getIcon())));
		shortRepresentation.add(getShortRepresenation()).margin(MARGIN, 0,
				MARGIN, 0);
		shortRepresentation.addSpace();

		add(shortRepresentation).expandX();
		editor = new LinearLayout(false);
		addFieldsEditors();
		if (editor.getChildren().size > 0) {
			Button editButton = new Button(style.editButton);
			editButton.setName(EDIT_BUTTON);
			editButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					editor.setVisible(!editor.isVisible());
					invalidateHierarchy();
				}
			});
			shortRepresentation.add(editButton);

			editor.setVisible(false);
			add(editor).expandX().margin(MARGIN);
		}
	}

	private void addFieldsEditors() {
		optionsController = new ParameterOptionsController<T>(controller, skin,
				getEffectClass(), IGNORED_FIELDS);
		if (optionsController.getPanel().getChildren().size > 0) {
			editor.add(optionsController.getPanel()).expandX();
		}
	}

	@Override
	public boolean listenToField(String fieldName) {
		return true;
	}

	@Override
	public void modelChanged(FieldEvent event) {
		updateShortRepresentation();
	}

	protected void updateShortRepresentation() {
		if (stringRepresentation != null) {
			stringRepresentation.setText(effectToString());
		}
	}

	public static class EffectWidgetStyle {

		public ButtonStyle editButton;

		public Drawable background;

		public float padding = 1.0f;

	}
}
