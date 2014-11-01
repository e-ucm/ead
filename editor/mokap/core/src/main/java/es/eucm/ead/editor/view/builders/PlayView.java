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
package es.eucm.ead.editor.view.builders;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapViews;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.ForceSave;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.control.engine.Engine;
import es.eucm.ead.editor.exporter.Exporter;
import es.eucm.ead.editor.view.widgets.EnginePlayer;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * View that shows the engine in debug mode
 */
public class PlayView implements ViewBuilder {

	private static final String IC_GO_BACK = "backWhite";

	private Controller controller;

	private EnginePlayer enginePlayer;
	private Stack window;

	@Override
	public void initialize(final Controller controller) {
		this.controller = controller;
		enginePlayer = new EnginePlayer(controller.getEngine().getGameLoop());
		enginePlayer.setFillParent(true);

		Skin skin = controller.getApplicationAssets().getSkin();
		Button back = new IconButton(IC_GO_BACK, 0f, skin, "inverted") {
			@Override
			public void layout() {
				super.layout();
				setBounds(0f, getParent().getHeight() - getPrefHeight(),
						getPrefWidth(), getPrefHeight());
			}
		};
		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((MokapViews) controller.getViews()).onBackPressed();
			}
		});

		window = new Stack();
		window.setFillParent(true);
		window.add(enginePlayer);
		window.add(back);
	}

	@Override
	public Actor getView(Object... args) {

		controller.action(SetSelection.class, Selection.EDITED_GROUP,
				Selection.SCENE_ELEMENT);
		controller.action(ForceSave.class);
		String gameString = GameAssets.GAME_FILE;

		if (args.length == 1 && args[0] instanceof String) {
			String play = (String) args[0];
			if (play.equals(GameAssets.GAME_DEBUG)) {
				ModelEntity currentScene = (ModelEntity) controller.getModel()
						.getSelection().getSingle(Selection.SCENE);
				String currentSceneId = controller.getModel().getIdFor(
						currentScene);
				ModelEntity game = controller.getModel().getGame();
				Exporter.createInitComponent(game, currentSceneId);
				controller.getEditorGameAssets().toJsonPath(game,
						GameAssets.GAME_DEBUG);
				gameString = GameAssets.GAME_DEBUG;
			}
		}

		Engine engine = controller.getEngine();
		engine.getGameLoader().loaded(gameString,
				controller.getModel().getGame());
		engine.setGameView(enginePlayer);
		engine.play();
		return window;
	}

	@Override
	public void release(Controller controller) {
		Engine engine = controller.getEngine();
		engine.stop();
		engine.setGameView(null);
	}
}
