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

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.model.Model;

/**
 * The current selection in the editor changed
 */
public class SelectionEvent implements ModelEvent {

	public enum Type {
		SELECTION_UPDATED, EDITION_CONTEXT_UPDATED
	}

	private Type type;

	private Model model;

	private Array<Object> editionContext;

	private Array<Object> selection;

	public SelectionEvent(Type type, Model model, Array<Object> editionContext,
			Array<Object> selection) {
		this.type = type;
		this.model = model;
		this.editionContext = editionContext;
		this.selection = selection;
	}

	/**
	 * @return type of the event. A {@link Type#EDITION_CONTEXT_UPDATED} always
	 *         implicates that selection has been updated.
	 */
	public Type getType() {
		return type;
	}

	public Array<Object> getEditionContext() {
		return editionContext;
	}

	public Array<Object> getSelection() {
		return selection;
	}

	@Override
	public Object getTarget() {
		return model;
	}
}
