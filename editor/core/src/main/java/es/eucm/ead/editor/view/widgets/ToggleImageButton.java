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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.engine.gdx.AbstractWidget;

public class ToggleImageButton extends AbstractWidget {

	private ToggleImageButtonStyle style;

	private Image image;

	private ClickListener clickListener;

	private boolean checked;

	public ToggleImageButton(Drawable drawable, Skin skin) {
		this.style = skin.get(ToggleImageButtonStyle.class);
		image = new Image(drawable);
		addActor(image);
		addListener(clickListener = new ClickListener());
		checked = false;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	@Override
	public float getPrefWidth() {
		return image.getPrefWidth() + style.pad * 2;
	}

	@Override
	public float getPrefHeight() {
		return image.getPrefHeight() + style.pad * 2;
	}

	@Override
	public float getMaxWidth() {
		return getPrefWidth();
	}

	@Override
	public float getMaxHeight() {
		return getPrefHeight();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		validate();

		if (checked) {
			style.pressed.draw(batch, getX(), getY(), getWidth(), getHeight());
		}

		if (clickListener.isPressed()) {
			style.pressed.draw(batch, getX(), getY(), getWidth(), getHeight());
		}

		if (clickListener.isOver()) {
			style.over.draw(batch, getX(), getY(), getWidth() - 1, getHeight());
		}
		super.draw(batch, parentAlpha);
	}

	@Override
	public void layout() {
		setBounds(image, style.pad, style.pad, getWidth() - style.pad * 2,
				getHeight() - style.pad * 2);
	}

	public static class ToggleImageButtonStyle {
		public Drawable over, pressed;
		public float pad = 10.0f;
	}
}
