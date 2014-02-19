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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.mockup.panels.HiddenLateralOptionsPanel;
import es.eucm.ead.editor.view.widgets.mockup.panels.HiddenPanel;

public class Options extends Table {

	private final static String IC_OPTIONS = "ic_settings";
	protected static final float PREF_BUTTON_WIDTH = .075F;

	private ImageButton optButton;
	private HiddenPanel optPanel;
	private boolean opened;

	public Options(Controller controller, Skin skin) {
		super(skin);

		optButton = new ImageButton(skin, IC_OPTIONS) {
			@Override
			public float getPrefWidth() {
				// We make sure it's a square and return the prefWidth
				return Math.max(super.getPrefHeight(), Gdx.graphics.getWidth()
						* PREF_BUTTON_WIDTH);
			}

			@Override
			public float getPrefHeight() {
				// We make sure it's a square
				return Math.max(super.getPrefHeight(), getPrefWidth());
			}
		};
		optButton.getImageCell().expand().fill();
		optButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				opened = !optPanel.isVisible();
				if (optPanel.isVisible()) {
					optPanel.hide();
				} else {
					optPanel.show();
				}
				return false;
			}
		});

		optPanel = new HiddenLateralOptionsPanel(controller, skin);
		opened = optPanel.isVisible();

		this.add(optButton).top().right();
		this.row();
		this.add(optPanel).top().right().expand();
	}

	public boolean isOpened() {
		return opened;
	}

	public Button getButton() {
		return optButton;
	}

	public HiddenPanel getPanel() {
		return optPanel;
	}

}
