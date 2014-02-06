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
import es.eucm.ead.editor.platform.Platform.StringListener;

import java.io.FileNotFoundException;

/**
 * Opens a game. Accepts one path (the path where the game is) as argument. If
 * no argument is passed along, the action uses {@link ChooseFolder} to ask user
 * to select a folder in the file system
 */
public class OpenGame extends EditorAction implements StringListener {

	public static final String NAME = "openGame";

	public OpenGame() {
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
		load(result);
	}

	private void load(String gamepath) {
		if (gamepath != null) {
			FileHandle fileHandle = controller.getEditorAssets().resolve(
					gamepath);
			if (fileHandle.exists()) {
				controller.loadGame(gamepath, false);
			} else {
				throw new EditorActionException("Invalid project folder",
						new FileNotFoundException(gamepath));
			}
		}
	}
}
