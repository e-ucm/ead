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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.engine.gdx.AbstractWidget;

public class ImageChooser extends AbstractWidget {

	private float prefWidth;

	private float prefHeight;

	private ImageChooserStyle style;

	private Image image;

	private Image selectButton;

	public ImageChooser(Skin skin, float prefWidth, float prefHeight) {
		this.style = skin.get(ImageChooserStyle.class);
		this.prefWidth = prefWidth;
		this.prefHeight = prefHeight;
		image = new Image();
		addActor(image);
		selectButton = new Image(style.selectIcon);
		addActor(selectButton);
	}

	public void setImage(Drawable image) {
		this.image.setDrawable(image);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		validate();
		style.background.draw(batch, getX(), getY(), getWidth(), getHeight());
		super.draw(batch, parentAlpha);
	}

	@Override
	public void layout() {
		setBounds(image, 0, 0, getWidth(), getHeight());
		setBounds(selectButton, 0, 0, selectButton.getPrefWidth(),
				selectButton.getPrefHeight());
	}

	@Override
	public float getPrefWidth() {
		return prefWidth;
	}

	@Override
	public float getPrefHeight() {
		return prefHeight;
	}

	@Override
	public float getMaxWidth() {
		return prefWidth;
	}

	@Override
	public float getMaxHeight() {
		return prefHeight;
	}

	public static class ImageChooserStyle {
		public Drawable background;
		public Drawable selectIcon;
	}

}
