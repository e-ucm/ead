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

import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.FileWidget;
import es.eucm.ead.editor.view.widgets.options.Option.OptionStyle;
import es.eucm.ead.engine.gdx.Spinner;

/**
 * Widget holding a bunch options in rows. Each row has two columns. In the
 * first column, it is a label, describing the option, and an optional tooltip,
 * with a more detailed description of the option. The second column, contains
 * the option widget. Depending on the option, this can be a text field, a text
 * area, a spinner, or any other widget.
 */
public class OptionsPanel extends AbstractWidget {

	private Skin skin;

	private OptionsPanelStyle style;

	private Array<Option> options;

	public OptionsPanel(Skin skin) {
		this.skin = skin;
		style = skin.get(OptionsPanelStyle.class);
		options = new Array<Option>();
	}

	/**
	 * Creates an integer option
	 * 
	 * @param label
	 *            the label for the option
	 * @param tooltip
	 *            the tooltip for the option (can be null)
	 * @return the spinner created for the option and added to the row of this
	 *         option
	 */
	public Spinner number(String label, String tooltip) {
		Spinner spinner = new Spinner(skin);
		Option option = new Option(label, tooltip, new Spinner(skin),
				style.optionStyle);
		addOption(option);
		return spinner;
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
	 * @return the select box created for the option
	 */
	public Option values(String label, String tooltip,
			Map<String, Object> values) {
		SelectBox<String> selectBox = new SelectBox<String>(skin);
		selectBox.setItems(values.keySet().toArray(new String[] {}));
		Option option = new Option(label, tooltip, selectBox, style.optionStyle);
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
	 * @param maxLength
	 *            maximum characters for the text. maxLength <= 0 is considered
	 *            as infinite length
	 * @return the option created
	 */
	public Option string(String label, String tooltip, int maxLength) {
		TextField textField = new TextField("", skin);
		if (maxLength > 0) {
			textField.setPrefColumns(maxLength);
		}
		Option option = new Option(label, tooltip, textField, style.optionStyle);
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
	 * @param maxLength
	 *            maximum characters for the text. maxLength <= 0 is considered
	 *            as infinite length
	 * @param maxLines
	 *            the number of lines for the text area
	 * @return the option created
	 */
	public Option text(String label, String tooltip, int maxLength, int maxLines) {
		TextArea textArea = new TextArea("", skin);
		textArea.setPrefRows(maxLines);
		if (maxLength > 0) {
			textArea.setPrefColumns(maxLength);
		}
		Option option = new Option(label, tooltip, textArea, style.optionStyle);
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
	 * @return the check box created for the option
	 */
	public CheckBox bool(String label, String tooltip) {
		CheckBox checkBox = new CheckBox("", skin);
		Option option = new Option(label, tooltip, checkBox, style.optionStyle);
		addOption(option);
		return checkBox;
	}

	/**
	 * Creates a file selector option, with a file widget
	 * 
	 * @param label
	 *            the label for the option
	 * @param tooltip
	 *            the tooltip for the option (can be null)
	 * @param maxLength
	 *            maximum characters for the text. maxLength <= 0 is considered
	 *            as infinite length
	 * @return the option created
	 */
	public Option file(String label, String tooltip, int maxLength) {
		FileWidget fileWidget = new FileWidget(skin);
		if (maxLength > 0) {
			fileWidget.getTextField().setMaxLength(maxLength);
		}
		Option option = new Option(label, tooltip, fileWidget,
				style.optionStyle);
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
		Option option = new Option(label, tooltip, optionWidget,
				style.optionStyle);
		addOption(option);
		return option;
	}

	/**
	 * Adds the option to the panel
	 * 
	 * @param option
	 *            the option
	 */
	protected void addOption(Option option) {
		options.add(option);
		addActor(option);
	}

	@Override
	public float getPrefWidth() {
		float maxLeftWidth = 0;
		float maxRightWidth = 0;
		for (Option option : options) {
			maxLeftWidth = Math.max(option.getLeftPrefWidth(), maxLeftWidth);
			maxRightWidth = Math.max(option.getRightPrefWidth(), maxRightWidth);
		}
		return maxLeftWidth + maxRightWidth;
	}

	@Override
	public float getPrefHeight() {
		return super.getChildrenTotalHeight()
				+ (style.marginTop + style.marginBottom) * getChildren().size;
	}

	@Override
	public void layout() {
		float maxLabelWidth = 0;
		for (Option option : options) {
			maxLabelWidth = Math.max(maxLabelWidth, option.getLeftPrefWidth());
		}

		float y = getHeight() - style.marginTop;
		for (Option option : options) {
			option.setLeftWidth(maxLabelWidth);
			float height = option.getPrefHeight();
			float width = getWidth();
			y -= height;
			setBounds(option, 0, y, width, height);
			y -= style.marginBottom + style.marginTop;
		}
	}

	public static class OptionsPanelStyle {

		/**
		 * Style for the options
		 */
		OptionStyle optionStyle;

		public float marginTop = 5.0f, marginBottom = 5.0f;

	}

}
