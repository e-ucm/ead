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
import com.badlogic.gdx.utils.SnapshotArray;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.Selection.Context;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.MultipleEvent;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.model.events.SelectionEvent.Type;

/**
 * A command to change the selection in a model
 */
public class SelectionCommand extends Command {

	private Model model;

	private String parentContextId;

	private String contextId;

	private Object[] selection;

	private Array<Context> contextsRemoved;

	private Context oldContext;

	private boolean added;

	private SnapshotArray<Object> arraySelection;

	public SelectionCommand(Model model, String parentContextId,
			String contextId, Object... selection) {
		this.model = model;
		this.parentContextId = parentContextId;
		this.contextId = contextId;
		this.selection = selection;
	}

	@Override
	public MultipleEvent doCommand() {
		Selection selection = model.getSelection();
		Context currentContext = selection.getCurrentContext();

		if (currentContext != null) {
			oldContext = new Context(currentContext.getParentId(),
					currentContext.getId());
			oldContext.getSelection().addAll(currentContext.getSelection());
		}

		added = selection.getContext(contextId) == null;

		if (parentContextId == null) {
			contextsRemoved = selection.setRootContext(contextId,
					this.selection);
		} else {
			contextsRemoved = selection.set(parentContextId, contextId,
					this.selection);
		}

		MultipleEvent multipleEvent = new MultipleEvent();

		if (oldContext != null
				&& oldContext.equals(selection.getCurrentContext())) {
			return multipleEvent;
		}

		for (Context context : contextsRemoved) {
			multipleEvent.addEvent(new SelectionEvent(model, Type.REMOVED,
					context.getParentId(), context.getId(), context
							.getSelection()));
		}

		arraySelection = new SnapshotArray<Object>(this.selection);
		if (added) {
			multipleEvent.addEvent(new SelectionEvent(model, Type.ADDED,
					parentContextId, contextId, arraySelection));
		}
		multipleEvent.addEvent(new SelectionEvent(model, Type.FOCUSED,
				parentContextId, contextId, arraySelection));
		return multipleEvent;
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public MultipleEvent undoCommand() {
		Selection selection = model.getSelection();
		MultipleEvent multipleEvent = new MultipleEvent();

		if (added) {
			selection.remove(contextId);
			multipleEvent.addEvent(new SelectionEvent(model, Type.REMOVED,
					parentContextId, contextId, arraySelection));
		}

		for (Context context : contextsRemoved) {
			selection.set(context.getParentId(), context.getId(),
					context.getSelection());
			multipleEvent.addEvent(new SelectionEvent(model, Type.ADDED,
					context.getParentId(), context.getId(), context
							.getSelection()));
		}

		if (oldContext != null) {
			Object[] oldSelection = new Object[oldContext.getSelection().size];
			int i = 0;
			for (Object o : oldContext.getSelection()) {
				oldSelection[i++] = o;
			}
			selection.set(oldContext.getParentId(), oldContext.getId(),
					oldSelection);
			multipleEvent.addEvent(new SelectionEvent(model, Type.FOCUSED,
					oldContext.getParentId(), oldContext.getId(), oldContext
							.getSelection()));
		}
		return multipleEvent;
	}

	@Override
	public boolean combine(Command other) {
		return false;
	}

	@Override
	public boolean isTransparent() {
		return true;
	}
}
