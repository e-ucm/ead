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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class FileWidget extends AbstractWidget {

	private FileWidgetStyle style;

	private TextField textField;

	private ImageButton button;

	public FileWidget(Skin skin) {
		style = skin.get(FileWidgetStyle.class);
		textField = new TextField("", skin);
		button = new ImageButton(style.selectIcon);
		addActor(textField);
		addActor(button);
	}

	@Override
	public float getPrefWidth() {
		return getChildrenTotalWidth();
	}

	@Override
	public float getPrefHeight() {
		return getChildrenMaxHeight();
	}

	@Override
	public void layout() {
		float buttonWidth = getPrefWidth(button);
		float textFieldWidth = getWidth() - buttonWidth;
		textField.setBounds(0, 0, textFieldWidth, getHeight());
		button.setBounds(textFieldWidth, 0, buttonWidth, getHeight());
	}

	public void addButtonListener(EventListener listener) {
		button.addListener(listener);
	}

	public static class FileWidgetStyle {
		Drawable selectIcon;
	}
}
