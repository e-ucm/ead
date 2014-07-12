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
package es.eucm.ead.editor.view.controllers;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.view.controllers.values.ValueController;
import es.eucm.ead.editor.view.widgets.options.ParameterOption;

/**
 * Created by angel on 11/07/14.
 */
public class ParameterOptionController extends OptionController {

	private ParameterOption parameterOption;

	private Button parameterButton;

	public ParameterOptionController(
			final ParameterOptionsController optionsController,
			String optionField, final ParameterOption option,
			ValueController valueController) {
		super(optionsController, optionField, option, valueController);

		this.parameterOption = option;
		parameterButton = parameterOption.getParameterButton();
		parameterButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				optionsController.expression(
						field,
						parameterButton.isChecked() ? parameterOption
								.getExpressionValue() : null);
			}
		});

		parameterOption.getExpressionEditor().addListener(new InputListener() {
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				optionsController.expression(
						field,
						parameterButton.isChecked() ? parameterOption
								.getExpressionValue() : null);
				return true;
			}
		});
	}

	/**
	 * Sets the expression in the expression widget.
	 * 
	 * @param expression
	 *            the expression. If null, the expression editor is hidden
	 */
	public void setExpression(String expression) {
		parameterButton.setChecked(expression != null);
		parameterOption.setExpressionEditorVisible(expression != null);
	}
}
