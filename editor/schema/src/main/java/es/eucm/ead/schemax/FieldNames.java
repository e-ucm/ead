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
package es.eucm.ead.schemax;

import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.components.Tags;

/**
 * The only purpose of this enum is to hold String constants with the names of
 * the schema fields actions modify.
 * 
 * All Actions that modify the model through {@code FieldCommand}s should
 * retrieve the name of the field from this class.
 * 
 * {@link FieldNames} should only be referenced in package
 * {@link es.eucm.ead.editor.control.actions.model} and also in
 * {@link es.eucm.ead.editor.model.events}, since events also need to access the
 * name of the fields
 * 
 * Whenever it is needed to edit a new field in the model, please add a new
 * constant here following the next convention: CONSTANT_NAME("actualFieldName")
 * 
 * Created by Javier Torrente on 7/03/14.
 */
public enum FieldNames {

	/**
	 * Refers to {@link ModelEntity#components}
	 */
	COMPONENTS("components"),

	/**
	 * Refers to {@link ModelEntity#children}
	 */
	CHILDREN("children"),

	/**
	 * Refers to {@link Documentation#name}
	 */
	NAME("name"),

	/**
	 * Refers to {@link Note#title}.
	 */
	NOTE_TITLE("title"),

	/**
	 * Refers to {@link Note#description}.
	 */
	NOTE_DESCRIPTION("description"),

	/**
	 * Refers to {@link GameData#initialScene}
	 */
	INITIAL_SCENE("initialScene"),

	/**
	 * Refers to {@link EditState#editScene}
	 */
	EDIT_SCENE("editScene"),

	/**
	 * Refers to {@link ModelEntity#rotation}
	 */
	ROTATION("rotation"),

	/**
	 * Refers to {@link ModelEntity#scaleX}
	 */
	SCALE_X("scaleX"),

	/**
	 * Refers to {@link ModelEntity#scaleY}
	 */
	SCALE_Y("scaleY"),

	/**
	 * Refers to {@link ModelEntity#x}
	 */
	X("x"),

	/**
	 * Refers to {@link ModelEntity#y}
	 */
	Y("y"),

	/**
	 * Refers to {@link ModelEntity#originX}
	 */
	ORIGIN_X("originX"),

	/**
	 * Refers to {@link ModelEntity#originY}
	 */
	ORIGIN_Y("originY"),

	/**
	 * Refers to {@link Tags#tags}
	 */
	TAGS("tags"),

	/**
	 * Refers to {@link Parent#parent}
	 */
	PARENT("parent");

	/**
	 * The name of the field. This attribute should match the exact name of the
	 * field as defined in the model, as it is used to set values by reflection
	 */
	private String fieldName;

	/**
	 * Private constructor
	 * 
	 * @param fieldName
	 *            The name of the field as specified in the model
	 */
	FieldNames(String fieldName) {
		this.fieldName = fieldName;
	}

	public String toString() {
		return fieldName;
	}

}
