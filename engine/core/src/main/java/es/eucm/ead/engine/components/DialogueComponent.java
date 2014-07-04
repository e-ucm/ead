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
package es.eucm.ead.engine.components;

import ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import es.eucm.ead.engine.entities.EngineEntity;

/**
 * An entity with this component is speaking a line of dialogue.
 * 
 * Created by mfreire on 6/30/14.
 */
public class DialogueComponent extends Component implements Pool.Poolable {

	/**
	 * The text keys to look up when rendering this component. In the case of
	 * non-menus, each key is rendered as a paragraph. In the case of menus,
	 * each entry is an option.
	 */
	private String[] keys;

	/**
	 * Engine-Entities that are currently rendering this Component
	 */
	private Array<EngineEntity> entities;

	/**
	 * Whether this is a menu or not. Menus leave a choice available (via
	 * getMenuChoice()) once a selection is made.
	 */
	private boolean menu;

	/**
	 * The user's choice, if a menu and a choice is available. -1 otherwise.
	 */
	private int menuChoice;

	/**
	 * True if already displayed.
	 */
	private boolean displayed;

	/**
	 * True if no longer needed.
	 */
	private boolean dismissed;

	/**
	 * A callback to invoke once this dialogue is dismissed or interacted with.
	 */
	private DialogueCallback callback;

	public void init(String[] keys, DialogueCallback callback, boolean menu) {
		this.keys = keys;
		this.menu = menu;
		this.callback = callback;
		this.displayed = false;
		this.dismissed = false;
		this.menuChoice = -1;
		this.entities = new Array<EngineEntity>();
	}

	public String[] getKeys() {
		return keys;
	}

	public DialogueCallback getCallback() {
		return callback;
	}

	public int getMenuChoice() {
		return menuChoice;
	}

	public void setMenuChoice(int menuChoice) {
		this.menuChoice = menuChoice;
	}

	public boolean isMenu() {
		return menu;
	}

	public void setMenu(boolean menu) {
		this.menu = menu;
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
		displayed = dismissed = false;
	}

	public static interface DialogueCallback {
		void dialogueChanged(DialogueComponent component);
	}
}
