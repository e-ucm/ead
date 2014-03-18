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
package es.eucm.ead.editor.platform;

import com.badlogic.gdx.math.Vector2;
import es.eucm.network.requests.RequestHelper;

/**
 * Interface to implements platform-dependent functionality.
 */
public interface Platform {

	/**
	 * Asks for a file in the platform file system
	 * 
	 * @param listener
	 *            listener processing the result. The listener will receive a
	 *            null if no file was selected (e.g., the action was cancelled)
	 */
	void askForFile(FileChooserListener listener);

	/**
	 * Asks for a folder in the platform file system
	 * 
	 * @param listener
	 *            listener processing the result. The listener will receive a
	 *            null if no folder was selected (e.g., the action was
	 *            cancelled)
	 */
	void askForFolder(FileChooserListener listener);

	/**
	 * Sets the window title
	 * 
	 * @param title
	 *            the internationalized string for the title
	 */
	void setTitle(String title);

	/**
	 * Sets the size for the platform. In desktop, the window's size, in
	 * Android, this method is ignored
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 */
	void setSize(int width, int height);

	/**
	 * @return Returns the windows size. In Desktop, returns the frame size, in
	 *         Android, the screen size.
	 */
	Vector2 getSize();

	/**
	 * 
	 * @return returns a platform dependant helper to generate network requests
	 */
	RequestHelper getRequestHelper();

	/**
	 * Opens the system's default browser with the given URL.
	 * 
	 * @param URL
	 *            The URL to open on the browser (e.g.
	 *            http://e-adventure.e-ucm.es)
	 * @return True if it was possible to complete the operation, false
	 *         otherwise. When false is returned, it may be due to
	 *         {@link java.awt.Desktop#isDesktopSupported()} returning false, so
	 *         the operation is not supported, or due to an error parsing
	 *         {@code URL}
	 */
	boolean browseURL(String URL);

	interface FileChooserListener {
		/**
		 * 
		 * @param path
		 *            the path chosen. Remember that the path can be in
		 *            different formats (C:\\user, /home/user/, ...)
		 */
		void fileChosen(String path);
	}
}
