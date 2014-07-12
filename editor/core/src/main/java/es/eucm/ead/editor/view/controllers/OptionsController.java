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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.indexes.FuzzyIndex;
import es.eucm.ead.editor.view.controllers.values.*;
import es.eucm.ead.editor.view.controllers.values.SearchController;
import es.eucm.ead.editor.view.widgets.FileWidget;
import es.eucm.ead.editor.view.widgets.SearchWidget;
import es.eucm.ead.editor.view.widgets.ToggleImagesList;
import es.eucm.ead.editor.view.widgets.options.Option;
import es.eucm.ead.editor.view.widgets.options.OptionsPanel;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.gdx.Spinner;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for an options panel. Allows creation of options, handles
 * constraints and the final values for each of the options created
 */
public class OptionsController {

	protected Controller controller;

	private I18N i18n;

	private Skin skin;

	private OptionsPanel panel;

	private String i18nPrefix;

	private Map<String, Object> optionValues;

	protected Map<String, OptionController> optionControllers;

	private Array<ChangeListener> changeListeners;

	public OptionsController(Controller controller, Skin skin) {
		this.controller = controller;
		i18n = controller.getApplicationAssets().getI18N();
		this.skin = skin;
		this.optionValues = new HashMap<String, Object>();
		this.optionControllers = new HashMap<String, OptionController>();
		this.changeListeners = new Array<ChangeListener>();
		panel = newOptionsPanel(skin);
	}

	/**
	 * Create the basis options panel. Intended to be overridden for those
	 * classes interested in other types of panels.
	 */
	protected OptionsPanel newOptionsPanel(Skin skin) {
		return new OptionsPanel(skin);
	}

	/**
	 * @param field
	 *            te field name
	 * @return Returns the current value for the given field inside this option
	 *         panel
	 */
	public Object getValue(String field) {
		return optionValues.get(field);
	}

	/**
	 * Sets the value for a key. It updates the widget related
	 */
	public void setValue(String key, Object value) {
		optionValues.put(key, value);
		OptionController optionController = optionControllers.get(key);
		if (optionController != null) {
			optionController.getValueController().setWidgetValue(value);
		}
	}

	/**
	 * @return Returns the values for the options of this controller
	 */
	public Map<String, Object> getValues() {
		return optionValues;
	}

	/**
	 * 
	 * @return returns controlled the panel
	 */
	public OptionsPanel getPanel() {
		return panel;
	}

	/**
	 * Set the i18n prefix to use when automatically loading labels
	 * 
	 * @param prefix
	 *            the prefix
	 * @return this controller
	 */
	public OptionsController i18nPrefix(String prefix) {
		this.i18nPrefix = prefix;
		return this;
	}

	/**
	 * 
	 * @return the label text for the given field
	 */
	private String label(String field) {
		return i18n.m(i18nPrefix + "." + field) + ":";
	}

	/**
	 * @return the tooltip text for the given field
	 */
	private String tooltip(String field) {
		String key = i18nPrefix + "." + field + ".tooltip";
		String value = i18n.m(key);
		return (key.equals(value) ? null : value);
	}

	/**
	 * Adds a change listener
	 * 
	 * @param changeListener
	 *            will be notified whenever an option changes its value
	 */
	public void addChangeListener(ChangeListener changeListener) {
		changeListeners.add(changeListener);
	}

	/**
	 * Creates an string option
	 * 
	 * @param field
	 *            the name for the option
	 */
	public StringController string(String field) {
		Option option = panel.string(label(field), tooltip(field));
		TextField widget = (TextField) option.getOptionWidget();
		StringController value = newValueController(StringController.class,
				widget);
		add(field, option, value);
		return value;
	}

	/**
	 * Creates an int option
	 * 
	 * @param field
	 *            the name for the option
	 */
	public IntController intNumber(String field) {
		Option option = panel.number(label(field), tooltip(field));
		Spinner spinner = (Spinner) option.getOptionWidget();
		IntController value = newValueController(IntController.class, spinner);
		add(field, option, value);
		return value;
	}

	/**
	 * Creates a float option
	 * 
	 * @param field
	 *            the name for the option
	 */
	public FloatController floatNumber(String field) {
		Option option = panel.number(label(field), tooltip(field));
		Spinner spinner = (Spinner) option.getOptionWidget();
		FloatController value = newValueController(FloatController.class,
				spinner);
		add(field, option, value);
		return value;
	}

	/**
	 * Creates a boolean option
	 * 
	 * @param field
	 *            the name for the option
	 */
	public BooleanController bool(String field) {
		Option option = panel.bool(label(field), tooltip(field));
		CheckBox checkBox = (CheckBox) option.getOptionWidget();
		BooleanController value = newValueController(BooleanController.class,
				checkBox);
		add(field, option, value);
		return value;
	}

