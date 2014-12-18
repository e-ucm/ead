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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.StreamUtils;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Tracker;
import es.eucm.ead.engine.assets.GameAssets.ImageUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractPlatform implements Platform {

	private static final String ABSTRACT_PLATFORM_TAG = "AbstractPlatform";
	private static final int TIMEOUT = 25000;

	private Object[] applicationArguments;
	private Batch batch;

	protected AbstractPlatform() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				Gdx.app.error("EditorApplicationListener",
						"Fatal error: " + t.getName() + "(" + t.getId() + ")",
						e);
			}
		});
	}

	@Override
	public Tracker createTracker(Controller controller) {
		return new Tracker(controller);
	}

	public boolean browseURL(String URL) {
		try {
			Gdx.net.openURI(URL);
			return true;
		} catch (Throwable t) {
			Gdx.app.debug("AbstractPlatform", "Error opening URL " + URL, t);
			return false;
		}
	}

	@Override
	public void setBatch(Batch batch) {
		this.batch = batch;
	}

	@Override
	public Batch getBatch() {
		return batch;
	}

	@Override
	public String getLocale() {
		return Locale.getDefault().getLanguage() + "_"
				+ Locale.getDefault().getCountry();
	}

	@Override
	public String getDefaultProjectsFolder() {
		return System.getProperty("user.dir");
	}

	@Override
	public String getLibraryFolder() {
		return getDefaultProjectsFolder();
	}

	@Override
	public <T> T sendHttpGetRequest(String URL, Class<T> type)
			throws IOException {
		HttpRequest httpRequest = Pools.obtain(HttpRequest.class);
		httpRequest.setMethod(Net.HttpMethods.GET);
		httpRequest.setTimeOut(TIMEOUT);
		httpRequest.setUrl(URL);
		try {
			return sendHttpRequest(httpRequest, type);
		} finally {
			Pools.free(httpRequest);
		}
	}

	@Override
	public <T> T sendHttpRequest(HttpRequest httpRequest, Class<T> type)
			throws IOException {
		String method = httpRequest.getMethod();

		URL url;
		String queryUrl;
		if (method.equalsIgnoreCase(HttpMethods.GET)) {
			String queryString = "";
			String value = httpRequest.getContent();
			if (value != null && !"".equals(value))
				queryString = "?" + value;
			queryUrl = httpRequest.getUrl() + queryString;
		} else {
			queryUrl = httpRequest.getUrl();
		}
		url = new URL(queryUrl);

		Gdx.app.log(ABSTRACT_PLATFORM_TAG, "Sending HTTP " + method
				+ " request to " + queryUrl);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		// should be enabled to upload data.
		boolean doingOutPut = method.equalsIgnoreCase(HttpMethods.POST)
				|| method.equalsIgnoreCase(HttpMethods.PUT);
		connection.setDoOutput(doingOutPut);
		connection.setDoInput(true);
		connection.setRequestMethod(method);
		HttpURLConnection.setFollowRedirects(httpRequest.getFollowRedirects());

		// Headers get set regardless of the method
		for (Map.Entry<String, String> header : httpRequest.getHeaders()
				.entrySet())
			connection.addRequestProperty(header.getKey(), header.getValue());

		// Set Timeouts
		connection.setConnectTimeout(httpRequest.getTimeOut());
		connection.setReadTimeout(httpRequest.getTimeOut());

		// Set the content for POST and PUT (GET has the
		// information embedded in the URL)
		if (doingOutPut) {
			// we probably need to use the content as stream
			// here instead of using it as a string.
			String contentAsString = httpRequest.getContent();
			if (contentAsString != null) {
				OutputStreamWriter writer = new OutputStreamWriter(
						connection.getOutputStream());
				try {
					writer.write(contentAsString);
				} finally {
					StreamUtils.closeQuietly(writer);
				}
			} else {
				InputStream contentAsStream = httpRequest.getContentStream();
				if (contentAsStream != null) {
					OutputStream os = connection.getOutputStream();
					try {
						StreamUtils.copyStream(contentAsStream, os);
					} finally {
						StreamUtils.closeQuietly(os);
					}
				}
			}
		}

		connection.connect();
		int status = connection.getResponseCode();

		if (status != HttpStatus.SC_OK) {
			throw new IOException("Http stauts code: " + status);
		}

		if (type == HttpURLConnection.class) {
			return (T) connection;
		}

		try {
			InputStream input = null;
			input = connection.getInputStream();

			if (type == String.class) {
				try {
					return (T) StreamUtils.copyStreamToString(input,
							connection.getContentLength());
				} finally {
					StreamUtils.closeQuietly(input);
				}
			} else if (type == byte[].class) {
				try {
					return (T) StreamUtils.copyStreamToByteArray(input,
							connection.getContentLength());
				} finally {
					StreamUtils.closeQuietly(input);
				}
			}
			return null;
		} finally {
			connection.disconnect();
		}
	}

	public void setApplicationArguments(Object... applicationArguments) {
		this.applicationArguments = applicationArguments;
	}

	@Override
	public Object[] getApplicationArguments() {
		return applicationArguments;
	}

	@Override
	public ImageUtils getImageUtils() {
		return null;
	}

	@Override
	public boolean isDebug() {
		return false;
	}
}
