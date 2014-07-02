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
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.schema.components.ModelComponent;

/**
 * A simple note for annotating stuff on editor schema.
 * 
 */
@Generated("org.jsonschema2pojo")
public class EditState extends ModelComponent {

	/**
	 * The last scene edited
	 * 
	 */
	private String editScene;
	/**
	 * Holds the list of scenes in the order they have to be shown in the
	 * editor. For each scene its id (e.g. scene0) is stored in this array. This
	 * element has no effect whatsoever in the actual order in which scenes are
	 * displayed in the game.
	 * 
	 */
	private Array<String> sceneorder = new Array<String>();

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
	 * Holds the list of scenes in the order they have to be shown in the
	 * editor. For each scene its id (e.g. scene0) is stored in this array. This
	 * element has no effect whatsoever in the actual order in which scenes are
	 * displayed in the game.
	 * 
	 */
	public Array<String> getSceneorder() {
		return sceneorder;
	}

	/**
	 * Holds the list of scenes in the order they have to be shown in the
	 * editor. For each scene its id (e.g. scene0) is stored in this array. This
	 * element has no effect whatsoever in the actual order in which scenes are
	 * displayed in the game.
	 * 
	 */
	public void setSceneorder(Array<String> sceneorder) {
		this.sceneorder = sceneorder;
	}

}
