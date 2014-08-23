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

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * A {@link IconWithPanel} that has a Fade in/out animation.
 */
public class IconWithFadePanel extends IconWithPanel {

	private static final float IN_DURATION = .3F;
	private static final float OUT_DURATION = .2F;
	private Position position;

	public static enum Position {
		BOTTOM, RIGHT
	}

	public IconWithFadePanel(String icon, float padding, Skin skin) {
		this(icon, padding, -1, skin, Position.RIGHT);

	}

	public IconWithFadePanel(String icon, float padding, Skin skin,
			Position position) {
		this(icon, padding, -1, skin, position);

	}

	public IconWithFadePanel(String icon, float padding, float size, Skin skin) {
		this(icon, padding, size, skin, Position.RIGHT);

	}

	public IconWithFadePanel(String icon, float padding, float size, Skin skin,
			Position position) {
		super(icon, padding, size, skin);
		this.position = position;

	}

	@Override
	protected void init(Drawable icon, float padding, Skin skin) {
		super.init(icon, padding, skin);
		panel.setBackground("panel");
	}

	@Override
	protected Action getShowAction(float x, float y) {
		positionPanel(x, y);
		panel.getColor().a = 0f;
		return fadeIn(IN_DURATION, Interpolation.fade);
	}

	@Override
	protected Action getHideAction() {
		return fadeOut(OUT_DURATION, Interpolation.fade);
	}

	/**
	 * Invoked when this panels is going to be shown, use this method to decide
	 * the bounds of the panel.
	 * 
	 * @param y
	 *            position of the icon in {@link Stage} coordinates.
	 * @param x
	 *            position of the icon in {@link Stage} coordinates.
	 */
	protected void positionPanel(float x, float y) {
		float panelPrefHeight = panel.getPrefHeight();
		if (position == Position.RIGHT) {
			float panelPrefY = Math.max(0f, y + getHeight() - panelPrefHeight);
			setPanelBounds(x + getWidth(), panelPrefY, panel.getPrefWidth(),
					panelPrefHeight);
		} else if (position == Position.BOTTOM) {
			float panelPrefWidth = panel.getPrefWidth();
			setPanelBounds(x + (getWidth() - panelPrefWidth) * .5f, y
					- panelPrefHeight, panel.getPrefWidth(), panelPrefHeight);
		}
	}
}
