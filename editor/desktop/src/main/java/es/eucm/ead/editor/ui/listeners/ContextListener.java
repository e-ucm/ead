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
package es.eucm.ead.editor.ui.listeners;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;

import es.eucm.ead.editor.ui.DisplayableContextMenu;

/**
 * Displays different contexts when detected necessary.
 */
public class ContextListener extends ClickListener {

	private static final Vector2 TEMP = new Vector2();

	private ObjectMap<Class, DisplayableContextMenu> menus;

	/**
	 * Creates a {@link ContextListener} that listens to the
	 * {@link Buttons#RIGHT} button.
	 */
	public ContextListener() {
		this(Buttons.RIGHT);
	}

	public ContextListener(int button) {
		super(button);
		menus = new ObjectMap<Class, DisplayableContextMenu>();
	}

	public void clicked(InputEvent event, float x, float y) {

		Actor target = event.getTarget();

		DisplayableContextMenu menu = null;
		while (target != null) {
			menu = menus.get(target.getClass());
			if (menu != null) {
				break;
			}
			target = target.getParent();
		}
		if (menu != null) {
			target.stageToLocalCoordinates(TEMP.set(event.getStageX(),
					event.getStageY()));
			menu.displayContext(target, TEMP.x, TEMP.y);
		}
	};

	/**
	 * Add a {@link DisplayableContextMenu} that will be displayed when this
	 * listener is activated over a target with a specific class.
	 * 
	 * @param clazz
	 * @param menu
	 */
	public <T extends Actor> void registerContextMenu(Class<T> clazz,
			DisplayableContextMenu<T> menu) {
		menus.put(clazz, menu);
	}
}
