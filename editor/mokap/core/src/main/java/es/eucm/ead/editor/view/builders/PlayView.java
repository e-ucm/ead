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
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ShowToast;
import es.eucm.ead.editor.control.engine.Engine;
import es.eucm.ead.editor.exporter.Exporter;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.widgets.EnginePlayer;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * View that shows the engine in debug mode
 */
public class PlayView implements ViewBuilder {

	private Controller controller;

	private EnginePlayer enginePlayer;

	private Stack window;

	@Override
	public void initialize(final Controller controller) {
		this.controller = controller;
		enginePlayer = new EnginePlayer(controller.getEngine().getGameLoop());
		enginePlayer.setFillParent(true);
		window = new Stack();
		window.setFillParent(true);
		window.add(enginePlayer);
	}

	@Override
	public Actor getView(Object... args) {
		controller.action(ShowToast.class, controller.getApplicationAssets()
				.getI18N().m("play.back"));

		String currentSceneId = Q.getComponent(controller.getModel().getGame(),
				GameData.class).getInitialScene();
		ModelEntity game = controller.getModel().getGame();
		Exporter.createInitComponent(game, currentSceneId);
		controller.getEditorGameAssets()
				.toJsonPath(game, GameAssets.GAME_DEBUG);
		String gameString = GameAssets.GAME_DEBUG;

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
