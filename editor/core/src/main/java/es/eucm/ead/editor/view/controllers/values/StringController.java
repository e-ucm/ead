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
package es.eucm.ead.editor.view.controllers.values;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import es.eucm.ead.editor.view.controllers.constraints.MaxLengthConstraint;
import es.eucm.ead.editor.view.controllers.constraints.MinLengthConstraint;

public class StringController extends ValueController<TextField, String> {

	/**
	 * Adds a maximum length constraint to this option
	 * 
	 * @param maxLength
	 *            the maximum length for the constraint
	 * @return the option
	 */
	public StringController maxLength(int maxLength) {
		addConstraint(new MaxLengthConstraint(i18N, maxLength));
		return this;
	}

	/**
	 * Adds a minimum length constraint to this option
	 * 
	 * @param minLength
	 *            the minimum length for the constraint
	 * @return the option
	 */
	public StringController minLength(int minLength) {
		addConstraint(new MinLengthConstraint(i18N, minLength));
		return this;
	}

	@Override
	protected void initialize() {
		widget.addListener(new InputListener() {
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				change(widget.getText());
				return true;
			}
		});
	}

	@Override
	public void setWidgetValue(String value) {
		if (value == null) {
			value = "";
		}
		if (!value.equals(widget.getText())) {
			widget.setText(value);
		}
	}
}
