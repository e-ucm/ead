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
package es.eucm.ead.editor.view.widgets.mockup;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.mockup.buttons.IconButton;
import es.eucm.ead.editor.view.widgets.mockup.panels.HiddenPanel;
import es.eucm.ead.editor.view.widgets.mockup.panels.NavigationPanel;

public class Navigation extends Table {

	private final static String IC_MENU = "ic_menu";
	protected static final float PREF_BUTTON_WIDTH = .075F;

	private Button menuButton;
	private NavigationPanel navigationPanel;
	private boolean opened;

	public Navigation(Controller controller, Skin skin) {
		super(skin);
		setFillParent(true);

		menuButton = new IconButton(skin, IC_MENU);
		menuButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				opened = !navigationPanel.isVisible();
				if (navigationPanel.isVisible()) {
					navigationPanel.hide();
				} else {
					navigationPanel.show();
				}
				return false;
			}
		});

		navigationPanel = new NavigationPanel(controller, skin);
		opened = navigationPanel.isVisible();

		this.add(menuButton).top().left();
		this.row();
		this.add(navigationPanel).top().left().expand();
	}

	public boolean isOpened() {
		return opened;
	}

	public Button getButton() {
		return menuButton;
	}

	public HiddenPanel getPanel() {
		return navigationPanel;
	}

}
