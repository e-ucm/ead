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
package es.eucm.ead.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import es.eucm.ead.editor.scene.EditorSceneManager;
import es.eucm.ead.engine.Engine;

public class EditorStage extends Stage {

	private static final float FRAME_RATE = 1.0f / 33f;

	private Group ui;

	private Group scene;

	private boolean playing = false;

	private int frames = 0;

	private Button playButton, stepButton, stopButton;

	private EditorSceneManager sceneManager = (EditorSceneManager) Engine.gameController;

	public EditorStage(int width, int height, boolean keepAspectRatio) {
		super(width, height, keepAspectRatio);
		ui = new Group();
		scene = new SceneContainer();
		this.addActor(scene);
		this.addActor(ui);
		this.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent e, int keycode) {
				String shortcut = "";
				if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)
						|| Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT)) {
					shortcut += "ctrl+";
				}
				if (Gdx.input.isKeyPressed(Keys.ALT_LEFT)
						|| Gdx.input.isKeyPressed(Keys.ALT_RIGHT)) {
					shortcut += "alt+";
				}
				if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)
						|| Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
					shortcut += "shift+";
				}
				shortcut += Keys.toString(keycode).toLowerCase();
				return Editor.controller.shortcut(shortcut);
			}
		});
		initUI();
	}

	private void initUI() {
		Skin skin = Engine.assets.getSkin();
		VerticalGroup buttons = new VerticalGroup();
		buttons.setAlignment(Align.left);
		buttons.setPosition(0, 450);

		Button button = new TextButton("New game", skin);
		button.clearListeners();
		button.setSize(50, 50);
		button.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				((EditorSceneManager) Engine.gameController).newGame();
				return false;
			}
		});
		buttons.addActor(button);

		button = new TextButton("Load game", skin);
		button.clearListeners();
		button.setSize(50, 50);
		button.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				sceneManager.readGame();
				return false;
			}
		});
		buttons.addActor(button);

		button = new TextButton("Save", skin);
		button.clearListeners();
		button.setSize(50, 50);
		button.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				sceneManager.save(false);
				return false;
			}
		});
		buttons.addActor(button);

		button = new TextButton("Export", skin);
		button.clearListeners();
		button.setSize(50, 50);
		button.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				sceneManager.save(true);
				return false;
			}
		});

		buttons.addActor(button);
		playButton = new TextButton("Play", skin);
		playButton.clearListeners();
		playButton.setSize(50, 50);
		playButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				play();
				return false;
			}
		});
		buttons.addActor(playButton);
		stopButton = new TextButton("Stop", skin);
		stopButton.clearListeners();
		stopButton.setSize(50, 50);
		stopButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				stop();
				return false;
			}
		});
		buttons.addActor(stopButton);

		stepButton = new TextButton("Step", skin);
		stepButton.clearListeners();
		stepButton.setSize(50, 50);
		stepButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				step();
				return false;
			}
		});
		buttons.addActor(stepButton);

		Button addButton = new TextButton("Add Scene Element", skin);
		addButton.clearListeners();
		addButton.setSize(50, 50);
		addButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				sceneManager.addSceneElement();
				return false;
			}
		});
		buttons.addActor(addButton);
		addUi(buttons);

		addButton = new TextButton("Add Scene", skin);
		addButton.clearListeners();
		addButton.setSize(50, 50);
		addButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				/*
				 * SceneElement sceneElement = ((EditorEventListener)
				 * Editor.engine .getEventListener()).getElement();
				 * sceneManager.newScene(sceneElement);
				 */
				return false;
			}
		});
		buttons.addActor(addButton);
		addUi(buttons);

	}

	public void addUi(Actor a) {
		ui.addActor(a);
	}

	public void setScene(Actor s) {
		scene.clear();
		scene.addActor(s);
	}

	public void play() {
		playing = !playing;
		playButton.setChecked(playing);
		frames = 0;
	}

	public void step() {
		frames++;
		Gdx.graphics.requestRendering();
		stepButton.setChecked(false);
	}

	public void stop() {
		frames = 0;
		playing = false;
		stopButton.setChecked(false);
		playButton.setChecked(false);
		Editor.gameController.reloadCurrentScene();
	}

	public class SceneContainer extends Group {
		@Override
		public void act(float delta) {
			delta = Math.min(FRAME_RATE, (playing || frames > 0) ? delta : 0);
			if (frames > 0) {
				frames--;
			}
			super.act(delta);
		}
	}

	public boolean isPlaying() {
		return playing;
	}

}
