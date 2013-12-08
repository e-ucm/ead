/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.mockup.core.scene;

import java.io.IOException;
import java.io.StringReader;

import biz.source_code.miniTemplator.MiniTemplator;
import biz.source_code.miniTemplator.MiniTemplator.Builder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;

import es.eucm.ead.core.EAdEngine;
import es.eucm.ead.core.EditorEngine;
import es.eucm.ead.core.io.Platform.StringListener;
import es.eucm.ead.core.scene.SceneManager;
import es.eucm.ead.mockup.core.io.MockupIO;
import es.eucm.ead.mockup.core.screens.BaseScreen;
import es.eucm.ead.schema.actors.SceneElement;

public class MockupSceneManager extends SceneManager {
	private MockupIO io = (MockupIO) EAdEngine.jsonIO;
	private FileHandle currentPath;

	public MockupSceneManager(AssetManager assetManager) {
		super(assetManager);
		loadTemplates();
	}

	@Override
	public void loadGame() {
		if (currentPath != null) {
			super.loadGame();
		}
	}

	private void loadTemplate(String template) {
		EditorEngine.assetManager.load(template, String.class);
		EditorEngine.assetManager.finishLoading();
	}

	private void loadTemplates() {
		loadTemplate("@templates/imageactor.json");
		loadTemplate("@templates/gosceneb.json");
	}

	public void addSceneElement() {
		BaseScreen.resolver.askForFile(new StringListener() {

			@Override
			public void string(String result) {
				if (result == null || currentPath == null)
					return;

				SceneElement sceneElement = buildFromTemplate(
						SceneElement.class, "imageactor.json", "uri", result);
				EditorEngine.sceneManager.loadSceneElement(sceneElement);
			}
		});
	}

	public void readGame() {
		BaseScreen.resolver.askForFile(new StringListener() {

			@Override
			public void string(String result) {
				if (result != null && result.endsWith("game.json")) {
					currentPath = Gdx.files.absolute(result).parent();
					EAdEngine.engine.setLoadingPath(currentPath.path());
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							loadGame();
						}
					});
				}
			}
		});
	}

	private <T> T buildFromTemplate(Class<T> clazz, String templateName,
			String... params) {
		String template = EditorEngine.assetManager.get("@templates/"
				+ templateName);
		MiniTemplator.Builder builder = new Builder();
		try {
			MiniTemplator t = builder.build(new StringReader(template));
			for (int i = 0; i < params.length - 1; i++) {
				t.setVariable(params[i], params[i + 1]);
			}
			return io.fromJson(clazz, t.generateOutput());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
