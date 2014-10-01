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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class ScrollPaneDif extends ScrollPane {

	private ScrollPaneDifStyle style;

	public ScrollPaneDif(Actor actor) {
		super(actor);
	}

	public ScrollPaneDif(Actor widget, Skin skin, String styleName) {
		super(widget);
		setStyle(skin.get(styleName, ScrollPaneDifStyle.class));
	}

	public ScrollPaneDif(Actor widget, Skin skin) {
		super(widget);
		setStyle(skin.get(ScrollPaneDifStyle.class));
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		style.topBackground
				.draw(batch, getX(), getY(), getWidth(), getHeight());
		batch.setColor(Color.WHITE);
	}

	public void setStyle(ScrollPaneDifStyle style) {
		this.style = style;
		super.setStyle(style);
	}

	/**
	 * The style for {@link ScrollPaneDif} See also {@link ScrollPane}
	 */
	public static class ScrollPaneDifStyle extends ScrollPaneStyle {

		/**
		 * {@link ScrollPaneDif#inUse}.
		 */
		public Drawable topBackground;

		/**
		 * Default constructor used for reflection
		 */
		public ScrollPaneDifStyle() {
		}

		public ScrollPaneDifStyle(Drawable topBackground) {
			this.topBackground = topBackground;
		}

		public ScrollPaneDifStyle(ScrollPaneDifStyle scrollPaneDifStyle) {
			super(scrollPaneDifStyle);
			this.topBackground = scrollPaneDifStyle.topBackground;
		}

	}
}
