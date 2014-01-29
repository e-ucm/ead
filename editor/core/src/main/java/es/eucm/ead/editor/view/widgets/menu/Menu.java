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
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.LinearLayout;

public class Menu extends LinearLayout {

	private Controller controller;

	private Skin skin;

	private boolean opened;

	public Menu(Controller controller, Skin skin) {
		super(true);
		this.controller = controller;
		this.skin = skin;
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
		MenuItem item = new MenuItem(controller, this, label, skin);
		addActor(item);
		return item;
	}

	public void selected(MenuItem menuItem, boolean pressed) {
		if (pressed || opened) {
			for (Actor item : getChildren()) {
				if (item instanceof MenuItem) {
					((MenuItem) item).setSubmenuVisible(false);
				}
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
