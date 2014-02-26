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
package es.eucm.ead.editor.view.controllers.options;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.view.controllers.OptionsController;
import es.eucm.ead.editor.view.controllers.constraints.Constraint;
import es.eucm.ead.editor.view.widgets.options.Option;
import es.eucm.ead.engine.I18N;

public abstract class OptionController<T extends Actor, S> {

	protected I18N i18N;

	protected OptionsController optionsController;

	private Array<Constraint<S>> constraints;

	private String field;

	private Option option;

	protected T widget;

	protected OptionController(I18N i18N, OptionsController optionsController,
			String field, Option option, T widget) {
		this.i18N = i18N;
		this.optionsController = optionsController;
		this.field = field;
		this.option = option;
		this.widget = widget;
		this.constraints = new Array<Constraint<S>>();
		initialize();
	}

	protected abstract void initialize();

	protected void addConstraint(Constraint<S> constraint) {
		this.constraints.add(constraint);
	}

	private String checkConstraints(S value) {
		for (Constraint<S> c : constraints) {
			if (!c.validate(value)) {
				return c.getErrorMessage();
			}
		}
		return null;
	}

	/**
	 * This option controller changed the value for the option. The value will
	 * be checked with the constraints, and then, the parent controller will be
	 * notified
	 * 
	 * @param value
	 *            the new value
	 */
	public OptionController change(S value) {
		setWidgetValue(value);
		String errorMessage = checkConstraints(value);
		if (errorMessage == null) {
			optionsController.notifyChange(this, field, value);
			option.setValid(true);
		} else {
			option.setValid(false);
			option.errorMessage(errorMessage);
		}
		return this;
	}

	/**
	 * Sets the value in the widget. It does not trigger any notification to the
	 * parent controller
	 * 
	 * @param value
	 *            the value
	 */
	protected abstract void setWidgetValue(S value);

}
