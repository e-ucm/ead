/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.model;

/**
 * The only purpose of this enum is to hold String constants with the names of
 * the schema fields actions modify.
 * 
 * All Actions that modify the model through
 * {@link es.eucm.ead.editor.control.commands.FieldCommand}s should retrieve the
 * name of the field from this class.
 * 
 * {@link FieldNameForActions} should only be referenced in package
 * {@link es.eucm.ead.editor.control.actions} and also in
 * {@link es.eucm.ead.editor.model.events}, since events also need to access the
 * name of the fields
 * 
 * Whenever it is needed to edit a new field in the model, please add a new
 * constant here following the next convention: CONSTANT_NAME("actualFieldName")
 * 
 * Created by Javier Torrente on 7/03/14.
 */
public enum FieldNameForActions {

	/**
	 * Refers to {@link es.eucm.ead.schema.game.GameMetadata#title}.
	 */
	PROJECT_TITLE("title"),

	/**
	 * Refers to {@link es.eucm.ead.schema.game.Game#initialScene}
	 */
	INITIAL_SCENE("initialScene"),

	/**
	 * Refers to {@link es.eucm.ead.schema.game.GameMetadata#editScene}
	 */
	EDIT_SCENE("editScene"),

	/**
	 * Refers to {@link es.eucm.ead.schema.effects.Transform#duration}
	 */
	DURATION("duration"),

	/**
	 * Refers to {@link es.eucm.ead.schema.components.Transformation#rotation}
	 */
	ROTATION("rotation"),

	/**
	 * Refers to {@link es.eucm.ead.schema.components.Transformation#scaleX}
	 */
	SCALE_X("scaleX"),

	/**
	 * Refers to {@link es.eucm.ead.schema.components.Transformation#scaleY}
	 */
	SCALE_Y("scaleY"),

	/**
	 * Refers to {@link es.eucm.ead.schema.components.Transformation#x}
	 */
	X("x"),

	/**
	 * Refers to {@link es.eucm.ead.schema.components.Transformation#y}
	 */
	Y("y"),

	/**
	 * Refers to {@link es.eucm.ead.schema.components.Transformation#originX}
	 */
	ORIGIN_X("originX"),

	/**
	 * Refers to {@link es.eucm.ead.schema.components.Transformation#originY}
	 */
	ORIGIN_Y("originY");

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
	FieldNameForActions(String fieldName) {
		this.fieldName = fieldName;
	}

	public String toString() {
		return fieldName;
	}

}
