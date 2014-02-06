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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class LinearLayout extends AbstractWidget {

	private boolean horizontal;

	private Drawable background;

	private float padLeft, padTop, padRight, padBottom;

	private boolean expand;

	private int verticalAlign = Align.top;

	private int horizontalAlign = Align.left;

	public LinearLayout(boolean horizontal) {
		this.horizontal = horizontal;
	}

	public LinearLayout background(Drawable background) {
		this.background = background;
		return this;
	}

	public LinearLayout pad(float pad) {
		padLeft = padTop = padRight = padBottom = pad;
		return this;
	}

	public LinearLayout padLeft(float padLeft) {
		this.padLeft = padLeft;
		return this;
	}

	public LinearLayout padTop(float padTop) {
		this.padTop = padTop;
		return this;
	}

	public LinearLayout padRight(float padRight) {
		this.padRight = padRight;
		return this;
	}

	public LinearLayout padBottom(float padBottom) {
		this.padBottom = padBottom;
		return this;
	}

	public LinearLayout center() {
		this.horizontalAlign = Align.center;
		return this;
	}

	public LinearLayout left() {
		this.horizontalAlign = Align.left;
		return this;
	}

	public LinearLayout right() {
		this.horizontalAlign = Align.right;
		return this;
	}

	public LinearLayout middle() {
		this.verticalAlign = Align.center;
		return this;
	}

	public LinearLayout top() {
		this.verticalAlign = Align.top;
		return this;
	}

	public LinearLayout bottom() {
		this.verticalAlign = Align.bottom;
		return this;
	}

	public LinearLayout expand() {
		this.expand = true;
		return this;
	}

	@Override
	public float getPrefWidth() {
		return horizontal ? getChildrenTotalWidth() + (padLeft + padRight)
				* getChildren().size : getChildrenMaxWidth() + padLeft
				+ padRight;
	}

	@Override
	public float getPrefHeight() {
		return horizontal ? getChildrenMaxHeight() + padTop + padBottom
				: getChildrenTotalHeight() + (padBottom + padTop)
						* getChildren().size;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (background != null) {
			background.draw(batch, getX(), getY(), getWidth(), getHeight());
		}
		super.draw(batch, parentAlpha);
	}

	@Override
	public void layout() {
		float xOffset = padLeft;
		float yOffset = padBottom;

		for (Actor a : getChildren()) {
			float width = !horizontal && expand ? getWidth() : Math.min(
					getWidth(), getPrefWidth(a));
			float height = horizontal && expand ? getHeight() : Math.min(
					getHeight(), getPrefHeight(a));

			a.setBounds(xOffset, getHeight() - yOffset - height, width, height);

			if (horizontal) {
				xOffset += padLeft + padRight + width;
			} else {
				yOffset += padTop + padBottom + height;
			}
		}
	}

}
