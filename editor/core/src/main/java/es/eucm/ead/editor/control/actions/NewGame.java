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
package es.eucm.ead.editor.control.actions;

import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.model.Project;
import es.eucm.ead.editor.platform.Platform.StringListener;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.game.Game;

public class NewGame extends EditorAction implements StringListener {

	public static final String NAME = "newGame";

	public NewGame() {
		super(NAME);
	}

	@Override
	public void perform(Object... args) {
		if (args.length == 0) {
			controller.action(ChooseFolder.NAME, this);
		} else {
			string(args[0].toString());
		}
	}

	@Override
	public void string(String result) {
		Assets assets = controller.getEditorAssets();
		I18N i18N = assets.getI18N();
		FileHandle projectFile = assets.resolve(result).child(
				i18N.m("project.untitled"));
		projectFile.mkdirs();

		Project project = new Project();
		Game game = new Game();
		game.setTitle(i18N.m("game.untitled"));
		game.setInitialScene("initial");
		Scene scene = new Scene();

		controller.getProjectAssets().setLoadingPath(result, false);
		controller.getModel().clear();
		controller.getModel().setGame(game);
		controller.getModel().addScene("initial", scene);
		controller.getModel().setProject(project);
		controller.saveAll();

	}
}
