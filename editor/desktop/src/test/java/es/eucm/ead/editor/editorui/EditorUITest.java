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
package es.eucm.ead.editor.editorui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import es.eucm.ead.editor.DesktopPlatform;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.*;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.platform.Platform.FileChooserListener;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ModelEntityCategory;

import java.util.Map;

/**
 * <p>
 * This recreates the minimum classes to have a complete editor without UI, and
 * is intended as playground to test editor views individually, implementing
 * {@link #builUI(Group)}. In this method, any view can be added to the root and
 * it will be shown.
 * <p>
 * <p>
 * This class also provides some shortcuts for common tasks in the editor:
 * <ol>
 * <li>Ctrl+O: Open a game</li>
 * <li>Ctrl+Z / Ctrl+Y : Undo/Redo</li>
 * <li>Ctrl+S: Save</li>
 * <li>Ctrl+Q: Edit next scene</li>
 * <li>A: Add a scene element to the current list from an image</li>
 * </ol>
 * </p>
 * 
 */
public abstract class EditorUITest implements ApplicationListener {

	protected Stage stage;

	protected Controller controller;

	protected DesktopPlatform platform;

	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		stage = new Stage(new ScreenViewport());
		controller = new Controller(platform = new DesktopPlatform(),
				Gdx.files, stage.getRoot());
		platform.initFileChooser(controller, stage);
		Gdx.input.setInputProcessor(stage);
		builUI(stage.getRoot());

		stage.addListener(new ShortcutListener());
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		controller.act();
		stage.act();
		stage.draw();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		stage.dispose();
		controller.exit();
	}

	protected abstract void builUI(Group root);

	public class ShortcutListener extends InputListener {
		@Override
		public boolean keyUp(InputEvent event, int keycode) {
			switch (keycode) {
			case Keys.ESCAPE:
				if (UIUtils.ctrl()) {
					controller.action(Exit.class);
				}
				break;
			case Keys.O:
				if (UIUtils.ctrl()) {
					controller.action(OpenGame.class);
				}
				break;
			case Keys.S:
				if (UIUtils.ctrl()) {
					controller.action(Save.class);
				}
				break;
			case Keys.Z:
				if (UIUtils.ctrl()) {
					controller.action(Undo.class);
				}
				break;
			case Keys.Y:
				if (UIUtils.ctrl()) {
					controller.action(Redo.class);
				}
				break;
			case Keys.Q:
				if (UIUtils.ctrl()) {
					nextScene();
				}
				break;
			case Keys.A:
				if (UIUtils.ctrl()) {
					addSceneElement();
				}
				break;
			}
			return super.keyUp(event, keycode);
		}
	}

	private void nextScene() {
		Model model = controller.getModel();

		ModelEntity game = model.getGame();
		EditState editState = Model.getComponent(game, EditState.class);

		String scene = editState.getEditScene();

		Map<String, ModelEntity> scenes = model
				.getEntities(ModelEntityCategory.SCENE);

		String nextScene = null;
		boolean next = scene == null;
		for (String id : scenes.keySet()) {
			if (next) {
				nextScene = id;
				break;
			}
			next = id.equals(scene);
		}

		if (nextScene == null) {
			nextScene = scenes.keySet().iterator().next();
		}

		controller.action(EditScene.class, nextScene);
	}

	private void addSceneElement() {
		controller.action(ChooseFile.class, false, new FileChooserListener() {
			@Override
			public void fileChosen(String path) {
				ModelEntity sceneElemenet = controller.getTemplates()
						.createSceneElement(path);
				controller.action(AddSceneElement.class, sceneElemenet);
			}
		});
	}

}
