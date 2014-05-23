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

import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.RootEntityEvent;
import es.eucm.ead.editor.model.events.RootEntityEvent.Type;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ModelEntityCategory;

/**
 * Commands to add/remove entities pending directly from the {@link Model}
 */
public class RootEntityCommand extends Command {

	private Model model;

	private String id;

	private ModelEntity modelEntity;

	private ModelEntityCategory category;

	private boolean add;

	public RootEntityCommand(Model model, String id, ModelEntity modelEntity,
			ModelEntityCategory category, boolean add) {
		this.model = model;
		this.id = id;
		this.modelEntity = modelEntity;
		this.category = category;
		this.add = add;
	}

	@Override
	public ModelEvent doCommand() {
		if (add) {
			model.putEntity(id, category, modelEntity);
		} else {
			modelEntity = model.removeEntity(id, category);
		}
		return new RootEntityEvent(add ? Type.ADDED : Type.REMOVED, model, id,
				modelEntity, category);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public ModelEvent undoCommand() {
		if (add) {
			model.removeEntity(id, category);
		} else {
			model.putEntity(id, category, modelEntity);
		}
		return new RootEntityEvent(add ? Type.REMOVED : Type.ADDED, model, id,
				modelEntity, category);
	}

	@Override
	public boolean combine(Command other) {
		return false;
	}

	public static class AddRootEntityCommand extends RootEntityCommand {

		public AddRootEntityCommand(Model model, String id,
				ModelEntity modelEntity, ModelEntityCategory category) {
			super(model, id, modelEntity, category, true);
		}
	}

	public static class RemoveRootEntityCommand extends RootEntityCommand {

		public RemoveRootEntityCommand(Model model, String id,
				ModelEntity modelEntity, ModelEntityCategory category) {
			super(model, id, modelEntity, category, false);
		}
	}
}
