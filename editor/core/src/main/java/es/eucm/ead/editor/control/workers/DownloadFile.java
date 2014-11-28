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
package es.eucm.ead.editor.control.workers;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.StreamUtils;

/**
 * Downloads from a given URL.
 * <dl>
 * <dt><strong>The input arguments are</strong></dt>
 * <dd><strong>args[0]</strong> <em>String</em> URL to start the download.
 * <dd><strong>args[1]</strong> <em>String</em> Absolute path to the destination
 * file.</dd>
 * </dl>
 * <dl>
 * <dt><strong>The result argument is</strong></dt>
 * <dd><strong>args[0]</strong> <em>Boolean</em> success.
 * </dl>
 */
public class DownloadFile extends Worker {

	private static final String DOWNLOAD_TAG = "DownloadFileWorker";

	/**
	 * A temporal byte array used to write to disk efficiently.
	 */
	private final byte data[] = new byte[2048];

	@Override
	protected void prepare() {

	}

	@Override
	protected boolean step() {
		String URL = (String) args[0];
		FileHandle dstFile = controller.getApplicationAssets().absolute(
				(String) args[1]);
		boolean succeeded = true;

		int count = -1;
		InputStream input = null;
		OutputStream output = null;
		HttpURLConnection connection = null;
		try {
			connection = controller.getPlatform().sendHttpGetRequest(URL,
					HttpURLConnection.class);
			input = connection.getInputStream();
			output = dstFile.write(false);

			while ((count = input.read(data)) != -1)
				output.write(data, 0, count);

			output.flush();
		} catch (Exception e) {
			Gdx.app.error(DOWNLOAD_TAG, "Exception while downloading file "
					+ dstFile.toString(), e);
			succeeded = false;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			StreamUtils.closeQuietly(output);
			StreamUtils.closeQuietly(input);
		}
		result(succeeded);
		return true;
	}
}
