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
 * <dd><strong>args[1]</strong> <em>FileHandle</em> FileHandle to the output
 * file.</dd>
 * </dl>
 * <dl>
 * <dt><strong>The result argument is</strong></dt>
 * <dd><strong>args[0]</strong> <em>Float</em> the completion value, ranging
 * from 0 to 1.
 * </dl>
 */
public class DownloadFile extends Worker {

	private static final String DOWNLOAD_TAG = "DownloadFileWorker";

	private static final float THRESHOLD = 0.1F;

	/**
	 * A temporal byte array used to write to disk efficiently.
	 */
	private final byte data[] = new byte[2048];
	private int lengthOfFile, total;
	private float completion;

	private InputStream input;
	private OutputStream output;
	private HttpURLConnection connection;

	private FileHandle dstFile;

	public DownloadFile() {
		super(true);
	}

	@Override
	protected void prepare() {
		String url = null;
		input = null;

		if (args[0] instanceof String) {
			url = (String) args[0];
		} else {
			input = (InputStream) args[0];
		}

		dstFile = (FileHandle) args[1];

		completion = 0f;
		total = 0;
		output = null;
		connection = null;

		try {
			if (input == null) {
				connection = controller.getPlatform().sendHttpGetRequest(url,
						HttpURLConnection.class);
				lengthOfFile = connection.getContentLength();
				input = connection.getInputStream();
			}
			result(.1f);
			output = dstFile.write(false);
		} catch (Exception e) {
			Gdx.app.error(DOWNLOAD_TAG, "Exception while downloading file "
					+ dstFile.toString(), e);
			closeStreams();
			error(e);
		}
	}

	@Override
	protected boolean step() {
		if (input != null) {
			try {
				int count;
				count = input.read(data);
				if (count != -1) {
					output.write(data, 0, count);
					total += count;
					float completed = (total / (float) lengthOfFile);
					if (completed == 1f || completed - completion > THRESHOLD) {
						completion = completed;
						result(Math.min(.1f + completed * .9f, 0.95f));
					}
				} else {
					output.flush();
					closeStreams();
					result(1f);
				}
			} catch (Exception e) {
				Gdx.app.error(DOWNLOAD_TAG, "Exception while downloading file "
						+ dstFile.toString(), e);
				closeStreams();
				error(e);
			}
		}
		return input == null;
	}

	@Override
	protected void cancelled() {
		super.cancelled();
		closeStreams();
		if (dstFile != null && dstFile.exists()) {
			dstFile.delete();
		}
	}

	private void closeStreams() {
		if (connection != null) {
			connection.disconnect();
			connection = null;
		}
		StreamUtils.closeQuietly(output);
		StreamUtils.closeQuietly(input);
		output = null;
		input = null;
	}
}
