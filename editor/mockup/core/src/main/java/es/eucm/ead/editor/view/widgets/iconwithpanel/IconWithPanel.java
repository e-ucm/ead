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
package es.eucm.ead.editor.view.widgets.iconwithpanel;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.view.widgets.HiddenPanel;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.PositionedHiddenPanel;
import es.eucm.ead.editor.view.widgets.PositionedHiddenPanel.Position;
import es.eucm.ead.editor.view.widgets.ToolbarIcon;

/**
 * An {@link IconButton} with a {@link HiddenPanel} as attribute, when clicked
 * the panel is automatically added to the {@link Stage} and removed from it
 * when clicked again. The panel is drawn in {@link Stage} coordinates.
 * 
 */
public abstract class IconWithPanel extends ToolbarIcon {

	protected static HiddenPanel openPanel;
	protected static final float IN_DURATION = .3F;
	protected static final float OUT_DURATION = .2F;

	private static final ChangeListener showOrHide = new ChangeListener() {

		public void changed(ChangeEvent event,
				com.badlogic.gdx.scenes.scene2d.Actor actor) {
			IconWithPanel icon = (IconWithPanel) event.getListenerActor();
			if (icon.panel.hasParent()) {
				icon.hidePanel();
			} else {
				icon.showPanel();
			}
		};
	};

	protected PositionedHiddenPanel panel;

	public IconWithPanel(String icon, float padding, float separation,
			float size, Skin skin, Position position) {
		super(icon, padding, size, skin);
		panel.setPosition(position);
		panel.setSpace(separation);
	}

	@Override
	protected void init(Drawable icon, float padding, Skin skin) {
		super.init(icon, padding, skin);
		panel = createPanel(skin);
		panel.setReference(this);
		addListener(showOrHide);
	}

	protected PositionedHiddenPanel createPanel(Skin skin) {
		return new Panel(skin);
	}

	/**
	 * Invoked when the panel is going to be displayed, this method should
	 * return the {@link Action} used to display the {@link #panel}.
	 */
	protected abstract Action getShowAction();

	/**
	 * Invoked when the panel is going to be hidden, this method should return
	 * the {@link Action} used to hide the {@link #panel}.
	 */
	protected abstract Action getHideAction();

	protected void showPanel() {
		if (openPanel != panel && openPanel != null) {
			openPanel.hide();
		}

		panel.show(getShowAction());
		openPanel = panel;
	}

	protected void hidePanel() {
		panel.hide(getHideAction());
	}

	public HiddenPanel getPanel() {
		return panel;
	}

	protected class Panel extends PositionedHiddenPanel {

		public Panel(Skin skin) {
			super(skin);

		}

		@Override
		public void hide() {
			hide(getHideAction());
		}

		@Override
		public void hide(Action action) {
			openPanel = null;
			super.hide(action);
		}
	}
}
