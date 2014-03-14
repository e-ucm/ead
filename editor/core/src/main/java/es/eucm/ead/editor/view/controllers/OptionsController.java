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
import es.eucm.ead.editor.view.controllers.options.FileOptionController;
import es.eucm.ead.editor.view.controllers.options.OptionController;
import es.eucm.ead.editor.view.controllers.options.StringOptionController;
import es.eucm.ead.editor.view.controllers.options.ToggleImagesController;
import es.eucm.ead.editor.view.widgets.FileWidget;
import es.eucm.ead.editor.view.widgets.TextArea;
import es.eucm.ead.editor.view.widgets.TextField;
import es.eucm.ead.editor.view.widgets.ToggleImagesList;
import es.eucm.ead.editor.view.widgets.options.Option;
import es.eucm.ead.editor.view.widgets.options.OptionsPanel;
import es.eucm.ead.engine.I18N;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for an options panel. Allows creation of options, handles
 * constraints and the final values for each of the options created
 */
public class OptionsController {

	private Controller controller;

	private I18N i18n;

	private Skin skin;

	private OptionsPanel panel;

	private String i18nPrefix;

	private Map<String, Object> optionValues;

	private Array<ChangeListener> updaters;

	public OptionsController(Controller controller, Skin skin) {
		this.controller = controller;
		i18n = controller.getApplicationAssets().getI18N();
		this.skin = skin;
		this.optionValues = new HashMap<String, Object>();
		panel = new OptionsPanel(skin);
		this.updaters = new Array<ChangeListener>();
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
	 * Sets the value for a key
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void setValue(String key, Object value) {
		optionValues.put(key, value);
	}

	/**
	 * @return Returns the values for the options of this controller
	 */
	public Map<String, Object> getValues() {
		return optionValues;
	}

	/**
	 * @param field
	 *            the field
	 * @return Returns the label text for the given field
	 */
	private String label(String field) {
		return i18n.m(i18nPrefix + "." + field) + ":";
	}

	/**
	 * @param field
	 *            the field
	 * @return Returns the tooltip text for the given field
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
		updaters.add(changeListener);
	}

	/**
	 * Creates a string option
	 * 
	 * @param field
	 *            the field
	 * @return the option created
	 */
	public StringOptionController string(String field) {
		return string(field, 0);
	}

	/**
	 * Creates an string option
	 * 
	 * @param field
	 *            the field
	 * @param widgetLength
	 *            the widget width, in characters
	 * @return the option created
	 */
	public StringOptionController string(String field, int widgetLength) {
		Option option = panel
				.string(label(field), tooltip(field), widgetLength);
		TextField textField = (TextField) option.getOptionWidget();
		return new StringOptionController(controller.getEditorGameAssets()
				.getI18N(), this, field, option, textField);
	}

	/**
	 * Creates a text option
	 * 
	 * @param field
	 *            the field
	 */
	public StringOptionController text(String field) {
		return text(field, 0, 5);
	}

	/**
	 * Creates a text option
	 * 
	 * @param field
	 *            the field
	 * @param widgetLength
	 *            length, in characters, for each line of the text area
	 * @param widgetLines
	 *            lines to be shown by the widget
	 * @return the option controller created
	 */
	public StringOptionController text(String field, int widgetLength,
			int widgetLines) {
		Option option = panel.text(label(field), tooltip(field), widgetLength,
				widgetLines);
		TextArea textArea = (TextArea) option.getOptionWidget();
		return new StringOptionController(controller.getEditorGameAssets()
				.getI18N(), this, field, option, textArea);
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
	public FileOptionController file(String field, int widgetLength) {
		Option option = panel.file(label(field), tooltip(field), widgetLength);
		FileWidget fileWidget = (FileWidget) option.getOptionWidget();
		return new FileOptionController(controller, controller
				.getEditorGameAssets().getI18N(), this, field, option, fileWidget);
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
		return new ToggleImagesController(controller.getEditorGameAssets()
				.getI18N(), this, field, option, widget);
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
		for (ChangeListener changeListener : updaters) {
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
