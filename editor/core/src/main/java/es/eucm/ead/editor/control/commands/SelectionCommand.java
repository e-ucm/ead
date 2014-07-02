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
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.model.events.SelectionEvent.Type;

/**
 * A command to change the selection in a model
 */
public class SelectionCommand extends Command {

	private static final Array<Object> NO_SELECTION = new Array<Object>();

	private Type type;

	private Model model;

	private Array<Object> newEditionContext;

	private Array<Object> newSelection;

	private Array<Object> oldEditionContext;

	private Array<Object> oldSelection;

	private SelectionCommand(Type type, Model model,
			Array<Object> newEditionContext, Array<Object> newSelection) {
		this.type = type;
		this.model = model;
		this.newEditionContext = newEditionContext;
		this.newSelection = newSelection;
		this.oldSelection = new Array<Object>();
	}

	@Override
	public SelectionEvent doCommand() {
		oldSelection.addAll(model.getSelection());
		if (newEditionContext != null) {
			oldEditionContext = model.getEditionContext();
			model.setEditionContext(newEditionContext);
		}
		model.setSelection(newSelection);
		return new SelectionEvent(type, model, newEditionContext, newSelection);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public SelectionEvent undoCommand() {
		model.setSelection(oldSelection);
		if (oldEditionContext != null) {
			model.setEditionContext(oldEditionContext);
		}
		return new SelectionEvent(type, model, oldEditionContext, oldSelection);
	}

	@Override
	public boolean combine(Command other) {
		if (other instanceof SelectionCommand) {
			this.type = ((SelectionCommand) other).type == Type.EDITION_CONTEXT_UPDATED ? Type.EDITION_CONTEXT_UPDATED
					: this.type;
			this.newEditionContext = ((SelectionCommand) other).newEditionContext;
			this.newSelection = ((SelectionCommand) other).newSelection;
			return true;
		}
		return false;
	}

	@Override
	public boolean isTransparent() {
		return true;
	}

	/**
	 * Command to set the current selection
	 */
	public static class SetSelectionCommand extends SelectionCommand {

		public SetSelectionCommand(Model model, Array<Object> newSelection) {
			super(Type.SELECTION_UPDATED, model, null, newSelection);
		}
	}

	/**
	 * Command to set the current edition context. Clears the the current
	 * selection
	 */
	public static class SetEditionContextCommand extends SelectionCommand {

		public SetEditionContextCommand(Model model,
				Array<Object> editionContext) {
			super(Type.EDITION_CONTEXT_UPDATED, model, editionContext,
					NO_SELECTION);
		}
	}
}
