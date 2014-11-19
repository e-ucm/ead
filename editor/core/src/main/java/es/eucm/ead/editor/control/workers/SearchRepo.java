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

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SerializationException;

import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.schema.editor.components.repo.RepoElement;

/**
 * Load all the projects and their associated thumbnails. Thumbnails path are
 * absolute and can be null
 */
public class SearchRepo extends Worker {

	private static final String SEARCH_REPO_TAG = "SearchRepoWorker";

	/**
	 * 25 s.
	 */
	private static final int TIMEOUT = 25000;

	private Array<RepoElement> repoElems;

	/**
	 * Sends an HTTP request and returns the response. See also:
	 * {@link Platform#sendHttpRequest(HttpRequest, Class)}
	 * 
	 * @throws IOException
	 * 
	 */
	private <T> T sendHTTPRequest(String URL, Class<T> type) throws IOException {
		Gdx.app.log(SEARCH_REPO_TAG, "Sending HTTP request to " + URL);
		HttpRequest httpRequest = Pools.obtain(HttpRequest.class);
		httpRequest.setMethod(Net.HttpMethods.GET);
		httpRequest.setUrl(URL);
		httpRequest.setContent(null);
		httpRequest.setTimeOut(TIMEOUT);
		try {
			return controller.getPlatform().sendHttpRequest(httpRequest, type);
		} finally {
			Pools.free(httpRequest);
		}
	}

	@Override
	protected void prepare() {

		String URL = "";
		repoElems = null;
		String httpResponse = null;

		try {
			httpResponse = sendHTTPRequest(URL, String.class);
		} catch (IOException e) {
			Gdx.app.error(SEARCH_REPO_TAG,
					"Failed to perform the HTTP request. ", e);
			error(e);
			return;
		}

		try {
			repoElems = getRepoElementsFromResult(httpResponse);
		} catch (SerializationException se) {
			Gdx.app.log(SEARCH_REPO_TAG, "Error parsing JSON result.", se);
			error(se);
		}
	}

	/**
	 * 
	 * @param result
	 *            a correctly formatted .json as an {@link Array} of
	 *            {@link RepoElement}s.
	 * @return an {@link Array} of {@link RepoElement}s.
	 */
	private Array<RepoElement> getRepoElementsFromResult(String result) {
		return controller.getEditorGameAssets().fromJson(Array.class, result);
	}

	@Override
	protected boolean step() {
		if (repoElems == null || repoElems.size == 0) {
			return true;
		}
		RepoElement elem = repoElems.removeIndex(0);

		String thumbnailURL = elem.getThumbnail();

		byte[] httpResponse = null;
		try {
			httpResponse = sendHTTPRequest(thumbnailURL, byte[].class);

			Pixmap pixmap = new Pixmap(httpResponse, 0, httpResponse.length);

			if (pixmap != null) {
				result(elem, pixmap);
			}
		} catch (Exception e) {
			Gdx.app.error(SEARCH_REPO_TAG,
					"Failed to perform the HTTP request. ", e);

		}
		return repoElems.size == 0;
	}
}
