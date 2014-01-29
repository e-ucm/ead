package es.eucm.ead.editor.view.widgets.scenes;

import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.schema.game.Game;

public class EditorGameLoop extends GameLoop {

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
