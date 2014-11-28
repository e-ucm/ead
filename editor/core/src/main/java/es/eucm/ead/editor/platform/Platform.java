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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Tracker;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.data.Dimension;

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
	 * On Android starts an ACTION_EDIT intent that should allow the use to add
	 * extra effects to an image.
	 * 
	 * @param image
	 */
	void editImage(I18N i18n, String image, final FileChooserListener listener);

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

	public void getMultilineTextInput(TextInputListener listener,
			final String title, final String text, I18N i18n);

	/**
	 * Creates the tracker for the specific platform
	 * 
	 * @param controller
	 *            the controller
	 * @return the tracker created
	 */
	Tracker createTracker(Controller controller);

	String getDefaultProjectsFolder();

	public interface FileChooserListener {
		/**
		 * 
		 * @param path
		 *            the path chosen. Remember that the path can be in
		 *            different formats (C:\\user, /home/user/, ...)
		 */
		void fileChosen(String path);

	}

	/**
	 * @return the application batch
	 */
	Batch getBatch();

	/**
	 * Sets the batch of the application
	 */
	void setBatch(Batch spriteBatch);

	/**
	 * Returns the dimensions of the image represented by the given
	 * {@code imageInputStream} without needing to load the whole image from
	 * disk.
	 * 
	 * @param imageInputStream
	 *            The inputStream of the image to determine its dimensions
	 * @return The Dimension with the width and height of the image.
	 */
	Dimension getImageDimension(InputStream imageInputStream);

	/**
	 * @return the default string locale in the device
	 */
	String getLocale();

	/**
	 * Sends an HTTP request and returns the response. *
	 * 
	 * @throws IOException
	 * @see {@link Platform#sendHttpRequest(HttpRequest, Class)}
	 */
	<T> T sendHttpGetRequest(String URL, Class<T> type) throws IOException;

	/**
	 * Process the specified {@link HttpRequest} and returns the result.
	 * 
	 * @param httpRequest
	 * @param type
	 *            decides what result will be returned, currently supported
	 *            values:
	 *            <dl>
	 *            <dd><strong>String.class</strong> Returns the result stream as
	 *            a <em>String</em></dd>
	 *            <dd><strong>byte[].class</strong> Returns the result stream as
	 *            a <em>byte array</em></dd>
	 *            <dd><strong>HttpURLConnection.class</strong> Returns the
	 *            <em>connection</em>. Note that you must disconnect when you've
	 *            finished via {@link HttpURLConnection#disconnect()}.</dd>
	 *            </dl>
	 * @return the result depending on {@code type} value.
	 * @throws IOException
	 *             if something went wrong.
	 */
	<T> T sendHttpRequest(HttpRequest httpRequest, Class<T> type)
			throws IOException;

	/**
	 * Creates a scaled image given a source {@code imageFile}. The
	 * implementation may vary with the platform.
	 * 
	 * @param imageFile
	 *            the image that we want to scale.
	 * @param maxWidth
	 *            the maximum width of the resulting image.
	 * @param maxHeight
	 *            the maximum height of the resulting image.
	 * @param resultImage
	 *            a {@link FileHandle} to the resulting scaled image or null if
	 *            you want to override the source {@code imageFile}.
	 * @return false if something went wrong, true if the scaling succeeded.
	 */
	boolean scaleImage(FileHandle imageFile, int maxWidth, int maxHeight,
			FileHandle resultImage);
}
