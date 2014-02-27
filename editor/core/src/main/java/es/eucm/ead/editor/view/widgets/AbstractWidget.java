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

public class AbstractWidget extends WidgetGroup {

	protected float getPrefWidth(Actor a) {
		if (a instanceof Widget) {
			return ((Widget) a).getPrefWidth();
		} else if (a instanceof WidgetGroup) {
			return ((WidgetGroup) a).getPrefWidth();
		} else {
			return a.getWidth();
		}
	}

	protected float getPrefHeight(Actor a) {
		if (a instanceof Widget) {
			return ((Widget) a).getPrefHeight();
		} else if (a instanceof WidgetGroup) {
			return ((WidgetGroup) a).getPrefHeight();
		} else {
			return a.getHeight();
		}
	}

	protected float getMaxWidth(Actor a) {
		if (a instanceof Widget) {
			return ((Widget) a).getMaxWidth();
		} else if (a instanceof WidgetGroup) {
			return ((WidgetGroup) a).getMaxWidth();
		} else {
			return a.getWidth();
		}
	}

	protected float getMaxHeight(Actor a) {
		if (a instanceof Widget) {
			return ((Widget) a).getMaxHeight();
		} else if (a instanceof WidgetGroup) {
			return ((WidgetGroup) a).getMaxHeight();
		} else {
			return a.getHeight();
		}
	}

	protected float getChildrenTotalWidth() {
		float totalWidth = 0;
		for (Actor a : this.getChildren()) {
			totalWidth += getPrefWidth(a);
		}
		return totalWidth;
	}

	protected float getChildrenTotalHeight() {
		float totalHeight = 0;
		for (Actor a : this.getChildren()) {
			totalHeight += getPrefHeight(a);
		}
		return totalHeight;
	}

	protected float getChildrenMaxHeight() {
		float maxHeight = 0;
		for (Actor a : this.getChildren()) {
			maxHeight = Math.max(getPrefHeight(a), maxHeight);
		}
		return maxHeight;
	}

	protected float getChildrenMaxWidth() {
		float maxWidth = 0;
		for (Actor a : this.getChildren()) {
			maxWidth = Math.max(getPrefWidth(a), maxWidth);
		}
		return maxWidth;
	}

	@Override
	public void setX(float x) {
		super.setX(Math.round(x));
	}

	@Override
	public void setY(float y) {
		super.setY(Math.round(y));
	}

	@Override
	public void setWidth(float width) {
		super.setWidth(Math.round(width));
	}

	@Override
	public void setHeight(float height) {
		super.setHeight(Math.round(height));
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(Math.round(x), Math.round(y));
	}

	@Override
	public void setSize(float width, float height) {
		super.setSize(Math.round(width), Math.round(height));
	}

	@Override
	public void setBounds(float x, float y, float width, float height) {
		super.setBounds(Math.round(x), Math.round(y), Math.round(width),
				Math.round(height));
	}

	public void setPosition(Actor a, float x, float y) {
		a.setPosition(Math.round(x), Math.round(y));
	}

	public void setBounds(Actor a, float x, float y, float width, float height) {
		a.setBounds(Math.round(x), Math.round(y), Math.round(width),
				Math.round(height));
	}
}
