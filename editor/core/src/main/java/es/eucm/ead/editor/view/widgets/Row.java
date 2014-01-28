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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class Row extends WidgetGroup {

	public boolean uniform;

	public void uniform() {
		this.uniform = true;
	}

	@Override
	public float getPrefHeight() {
		float prefHeight = 0;
		for (Actor a : this.getChildren()) {
			if (a instanceof Widget) {
				prefHeight = Math.max(((Widget) a).getPrefHeight(), prefHeight);
			} else if (a instanceof WidgetGroup) {
				prefHeight = Math.max(((WidgetGroup) a).getPrefHeight(),
						prefHeight);
			} else {
				prefHeight = Math.max(a.getHeight(), prefHeight);
			}
		}
		return prefHeight;
	}

	@Override
	public float getPrefWidth() {
		float prefWidth = 0;
		for (Actor a : this.getChildren()) {
			if (a instanceof Widget) {
				prefWidth += ((Widget) a).getPrefWidth();
			} else if (a instanceof WidgetGroup) {
				prefWidth += ((WidgetGroup) a).getPrefWidth();
			} else {
				prefWidth += a.getWidth();
			}
		}
		return prefWidth;
	}

	@Override
	public void layout() {
		float xOffset = 0;
		float yOffset = this.getHeight() / 2;

		float xDelta = 0;
		if (uniform) {
			xOffset = xDelta = getWidth() / (getChildren().size + 1);
		}

		for (Actor a : this.getChildren()) {
			float width = getPrefWidth(a);
			float height = getPrefHeight(a);
			a.setBounds(uniform ? xOffset - width / 2 : xOffset, yOffset
					- height / 2, width, height);
			xOffset += uniform ? xDelta : width;
		}
	}

	private float getPrefWidth(Actor a) {
		if (a instanceof Widget) {
			return ((Widget) a).getPrefWidth();
		} else if (a instanceof WidgetGroup) {
			return ((WidgetGroup) a).getPrefWidth();
		} else {
			return a.getWidth();
		}
	}

	private float getPrefHeight(Actor a) {
		if (a instanceof Widget) {
			return ((Widget) a).getPrefHeight();
		} else if (a instanceof WidgetGroup) {
			return ((WidgetGroup) a).getPrefHeight();
		} else {
			return a.getHeight();
		}
	}
}
