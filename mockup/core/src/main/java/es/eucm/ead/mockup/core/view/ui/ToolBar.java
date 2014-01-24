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

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.esotericsoftware.tablelayout.Cell;

import es.eucm.ead.mockup.core.control.screens.AbstractScreen;
import es.eucm.ead.mockup.core.view.UIAssets;

public class ToolBar extends Table {

	/**
	 * Create a {@link ToolBar toolbar} with default style.
	 * 
	 * @param skin
	 *            the skin to use
	 */
	public ToolBar(Skin skin) {
		this(skin, "default");
	}

	/**
	 * Create a {@link ToolBar toolbar} with the specified style.
	 */
	public ToolBar(Skin skin, String styleName) {
		super(skin);
		setBounds(0, AbstractScreen.stageh - UIAssets.TOOLBAR_HEIGHT,
				AbstractScreen.stagew, UIAssets.TOOLBAR_HEIGHT);
		setStyle(skin.get(styleName, ToolBarStyle.class));
	}

	/**
	 * Apply the style of this {@link ToolBar toolbar}.
	 * 
	 * @param style
	 *            the style to apply
	 */
	public void setStyle(ToolBarStyle style) {
		// this.style = style;

		if (style.background != null)
			this.setBackground(style.background);

	}

	@Override
	public Cell<?> row() {
		throw new IllegalStateException("There are no rows in a ToolBar");
	}

	/**
	 * Define the style of a {@link ToolBar toolbar}.
	 */
	public static class ToolBarStyle {

		/** Optional */
		public Drawable background;

		public ToolBarStyle() {

		}

		public ToolBarStyle(Drawable background) {
			this.background = background;
		}

		public ToolBarStyle(ToolBarStyle style) {
			this.background = style.background;
		}
	}
}
