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

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.editor.model.events.SelectionEvent;

/**
 * A command to change the selection in a model
 */
public class SelectionCommand extends Command {

	private Model model;

	private Array<Object> newSelection;

	private Array<Object> oldSelection;

	public SelectionCommand(Model model, Array<Object> newSelection) {
		this.model = model;
		this.newSelection = newSelection;
		this.oldSelection = new Array<Object>();
	}

	@Override
	public ModelEvent doCommand() {
		oldSelection.addAll(model.getSelection());
		model.setSelection(newSelection);
		return new SelectionEvent(model, newSelection);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public ModelEvent undoCommand() {
		model.setSelection(oldSelection);
		return new SelectionEvent(model, oldSelection);
	}

	@Override
	public boolean combine(Command other) {
		if (other instanceof SelectionCommand) {
			this.newSelection = ((SelectionCommand) other).newSelection;
			return true;
		}
		return false;
	}

	@Override
	public boolean isTransparent() {
		return true;
	}
}
