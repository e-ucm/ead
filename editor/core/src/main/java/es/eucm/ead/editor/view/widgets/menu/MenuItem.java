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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.control.Controller;

public class MenuItem extends WidgetGroup {

	private ContextMenu contextMenu;

	private TextButton textButton;

	private Menu parentMenu;

	public MenuItem(Controller controller, Menu parentMenu, String label,
			Skin skin) {
		this.parentMenu = parentMenu;
		textButton = new TextButton(label, skin);
		textButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				MenuItem.this.parentMenu.selected(MenuItem.this, true);
				event.stop();
				return false;
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer,
					Actor fromActor) {
				MenuItem.this.parentMenu.selected(MenuItem.this, false);
			}
		});
		contextMenu = new ContextMenu(controller, skin);
		contextMenu.setVisible(false);
		addActor(textButton);
		addActor(contextMenu);
	}

	public void setSubmenuVisible(boolean visible) {
		contextMenu.setVisible(visible);
	}

	public MenuItem subitem(String label, ContextMenu submenu) {
		this.contextMenu.item(label, submenu);
		return this;
	}

	public MenuItem subitem(String label) {
		contextMenu.item(label);
		return this;
	}

	public MenuItem subitem(String label, String actionName, Object... args) {
		contextMenu.item(label, actionName, args);
		return this;
	}

	@Override
	public float getPrefWidth() {
		return textButton.getPrefWidth();
	}

	@Override
	public float getPrefHeight() {
		return textButton.getPrefHeight();
	}

	@Override
	public void layout() {
		super.layout();
		textButton.setBounds(0, 0, getWidth(), getHeight());
		float height = contextMenu.getPrefHeight();
		float width = contextMenu.getPrefWidth();
		contextMenu.setBounds(0, -getHeight(), width, height);
	}
}
