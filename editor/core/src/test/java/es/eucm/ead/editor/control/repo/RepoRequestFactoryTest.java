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

import es.eucm.ead.editor.control.appdata.ReleaseInfo;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.schema.editor.components.repo.request.SearchRequest;
import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

/**
 * Created by jtorrente on 27/11/14.
 */
public class RepoRequestFactoryTest {

	@Test
	public void testNormal() {
		assertEquals(
				build("api.mokap.es/backend", "XXXXXX", "text to search for"),
				"http://api.mokap.es/backend?q=text+to+search+for&k=XXXXXX");
		assertEquals(
				build("http://api.mokap.es/backend", "XXXXXX",
						"text to search for"),
				"http://api.mokap.es/backend?q=text+to+search+for&k=XXXXXX");
	}

	@Test
	public void testBackendDataNotAvailable() {
		MockApplication.initStatics();
		assertNull(build(null, "XXXXXX", "text to search for"));
		assertNull(build("api.mokap.es/backend", null, "text to search for"));
	}

	private String build(String backendUrl, String backendApiKey, String query) {
		ReleaseInfo releaseInfo = new ReleaseInfo();
		releaseInfo.setBackendURL(backendUrl);
		releaseInfo.setBackendApiKey(backendApiKey);

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQ(query);

		return new RepoRequestFactory(releaseInfo)
				.buildRequestURL(searchRequest);
	}
}
