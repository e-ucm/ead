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

import java.io.InputStream;

import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import com.badlogic.gdx.math.Vector2;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Tracker;
import es.eucm.ead.editor.platform.AbstractPlatform;
import es.eucm.ead.editor.platform.Platform.FileChooserListener;
import es.eucm.ead.engine.I18N;
import es.eucm.network.requests.RequestHelper;

public class MockupDesktopPlatform extends AbstractPlatform implements
		FileChooserListener {

	private LwjglFrame frame;

	public void setFrame(LwjglFrame frame) {
		this.frame = frame;
	}

	@Override
	public void askForFile(FileChooserListener listener) {

	}

	@Override
	public void askForFolder(FileChooserListener listener) {

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
		return null;
	}

	@Override
	public Tracker createTracker(Controller controller) {
		return new Tracker(controller);
	}

	@Override
	public RequestHelper getRequestHelper() {
		return null;
	}

	public LwjglFrame getFrame() {
		return frame;
	}

	@Override
	public void fileChosen(String path) {

	}

	@Override
	public void editImage(I18N i18n, String image, FileChooserListener listener) {
		// Nothing to do
	}

	/**
	 * Determines the width and height of an image without loading it from disk.
	 */
	@Override
	public es.eucm.ead.schema.data.Dimension getImageDimension(
			InputStream imageInputStream) {
		return null;
	}
}
