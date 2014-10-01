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

import es.eucm.ead.schema.entities.ModelEntity;

/**
 * The only purpose of this class is to hold String constants with the names of
 * the schema fields actions modify.
 * 
 * Created by Javier Torrente on 7/03/14.
 */
public class FieldName {

	/**
	 * Refers to {@link ModelEntity#components}
	 */
	public static final String COMPONENTS = "components",

	/**
	 * Refers to {@link ModelEntity#children}
	 */
	CHILDREN = "children",

	/**
	 * Refers to {@link Documentation#name}
	 */
	NAME = "name",

	/**
	 * Refers to {@link Documentation#description}
	 */
	DESCRIPTION = "description",

	/**
	 * Refers to {@link GameData#initialScene}
	 */
	INITIAL_SCENE = "initialScene",

	/**
	 * Refers to {@link ModelEntity#rotation}
	 */
	ROTATION = "rotation",

	/**
	 * Refers to {@link ModelEntity#scaleX}
	 */
	SCALE_X = "scaleX",

	/**
	 * Refers to {@link ModelEntity#scaleY}
	 */
	SCALE_Y = "scaleY",

	/**
	 * Refers to {@link ModelEntity#x}
	 */
	X = "x",

	/**
	 * Refers to {@link ModelEntity#y}
	 */
	Y = "y",

	/**
	 * Refers to {@link ModelEntity#originX}
	 */
	ORIGIN_X = "originX",

	/**
	 * Refers to {@link ModelEntity#originY}
	 */
	ORIGIN_Y = "originY",

	/**
	 * Refers to {@link Tags#tags}
	 */
	TAGS = "tags",

	/**
	 * Refers to {@link Parent#parent}
	 */
	PARENT = "parent",

	TIME = "time",

	/**
	 * Refers to {@link es.eucm.ead.schema.renderers.Frames#sequence}
	 */
	SEQUENCE = "sequence",

	/**
	 * Refers to {@link es.eucm.ead.schema.effects.Effect#target}
	 */
	TARGET = "target",

	/**
	 * Refers to {@link es.eucm.ead.schema.data.Parameter#value}
	 */
	VALUE = "value",

	FOLDER = "folder",

	/**
	 * Refers to {@link es.eucm.ead.schema.editor.data.Cell#row}
	 */
	ROW = "row",

	/**
	 * Refers to {@link es.eucm.ead.schema.editor.data.Cell#column}
	 */
	COLUMN = "column",

	/**
	 * Refers to {@link es.eucm.ead.schema.editor.components.SceneMap#rows}
	 */
	ROWS = "rows",

	/**
	 * Refers to {@link es.eucm.ead.schema.editor.components.SceneMap#columns}
	 */
	COLUMNS = "columns",

	/**
	 * Refers to {@link es.eucm.ead.schema.components.controls.Label#text}
	 */
	TEXT = "text",

	/**
	 * Refers to {@link es.eucm.ead.schema.effects.AnimationEffect#duration}
	 */
	DURATION = "duration",

	/**
	 * Refers to {@link com.badlogic.gdx.scenes.scene2d.Actor#touchable}
	 */
	TOUCHABLE = "touchable",

	/**
	 * Refers to {@link es.eucm.ead.schema.editor.components.LockProperty#lock}
	 */
	LOCK = "lock",

	/**
	 * Refers to
	 * {@link es.eucm.ead.schema.editor.components.InvisibilityProperty#invisible}
	 */
	INVISIBLE = "invisible",

	/**
	 * Refers to {@link com.badlogic.gdx.scenes.scene2d.Actor#visible}
	 */
	VISIBLE = "visible";

}
