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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;

public class Menu extends WidgetGroup {

	private Controller controller;

	private Skin skin;

	private Array<MenuItem> menuItems;

	private boolean opened;

	public Menu(Controller controller, Skin skin) {
		this.controller = controller;
		this.skin = skin;
		menuItems = new Array<MenuItem>();
		opened = false;
		this.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (opened) {
					selected(null, true);
				}
				return false;
			}
		});
	}

	public MenuItem item(String label) {
		MenuItem menuItem = new MenuItem(controller, this, label, skin);
		addItem(menuItem);
		return menuItem;
	}

	private void addItem(MenuItem item) {
		menuItems.add(item);
		addActor(item);
	}

	@Override
	public float getPrefWidth() {
		float prefWidth = 0;
		for (MenuItem menuItem : menuItems) {
			prefWidth += menuItem.getPrefWidth();
		}
		return prefWidth;
	}

	@Override
	public float getPrefHeight() {
		float prefHeight = 0;
		for (MenuItem menuItem : menuItems) {
			prefHeight = Math.max(prefHeight, menuItem.getPrefHeight());
		}
		return prefHeight;
	}

	public void layout() {
		float x = 0;
		for (MenuItem menuItem : menuItems) {
			float width = menuItem.getPrefWidth();
			float height = menuItem.getPrefHeight();
			menuItem.setBounds(x, 0, width, height);
			x += width;
		}
	}

	public void selected(MenuItem menuItem, boolean pressed) {
		if (pressed || opened) {
			for (MenuItem item : menuItems) {
				item.setSubmenuVisible(false);
			}

			if (menuItem != null) {
				menuItem.setSubmenuVisible(true);
				opened = true;
			} else {
				opened = false;
			}
		}
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		Actor actor = super.hit(x, y, touchable);
		return actor == null && opened ? this : actor;
	}
}
