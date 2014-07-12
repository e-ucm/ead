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

import es.eucm.ead.editor.view.controllers.values.ValueController;
import es.eucm.ead.editor.view.widgets.options.Option;

public class OptionController {

	private OptionsController optionsController;

	protected String field;

	private Option option;

	private ValueController valueController;

	public OptionController(OptionsController optionsController, String field,
			Option option, ValueController valueController) {
		this.optionsController = optionsController;
		this.field = field;
		this.option = option;
		this.valueController = valueController;
		valueController.setOptionController(this);
	}

	/**
	 * @return the option controlled by this object
	 */
	public Option getOption() {
		return option;
	}

	/**
	 * @return the value controller of this object
	 */
	public ValueController getValueController() {
		return valueController;
	}

	/**
	 * This option controller changed the value for the option. The value will
	 * be checked with the constraints, and then, the parent controller will be
	 * notified
	 * 
	 * @param value
	 *            the new value
	 */
	public OptionController change(Object value) {
		valueController.setWidgetValue(value);
		valueController.checkConstraints(value);
		optionsController.notifyChange(this, field, value);
		return this;
	}
}
