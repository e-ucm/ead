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
package es.eucm.ead.editor.view.widgets.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import es.eucm.ead.editor.assets.ProjectAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.AddSceneElement;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.LinearLayout;
import es.eucm.ead.editor.view.widgets.engine.wrappers.EditorGameLoop;
import es.eucm.ead.editor.view.widgets.engine.wrappers.SceneElementEditorObject;
import es.eucm.ead.editor.view.widgets.engine.wrappers.EditorSceneView;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.game.Game;

public class EngineView extends WidgetGroup {

	private Controller controller;

	private EditorSceneView sceneView;

	private EditorGameLoop gameLoop;

	private LinearLayout tools;

	public EngineView(Controller controller) {
		this.controller = controller;

		ProjectAssets projectAssets = controller.getProjectAssets();
		projectAssets.bind("sceneelement", SceneElement.class,
				SceneElementEditorObject.class);
		sceneView = new EditorSceneView(controller.getModel(), projectAssets);
		gameLoop = new EditorGameLoop(controller, controller.getEditorAssets()
				.getSkin(), sceneView);

		addActor(sceneView);
		addTools();
	}

	private void addTools() {
		tools = new LinearLayout(true);
		addActor(tools);
		Skin skin = controller.getEditorAssets().getSkin();
		TextButton textButton = new TextButton("Play", skin);
		textButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				gameLoop.setPlaying(!gameLoop.isPlaying());
				return false;
			}
		});

		tools.addActor(textButton);

		textButton = new TextButton("Add", skin);
		textButton.addListener(new ActionOnClickListener(controller,
				AddSceneElement.NAME));
		tools.addActor(textButton);

	}

	@Override
	public void act(float delta) {
		super.act(delta);
		gameLoop.act(delta);
	}

	@Override
	public float getPrefWidth() {
		return Gdx.graphics.getWidth();
	}

	@Override
	public float getPrefHeight() {
		return Gdx.graphics.getHeight();
	}

	public void reloadGame(Game game) {
		gameLoop.startSubgame(null, null);
		sceneView.setCameraSize(game.getWidth(), game.getHeight());
		layout();
		fit();
	}

	public void layout() {
		sceneView.setSize(sceneView.getPrefWidth(), sceneView.getPrefHeight());
		float width = tools.getPrefWidth();
		float height = tools.getPrefHeight();
		tools.setBounds(0, 0, width, height);
	}

	public void fit() {
		float scaleX = getWidth() / sceneView.getWidth();
		float scaleY = getHeight() / sceneView.getHeight();
		float scale = Math.min(Math.min(scaleX, scaleY), 1.0f);

		sceneView.setScale(scale);

		float xOffset = (getWidth() - sceneView.getWidth() * scale) / 2;
		float yOffset = (getHeight() - sceneView.getHeight() * scale) / 2;

		sceneView.setPosition(xOffset, yOffset);
	}

}
