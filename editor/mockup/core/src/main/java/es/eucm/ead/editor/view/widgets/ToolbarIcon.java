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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * {@link IconButton} for {@link Toolbar} that can receive by constructor the
 * size
 * 
 */
public class ToolbarIcon extends IconButton {

	private float height;

	private float width;

	public ToolbarIcon(String icon, float padding, Skin skin) {
		this(icon, padding, -1, -1, skin);
	}

	public ToolbarIcon(String icon, float padding, Skin skin, String styleName) {
		this(icon, padding, -1f, -1f, skin, styleName);
	}

	public ToolbarIcon(String icon, float padding, float size, Skin skin) {
		this(icon, padding, size, size, skin);
	}

	public ToolbarIcon(String icon, float padding, float width, float height,
			Skin skin) {
		super(icon, padding, skin);
		this.height = height;
		this.width = width;
	}

	public ToolbarIcon(String icon, float padding, float size, Skin skin,
			String styleName) {
		this(icon, padding, size, size, skin, styleName);
	}

	public ToolbarIcon(String icon, float padding, float width, float height,
			Skin skin, String styleName) {
		super(icon, padding, skin, styleName);
		this.height = height;
		this.width = width;
	}

	@Override
	public float getPrefHeight() {
		if (height == -1) {
			return super.getPrefHeight();
		}
		return height;
	}

	@Override
	public float getPrefWidth() {
		if (width == -1) {
			return super.getPrefWidth();
		}
		return width;
	}

}
