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
package es.eucm.ead.mockup.core.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.mockup.core.control.MockupController;
import es.eucm.ead.mockup.core.control.listeners.EventListener;
import es.eucm.ead.mockup.core.control.listeners.FocusListener;

/**
 * Has auxiliary attributes.
 */
public class Screen implements EventListener, FocusListener {

	/**
	 * Used to draw and update the UI. 
	 * Has constant width and height defined in Constants.
	 */
	public static Stage stage;

	/**
	 * Used for the UI elements.
	 */
	public static Skin skin;

	/**
	 * Used to manage UI resources.
	 */
	public static AssetManager am;

	/**
	 * Static reference to the main controller.
	 */
	public static MockupController mockupController;

	/**
	 * Static reference to the default bitmap font.
	 */
	public static BitmapFont font;

	/**
	 * Stage's width.
	 * The stage is used for the UI.
	 */
	public static float stagew;
	public static float halfstagew;

	/**
	 * Stage's height.
	 * The stage is used for the UI.
	 */
	public static float stageh;
	public static float halfstageh;

	@Override
	public void create() {

	}

	@Override
	public void show() {

	}

	@Override
	public void hide() {

	}
}
