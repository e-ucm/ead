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
package es.eucm.ead.editor.control.commands;

import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.engine.Engine;
import es.eucm.ead.editor.control.Command;
import es.eucm.ead.editor.model.EditorModel;
import es.eucm.ead.editor.model.ModelEvent;
import es.eucm.ead.schema.game.Game;

public class NewProjectCommand extends Command {

	private Game game;

	private FileHandle currentPath;

	public NewProjectCommand(Game game, FileHandle currentPath) {
		this.game = game;
		this.currentPath = currentPath;
	}

	@Override
	public ModelEvent performCommand(EditorModel em) {
		Engine.schemaIO.toJson(game, currentPath.child("game.json"));
		currentPath.child("scenes").mkdirs();
		/*
		 * Engine.engine.setLoadingPath(currentPath.file().getAbsolutePath(),
		 * false);
		 */
		return null;
	}

	@Override
	public boolean canUndo() {
		return false;
	}

	@Override
	public ModelEvent undoCommand(EditorModel em) {
		return null;
	}

	@Override
	public boolean canRedo() {
		return false;
	}

	@Override
	public ModelEvent redoCommand(EditorModel em) {
		return null;
	}

	@Override
	public boolean combine(Command other) {
		return false; // To change body of implemented methods use File |
		// Settings | File Templates.
	}
}
