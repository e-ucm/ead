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
package es.eucm.ead.editor.control.views;

import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.EnginePlayer;
import es.eucm.ead.schemax.GameStructure;

/**
 * View that shows the engine in debug mode
 */
public class DebugView implements ViewBuilder {

	private Controller controller;

	private EnginePlayer enginePlayer;

	@Override
	public void initialize(Controller controller) {
		this.controller = controller;
		enginePlayer = new EnginePlayer(controller.getEngine().getGameLoop());
	}

	@Override
	public Actor getView(Object... args) {
		// Disable undo/redo
		controller.action(SetSelection.class, null, Selection.DEBUG);
		controller.getCommands().pushStack();
		controller
				.getEngine()
				.getGameLoader()
				.loaded(GameStructure.GAME_FILE,
						controller.getModel().getGame());
		controller.getEngine().setGameView(enginePlayer);
		controller.getEngine().play();
		return enginePlayer;
	}

	@Override
	public void release(Controller controller) {
		controller.getCommands().popStack(false);
		controller.getEngine().stop();
		controller.getEngine().setGameView(null);
	}
}
