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
package es.eucm.ead.editor.editorui.layouts;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.editorui.MockPlatform;
import es.eucm.ead.editor.editorui.UITest;
import es.eucm.ead.editor.view.builders.SearchView;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schema.editor.components.repo.response.SearchResponse;

public class SearchRepoViewTest extends UITest {

	private static final String URL = "";
	private static final int ELEMS = 10;

	@Override
	protected Actor buildUI(Skin skin, I18N i18n) {

		prepareLocalAssets();

		SearchView repoView = new SearchView();
		repoView.initialize(controller);

		return repoView.getView();
	}

	private void prepareLocalAssets() {
		MockPlatform platform = ((MockPlatform) controller.getPlatform());
		EditorGameAssets gameAssets = controller.getEditorGameAssets();

		// Prepare some images...
		gameAssets.setLoadingPath("", true);
		FileHandle image = gameAssets.resolve("thumbnail.png");
		byte[] bytes = image.readBytes();

		Array<RepoElement> repoElems = new Array<RepoElement>();
		for (int i = 0; i < ELEMS; ++i) {
			RepoElement elem = new RepoElement();
			elem.getNameList().add("RepoElem " + i);
			String currentThumbnail = i + ".png";
			elem.getThumbnailUrlList().add(currentThumbnail);
			repoElems.add(elem);
			platform.putHttpResponse(currentThumbnail, bytes);
		}

		SearchResponse response = new SearchResponse();
		response.setCount(ELEMS);
		response.setTotal(ELEMS);
		response.setResults(repoElems);
		String json = gameAssets.toJson(response, SearchResponse.class);
		platform.putHttpResponse(URL, json);
		platform.putDefaultHttpResponse(json);
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 600;
		config.height = 350;
		new LwjglApplication(new SearchRepoViewTest(), config);
	}
}
