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
package es.eucm.ead.editor.view.widgets.layouts;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.view.widgets.AbstractWidget;

public class LeftRightLayout extends AbstractWidget {

	private Array<Actor> left;

	private Array<Actor> right;

	private float margin = 0.0f;

	private float pad = 0.0f;

	private Drawable background;

	public LeftRightLayout() {
		this(null);
	}

	public LeftRightLayout(Drawable background) {
		this.background = background;
		left = new Array<Actor>();
		right = new Array<Actor>();
	}

	public void setBackground(Drawable background) {
		this.background = background;
	}

	@Override
	public float getPrefWidth() {
		return super.getChildrenTotalWidth() + margin
				* (this.getChildren().size - 1) + pad * 2;
	}

	@Override
	public float getPrefHeight() {
		return super.getChildrenMaxHeight() + pad * 2;
	}

	@Override
	public float getMaxWidth() {
		return getPrefWidth();
	}

	@Override
	public float getMaxHeight() {
		return getPrefHeight();
	}

	public LeftRightLayout left(Actor actor) {
		left.add(actor);
		addActor(actor);
		return this;
	}

	public LeftRightLayout right(Actor actor) {
		right.add(actor);
		addActor(actor);
		return this;
	}

	public LeftRightLayout margin(float margin) {
		this.margin = margin;
		return this;
	}

	public LeftRightLayout pad(float pad) {
		this.pad = pad;
		return this;
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
		float x = pad;
		for (Actor a : left) {
			float width = getPrefWidth(a);
			float height = getPrefHeight(a);
			float y = (getHeight() - height) / 2.0f;
			setBounds(a, x, y, width, getPrefHeight(a));
			x += width + margin;
		}

		x = getWidth() - pad;
		for (Actor a : right) {
			float width = getPrefWidth(a);
			x -= width + margin;
			float height = getPrefHeight(a);
			float y = (getHeight() - height) / 2.0f;
			setBounds(a, x, y, width, getPrefHeight(a));
		}
	}
}
