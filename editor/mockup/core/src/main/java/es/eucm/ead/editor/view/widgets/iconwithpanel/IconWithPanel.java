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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.view.widgets.HiddenPanel;
import es.eucm.ead.editor.view.widgets.IconButton;

/**
 * An {@link IconButton} with a {@link HiddenPanel} as attribute, when clicked
 * the panel is automatically added to the {@link Stage} and removed from it
 * when clicked again. The panel is drawn in {@link Stage} coordinates.
 * 
 */
public abstract class IconWithPanel extends IconButton {

	private static final Vector2 TEMP = new Vector2();
	private static HiddenPanel openPanel;

	private static final ClickListener showOrHide = new ClickListener() {

		public void clicked(InputEvent event, float x, float y) {
			IconWithPanel icon = (IconWithPanel) event.getListenerActor();
			if (icon.panel.hasParent()) {
				icon.hidePanel();
			} else {
				icon.showPanel();
			}
		};
	};

	protected HiddenPanel panel;

	public IconWithPanel(Drawable icon, float padding, Skin skin,
			String styleName) {
		super(icon, padding, skin, styleName);

	}

	public IconWithPanel(Drawable icon, float padding, Skin skin) {
		super(icon, padding, skin);

	}

	public IconWithPanel(Drawable icon, Skin skin) {
		super(icon, skin);

	}

	public IconWithPanel(String icon, float padding, Skin skin, String styleName) {
		super(icon, padding, skin, styleName);

	}

	public IconWithPanel(String icon, float padding, Skin skin) {
		super(icon, padding, skin);

	}

	public IconWithPanel(String icon, Skin skin) {
		super(icon, skin);

	}

	@Override
	protected void init(Drawable icon, float padding, Skin skin) {
		super.init(icon, padding, skin);
		panel = new HiddenPanel(skin) {
			@Override
			public void hide() {
				hide(getHideAction());
			}
		};
		addListener(showOrHide);
	}

	/**
	 * Invoked when the panel is going to be displayed, this method should
	 * return the {@link Action} used to display the {@link #panel}.
	 */
	protected abstract Action getShowAction(float x, float y);

	/**
	 * Invoked when the panel is going to be hidden, this method should return
	 * the {@link Action} used to hide the {@link #panel}.
	 */
	protected abstract Action getHideAction();

	private void showPanel() {
		if (openPanel != panel && openPanel != null) {
			openPanel.hide();
		}

		localToStageCoordinates(TEMP.set(0f, 0f));
		panel.show(getStage(), getShowAction(TEMP.x, TEMP.y));
		openPanel = panel;
	}

	private void hidePanel() {
		panel.hide(getHideAction());
	}

	/**
	 * Rounds and sets the given bounds for the {@link #panel}.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	protected void setPanelBounds(float x, float y, float width, float height) {
		panel.setBounds(MathUtils.round(x), MathUtils.round(y),
				MathUtils.round(width), MathUtils.round(height));
	}

	public HiddenPanel getPanel() {
		return panel;
	}
}
