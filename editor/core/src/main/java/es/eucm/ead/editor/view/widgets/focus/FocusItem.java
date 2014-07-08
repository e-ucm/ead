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
package es.eucm.ead.editor.view.widgets.focus;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.control.Controller;

/**
 * An item inside a {@link FocusItemList} that can display a "focus"
 * {@link Drawable} when selected.
 */
public class FocusItem extends Button {

	private Drawable focus;
	protected Widget widget;
	private boolean focused;

	public FocusItem(Widget widget, Controller controller) {
		super(controller.getApplicationAssets().getSkin()
				.get("default", FocusItemStyle.class));
		this.focus = ((FocusItemStyle) getStyle()).focus;
		this.widget = widget;
		build(controller);
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		super.drawChildren(batch, parentAlpha);
		if (focused) {
			focus.draw(batch, getX(), getY(), getWidth(), getHeight());
		}
	}

	/**
	 * Executed when this widget gains or loses focus.
	 * 
	 * @param focus
	 */
	public void setFocus(boolean focus) {
		this.focused = focus;
	}

	/**
	 * Build the content and add it to this item in this function.
	 * 
	 * @param controller
	 */
	protected void build(Controller controller) {
		add(widget);
	}

	/**
	 * The style for a focus item (extends
	 * {@link com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle}), see
	 * {@link Button} and {@link FocusItem}.
	 */
	static public class FocusItemStyle extends ButtonStyle {

		public Drawable focus;

		public FocusItemStyle() {
			super();
		}

		public FocusItemStyle(Drawable up, Drawable down, Drawable checked,
				Drawable focus) {
			super(up, down, checked);
			this.focus = focus;
		}

		public FocusItemStyle(FocusItemStyle style) {
			super(style);
			this.focus = style.focus;
		}
	}
}
