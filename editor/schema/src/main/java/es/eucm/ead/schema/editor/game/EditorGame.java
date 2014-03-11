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

package es.eucm.ead.schema.editor.game;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.game.Game;

/**
 * Metadata for a game schema object
 * 
 */
@Generated("org.jsonschema2pojo")
public class EditorGame extends Game {

	/**
	 * The last scene edited
	 * 
	 */
	private String editScene;
	/**
	 * A simple note for annotating stuff on editor schema.
	 * 
	 */
	private Note notes;
	/**
	 * Holds the list of scenes in the order they have to be shown in the
	 * editor. For each scene its id (e.g. scene0) is stored in this array. This
	 * element has no effect whatsoever in the actual order in which scenes are
	 * displayed in the game.
	 * 
	 */
	private List<String> sceneorder = new ArrayList<String>();

	/**
	 * The last scene edited
	 * 
	 */
	public String getEditScene() {
		return editScene;
	}

	/**
	 * The last scene edited
	 * 
	 */
	public void setEditScene(String editScene) {
		this.editScene = editScene;
	}

	/**
	 * A simple note for annotating stuff on editor schema.
	 * 
	 */
	public Note getNotes() {
		return notes;
	}

	/**
	 * A simple note for annotating stuff on editor schema.
	 * 
	 */
	public void setNotes(Note notes) {
		this.notes = notes;
	}

	/**
	 * Holds the list of scenes in the order they have to be shown in the
	 * editor. For each scene its id (e.g. scene0) is stored in this array. This
	 * element has no effect whatsoever in the actual order in which scenes are
	 * displayed in the game.
	 * 
	 */
	public List<String> getSceneorder() {
		return sceneorder;
	}

	/**
	 * Holds the list of scenes in the order they have to be shown in the
	 * editor. For each scene its id (e.g. scene0) is stored in this array. This
	 * element has no effect whatsoever in the actual order in which scenes are
	 * displayed in the game.
	 * 
	 */
	public void setSceneorder(List<String> sceneorder) {
		this.sceneorder = sceneorder;
	}

}
