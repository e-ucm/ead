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

public class TopBottomLayout extends SidesLayout {

	public TopBottomLayout() {
		super();
	}

	public TopBottomLayout(Drawable background) {
		super(background);
	}

	@Override
	public float getPrefWidth() {
		return super.getChildrenMaxWidth(computeInvisibles) + widgetsPad * 2
				+ padLeft + padRight;
	}

	@Override
	public float getPrefHeight() {
		return super.getChildrenTotalHeight(computeInvisibles) + widgetsMargin
				* (this.getChildren().size - 1) + widgetsPad * 2 + padBottom
				+ padTop;
	}

	public TopBottomLayout addBottom(Actor actor) {
		addFirst(actor);
		return this;
	}

	public TopBottomLayout addTop(int index, Actor actor) {
		addSecond(index, actor);
		return this;
	}

	public TopBottomLayout addTop(Actor actor) {
		addSecond(actor);
		return this;
	}

	public TopBottomLayout removeBottom(Actor actor) {
		removeFirst(actor);
		return this;
	}

	public TopBottomLayout removeTop(Actor actor) {
		removeSecond(actor);
		return this;
	}

	public TopBottomLayout expandChildrenWidth() {
		expand();
		return this;
	}

	@Override
	public void layout() {
		float y = widgetsPad + padBottom;
		for (Actor a : first) {
			float width = expand ? getWidth() : Math.max(getPrefWidth(a),
					a.getWidth());
			float height = Math.max(getPrefHeight(a), a.getHeight());
			setBounds(a, padLeft + getXAligned(width), y, width, height);
			y += height + widgetsMargin;
		}

		y = getHeight() - widgetsPad - padTop;
		for (Actor a : second) {
			float height = getPrefHeight(a);
			y -= height + widgetsMargin;
			float width = expand ? getWidth() : Math.max(getPrefWidth(a),
					a.getWidth());
			setBounds(a, padLeft + getXAligned(width), y, width, height);
		}
	}

	public float getXAligned(float width) {
		switch (align) {
		case Align.left:
			return 0;
		case Align.right:
			return (getWidth() - width - padRight - padLeft);
		default:
			// Align.center
			return (getWidth() - width - padRight - padLeft) / 2.0f;
		}
	}
}
