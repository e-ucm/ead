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
package es.eucm.ead.editor.view.widgets.options;

import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.FileWidget;
import es.eucm.ead.editor.view.widgets.TextArea;
import es.eucm.ead.editor.view.widgets.TextField;
import es.eucm.ead.editor.view.widgets.options.Option.OptionStyle;
import es.eucm.ead.engine.gdx.Spinner;

public class OptionsPanel extends AbstractWidget {

	private Skin skin;

	private OptionsPanelStyle style;

	private Array<Option> options;

	public OptionsPanel(Skin skin) {
		this.skin = skin;
		style = skin.get(OptionsPanelStyle.class);
		options = new Array<Option>();
	}

	public Option number(String label, String tooltip) {
		Option option = new Option(label, tooltip, new Spinner(skin),
				style.optionStyle);
		addOption(option);
		return option;
	}

	public Option values(String label, String tooltip,
			Map<String, Object> values) {
		SelectBox<Object> valuesSelectBox = new SelectBox<Object>(skin);
		valuesSelectBox.setItems(values
				.keySet().toArray());
		Option option = new Option(label, tooltip, valuesSelectBox, style.optionStyle);
		addOption(option);
		return option;
	}

	public Option string(String label, String tooltip, int maxLength) {
		TextField textField = new TextField("", skin);
		textField.setLineCharacters(maxLength);
		Option option = new Option(label, tooltip, textField, style.optionStyle);
		addOption(option);
		return option;
	}

	public Option text(String label, String tooltip, int maxLength, int maxLines) {
		TextArea textArea = new TextArea("", skin);
		textArea.setPreferredLines(maxLines);
		textArea.setLineCharacters(maxLength);
		Option option = new Option(label, tooltip, textArea, style.optionStyle);
		addOption(option);
		return option;
	}

	public Option custom(String label, String tooltip, Actor optionWidget) {
		Option option = new Option(label, tooltip, optionWidget,
				style.optionStyle);
		addOption(option);
		return option;
	}

	public Option bool(String label, String tooltip) {
		Option option = new Option(label, tooltip, new CheckBox("", skin),
				style.optionStyle);
		addOption(option);
		return option;
	}

	public Option file(String label, String tooltip) {
		final FileWidget fileWidget = new FileWidget(skin);
		Option option = new Option(label, tooltip, fileWidget,
				style.optionStyle);
		addOption(option);
		return option;
	}

	protected void addOption(Option option) {
		options.add(option);
		addActor(option);
	}

	@Override
	public float getPrefWidth() {
		return super.getChildrenMaxWidth();
	}

	@Override
	public float getPrefHeight() {
		return super.getChildrenTotalHeight();
	}

	@Override
	public void layout() {
		float maxLabelWidth = 0;
		for (Option option : options) {
			maxLabelWidth = Math.max(maxLabelWidth, option.getLeftPrefWidth());
		}

		float y = getHeight();
		for (Option option : options) {
			option.setLeftWidth(maxLabelWidth);
			float height = option.getPrefHeight();
			float width = getWidth();
			y -= height;
			option.setBounds(0, y, width, height);
		}
	}

	public static class OptionsPanelStyle {

		OptionStyle optionStyle;

	}

}
