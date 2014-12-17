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
package es.eucm.ead.editor.view.widgets.options;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Represents a row in an {@link OptionsPanel}. Contains a label, an optional
 * tooltip (in the left column) and a parameter button that, when pressed,
 * interchanges the option widget with an expression editor.
 */
public class ParameterOption extends Option {

	public static final String PARAMETER_BUTTON = "parameterbutton";

	private Skin skin;

	private TextField expressionEditor;

	private ImageButton parameterButton;

	public ParameterOption(Skin skin, ParameterOptionStyle style) {
		this(skin, null, null, null, style);
	}

	public ParameterOption(Skin skin, String label, String tooltip,
			Actor optionWidget, ParameterOptionStyle style) {
		super(label, tooltip, optionWidget, style);
		this.skin = skin;
		expressionEditor = new TextField("", skin);
	}

	@Override
	protected void init(String label, String tooltip, Actor optionWidget) {
		tooltip(tooltip);
		addParameterButton();
		label(label);
		addSpace();
		option(optionWidget);
	}

	private void addParameterButton() {
		parameterButton = new ImageButton(
				((ParameterOptionStyle) style).parameterButton);
		parameterButton.setName(PARAMETER_BUTTON);
		add(parameterButton);
		parameterButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setExpressionEditorVisible(optionContainer.getActor() == optionWidget);
			}
		});
	}

	/**
	 * Shows or hide the expression editor
	 */
	public void setExpressionEditorVisible(boolean visible) {
		if (visible) {
			optionContainer.setActor(expressionEditor);
		} else {
			optionContainer.setActor(optionWidget);
		}
	}

	public ImageButton getParameterButton() {
		return parameterButton;
	}

	public Actor getExpressionEditor() {
		return expressionEditor;
	}

	public String getExpressionValue() {
		return expressionEditor.getText();
	}

	public void setExpressionValue(String expressionValue) {
		expressionEditor.setText(expressionValue);
	}

	public static class ParameterOptionStyle extends OptionStyle {
		public ImageButtonStyle parameterButton;
	}
}
