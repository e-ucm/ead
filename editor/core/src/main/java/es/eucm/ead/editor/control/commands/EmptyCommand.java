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
package es.eucm.ead.editor.control.commands;

import es.eucm.ead.editor.model.events.ModelEvent;

/**
 * Command returned for those actions that must return a command but produces no
 * action over the model.
 */
public class EmptyCommand extends Command {

	private boolean modifiesResource;

	public EmptyCommand() {
		this(false);
	}

	public EmptyCommand(boolean modifiesResource) {
		this.modifiesResource = modifiesResource;
	}

	public void setModifiesResource(boolean modifiesResource) {
		this.modifiesResource = modifiesResource;
	}

	@Override
	public ModelEvent doCommand() {
		return null;
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public ModelEvent undoCommand() {
		return null;
	}

	@Override
	public boolean modifiesResource() {
		return modifiesResource;
	}
}
