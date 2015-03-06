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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.engine.gdx.AbstractWidget;

/**
 * A separator with a background
 */
public class Separator extends AbstractWidget {

	private SeparatorStyle style;

	private boolean horizontal;

	/**
	 * @param horizontal
	 *            if true, the separator is horizontal. If false, is vertical
	 */
	public Separator(boolean horizontal, Skin skin) {
		this(horizontal, skin.get(SeparatorStyle.class));
	}

	/**
	 * @param horizontal
	 *            if true, the separator is horizontal. If false, is vertical
	 */
	public Separator(boolean horizontal, String name, Skin skin) {
		this(horizontal, skin.get(name, SeparatorStyle.class));
	}

	/**
	 * @param horizontal
	 *            if true, the separator is horizontal. If false, is vertical
	 */
	public Separator(boolean horizontal, SeparatorStyle style) {
		this.horizontal = horizontal;
		this.style = style;
	}

	@Override
	public float getPrefWidth() {
		return horizontal ? getParent().getWidth() : style.size;
	}

	@Override
	public float getPrefHeight() {
		return horizontal ? style.size : getParent().getHeight();
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		style.background.draw(batch, 0, 0, getWidth(), getHeight());
	}

	public static class SeparatorStyle {
		public Drawable background;
		public float size;

		public SeparatorStyle() {

		}

		public SeparatorStyle(Drawable background, float size) {
			this.background = background;
			this.size = size;
		}
	}
}
