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
package es.eucm.ead.editor.view.widgets.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.AddSceneElementFromResource;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.listeners.ActionOnDownListener;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.GameLayers;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.schema.components.game.GameData;
import es.eucm.ead.schema.entities.ModelEntity;

public class EngineView extends AbstractWidget {

	private Controller controller;

	private GameLoop gameLoop;

	private LinearLayout tools;

	protected GameLayers sceneView;

	protected ModelEntity game;

	public EngineView(Controller controller) {
		this.controller = controller;

		GameLayers gameLayers = new GameLayers();
		sceneView = gameLayers;
		addActor(gameLayers);
		addTools();
		gameLoop = new GameLoop();
	}

	protected void addTools() {
		tools = new LinearLayout(true);
		addActor(tools);
		Skin skin = controller.getApplicationAssets().getSkin();
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
		textButton.addListener(new ActionOnDownListener(controller,
				AddSceneElementFromResource.class));
		tools.addActor(textButton);

	}

	@Override
	public void act(float delta) {
		super.act(delta);
		gameLoop.update(delta);
	}

	@Override
	public float getPrefWidth() {
		return Gdx.graphics.getWidth();
	}

	@Override
	public float getPrefHeight() {
		return Gdx.graphics.getHeight();
	}

	public void reloadGame(ModelEntity game) {
		this.game = game;
		layout();
		fit();
	}

	public void layout() {
		if (game != null) {
			GameData gameData = Model.getComponent(game, GameData.class);
			sceneView.setSize(gameData.getWidth(), gameData.getHeight());
			float width = tools.getPrefWidth();
			float height = tools.getPrefHeight();
			setBounds(tools, 0, 0, width, height);
			fit();
		}
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
