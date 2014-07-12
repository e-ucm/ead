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

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.parameters.RemoveParameter;
import es.eucm.ead.editor.control.actions.model.parameters.SetParameter;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.view.controllers.values.ValueController;
import es.eucm.ead.editor.view.widgets.options.Option;
import es.eucm.ead.editor.view.widgets.options.OptionsPanel;
import es.eucm.ead.editor.view.widgets.options.ParameterOption;
import es.eucm.ead.editor.view.widgets.options.ParameterOptionsPanel;
import es.eucm.ead.schema.data.Parameter;
import es.eucm.ead.schema.data.Parameters;
import es.eucm.ead.schemax.FieldName;

/**
 * An options controller that controls a {@link Parameters} object
 */
public class ParameterOptionsController<T extends Parameters> extends
		ClassOptionsController<T> {

	private ParameterListListener parametersListener = new ParameterListListener();

	private ParameterValueListener valueListener = new ParameterValueListener();

	public ParameterOptionsController(Controller controller, Skin skin,
			Class<T> reflectedClass, Array<String> ignoredFields) {
		super(controller, skin, reflectedClass, ignoredFields);
	}

	@Override
	protected OptionController newOptionController(String field, Option option,
			ValueController valueController) {
		return new ParameterOptionController(this, field,
				(ParameterOption) option, valueController);
	}

	@Override
	protected OptionsPanel newOptionsPanel(Skin skin) {
		return new ParameterOptionsPanel(skin);
	}

	@Override
	public void read(T object) {
		super.read(object);
		controller.getModel().removeListenerFromAllTargets(parametersListener);
		controller.getModel().addListListener(object.getParameters(),
				parametersListener);
		controller.getModel().removeListenerFromAllTargets(valueListener);
		for (Parameter parameter : object.getParameters()) {
			controller.getModel().addFieldListener(parameter, valueListener);
			setParameterValue(parameter.getName(), parameter.getValue());
		}
	}

	private void setParameterValue(String field, String value) {
		ParameterOptionController option = (ParameterOptionController) optionControllers
				.get(field);
		option.setExpression(value);
	}

	/**
	 * Sets the expression value for a parameter
	 * 
	 * @param field
	 *            the name of the parameter
	 * @param expressionValue
	 *            the value for the parameter. If null, the parameter is removed
	 */
	public void expression(String field, String expressionValue) {
		if (expressionValue == null) {
			controller.action(RemoveParameter.class, object, field);
		} else {
			controller.action(SetParameter.class, object, field,
					expressionValue);
		}
	}

	public class ParameterListListener implements ModelListener<ListEvent> {

		@Override
		public void modelChanged(ListEvent event) {
			Parameter parameter = (Parameter) event.getElement();
			switch (event.getType()) {
			case ADDED:
				setParameterValue(parameter.getName(), parameter.getValue());
				controller.getModel()
						.addFieldListener(parameter, valueListener);
				break;
			case REMOVED:
				setParameterValue(parameter.getName(), null);
				controller.getModel().removeListener(parameter, valueListener);
				break;
			}

		}
	}

	public class ParameterValueListener implements FieldListener {

		@Override
		public boolean listenToField(String fieldName) {
			return FieldName.VALUE.equals(fieldName);
		}

		@Override
		public void modelChanged(FieldEvent event) {
			Parameter parameter = (Parameter) event.getTarget();
			setParameterValue(parameter.getName(), parameter.getValue());
		}
	}
}
