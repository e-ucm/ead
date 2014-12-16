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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import es.eucm.ead.editor.MokapDesktopPlatform;
import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.Exit;
import es.eucm.ead.editor.control.actions.editor.ForceSave;
import es.eucm.ead.editor.control.actions.editor.OpenGame;
import es.eucm.ead.editor.control.actions.editor.Redo;
import es.eucm.ead.editor.control.actions.editor.Undo;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.Resource;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schemax.entities.ResourceCategory;

import java.util.Map;

/**
 * <p>
 * This recreates the minimum classes to have a complete editor without UI, and
 * is intended as playground to test editor views individually, implementing
 * {@link #buildUI(Skin, I18N)}. In this method, any view can be added to the
 * root and it will be shown.
 * <p>
 * <p>
 * This class also provides some shortcuts for common tasks in the editor:
 * <ol>
 * <li>Ctrl+O: Open a game</li>
 * <li>Ctrl+Z / Ctrl+Y : Undo/Redo</li>
 * <li>Ctrl+S: Save</li>
 * <li>Ctrl+Q: Edit next scene</li>
 * </ol>
 * </p>
 */
public abstract class UITest implements ApplicationListener {

	protected Stage stage;

	protected Controller controller;

	protected MokapDesktopPlatform platform;

	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		stage = new Stage(new ScreenViewport());
		WidgetGroup modalContainer = new WidgetGroup();
		modalContainer.setFillParent(true);

		WidgetGroup viewContainer = new WidgetGroup();
		viewContainer.setFillParent(true);

		stage.getRoot().addActor(viewContainer);
		stage.getRoot().addActor(modalContainer);
		controller = new MokapController(platform = new MockPlatform(),
				Gdx.files, viewContainer, modalContainer);
		controller.getCommands().pushStack();
		platform.setBatch(stage.getBatch());
		Gdx.input.setInputProcessor(stage);
		ApplicationAssets assets = controller.getApplicationAssets();
		Actor actor = buildUI(assets.getSkin(), assets.getI18N());
		if (actor instanceof WidgetGroup) {
			((WidgetGroup) actor).setFillParent(true);
		}
		viewContainer.addActor(actor);

		stage.addListener(new ShortcutListener());
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		controller.act(Gdx.graphics.getDeltaTime());
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

	protected abstract Actor buildUI(Skin skin, I18N i18n);

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
					controller.action(ForceSave.class);
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
			}
			return super.keyUp(event, keycode);
		}
	}

	private void nextScene() {
		Model model = controller.getModel();

		String scene = model.getIdFor(model.getSelection().getSingle(
				Selection.SCENE));

		Map<String, Resource> scenes = model
				.getResources(ResourceCategory.SCENE);

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

}
