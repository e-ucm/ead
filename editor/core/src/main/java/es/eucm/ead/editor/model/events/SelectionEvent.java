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
package es.eucm.ead.editor.model.events;

import com.badlogic.gdx.utils.SnapshotArray;
import es.eucm.ead.editor.model.Model;

/**
 * The current selection in the editor changed
 */
public class SelectionEvent implements ModelEvent {

	public enum Type {
		ADDED, REMOVED, FOCUSED
	}

	private Model model;

	private Type type;

	private String parentContextId;

	private String contextId;

	private SnapshotArray<Object> selection;

	public SelectionEvent(Model model, Type type, String parentContextId,
			String contextId, SnapshotArray<Object> selection) {
		this.model = model;
		this.type = type;
		this.parentContextId = parentContextId;
		this.contextId = contextId;
		this.selection = selection;
	}

	public Type getType() {
		return type;
	}

	public String getParentContextId() {
		return parentContextId;
	}

	public String getContextId() {
		return contextId;
	}

	public SnapshotArray<Object> getSelection() {
		return selection;
	}

	@Override
	public Object getTarget() {
		return model;
	}
}
