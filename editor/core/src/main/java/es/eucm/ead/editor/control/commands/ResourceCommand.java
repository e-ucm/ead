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
import es.eucm.ead.editor.model.events.ResourceEvent;
import es.eucm.ead.editor.model.events.ResourceEvent.Type;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * Commands to add/remove entities pending directly from the {@link Model}
 */
public class ResourceCommand extends Command {

	private Model model;

	private String id;

	private ModelEntity modelEntity;

	private ResourceCategory category;

	private boolean add;

	public ResourceCommand(Model model, String id, ModelEntity modelEntity,
			ResourceCategory category, boolean add) {
		this.model = model;
		this.id = id;
		this.modelEntity = modelEntity;
		this.category = category;
		this.add = add;
	}

	@Override
	public ModelEvent doCommand() {
		if (add) {
			model.putResource(id, category, modelEntity);
		} else {
			modelEntity = (ModelEntity) model.removeResource(id, category)
					.getObject();
		}
		return new ResourceEvent(add ? Type.ADDED : Type.REMOVED, model, id,
				modelEntity, category);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public ModelEvent undoCommand() {
		if (add) {
			model.removeResource(id, category);
		} else {
			model.putResource(id, category, modelEntity);
		}
		return new ResourceEvent(add ? Type.REMOVED : Type.ADDED, model, id,
				modelEntity, category);
	}

	@Override
	public boolean combine(Command other) {
		return false;
	}

	public static class AddResourceCommand extends ResourceCommand {

		public AddResourceCommand(Model model, String id,
				ModelEntity modelEntity, ResourceCategory category) {
			super(model, id, modelEntity, category, true);
		}
	}

	public static class RemoveResourceCommand extends ResourceCommand {

		public RemoveResourceCommand(Model model, String id,
				ModelEntity modelEntity, ResourceCategory category) {
			super(model, id, modelEntity, category, false);
		}
	}
}
