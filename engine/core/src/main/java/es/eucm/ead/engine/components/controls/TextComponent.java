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
package es.eucm.ead.engine.components.controls;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

import es.eucm.ead.engine.components.I18nTextComponent.I18nTextSetter;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.engine.variables.VariablesManager.VariableListener;

public abstract class TextComponent<T extends Actor> extends
		ControlComponent<T> implements Poolable, VariableListener,
		I18nTextSetter {

	private String currentText;

	private VariablesManager variablesManager;

	private Array<String> variables = new Array<String>();

	private Array<String> expressions = new Array<String>();

	/**
	 * Sets the variable manager. Used to substitute expressions in strings for
	 * their values
	 */
	public void setVariablesManager(VariablesManager variablesManager) {
		this.variablesManager = variablesManager;
	}

	@Override
	public void setText(String text) {
		this.currentText = text;
		expressions.clear();
		variables.clear();
		variablesManager.readExpressions(text, expressions, variables);

		variablesManager.removeListener(this);
		if (variables.size > 0) {
			variablesManager.addListener(this);
		}

		updateText();
	}

	private void updateText() {
		updateText(variablesManager.replaceTextExpressions(currentText,
				expressions));
	}

	/**
	 * Sets the given text in the widget
	 */
	protected abstract void updateText(String newText);

	@Override
	public boolean listensTo(String variableName) {
		return variables.contains(variableName, false);
	}

	@Override
	public void variableChanged(String variableName, Object value) {
		updateText();
	}

	@Override
	public void reset() {
		variablesManager.removeListener(this);
		variables.clear();
	}

}
