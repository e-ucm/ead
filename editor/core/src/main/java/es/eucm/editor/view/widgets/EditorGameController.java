package es.eucm.editor.view.widgets;

import es.eucm.ead.engine.GameController;
import es.eucm.ead.schema.game.Game;

public class EditorGameController extends GameController {

	private Game game;

	@Override
	protected void loadGame(Game game) {
		this.game = game;
		super.loadGame(game);
	}

	public Game getGame() {
		return game;
	}
}
