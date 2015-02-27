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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SerializationException;

import es.eucm.ead.editor.control.repo.RepoRequestFactory;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schema.editor.components.repo.request.RepoRequest;
import es.eucm.ead.schema.editor.components.repo.request.SearchRequest;
import es.eucm.ead.schema.editor.components.repo.response.SearchResponse;

/**
 * Sends an HTTP request from a given URL and returns the search result and the
 * resulting elements with their thumbnails.
 * <dl>
 * <dt><strong>The input arguments are</strong></dt>
 * <dd><strong>args[0]</strong> <em>String</em> The string to search for (q
 * param). E.g.: "tree".
 * <dd><strong>args[1]</strong> (Optional) <em>String</em> The preferred width
 * for thumbnails, in pixels. E.g.: "512".
 * <dd><strong>args[2]</strong> (Optional) <em>String</em> The preferred height
 * for thumbnails, in pixels. E.g.: "512".
 * <dd><strong>args[3]</strong> (Optional) <em>String</em> The cursor provided
 * by the backend for paginated searching (c param)
 * </dl>
 * <dl>
 * <dt><strong>The result arguments are</strong></dt>
 * <strong>First result:</strong>
 * <dd>
 * <strong>args[0]</strong> <em>{@link SearchResponse}</em> with all the
 * results.
 * </dl>
 * <strong>Rest of results:</strong> <dd>
 * <strong>args[0]</strong> <em>RepoElement</em> the element. <dd>
 * <strong>args[1]</strong> <em>String</em> pixmap of the thumbnail of the
 * element.</dd> </dl>
 */
public class SearchRepo extends RepoWorker {

	private static final String SEARCH_REPO_TAG = "SearchRepoWorker";

	private SearchResponse response;

	private Array<RepoElement> repoElems;

	@Override
	protected void doPrepare(String URL) {

		response = null;
		repoElems = null;
		String httpResponse = null;

		try {
			httpResponse = controller.getPlatform().sendHttpGetRequest(URL,
					String.class);
		} catch (IOException e) {
			Gdx.app.error(SEARCH_REPO_TAG,
					"Failed to perform the HTTP request. ", e);
			error(e);
			return;
		}

		if (httpResponse == null || httpResponse.trim().isEmpty()) {
			IOException ioe = new IOException(
					"Invalid http response (null or empty)");
			Gdx.app.error(SEARCH_REPO_TAG, "", ioe);
			error(ioe);
			return;
		}

		try {
			response = getRepoElementsFromResult(httpResponse);
			result(response);
			repoElems = response.getResults();
		} catch (SerializationException se) {
			Gdx.app.log(SEARCH_REPO_TAG, "Error parsing JSON result.", se);
			repoElems = null;
			response = null;
			error(se);
		}
	}

	/**
	 * 
	 * @param result
	 *            a correctly formatted .json as an {@link SearchResponse}.
	 * @return a {@link SearchResponse}.
	 * @see {@link SearchResponse}
	 */
	private SearchResponse getRepoElementsFromResult(String result) {
		return controller.getEditorGameAssets().fromJson(SearchResponse.class,
				result);
	}

	@Override
	protected boolean step() {
		if (repoElems == null || repoElems.size == 0) {
			return true;
		}
		RepoElement elem = repoElems.removeIndex(0);

		String thumbnailURL = Q.getRepoElementThumbnailUrl(elem);

		result(elem, thumbnailURL);

		return repoElems.size == 0;
	}

	@Override
	protected String buildUrl(String[] args, RepoRequestFactory requestFactory) {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQ(args[0]);
		setPreferredThumbnailWidthAndHeight(args, searchRequest);
		if (args.length > 3) {
			searchRequest.setC(args[3]);
		}
		if (args.length > 4) {
			searchRequest.setCat(args[4]);
		}
		return requestFactory.buildRequestURL(searchRequest);
	}

	protected void setPreferredThumbnailWidthAndHeight(String[] args,
			RepoRequest searchRequest) {
		if (args.length > 2) {
			try {
				searchRequest.setW(Integer.parseInt(args[1]) + "");
				searchRequest.setH(Integer.parseInt(args[2]) + "");
			} catch (NumberFormatException e) {
				// Log it
				Gdx.app.debug(SEARCH_REPO_TAG,
						"Invalid preferred thumbnail width or height: "
								+ args[1] + "  " + args[2]);
			}
		}
	}
}