	/**
	 * Creates a boolean option
	 * 
	 * @param field
	 *            the name for the option
	 * @param values
	 *            possible values for the selection. The keys are the strings
	 *            that will be shown to the users, and the values the actual
	 *            values for the option
	 */
	public SelectController select(String field, Map<String, Object> values) {
		Option option = panel.select(label(field), tooltip(field), values);
		SelectBox widget = (SelectBox) option.getOptionWidget();
		SelectController value = newValueController(SelectController.class,
				widget, Map.class, values);
		add(field, option, value);
		return value;
	}

	/**
	 * Creates a boolean option
	 * 
	 * @param field
	 *            the name for the option
	 * @param index
	 *            the index with the possible values for the option
	 */
	public SearchController search(String field, FuzzyIndex index) {
		SearchWidget widget = new SearchWidget(skin);
		Option option = panel.custom(label(field), tooltip(field), widget);
		SearchController value = newValueController(
				es.eucm.ead.editor.view.controllers.values.SearchController.class,
				widget, FuzzyIndex.class, index);
		add(field, option, value);
		return value;
	}

	/**
	 * Creates a text option (text area)
	 * 
	 * @param field
	 *            the field
	 * @param widgetLines
	 *            lines to be shown by the widget
	 */
	public StringController text(String field, int widgetLines) {
		Option option = panel.text(label(field), tooltip(field), widgetLines);
		TextArea textArea = (TextArea) option.getOptionWidget();
		StringController value = newValueController(StringController.class,
				textArea);
		add(field, option, value);
		return value;
	}

	/**
	 * Creates a file option
	 * 
	 * @param field
	 *            the field
	 * @param widgetLength
	 *            the widget width, in characters
	 * @return the option controller created
	 */
	public FileController file(String field, int widgetLength) {
		Option option = panel.file(label(field), tooltip(field), widgetLength);
		FileWidget fileWidget = (FileWidget) option.getOptionWidget();
		FileController value = newValueController(FileController.class,
				fileWidget);
		add(field, option, value);
		return value;
	}

	/**
	 * Creates an option selector, based on images
	 * 
	 * @param field
	 *            the field
	 * @return the option controller created
	 */
	public ToggleImagesController toggleImages(String field) {
		ToggleImagesList widget = new ToggleImagesList(skin, true);
		Option option = panel.custom(label(field), tooltip(field), widget);
		ToggleImagesController value = newValueController(
				ToggleImagesController.class, widget);
		add(field, option, value);
		return value;
	}

	private void add(String field, Option option,
			ValueController valueController) {
		OptionController optionController = newOptionController(field, option,
				valueController);
		optionControllers.put(field, optionController);
	}

	/**
	 * 
	 * @param clazz
	 *            class of the value
	 * @param widget
	 *            the widget for the value
	 * @param args
	 *            arguments are interpreted as a list of objects paired in
	 *            class, value ([class, value, class, value]), and they would be
	 *            use to find the right constructor
	 */
	@SuppressWarnings("unchecked")
	private <V extends ValueController<T, S>, T extends Actor, W extends T, S> V newValueController(
			Class<V> clazz, W widget, Object... args) {
		try {
			V value;
			if (args.length == 0) {
				value = ClassReflection.newInstance(clazz);
			} else {
				Class[] argsClass = new Class[args.length / 2];
				Object[] argsObject = new Object[args.length / 2];
				for (int i = 0; i < args.length; i += 2) {
					argsClass[i] = (Class) args[i];
					argsObject[i] = args[i + 1];
				}
				Constructor constructor = ClassReflection
						.getDeclaredConstructor(clazz, argsClass);
				value = (V) constructor.newInstance(argsObject);
			}
			value.build(controller, widget);
			return value;
		} catch (ReflectionException e) {
			Gdx.app.error("OptionsController", "No value", e);
		}
		return null;
	}

	/**
	 * Creates an option controller wrapping the given parameters. Can be
	 * overridden for those interested in creating other types of controllers.
	 */
	protected OptionController newOptionController(String field, Option option,
			ValueController valueController) {
		return new OptionController(this, field, option, valueController);
	}

	/**
	 * Notifies a change in the field name with fieldName in the target
	 * 
	 * @param source
	 *            option controller
	 * @param fieldName
	 *            the field that changed
	 * @param newValue
	 *            the new value of the field
	 */
	public void notifyChange(OptionController source, String fieldName,
			Object newValue) {
		optionValues.put(fieldName, newValue);
		for (ChangeListener changeListener : changeListeners) {
			changeListener.valueUpdated(source, fieldName, newValue);
		}
	}

	public interface ChangeListener {

		/**
		 * The value was updated
		 * 
		 * @param source
		 *            the update source
		 * @param field
		 *            the field updated
		 * @param value
		 *            the new value
		 */
		void valueUpdated(OptionController source, String field, Object value);
	}
}
