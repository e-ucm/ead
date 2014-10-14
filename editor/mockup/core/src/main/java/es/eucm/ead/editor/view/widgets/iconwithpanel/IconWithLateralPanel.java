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

import es.eucm.ead.editor.view.widgets.PositionedHiddenPanel;

/**
 * A {@link IconWithPanel} that has a scale in/out animation.
 */
public class IconWithLateralPanel extends IconWithPanel {

	public IconWithLateralPanel(String icon, float space, Skin skin) {
		super(icon, space, skin, null);

	}

	@Override
	protected PositionedHiddenPanel createPanel(Skin skin, int colPane) {
		return new Panel(skin, colPane) {
			@Override
			protected void positionPanel(float x, float y) {
				Stage stage = IconWithLateralPanel.this.getStage();
				boolean left = x < stage.getWidth() * .5f;
				setTransform(true);
				adjustBackground(left);
				float panelPrefHeight = y - space;
				float panelPrefY = 0f;
				float prefX = left ? 0 : stage.getWidth() - getPrefWidth();
				setPanelBounds(prefX, panelPrefY, getPrefWidth(),
						panelPrefHeight);
				setOrigin(left ? 0 : getWidth(), getHeight());
			}

			private void adjustBackground(boolean left) {
				setBackground(left ? "left_panel" : "right_panel");
			}
		};
	}

	@Override
	protected Action getShowAction() {
		panel.setScale(0f);
		return Actions.scaleTo(1f, 1f, IN_DURATION, Interpolation.sine);
	}

	@Override
	protected Action getHideAction() {
		return Actions.scaleTo(0f, 0f, OUT_DURATION);
	}
}
