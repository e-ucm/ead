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

package es.eucm.ead.schema.editor.components;

import javax.annotation.Generated;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Component holding the parent of the entity. This facilitates finding the
 * place an object has in the hierarchy. Useful for dealing with search and
 * selection.
 * 
 */
@Generated("org.jsonschema2pojo")
public class Parent extends ModelComponent {

	/**
	 * Basic unit for interactive elements in eAdventure. An entity contain a
	 * set of components defining its behavior and appearance.
	 * 
	 */
	private ModelEntity parent;

	/**
	 * Basic unit for interactive elements in eAdventure. An entity contain a
	 * set of components defining its behavior and appearance.
	 * 
	 */
	public ModelEntity getParent() {
		return parent;
	}

	/**
	 * Basic unit for interactive elements in eAdventure. An entity contain a
	 * set of components defining its behavior and appearance.
	 * 
	 */
	public void setParent(ModelEntity parent) {
		this.parent = parent;
	}

}
