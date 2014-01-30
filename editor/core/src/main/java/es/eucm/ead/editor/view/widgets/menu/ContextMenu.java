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
package es.eucm.ead.editor.view.widgets.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.LinearLayout;

public class ContextMenu extends LinearLayout {

	private Controller controller;

	private Skin skin;

	public ContextMenu(Controller controller, Skin skin) {
		super(false);
		expand();
		this.controller = controller;
		this.skin = skin;
	}

	@Override
	public void clearChildren() {
		super.clearChildren();
	}

	public ContextMenuItem item(String label) {
		ContextMenuItem item = new ContextMenuItem(this, label, skin);
		addActor(item);
		return item;
	}

	public ContextMenuItem item(String label, String actionName, Object... args) {
		ContextMenuItem item = item(label);
		item.addListener(new ActionOnClickListener(controller, actionName, args));
		return item;
	}

	public ContextMenuItem item(String label, ContextMenu submenu) {
		ContextMenuItem item = item(label);
		item.setSubmenu(submenu);
		return item;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (!visible) {
			for (Actor a : getChildren()) {
				a.setVisible(visible);
			}
		}
	}

	public void hideAllExcept(Actor actor) {
		for (Actor a : getChildren()) {
			if (a != actor) {
				a.setVisible(false);
			}
		}
	}
}
