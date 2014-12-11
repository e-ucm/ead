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
package es.eucm.ead.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.GATracker;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.Tracker;
import es.eucm.ead.editor.platform.AbstractPlatform;
import es.eucm.ead.editor.platform.Platform.FileChooserListener;
import es.eucm.ead.editor.view.widgets.dialogs.FileChooserDialog;
import es.eucm.ead.engine.I18N;

import java.awt.*;

public class DesktopPlatform extends AbstractPlatform implements
		FileChooserListener {

	private LwjglFrame frame;
	private Vector2 screenDimensions;
	private FileChooserDialog fileChooser;
	private Controller controller;
	private Stage stage;
	private FileChooserListener fileChooserListener;

	public DesktopPlatform() {
		screenDimensions = new Vector2();
	}

	public void initFileChooser(final Controller controller, Stage stage) {
		this.controller = controller;
		String fileChooserSelectedFile = controller.getPreferences().getString(
				Preferences.FILE_CHOOSER_LAST_FOLDER);
		Skin skin = controller.getApplicationAssets().getSkin();
		I18N i18n = controller.getApplicationAssets().getI18N();

		this.stage = stage;
		fileChooser = new FileChooserDialog(skin, i18n.m("general.ok"),
				i18n.m("general.cancel"), this);

		FileHandle fh = new FileHandle(fileChooserSelectedFile);
		if (fh.exists()) {
			if (!fh.isDirectory()) {
				fh = fh.parent();
			}
		} else {
			fh = new FileHandle(System.getProperty("user.dir"));
		}
		fileChooser.setSelectedFile(fh);
	}

	public void setFrame(LwjglFrame frame) {
		this.frame = frame;
	}

	@Override
	public void askForFile(FileChooserListener listener) {
		showFileChooser(listener);
	}

	@Override
	public void askForFolder(FileChooserListener listener) {
		showFileChooser(listener);
	}

	/** Shows the file chooser **/
	private void showFileChooser(FileChooserListener fileChooserListener) {
		this.fileChooserListener = fileChooserListener;
		fileChooser.setSize(Gdx.graphics.getWidth() * 0.8f,
				Gdx.graphics.getHeight() * 0.8f);
		fileChooser.show(stage);
		fileChooser.center();
	}

	@Override
	public void setTitle(String title) {
		frame.setTitle(title);
	}

	@Override
	public void setSize(int width, int height) {
		frame.setSize(width, height);
	}

	@Override
	public Vector2 getSize() {
		Dimension d = frame.getSize();
		screenDimensions.set(d.width, d.height);
		return screenDimensions;
	}

	@Override
	public Tracker createTracker(Controller controller) {
		return new GATracker(controller);
	}

	public LwjglFrame getFrame() {
		return frame;
	}

	@Override
	public void fileChosen(String path) {
		if (path == null) {
			controller.getPreferences().putString(
					Preferences.FILE_CHOOSER_LAST_FOLDER,
					fileChooser.getSelectedFile().path());
		} else {
			controller.getPreferences().putString(
					Preferences.FILE_CHOOSER_LAST_FOLDER, path);
		}
		if (fileChooserListener != null) {
			fileChooserListener.fileChosen(path);
		}
		fileChooser.remove();
	}

	@Override
	public void editImage(I18N i18n, String image, FileChooserListener listener) {
		// Nothing to do
	}

	@Override
	public void getMultilineTextInput(TextInputListener listener, String title,
			String text, I18N i18n) {
	}
}
