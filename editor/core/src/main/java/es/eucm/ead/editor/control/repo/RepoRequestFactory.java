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
package es.eucm.ead.editor.control.repo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.control.appdata.ReleaseInfo;
import es.eucm.ead.schema.editor.components.repo.request.RepoRequest;
import es.eucm.ead.schema.editor.components.repo.request.SearchRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Simple utility to create urls
 * 
 * Created by jtorrente on 27/11/14.
 */
public class RepoRequestFactory {

	private static final String LOG_TAG = "RepoRequestFactory";

	// Needed to retrieve backend url, api key, servlet name
	private ReleaseInfo releaseInfo;

	public RepoRequestFactory(ReleaseInfo releaseInfo) {
		this.releaseInfo = releaseInfo;
	}

	/**
	 * Builds the url that has to be used to produce the given
	 * {@link RepoRequest}. The outcome URL includes the backend url, the
	 * servlet name, the api key, and the parameters specified in the given
	 * request. Example: if a {@link SearchRequest} has to be sent to the
	 * backend, with search string "tree", this method produces a url similar
	 * to:
	 * 
	 * http://backend.mokap.es/search?k=XXXXXXX&q=tree
	 * 
	 * @param request
	 *            The request to build a service URL for.
	 * @return The service URL, including parameters and key, or {@code null} if
	 *         for any reason the URL cannot be built (e.g. backend info missing
	 *         in release.json)
	 */
	public String buildRequestURL(RepoRequest request) {
		String serviceUrl = buildServiceUrl(request);
		if (serviceUrl == null) {
			Gdx.app.debug(LOG_TAG, "URL could not be built");
			return null;
		}

		// Set the api key, if necessary
		if (request.getK() == null) {
			if (releaseInfo.getBackendApiKey() == null) {
				Gdx.app.debug(LOG_TAG,
						"URL could not be built because a valid API key was not found");
				return null;
			}
			request.setK(releaseInfo.getBackendApiKey());
		}

		// Append parameters
		serviceUrl = appendParameters(serviceUrl, request.getClass(), request);

		return serviceUrl;
	}

	/*
	 * Appends the parameters recursively using reflection. For example, for a
	 * SearchRequest, this will append to serviceUrl the param
	 * q="search string", declared in SearchRequest, and also the param
	 * k=API_KEY, declared in superclass RepoRequest.
	 * 
	 * Value of parameters is encoded, so any string is supported, including
	 * blanks and strange symbols.
	 */
	private String appendParameters(String serviceUrl, Class clazz,
			RepoRequest request) {
		String url = serviceUrl;
		for (Field field : ClassReflection.getDeclaredFields(clazz)) {
			field.setAccessible(true);
			try {
				String name = field.getName();
				String value = field.get(request) == null ? null : ""
						+ field.get(request);
				if (value == null) {
					continue;
				}
				try {
					value = URLEncoder.encode(value, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					Gdx.app.debug(LOG_TAG,
							"Error occurred while encoding parameter " + name
									+ " with value " + value);
				}
				if (url.endsWith("?")) {
					url += name + "=" + value;
				} else {
					url += "&" + name + "=" + value;
				}
			} catch (ReflectionException e) {
				e.printStackTrace();
			}
		}

		if (clazz.getSuperclass() != null && clazz != RepoRequest.class) {
			return appendParameters(url, clazz.getSuperclass(), request);
		}

		return url;
	}

	/*
	 * Returns the base url for the backend (e.g. http://backend.mokap.es)
	 */
	private String buildBaseUrl() {
		String backendUrl = releaseInfo.getBackendURL();
		if (backendUrl == null) {
			return null;
		}

		// Append http:// if necessary
		if (!backendUrl.toLowerCase().startsWith("http://")
				&& !backendUrl.toLowerCase().startsWith("https://")) {
			backendUrl = "http://" + backendUrl;
		}

		return backendUrl;
	}

	/*
	 * Builds the service url for a given request, without parameters. For this
	 * method to work, it is important that a certain name convention is
	 * respected. Child requests extending RepoRequest (e.g. SearchRequest) must
	 * be named using either the name of the service (e.g. Search, Featured,
	 * etc.) or the name of the service plus the Request suffix (e.g.
	 * SearchRequest, FeaturedRequest, etc.). Then, there must be a param
	 * available in the {@link ReleaseInfo} object with the name
	 * backendServicenameServlet that specifies the relative path of the servlet
	 * to be invoked for that particular request.
	 * 
	 * Example: - Service: "search" - ClassName to encapsulate the Request:
	 * SearchRequest - Field for the name of the servlet (in ReleaseInfo):
	 * backendSearchServlet
	 */
	private String buildServiceUrl(RepoRequest request) {
		String url = buildBaseUrl();
		if (url == null) {
			Gdx.app.debug(LOG_TAG,
					"Error occurred while building service url: baseUrl could not be resolved");
			return null;
		}

		// Append "?"
		if (!url.endsWith("?")) {
			url += "?";
		}

		return url;
	}

}
