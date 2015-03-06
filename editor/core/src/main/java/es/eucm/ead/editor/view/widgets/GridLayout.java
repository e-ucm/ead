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

import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.ead.engine.gdx.AbstractWidget;

public class GridLayout extends AbstractWidget {

	private float padLeft, padTop, padRight, padBottom;

	private boolean expand;

	public GridLayout() {
		expand = false;
	}

	public GridLayout pad(float pad) {
		padLeft = padTop = padRight = padBottom = pad;
		return this;
	}

	public GridLayout expand() {
		this.expand = true;
		return this;
	}

	/*
	 * @Override public float getPrefWidth() { return getChildrenMaxWidth() +
	 * (padLeft + padRight) getChildren().size; }
	 * 
	 * @Override public float getPrefHeight() { return getChildrenMaxHeight() +
	 * padTop + padBottom; }
	 */
	@Override
	public void layout() {
		float xOffset = padRight;
		float yOffset = padBottom;

		for (Actor a : getChildren()) {
			float width = expand ? getWidth() : Math.min(getWidth(),
					getPrefWidth(a));
			float height = expand ? getHeight() : Math.min(getHeight(),
					getPrefHeight(a));

			if (xOffset + width > getWidth()) {
				yOffset += padTop + padBottom + height;
				xOffset = 0;
			}

			a.setBounds(xOffset, getHeight() - yOffset - height, width, height);
			xOffset += padLeft + padRight + width;
		}
	}

}
