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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.view.widgets.PositionedHiddenPanel;

public class IconWithScalePanel extends BaseIconWithScalePanel {

	public IconWithScalePanel(String icon, float space, Skin skin) {
		this(icon, space, skin, "checkable", null);
	}

	public IconWithScalePanel(String icon, float space, Skin skin, Color color) {
		this(icon, space, skin, "checkable", color);
	}

	public IconWithScalePanel(String icon, float space, Skin skin, String style) {
		this(icon, space, skin, style, null);
	}

	public IconWithScalePanel(String icon, float space, int colPane, Skin skin) {
		this(icon, space, colPane, skin, "checkable", null);
	}

	public IconWithScalePanel(String icon, float space, int colPane, Skin skin,
			Color color) {
		this(icon, space, colPane, skin, "checkable", color);
	}

	public IconWithScalePanel(String icon, float space, int colPane, Skin skin,
			String style) {
		this(icon, space, colPane, skin, style, null);
	}

	public IconWithScalePanel(String icon, float space, Skin skin,
			String style, Color color) {
		this(icon, space, -1, skin, style, color);
	}

	public IconWithScalePanel(String icon, float space, int colPane, Skin skin,
			String style, Color color) {
		super(icon, space, skin, null, colPane, style);
		panel.setBackground("panel");
		if (color != null) {
			panel.setColor(color);
		}
	}

	@Override
	protected PositionedHiddenPanel createPanel(Skin skin, int colPane) {
		return new Panel(skin, colPane) {
			@Override
			protected void positionPanel(float x, float y) {

				float panelPrefHeight = Math.min(getPrefHeight(),
						Gdx.graphics.getHeight());
				float panelPrefWidth = Math.min(getPrefWidth(),
						Gdx.graphics.getWidth());

				float coordinateX = 0;
				float coordinateY = 0;
				float originX = 0;
				float originY = 0;

				if (y > Gdx.graphics.getHeight() * 0.5f) {
					coordinateY = y - getPrefHeight() - space;
					originY = getPrefHeight();
				} else {
					coordinateY = y + reference.getHeight() + space;
				}

				if (x > Gdx.graphics.getWidth() * 0.5f) {
					coordinateX = x + reference.getWidth() - getPrefWidth();
					originX = getPrefWidth();
				} else {
					coordinateX = x;
				}

				setPanelBounds(coordinateX, coordinateY, panelPrefWidth,
						panelPrefHeight);

				setOrigin(originX, originY);
			}
		};
	}
}
