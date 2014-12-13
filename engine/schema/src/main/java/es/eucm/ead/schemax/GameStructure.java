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

/**
 * This interface describes the internal structure of game files and projects.
 * It provides constants for accessing the subfolders where scenes, images and
 * subgames are stored, for example.
 * 
 * Created by Javier Torrente on 3/04/14.
 */
public interface GameStructure {

	public static final String IMAGES_FOLDER = "images/";

	public static final String VIDEOS_FOLDER = "videos/";

	public static final String SOUNDS_FOLDER = "sounds/";

	public static final String GAME_FILE = "game.json";

	public static final String GAME_DEBUG = "game_debug.json";

	public static final String SCENES_PATH = "scenes/";

	public static final String HUDS_PATH = "huds/";

	public static final String SUBGAMES_PATH = "subgames/";

	public static final String ANIMATION_PATH = "anim/";

	public static final String METADATA_PATH = ".metadata/";

	/**
	 * Internal folder where the game is stored when it is exported as a Jar.
	 * This constant should be the same than the one defined in EngineJarGame,
	 * the class that launches jar games.
	 * 
	 * All the game contents (e.g. "scenes/", "game.json") should be placed
	 * under this folder in the jar file generated.
	 */
	public static final String JAR_GAME_FOLDER = "assets/";

	public static final String THUMBNAILS_PATH = METADATA_PATH + "thumbnails/";
}
