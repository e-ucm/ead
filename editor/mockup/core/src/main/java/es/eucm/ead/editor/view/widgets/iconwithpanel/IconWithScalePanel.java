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

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * A {@link IconWithPanel} that has a scale in/out animation.
 */
public class IconWithScalePanel extends IconWithPanel {

	private static final float IN_DURATION = .3F;
	private static final float OUT_DURATION = .2F;

	public IconWithScalePanel(String icon, float padding, Skin skin,
			String styleName) {
		super(icon, padding, skin, styleName);

	}

	public IconWithScalePanel(String icon, float padding, float size, Skin skin) {
		super(icon, padding, size, skin);

	}

	public IconWithScalePanel(String icon, float padding, Skin skin) {
		super(icon, padding, skin);

	}

	private void adjustBackground(boolean left) {
		panel.setBackground(left ? "left_panel" : "right_panel");
	}

	@Override
	protected Action getShowAction(float x, float y) {
		boolean left = x < getStage().getWidth() * .5f;
		adjustBackground(left);
		positionPanel(x, y, left);
		panel.setOrigin(left ? 0 : panel.getWidth(), panel.getHeight());
		panel.setScale(0f);
		panel.setTransform(true);
		return Actions.scaleTo(1f, 1f, IN_DURATION, Interpolation.sine);
	}

	@Override
	protected Action getHideAction() {
		panel.setTransform(true);
		return Actions.scaleTo(0f, 0f, OUT_DURATION);
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
	protected void positionPanel(float x, float y, boolean left) {
		float panelPrefHeight = y;
		float panelPrefY = 0f;
		float prefX = left ? 0 : getStage().getWidth() - panel.getPrefWidth();
		setPanelBounds(prefX, panelPrefY, panel.getPrefWidth(), panelPrefHeight);
	}
}
