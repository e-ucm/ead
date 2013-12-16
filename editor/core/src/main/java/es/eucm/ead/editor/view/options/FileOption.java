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
package es.eucm.ead.editor.view.options;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import es.eucm.ead.editor.Editor;
import es.eucm.ead.editor.io.Platform.StringListener;
import es.eucm.ead.editor.model.DependencyNode;

/**
 * An option to select a file frome the file system
 */
public class FileOption extends AbstractOption<String> implements
		StringListener {

	private boolean folder;

	private TextField textField;

	/**
	 * Creates an AbstractAction.
	 * 
	 * @param label
	 *            for the option
	 * @param toolTipText
	 *            for the option (can be null)
	 * @param changed
	 *            dependency nodes to be considered "changed" when this changes
	 */
	public FileOption(String label, String toolTipText,
			DependencyNode... changed) {
		super(label, toolTipText, changed);
	}

	/**
	 * Sets if the file must be a folder
	 * 
	 * @param folder
	 *            if the file must be a folder
	 * @return the file option
	 */
	public FileOption folder(boolean folder) {
		this.folder = folder;
		return this;
	}

	@Override
	protected Actor createControl() {
		Table table = new Table(skin);
		textField = new TextField("", skin);
		table.add(textField);

		TextButton textButton = new TextButton("...", skin);
		textButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (folder) {
					Editor.platform.askForFolder(FileOption.this);
				} else {
					Editor.platform.askForFile(FileOption.this);
				}
				return false;
			}
		});

		table.add(textButton);
		return table;
	}

	@Override
	public String getControlValue() {
		return textField.getText();
	}

	@Override
	protected void setControlValue(String newValue) {
		textField.setText(newValue);
	}

	@Override
	public void string(String result) {
		setControlValue(result);
	}
}
