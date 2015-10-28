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
package es.eucm.ead.engine.variables;

/**
 * Just a convenient place to store names for reserved variables, like "_this",
 * or "_time" Created by jtorrente on 28/10/2015.
 */
public class ReservedVariableNames {
	/**
	 * Prefix for variables are created and managed by the engine. This is a
	 * naming convention, intended to avoid name clashes with user-defined
	 * variables.
	 */
	public static final String RESERVED_VAR_PREFIX = "_";

	/**
	 * Current game language. See @see es.eucm.ead.engine.I18N for details on
	 * possible values
	 */
	public static final String LANGUAGE_VAR = RESERVED_VAR_PREFIX + "lang";

	/**
	 * Reserved keyword for the owner entity. It is a local variable (changes
	 * over time depending on what entity is being processed).
	 */
	public static final String THIS_VAR = RESERVED_VAR_PREFIX + "this";

	/**
	 * Reserved keyword for other entity involved in any expression or condition
	 * evaluation. It is a local variable.
	 */
	public static final String RESERVED_ENTITY_VAR = RESERVED_VAR_PREFIX
			+ "target";

	/**
	 * Reserved keyword pointing to the newest entity (the last entity added to
	 * game). It is a global variable.
	 */
	public static final String RESERVED_NEWEST_ENTITY_VAR = RESERVED_VAR_PREFIX
			+ "newest";

	/**
	 * Reserved variable that is set with the viewport width (defined in
	 * game.json)
	 */
	public static final String RESERVED_VIEWPORT_WIDTH_VAR = RESERVED_VAR_PREFIX
			+ "gameWidth";

	/**
	 * Reserved variable that is set with the viewport height (defined in
	 * game.json)
	 */
	public static final String RESERVED_VIEWPORT_HEIGHT_VAR = RESERVED_VAR_PREFIX
			+ "gameHeight";

	/**
	 * Counts in seconds the elapsed time from the moment we've started playing
	 */
	public static final String TIME = RESERVED_VAR_PREFIX + "time";

	/**
	 * The width in pixels of the display surface
	 */
	public static final String FRAME_WIDTH = RESERVED_VAR_PREFIX + "width";

	/**
	 * The height in pixels of the display surface
	 */
	public static final String FRAME_HEIGHT = RESERVED_VAR_PREFIX + "height";

	/**
	 * Float value between 0 and 1 that determines volume for sounds
	 */
	public static final String EFFECTS_VOLUME = RESERVED_VAR_PREFIX
			+ "effects_volume";

}
