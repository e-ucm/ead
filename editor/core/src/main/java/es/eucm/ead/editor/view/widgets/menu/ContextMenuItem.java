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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class ContextMenuItem extends WidgetGroup {

	private TextButton textButton;

	private ContextMenu parentContextMenu;

	private ContextMenu childContextMenu;

	public ContextMenuItem(ContextMenu parent, String text, Skin skin) {
		this.parentContextMenu = parent;
		textButton = new TextButton(text, skin);
		addActor(textButton);
		this.addListener(new InputListener() {
			@Override
			public void enter(InputEvent event, float x, float y, int pointer,
					Actor fromActor) {
				setVisible(true);
				parentContextMenu.hideAllExcept(ContextMenuItem.this);
				event.stop();
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				return false;
			}
		});
	}

	@Override
	public void setVisible(boolean visible) {
		if (childContextMenu != null) {
			childContextMenu.setVisible(visible);
		}
	}

	@Override
	public float getPrefWidth() {
		return textButton.getPrefWidth();
	}

	public void setSubmenu(ContextMenu submenu) {
		this.childContextMenu = submenu;
		childContextMenu.setVisible(false);
		addActor(childContextMenu);
	}

	@Override
	public float getPrefHeight() {
		return textButton.getPrefHeight();
	}

	@Override
	public void layout() {
		textButton.setBounds(0, 0, getWidth(), getHeight());
		if (childContextMenu != null) {
			float height = childContextMenu.getPrefHeight();
			float width = childContextMenu.getPrefWidth();
			childContextMenu.setBounds(getWidth(), getHeight() - height, width,
					height);
		}
	}
}
