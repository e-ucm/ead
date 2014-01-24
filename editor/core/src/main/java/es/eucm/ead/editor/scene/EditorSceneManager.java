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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.Editor;
import es.eucm.ead.editor.io.EditorIO;
import es.eucm.ead.editor.io.Platform.StringListener;
import es.eucm.ead.editor.model.DependencyNode;
import es.eucm.ead.editor.model.EditorModel;
import es.eucm.ead.editor.view.dialogs.OptionsDialog.DialogListener;
import es.eucm.ead.editor.view.options.AbstractOption;
import es.eucm.ead.editor.view.options.BooleanOption;
import es.eucm.ead.editor.view.options.DropdownOption;
import es.eucm.ead.editor.view.options.IntegerOption;
import es.eucm.ead.editor.view.options.OptionsPanel;
import es.eucm.ead.editor.view.options.TextOption;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.Engine;
import es.eucm.ead.engine.GameController;
import es.eucm.ead.engine.actors.SceneElementActor;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.behaviors.Behavior;
import es.eucm.ead.schema.game.Game;

import java.io.IOException;
import java.io.StringReader;

public class EditorSceneManager extends GameController {

	private FileHandle currentPath;

	private EditorIO io = (EditorIO) Engine.schemaIO;

	public EditorSceneManager(Assets assetManager) {
		super(null, null, null);
	}

	@Override
	public boolean loadGame() {
		if (currentPath != null) {
			super.loadGame();
		}
		return false;
	}

	public void readGame() {
		Editor.platform.askForFile(new StringListener() {
			@Override
			public void string(String result) {
				if (result != null && result.endsWith("game.json")) {
					currentPath = Gdx.files.absolute(result).parent();
					// Engine.engine.setLoadingPath(currentPath.path(), false);
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

	public void newGame() {

		// prepares objects that will be used to store config
		final Game game = new Game();
		game.setTitle("My eAdventure Game");
		game.setHeight(600);
		game.setWidth(800);
		game.setInitialScene("scene1");

		EditorModel em = Editor.controller.getModel();
		Skin skin = Engine.assets.getSkin();

		Object o = new Object() {
			public boolean stub;
		};

		DependencyNode dn = new DependencyNode(EditorModel.gameId, game);

		// requests config
		OptionsPanel op = new OptionsPanel(
				OptionsPanel.LayoutPolicy.VerticalBlocks);
		AbstractOption textOption = new TextOption("Name of the game",
				"Used to name the folder where the game will be saved", dn)
				.from(game, "title");
		op.add(textOption);
		op.add(new IntegerOption("Screen width",
				"Width of game screen, in pixels", dn).min(400).max(1600)
				.from(game, "width"));
		op.add(new IntegerOption("Screen height",
				"Tal of game screen, in pixels", dn).min(400).max(1600)
				.from(game, "width"));
		op.add(new IntegerOption("Screen height",
				"Height of game screen, in pixels", dn).min(300).max(1200)
				.from(game, "height"));
		op.add(new TextOption("Initial scene name",
				"Name of the initial scene; you can change it later", dn).from(
				game, "initialScene"));
		op.add(new BooleanOption("Check if you want", "Yeah, awesome option")
				.from(o, "stub"));
		op.add(new DropdownOption<String>("List", "Choose from list", dn)
				.items(new String[] { "My eAdventure Game",
						"Super big option in here", "Yeah, whatever" }).from(
						game, "title"));

		Editor.controller.getViewController().showOptionsDialog(op,
				new DialogListener() {
					@Override
					public void button(String buttonKey) {
						if ("general.ok".equals(buttonKey)) {
							createGame(game);
						}
					}
				}, "general.ok", "general.cancel");
	}

	public void createGame(Game game) {
		// FIXME create action to create game
		/*
		 * currentPath = Gdx.files.external("eadgames/" + game.getTitle());
		 * Editor.controller.getCommandManager().performCommand( new
		 * NewProjectCommand(game, currentPath)); loadGame();
		 */
	}

	public void save(boolean optimize) {
		/*
		 * String name = this.getCurrentScenePath(); if
		 * (!name.endsWith(".json")) { name += ".json"; }
		 * io.save(Editor.gameController.getCurrentScene(), (optimize ? "bin/" :
		 * "") + name, optimize);
		 */
	}

	public void addSceneElement() {
		Editor.platform.askForFile(new StringListener() {

			@Override
			public void string(String result) {
				if (result != null) {
					SceneElement sceneElement = buildFromTemplate(
							SceneElement.class, "imageactor.json", "uri",
							result);
					Editor.gameController.loadSceneElement(sceneElement);
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
		// currentScene.getChildren().add(sceneElement);
	}

	@Override
	public boolean removeSceneElement(SceneElementActor actor) {
		// currentScene.getChildren().remove(actor.getSchema());
		return super.removeSceneElement(actor);
	}
}
