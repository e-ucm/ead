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
package es.eucm.ead.editor.view.widgets.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.view.widgets.LinearLayout;

/**
 * A context menu, acting as container
 */
public class ContextMenu extends LinearLayout {

	private Skin skin;

	private ContextMenuStyle style;

	public ContextMenu(Skin skin) {
		super(false);
		expand();
		this.skin = skin;
		this.style = skin.get(ContextMenuStyle.class);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (style.background != null) {
			style.background.draw(batch, getX(), getY(), getWidth(),
					getHeight());
		}
		super.draw(batch, parentAlpha);
	}

	@Override
	public void clearChildren() {
		super.clearChildren();
	}

	public ContextMenuItem item(String label) {
		ContextMenuItem item = new ContextMenuItem(this, label, skin);
		addActor(item);
		return item;
	}

	public void separator() {
		addActor(new Separator());
	}

	public ContextMenuItem item(String label, ContextMenu submenu) {
		ContextMenuItem item = item(label);
		item.setSubmenu(submenu);
		return item;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (!visible) {
			for (Actor a : getChildren()) {
				a.setVisible(visible);
			}
		}
	}

	public void hideAllExcept(Actor actor) {
		for (Actor a : getChildren()) {
			if (a != actor) {
				a.setVisible(false);
			}
		}
	}

	public class Separator extends Widget {
		@Override
		public float getPrefHeight() {
			return style.separatorHeight;
		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			if (style.separator != null) {
				style.separator.draw(batch, getX(), getY(), getWidth(),
						getHeight());
			}
		}

		@Override
		public void setVisible(boolean visible) {
		}
	}

	public static class ContextMenuStyle {
		public Drawable background, separator;

		public float separatorHeight = 1.0f;
	}
}
