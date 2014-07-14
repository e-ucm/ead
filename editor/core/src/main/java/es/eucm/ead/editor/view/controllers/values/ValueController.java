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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.controllers.OptionController;
import es.eucm.ead.editor.view.controllers.constraints.Constraint;
import es.eucm.ead.editor.view.widgets.options.Option;
import es.eucm.ead.engine.I18N;

/**
 * Controls an option's value
 */
public abstract class ValueController<T extends Actor, S> {

	protected Controller controller;

	protected I18N i18N;

	protected T widget;

	private Array<Constraint<S>> constraints;

	private OptionController optionController;

	public void build(Controller controller, T widget) {
		this.constraints = new Array<Constraint<S>>();
		this.controller = controller;
		this.i18N = controller.getApplicationAssets().getI18N();
		this.widget = widget;
		initialize();
	}

	protected abstract void initialize();

	/**
	 * Sets the parent controller of this value
	 */
	public void setOptionController(OptionController optionController) {
		this.optionController = optionController;
	}

	/**
	 * Sets the value on the widget
	 */
	public abstract void setWidgetValue(S value);

	/**
	 * The value change due to some interaction with the value widget
	 */
	public void widgetUpdatedValue(S value) {
		optionController.widgetUpdatedValue(value);
	}

	protected void addConstraint(Constraint<S> constraint) {
		this.constraints.add(constraint);
	}

	public boolean checkConstraints(S value) {
		String errorMessage = null;
		for (Constraint<S> c : constraints) {
			if (!c.validate(value)) {
				errorMessage = c.getErrorMessage();
				break;
			}
		}

		Option option = optionController.getOption();
		if (errorMessage == null) {
			option.setValid(true);
			return true;
		} else {
			option.setValid(false);
			option.errorMessage(errorMessage);
			return false;
		}
	}
}
