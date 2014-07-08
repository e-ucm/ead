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
package es.eucm.ead.engine.components.dialogues;

import ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import es.eucm.ead.engine.entities.EngineEntity;

/**
 * An entity with this component displays one or more lines of dialogue.
 * Interacting with the dialogue will generally cause it to advance.
 * 
 * Dialogues may use several engine entities to be rendered: to have a group of
 * entities speak at the same time, or to display options that the player must
 * choose among. All these entities are collected into the 'entities' array, and
 * should be disposed before disposing the dialogue.
 */
public class DialogueComponent extends Component implements Pool.Poolable {

	/**
	 * The text keys to look up when rendering this component. In the case of
	 * non-menus, each key is rendered as a paragraph. In the case of menus,
	 * each entry is an option.
	 */
	private Array<String> keys;

	/**
	 * Engine-Entities that are currently rendering this Component. Useful for
	 * cleanup.
	 */
	private Array<EngineEntity> entities;

	/**
	 * True if already displayed.
	 */
	private boolean displayed;

	/**
	 * True if changed (for example: interacted with).
	 */
	private boolean changed;

	/**
	 * True if no longer needed (and can be recycled into the pool).
	 */
	private boolean dismissed;

	public void init(Array<String> keys) {
		this.keys = keys;
		this.displayed = false;
		this.changed = false;
		this.dismissed = false;
		this.entities = new Array<EngineEntity>();
	}

	public Array<String> getKeys() {
		return keys;
	}

	public boolean hasChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public boolean isDisplayed() {
		return displayed;
	}

	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}

	public boolean isDismissed() {
		return dismissed;
	}

	public void setDismissed(boolean dismissed) {
		this.dismissed = dismissed;
	}

	public Array<EngineEntity> getRenderingEntities() {
		return entities;
	}

	@Override
	public void reset() {
		displayed = changed = dismissed = false;
	}
}
