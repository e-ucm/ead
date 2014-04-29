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
package es.eucm.ead.editor.view.widgets.layouts;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class LeftRightLayout extends SidesLayout {

	public LeftRightLayout() {
		super();
	}

	public LeftRightLayout(Drawable background) {
		super(background);
	}

	@Override
	public float getPrefWidth() {
		return super.getChildrenTotalWidth(computeInvisibles) + widgetsMargin
				* (this.getChildren().size - 1) + widgetsPad * 2 + padLeft
				+ padRight;
	}

	@Override
	public float getPrefHeight() {
		return super.getChildrenMaxHeight(computeInvisibles) + widgetsPad * 2
				+ widgetsMargin * 2 + padBottom + padTop;
	}

	public LeftRightLayout addLeft(int index, Actor actor) {
		addFirst(index, actor);
		return this;
	}

	public LeftRightLayout addLeft(Actor actor) {
		addFirst(actor);
		return this;
	}

	public LeftRightLayout addRight(int index, Actor actor) {
		addSecond(index, actor);
		return this;
	}

	public LeftRightLayout addRight(Actor actor) {
		addSecond(actor);
		return this;
	}

	@Override
	public void layout() {
		float count = first.size + second.size;
		float maxWidth = (getWidth() - count * widgetsMargin - padLeft - padRight)
				/ count;

		float x = widgetsPad + padLeft;
		for (Actor a : first) {
			float width = Math.min(maxWidth, getPrefWidth(a));
			float height = getPrefHeight(a);
			setBounds(a, x, getYAligned(height), width, height);
			x += width + widgetsMargin;
		}

		x = getWidth() - widgetsPad - padRight;
		for (Actor a : second) {
			float width = Math.min(maxWidth, getPrefWidth(a));
			x -= width + widgetsMargin;
			float height = getPrefHeight(a);
			setBounds(a, x, getYAligned(height), width, height);
		}
	}

	private float getYAligned(float height) {
		switch (align) {
		case Align.top:
			return (getHeight() - height - padBottom - padTop);
		case Align.bottom:
			return 0;
		default:
			// Align.center
			return (getHeight() - height - padBottom - padTop) / 2.0f;
		}

	}
}
