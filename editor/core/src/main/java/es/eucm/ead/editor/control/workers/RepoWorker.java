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

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.repo.RepoRequestFactory;

/**
 * Base class to implement workers that connect to the backend. This kind of
 * worker holds a {@link RepoRequestFactory} object to help building the URLs
 * for querying the backend.
 * 
 * Subclasses must implement two methods:
 * <ol>
 * <li>1) {@link #buildUrl(String[], RepoRequestFactory)}: This method is
 * expected to build the url needed for this particular operation with the
 * backend.</li>
 * <li>2) {@link #doPrepare(String)}: This method just behaves as
 * {@link Worker#prepare()}, with the addition that it receives the URL as
 * produced by {@link #buildUrl(String[], RepoRequestFactory)}.</li>
 * </ol>
 * 
 * See {@link es.eucm.ead.editor.control.workers.SearchRepo} for an example.
 * 
 * Created by jtorrente on 28/11/14.
 */
public abstract class RepoWorker extends Worker {

	protected RepoRequestFactory requestUrlFactory = null;

	public RepoWorker() {
		super(true);
	}

	@Override
	public void setController(Controller controller) {
		super.setController(controller);
		requestUrlFactory = new RepoRequestFactory(controller.getReleaseInfo());
	}

	/**
	 * Builds the URL for this particular request. Implementations of this
	 * method will normally do the next operations: 1) Create an instance of a
	 * subclass of
	 * {@link es.eucm.ead.schema.editor.components.repo.request.RepoRequest}
	 * (e.g. {@link es.eucm.ead.editor.control.workers.SearchRepo}).
	 * 
	 * 2) Use the String arguments (
	 * 
	 * @param stringArgs
	 *            ) passed to the worker to set the fields of the RepoRequest
	 *            object created in step (1)
	 * 
	 *            3) Invoke {@code repoRequestFactory.buildRequestURL(request)},
	 *            where request is the object created in (1), to build the URL
	 *            that is to be returned
	 * 
	 *            See
	 *            {@link es.eucm.ead.editor.control.workers.SearchRepo#buildUrl(String[], es.eucm.ead.editor.control.repo.RepoRequestFactory)}
	 *            for an example
	 * 
	 * @param stringArgs
	 *            List of arguments passed to the worker, casted to String.
	 * @param repoRequestFactory
	 *            The helper that knows how to build requests.
	 * 
	 * @return The URL that {@link #doPrepare(String)} will receive to send a
	 *         request to the backend.
	 */
	protected abstract String buildUrl(String[] stringArgs,
			RepoRequestFactory repoRequestFactory);

	/**
	 * Equivalent to {@link Worker#prepare()}, with the addition that it
	 * receives the URL as produced by
	 * {@link #buildUrl(String[], RepoRequestFactory)}.
	 * 
	 * @param url
	 *            The url of the service to make the request. That is the result
	 *            of method
	 *            {@link #buildUrl(String[], es.eucm.ead.editor.control.repo.RepoRequestFactory)}
	 *            .
	 */
	protected abstract void doPrepare(String url);

	@Override
	protected void prepare() {
		String[] stringArgs = new String[args.length];
		for (int i = 0; i < args.length; i++) {
			if (args[i] != null) {
				stringArgs[i] = args[i].toString();
			}
		}
		String url = buildUrl(stringArgs, requestUrlFactory);
		doPrepare(url);
	}
}
