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

import com.badlogic.gdx.scenes.scene2d.Actor;
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
		return super.getChildrenTotalWidth() + margin
				* (this.getChildren().size - 1) + pad * 2;
	}

	@Override
	public float getPrefHeight() {
		return super.getChildrenMaxHeight() + pad * 2;
	}

	public LeftRightLayout left(Actor actor) {
		addFirst(actor);
		return this;
	}

	public LeftRightLayout right(Actor actor) {
		addSecond(actor);
		return this;
	}

	@Override
	public void layout() {
		float x = pad;
		for (Actor a : first) {
			float width = getPrefWidth(a);
			float height = getPrefHeight(a);
			float y = (getHeight() - height) / 2.0f;
			setBounds(a, x, y, width, height);
			x += width + margin;
		}

		x = getWidth() - pad;
		for (Actor a : second) {
			float width = getPrefWidth(a);
			x -= width + margin;
			float height = getPrefHeight(a);
			float y = (getHeight() - height) / 2.0f;
			setBounds(a, x, y, width, height);
		}
	}
}
