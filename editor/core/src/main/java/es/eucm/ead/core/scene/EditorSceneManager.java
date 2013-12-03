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
package es.eucm.ead.core.scene;

import biz.source_code.miniTemplator.MiniTemplator;
import biz.source_code.miniTemplator.MiniTemplator.Builder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import es.eucm.ead.core.EAdEngine;
import es.eucm.ead.core.EditorEngine;
import es.eucm.ead.core.io.EditorIO;
import es.eucm.ead.core.io.Platform.StringListener;
import es.eucm.ead.editor.control.CommandManager;
import es.eucm.ead.editor.model.DependencyNode;
import es.eucm.ead.editor.model.EditorModel;
import es.eucm.ead.editor.view.generic.IntegerOption;
import es.eucm.ead.editor.view.generic.OptionPanel;
import es.eucm.ead.editor.view.generic.TextOption;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.behaviors.Behavior;
import es.eucm.ead.schema.game.Game;

import java.io.IOException;
import java.io.StringReader;

public class EditorSceneManager extends SceneManager {

	private FileHandle currentPath;

	private EditorIO io = (EditorIO) EAdEngine.jsonIO;

	public EditorSceneManager(AssetManager assetManager) {
		super(assetManager);
	}

	@Override
	public void loadGame() {
		if (currentPath != null) {
			super.loadGame();
		}
		loadTemplates();
	}

	public void loadTemplate(String template) {
		EditorEngine.assetManager.load(template, String.class);
		EditorEngine.assetManager.finishLoading();
	}

	private void loadTemplates() {
		loadTemplate("@templates/imageactor.json");
		loadTemplate("@templates/gosceneb.json");
	}

	public void readGame() {
		EditorEngine.platform.askForFile(new StringListener() {
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

	public static class GameConfig {
		private String gameName = "My Game";
	}

	public void newGame() {

		// prepares objects that will be used to store config
		final Game game = new Game();
		game.setHeight(600);
		game.setWidth(800);
		game.setInitialScene("scene1");
		final GameConfig gameConfig = new GameConfig();

		EditorModel em = new EditorModel(game);
        Skin skin = EAdEngine.assetManager.get("@skin-packed/skin.json");

        DependencyNode dn = em.getRoot();
		// requests config
		OptionPanel op = new OptionPanel("New game options",
				OptionPanel.LayoutPolicy.VerticalBlocks, 4);
		op.add(new TextOption("Name of the game",
				"Used to name the folder where the game will be saved", dn)
                .from(gameConfig, "gameName"));       
		op.add(new IntegerOption("Screen width",
				"Width of game screen, in pixels", dn)
                .min(400).max(1600).from(game, "width"));
		op.add(new IntegerOption("Screen height",
				"Height of game screen, in pixels", dn)
                .min(300).max(1200).from(game, "height"));        
		op.add(new TextOption("Initial scene name",
				"Name of the initial scene; you can change it later", dn)
                .from(game, "initialScene"));    
        
        // falta un dialogo
        Dialog d = new Dialog(op.getTitle(), skin);
        Table t = d.getContentTable();
        t.add(op.getControl(new CommandManager(em), skin));
        
        d.show(EAdEngine.stage);
        
        createGame(gameConfig.gameName, game.getWidth(), game
                .getHeight(), game.getInitialScene());
	}

	public void createGame(String gameName, int gameWidth, int gameHeight,
			String initialScene) {
		currentPath = Gdx.files.external("eadgames/" + gameName);
		Game game = new Game();
		game.setHeight(gameHeight);
		game.setWidth(gameWidth);
		game.setInitialScene(initialScene);
		EAdEngine.jsonIO.toJson(game, currentPath.child("game.json"));
		currentPath.child("scenes").mkdirs();
		EAdEngine.engine.setLoadingPath(currentPath.file().getAbsolutePath());
		loadGame();
	}

	public void save(boolean optimize) {
		String name = this.getCurrentSceneName();
		if (!name.endsWith(".json")) {
			name += ".json";
		}
		io.save(EditorEngine.sceneManager.getScene(), (optimize ? "bin/" : "")
				+ name, optimize);
	}

	public void addSceneElement() {
		EditorEngine.platform.askForFile(new StringListener() {

			@Override
			public void string(String result) {
				if (result != null) {
					SceneElement sceneElement = buildFromTemplate(
							SceneElement.class, "imageactor.json", "uri",
							result);
					EditorEngine.sceneManager.loadSceneElement(sceneElement);
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
