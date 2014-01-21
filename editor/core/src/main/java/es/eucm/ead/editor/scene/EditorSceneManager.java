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
package es.eucm.ead.editor.scene;

import biz.source_code.miniTemplator.MiniTemplator;
import biz.source_code.miniTemplator.MiniTemplator.Builder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.Editor;
import es.eucm.ead.editor.io.EditorIO;
import es.eucm.ead.editor.io.Platform.StringListener;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.Engine;
import es.eucm.ead.engine.actors.SceneElementActor;
import es.eucm.ead.engine.scene.SceneManager;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.behaviors.Behavior;

import java.io.IOException;
import java.io.StringReader;

public class EditorSceneManager extends SceneManager {

	private FileHandle currentPath;

	private EditorIO io = (EditorIO) Engine.schemaIO;

	public EditorSceneManager(Assets assetManager) {
		super(assetManager);
	}

	@Override
	public void loadGame() {
		if (currentPath != null) {
			super.loadGame();
		}
	}

	public void readGame() {
		Editor.platform.askForFile(new StringListener() {
			@Override
			public void string(String result) {
				if (result != null && result.endsWith("game.json")) {
					currentPath = Gdx.files.absolute(result).parent();
					Engine.engine.setLoadingPath(currentPath.path());
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

	public void save(boolean optimize) {
		String name = this.getCurrentScenePath();
		if (!name.endsWith(".json")) {
			name += ".json";
		}
		io.save(Editor.sceneManager.getCurrentScene(), (optimize ? "bin/" : "")
				+ name, optimize);
	}

	public void addSceneElement() {
		Editor.platform.askForFile(new StringListener() {

			@Override
			public void string(String result) {
				if (result != null) {
					SceneElement sceneElement = buildFromTemplate(
							SceneElement.class, "imageactor.json", "uri",
							result);
					Editor.sceneManager.loadSceneElement(sceneElement);
				}
			}
		});
	}

	public void newScene(final SceneElement element) {
		Gdx.input.getTextInput(new TextInputListener() {
			@Override
			public void input(String result) {
				if (result != null) {
					final String scene = result;
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							if (element != null) {
								Behavior goscene = buildFromTemplate(
										Behavior.class, "gosceneb.json",
										"scene", scene, "event", "touchDown");
								element.getBehaviors().add(goscene);
							}
							save(false);
							loadScene(scene);
						}
					});
				}
			}

			@Override
			public void canceled() {
			}
		}, "New scene", "scene");
	}

	public <T> T buildFromTemplate(Class<T> clazz, String templateName,
			String... params) {
		String template = Editor.assets.resolve("templates/" + templateName)
				.readString();
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

	@Override
	public void loadSceneElement(SceneElement sceneElement) {
		super.loadSceneElement(sceneElement);
		currentScene.getChildren().add(sceneElement);
	}

	@Override
	public boolean removeSceneElement(SceneElementActor actor) {
		currentScene.getChildren().remove(actor.getSchema());
		return super.removeSceneElement(actor);
	}
}
