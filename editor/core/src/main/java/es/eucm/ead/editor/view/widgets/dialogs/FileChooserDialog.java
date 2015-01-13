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
package es.eucm.ead.editor.view.widgets.dialogs;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.platform.Platform.FileChooserListener;
import es.eucm.ead.editor.view.widgets.Dialog;
import es.eucm.ead.editor.view.widgets.files.FilesListWidget;

/**
 * A file chooser dialog. Created by angel on 25/03/14.
 */
public class FileChooserDialog extends Dialog {

	private FileChooserListener fileChooserListener;

	private FilesListWidget files;

	/**
	 * Constructs a file chooser dialog
	 * 
	 * @param skin
	 *            a skin to use for the creation of the dialog UI elements
	 * @param selectString
	 *            string for the "select" button (this allows i18n)
	 * @param cancelString
	 *            string for the "cancel" button (this allows i18n)
	 * @param fileChooserListener
	 *            listener to be invoked when the select or cancel button is
	 *            pressed
	 */
	public FileChooserDialog(Skin skin, String selectString,
			String cancelString, FileChooserListener fileChooserListener) {
		super(skin);
		this.fileChooserListener = fileChooserListener;
		root(files = new FilesListWidget(skin));
		button(selectString).addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				FileChooserDialog.this.fileChooserListener.fileChosen(files
						.getSelectedFile().path(),
						FileChooserListener.Result.SUCCESS);
			}
		});

		button(cancelString).addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				FileChooserDialog.this.fileChooserListener.fileChosen(null,
						null);
			}
		});
	}

	/**
	 * 
	 * @param fileHandle
	 *            the file selected in the file chooser. This updates the
	 *            content of the widget
	 */
	public void setSelectedFile(FileHandle fileHandle) {
		files.setSelectedFile(fileHandle, true);
	}

	/**
	 * @return the current selected filed
	 */
	public FileHandle getSelectedFile() {
		return files.getSelectedFile();
	}

}
