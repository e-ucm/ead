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

import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ModelEntityCategory;

/**
 * A {@link ModelEntity} has been added/removed from the {@link Model}
 */
public class EntityEvent implements ModelEvent {

	public enum Type {
		ADDED, REMOVED;
	}

	private Type type;

	private Model model;

	private String id;

	private ModelEntity modelEntity;

	private ModelEntityCategory category;

	public EntityEvent(Type type, Model model, String id,
			ModelEntity modelEntity, ModelEntityCategory category) {
		this.type = type;
		this.model = model;
		this.id = id;
		this.modelEntity = modelEntity;
		this.category = category;
	}

	public Type getType() {
		return type;
	}

	public Model getModel() {
		return model;
	}

	public String getId() {
		return id;
	}

	public ModelEntity getModelEntity() {
		return modelEntity;
	}

	public ModelEntityCategory getCategory() {
		return category;
	}

	@Override
	public Model getTarget() {
		return model;
	}
}
