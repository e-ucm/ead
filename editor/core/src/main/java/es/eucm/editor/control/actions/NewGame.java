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
package es.eucm.editor.control.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.editor.Editor;
import es.eucm.editor.model.DependencyNode;
import es.eucm.editor.model.EditorModel;
import es.eucm.editor.view.dialogs.DialogListener;
import es.eucm.editor.view.options.AbstractOption;
import es.eucm.editor.view.options.FileOption;
import es.eucm.editor.view.options.IntegerOption;
import es.eucm.editor.view.options.OptionsPanel;
import es.eucm.editor.view.options.TextOption;
import es.eucm.ead.schema.game.Game;

/**
 * Action representing the creation of a new game. This action shows a dialog
 * with the options of the new game, and once the user presses OK, the folder
 * structure for the game is created
 */
public class NewGame extends EditorAction implements DialogListener {

	private Game defaultGame;

	private String defaultGameFolder;

	public NewGame() {
		super(null);
		defaultGame = new Game();
		defaultGame.setTitle(Editor.i18n.m("game.title.default"));
		defaultGame.setWidth(800);
		defaultGame.setHeight(600);
		defaultGame.setInitialScene("scene1");

		defaultGameFolder = Gdx.files.external("eadgames").file()
				.getAbsolutePath();
	}

	public void setDefaultGame(Game defaultGame) {
		this.defaultGame = defaultGame;
	}

	public void setDefaultGameFolder(String defaultGameFolder) {
		this.defaultGameFolder = defaultGameFolder;
	}

	public Game getDefaultGame() {
		return defaultGame;
	}

	public String getDefaultGameFolder() {
		return defaultGameFolder;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void perform(Object... args) {
	}

	public void perform() {

		DependencyNode dependencyNode = new DependencyNode(EditorModel.gameId,
				defaultGame);

		OptionsPanel op = new OptionsPanel(
				OptionsPanel.LayoutPolicy.VerticalBlocks);

		AbstractOption option = new TextOption("game.title",
				"game.title.tooltip", dependencyNode)
				.from(defaultGame, "title");
		op.add(option);
		option = new IntegerOption("game.width", "game.width.tooltip",
				dependencyNode).from(defaultGame, "width");
		op.add(option);
		option = new IntegerOption("game.height", "game.height.tooltip",
				dependencyNode).from(defaultGame, "height");
		op.add(option);
		option = new FileOption("game.folder", "game.folder.tooltip")
				.directory(true).from(this, "defaultGameFolder");
		op.add(option);

		Editor.viewController.showOptionsDialog(op, this, "general.ok",
				"general.cancel");
	}

	@Override
	public void button(String buttonKey) {
		if ("general.ok".equals(buttonKey)) {
			createGame();
		}
	}

	/**
	 * Creates the files for the game
	 */
	private void createGame() {
		if (!defaultGameFolder.endsWith("/")) {
			defaultGameFolder += "/";
		}

		FileHandle gamePath = Gdx.files.absolute(defaultGameFolder
				+ defaultGame.getTitle());

		gamePath.mkdirs();
		if (!gamePath.exists()) {
			Editor.viewController.showError("game.error.path", this);
			return;
		}

		// Engine.schemaIO.toJson(defaultGame, gamePath.child("game.json"));
		FileHandle scenesFolder = gamePath.child("scenes");
		FileHandle scene1 = scenesFolder.child("scene1.json");
		scene1.writeString("{}", false);

		Editor.platform.setTitle(Editor.i18n.m("editor.title.game",
				defaultGame.getTitle()));

		DependencyNode node = new DependencyNode(EditorModel.gameId,
				defaultGame);
	}

}
