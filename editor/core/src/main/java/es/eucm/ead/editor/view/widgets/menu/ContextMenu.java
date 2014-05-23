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
package es.eucm.ead.editor.view.widgets.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.view.widgets.Separator;
import es.eucm.ead.editor.view.widgets.Separator.SeparatorStyle;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

/**
 * A context menu container. Use {@link #item(String)} or
 * {@link #item(String, ContextMenu)} to add items to the menu
 */
public class ContextMenu extends LinearLayout {

	private boolean opaque;

	private Skin skin;

	private ContextMenuStyle style;

	public ContextMenu(Skin skin) {
		super(false);
		this.skin = skin;
		this.style = skin.get(ContextMenuStyle.class);
		this.background(style.background);
	}

	/**
	 * If set to true, this context menu will always be hit
	 */
	public void setOpaque(boolean opaque) {
		this.opaque = opaque;
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		Actor actor = super.hit(x, y, touchable);
		return actor == null && opaque ? this : actor;
	}

	/**
	 * Adds in item to the context menu with the given label
	 * 
	 * @return a {@link ContextMenuItem} created with the label
	 */
	public ContextMenuItem item(String label) {
		ContextMenuItem item = new ContextMenuItem(this, label, skin);
		add(item).expandX().left();
		return item;
	}

	/**
	 * Adds in item to the context menu with the given label. A mouse over on
	 * the item will show the given submenu
	 * 
	 * @return a {@link ContextMenuItem} created with the label
	 */
	public ContextMenuItem item(String label, ContextMenu submenu) {
		ContextMenuItem item = item(label);
		item.submenu(submenu);
		return item;
	}

	@Override
	public void setBounds(float x, float y, float width, float height) {
		super.setBounds(x, y, width, height);
	}

	/**
	 * Adds a separator
	 */
	public void separator() {
		Separator separator = new Separator(true, style.separatorStyle);
		add(separator);
		separator.toBack();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (!visible) {
			for (Actor a : getChildren()) {
				if (!(a instanceof Separator)) {
					a.setVisible(false);
				}
			}
		}
	}

	public void hideAllExcept(Actor actor) {
		for (Actor a : getChildren()) {
			if (a != actor && !(a instanceof Separator)) {
				a.setVisible(false);
			}
		}
	}

	public static class ContextMenuStyle {
		/**
		 * Background for the context menu
		 */
		public Drawable background;

		/**
		 * Style for separators
		 */
		public SeparatorStyle separatorStyle;
	}
}
