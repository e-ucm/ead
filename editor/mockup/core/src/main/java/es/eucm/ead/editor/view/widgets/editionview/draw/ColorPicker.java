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
package es.eucm.ead.editor.view.widgets.editionview.draw;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.view.widgets.PositionedHiddenPanel.Position;
import es.eucm.ead.editor.view.widgets.editionview.SlideColorPicker;
import es.eucm.ead.editor.view.widgets.iconwithpanel.IconWithFadePanel;

public class ColorPicker extends IconWithFadePanel {

	private static final float TOP_PAD = 10F;

	protected SlideColorPicker picker;

	private Actor reference;

	public ColorPicker(boolean bottom, Skin skin) {
		this(bottom, skin, null);
	}

	public ColorPicker(boolean bottom, Skin skin, Actor reference) {
		super("colorpicker80x80", 5f, skin, bottom ? Position.BOTTOM
				: Position.TOP, "checkable");
		this.reference = reference == null ? this : reference;

		picker = new SlideColorPicker(skin) {
			@Override
			protected void colorChanged(Color newColor) {
				getIcon().setColor(newColor);
				panel.setColor(newColor);
				ColorPicker.this.colorChanged(newColor);
			}

			@Override
			public void draw(Batch batch, float parentAlpha) {
				super.draw(batch, parentAlpha);
				batch.setColor(Color.WHITE);
			}
		};
		panel.setReference(this.reference);

		if (!bottom) {
			panel.add(picker).padTop(TOP_PAD);
		} else {
			panel.add(picker);
		}
	}

	protected void colorChanged(Color newColor) {

	}

	public void colorChanged() {
		colorChanged(picker.getPickedColor());
	}

	public void showPanel() {
		if (reference.getY() - panel.getPrefHeight() < 0) {
			panel.setPosition(Position.TOP);
		} else {
			panel.setPosition(Position.BOTTOM);
		}
		picker.updateTexture();
		super.showPanel();
	}
}
