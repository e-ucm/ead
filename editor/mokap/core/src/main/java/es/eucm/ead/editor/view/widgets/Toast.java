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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class Toast extends Container<Label> {

	Label label;

	private float height;

	public Toast(Skin skin) {
		this(skin.get(ToastStyle.class), -1);
	}

	public Toast(Skin skin, float height) {
		this(skin.get(ToastStyle.class), height);
	}

	public Toast(Skin skin, String style, float height) {
		this(skin.get(style, ToastStyle.class), height);
	}

	public Toast(ToastStyle style, float height) {
		super(new Label("", style.label));
		this.setBackground(style.background);
		Color g = style.color;
		this.setColor(g);
		this.height(height);
	}

	@Override
	protected void drawBackground(Batch batch, float parentAlpha, float x,
			float y) {
		super.drawBackground(batch, parentAlpha, x, y);
		batch.setColor(Color.WHITE);
	}

	public void setText(String text) {
		getActor().setText(text);
	}

	@Override
	public float getPrefHeight() {
		return height > 0 ? height : super.getPrefHeight();
	}

	public static class ToastStyle {

		public Drawable background;

		public Color color;

		public LabelStyle label;

	}

}
