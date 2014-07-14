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
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.view.widgets.FileWidget;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.editor.view.widgets.options.Option.OptionStyle;
import es.eucm.ead.engine.gdx.Spinner;

import java.util.Map;

/**
 * Widget holding a bunch options in rows. Each row has two columns. In the
 * first column, it is a label, describing the option, and an optional tooltip,
 * with a more detailed description of the option. The second column, contains
 * the option widget. Depending on the option, this can be a text field, a text
 * area, a spinner, or any other widget.
 */
public class OptionsPanel extends LinearLayout {

	protected Skin skin;

	private OptionsPanelStyle style;

	private Array<Option> options;

	public OptionsPanel(Skin skin) {
		super(false);
		this.skin = skin;
		style = skin.get(OptionsPanelStyle.class);
		options = new Array<Option>();
		defaultWidgetsMargin(0, style.marginTop, 0, style.marginBottom);
	}

	/**
	 * Creates an integer option
	 * 
	 * @param label
	 *            the label for the option
	 * @param tooltip
	 *            the tooltip for the option (can be null)
	 * @return the option created
	 */
	public Option number(String label, String tooltip) {
		Option option = newOption(label, tooltip, new Spinner(skin, 0.1f));
		addOption(option);
		return option;
	}

	/**
	 * Creates a drop selection option
	 * 
	 * @param label
	 *            the label for the option
	 * @param tooltip
	 *            the tooltip for the option (can be null)
	 * @param values
	 *            the values that the drop selector must contain
	 * @return the option created
	 */
	public Option select(String label, String tooltip,
			Map<String, Object> values) {
		SelectBox<String> selectBox = new SelectBox<String>(skin);
		selectBox.setItems(values.keySet().toArray(new String[] {}));
		Option option = newOption(label, tooltip, selectBox);
		addOption(option);
		return option;
	}

	/**
	 * Creates a string option, with a text field as widget
	 * 
	 * @param label
	 *            the label for the option
	 * @param tooltip
	 *            the tooltip for the option (can be null)
	 * @return the option created
	 */
	public Option string(String label, String tooltip) {
		TextField textField = new TextField("", skin);
		Option option = newOption(label, tooltip, textField);
		addOption(option);
		return option;
	}

	/**
	 * Creates a text option, with a text area as widget
	 * 
	 * @param label
	 *            the label for the option
	 * @param tooltip
	 *            the tooltip for the option (can be null)
	 * @param maxLines
	 *            the number of lines for the text area
	 * @return the option created
	 */
	public Option text(String label, String tooltip, int maxLines) {
		TextArea textArea = new TextArea("", skin);
		textArea.setPrefRows(maxLines);
		Option option = newOption(label, tooltip, textArea);
		addOption(option);
		return option;
	}

	/**
	 * Creates a boolean option
	 * 
	 * @param label
	 *            the label for the option
	 * @param tooltip
	 *            the tooltip for the option (can be null)
	 * @return the option created
	 */
	public Option bool(String label, String tooltip) {
		Option option = newOption(label, tooltip, new CheckBox("", skin));
		addOption(option);
		return option;
	}

	/**
	 * Creates a file selector option, with a file widget
	 * 
	 * @param label
	 *            the label for the option
	 * @param tooltip
	 *            the tooltip for the option (can be null)
	 * @return the option created
	 */
	public Option file(String label, String tooltip) {
		FileWidget fileWidget = new FileWidget(skin);
		Option option = newOption(label, tooltip, fileWidget);
		addOption(option);
		return option;
	}

	/**
	 * Creates an option with a custom widget
	 * 
	 * @param label
	 *            the label for the option
	 * @param tooltip
	 *            the tooltip for the option (can be null)
	 * @param optionWidget
	 *            the custom option widget
	 * @return the option created
	 */
	public Option custom(String label, String tooltip, Actor optionWidget) {
		Option option = newOption(label, tooltip, optionWidget);
		addOption(option);
		return option;
	}

	/**
	 * Creates the option. Intended to be override for those classes interestead
	 * in create other types of options.
	 */
	protected Option newOption(String label, String tooltip, Actor optionWidget) {
		return new Option(label, tooltip, optionWidget, style.optionStyle);
	}

	/**
	 * Adds the option to the panel
	 * 
	 * @param option
	 *            the option
	 */
	protected void addOption(Option option) {
		options.add(option);
		add(option).expandX();
	}

	public static class OptionsPanelStyle {

		/**
		 * Style for the options
		 */
		OptionStyle optionStyle;

		public float marginTop = 5.0f, marginBottom = 5.0f;

	}

}
