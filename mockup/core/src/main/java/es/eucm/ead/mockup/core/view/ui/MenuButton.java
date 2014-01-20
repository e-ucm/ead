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
package es.eucm.ead.mockup.core.view.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * A button displayed in the MainMenu and PanelMenu Screens.
 */
public class MenuButton extends Button {
	
	private final float PAD_TOP = 17f, 
						PAD_LEFT = 17f, 
						PAD_BOTTOM = 10f, 
						PAD_RIGHT = 17f;

	public MenuButton(String name, Skin skin, String styleName) {
		super(skin);
		setSize(getPrefWidth(), getPrefHeight());
		initialize(name, skin, styleName);
	}
	
	public MenuButton(String name, Skin skin, String styleName, float width, float height) {
		super(skin);
		setSize(width, height);
		initialize(name, skin, styleName);
	}
	
	private void initialize(String name, Skin skin, String styleName){
		pad(PAD_TOP, PAD_LEFT, PAD_BOTTOM, PAD_RIGHT);
		Image sceneIcon = new Image(skin.getRegion(styleName));
		Label scene = new Label(name, skin);
		
		add(sceneIcon).expand();
		row();
		add(scene);
	}
}
