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

import es.eucm.ead.editor.control.repo.RepoRequestFactory;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.editor.components.repo.request.FeaturedRequest;

/**
 * <dl>
 * <dt><strong>The input arguments are</strong></dt>
 * <dd><strong>args[0]</strong> <em>String</em> The string to feature for (f
 * param). E.g.: "all".
 * </dl>
 * 
 * @see {@link SearchRepo}.
 */
public class FeaturedElements extends SearchRepo {

	@Override
	protected String buildUrl(String[] args, RepoRequestFactory requestFactory) {
		FeaturedRequest searchRequest = new FeaturedRequest();
		searchRequest.setCat(RepoCategories.MOKAPS.toString());

		return requestFactory.buildRequestURL(searchRequest);
	}
}
