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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.platform.MockPlatform;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schema.editor.components.repo.response.SearchResponse;

public class SearchRepoTest extends WorkerTest implements WorkerListener {

	private static final String URL = "";
	private static final int ELEMS = 10;

	private Array<RepoElement> repoElems = new Array<RepoElement>();
	private SearchResponse response;

	@Before
	public void buildElems() {

		EditorGameAssets gameAssets = controller.getEditorGameAssets();

		// Prepare some images...
		gameAssets.setLoadingPath("", true);
		FileHandle image = gameAssets.resolve("blank.png");
		byte[] bytes = image.readBytes();

		MockPlatform platform = (MockPlatform) controller.getPlatform();
		repoElems = new Array<RepoElement>();
		for (int i = 0; i < ELEMS; ++i) {
			RepoElement elem = new RepoElement();
			String currentThumbnail = i + ".png";
			elem.getThumbnailUrlList().add(currentThumbnail);
			repoElems.add(elem);
			platform.putHttpResponse(currentThumbnail, bytes);
		}

		response = new SearchResponse();
		response.setCount(ELEMS);
		response.setTotal(ELEMS);
		response.setResults(repoElems);

		String json = gameAssets.toJson(response, SearchResponse.class);
		platform.putHttpResponse(URL, json);
	}

	@Override
	public void testWorker() {
		controller.action(ExecuteWorker.class, SearchRepo.class, this, URL);
	}

	@Override
	public void asserts() {
		assertEquals(repoElems.size, 0);
	}

	@Override
	public void start() {

	}

	@Override
	public void result(Object... results) {
		Object firstResult = results[0];
		if (firstResult instanceof SearchResponse) {
			SearchResponse mockResponse = (SearchResponse) firstResult;
			assertEquals(response.getCount(), mockResponse.getCount());
			assertTrue(response.getTotal() == mockResponse.getTotal());
		} else {
			String expectedUrl = Q.getRepoElementThumbnailUrl(repoElems
					.removeIndex(0));
			assertEquals(expectedUrl,
					Q.getRepoElementThumbnailUrl((RepoElement) firstResult));
			assertTrue((results[1] instanceof Pixmap));
		}
	}

	@Override
	public void done() {
	}

	@Override
	public void error(Throwable ex) {
		fail("Exception thrown" + ex);
	}

	@Override
	public void cancelled() {

	}
}
