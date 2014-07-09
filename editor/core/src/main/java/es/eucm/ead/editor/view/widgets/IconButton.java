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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import es.eucm.ead.editor.view.tooltips.Tooltip;
import com.badlogic.gdx.graphics.Color;

/**
 * A button with an icon
 */
public class IconButton extends Button implements Tooltip {

	private String tooltip;

	private Image iconImage;

	private IconButtonStyle style;

	/**
	 * @param icon
	 *            the identifier of the icon drawable inside the given skin
	 * @param skin
	 *            the skin
	 */
	public IconButton(String icon, Skin skin) {
		this(icon, 0, skin);
	}

	/**
	 * @param icon
	 *            the identifier of the icon drawable inside the given skin
	 * @param padding
	 *            padding of the icon inside the button
	 * @param skin
	 *            the skin
	 */
	public IconButton(String icon, float padding, Skin skin) {
		this(skin.getDrawable(icon), padding, skin);
	}

	public IconButton(String icon, float padding, Skin skin, String styleName) {
		this(skin.getDrawable(icon), padding, skin, styleName);
	}

	/**
	 * @param icon
	 *            the drawable with the icon
	 * @param skin
	 *            the skin
	 */
	public IconButton(Drawable icon, Skin skin) {
		this(icon, 0, skin);
	}

	/**
	 * @param icon
	 *            the drawable with the icon
	 * @param padding
	 *            padding of the icon inside the button
	 * @param skin
	 *            the skin
	 */
	public IconButton(Drawable icon, float padding, Skin skin) {
		super(skin);
		init(icon, padding, skin);
	}

	/**
	 * @param icon
	 *            the drawable with the icon
	 * @param padding
	 *            padding of the icon inside the button
	 * @param skin
	 *            the skin
	 * @param styleName
	 *            the button style name
	 */
	public IconButton(Drawable icon, float padding, Skin skin, String styleName) {
		super(skin.get(styleName, IconButtonStyle.class));
		init(icon, padding, skin);

	}

	private void init(Drawable icon, float padding, Skin skin) {

		setStyle(skin.get(IconButtonStyle.class));

		iconImage = new Image(icon);
		iconImage.setScaling(Scaling.fit);
		iconImage.setTouchable(Touchable.disabled);
		add(iconImage).pad(padding);
	}

	/**
	 * Change the {@link Button#isDisabled} attribute and accordingly the color
	 * of the image.
	 * 
	 * The background color when disabled is managed with
	 * {@link ButtonStyle#disabled} attribute in {@link Button#draw} method.
	 * 
	 * @param isDisabled
	 */
	public void setDisabled(boolean isDisabled) {
		super.setDisabled(isDisabled);
		if (isDisabled) {
			iconImage.setColor(style.disabledImageColor);
		} else {
			iconImage.setColor(style.enabledImageColor);
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		// iconImage does not restore the batch color, and its color is
		// transmitted
		batch.setColor(Color.WHITE);
	}

	public void setStyle(IconButtonStyle style) {
		super.setStyle(style);
		this.style = style;
	}

	/**
	 * Sets tooltip text for this button. It'll appear with mouse over
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	@Override
	public String getTooltip() {
		return tooltip;
	}

	@Override
	public float getXOffset() {
		return 0.5f;
	}

	@Override
	public float getYOffset() {
		return 0.f;
	}

	/**
	 * The style for {@link IconButton} See also {@link ButtonStyle}
	 */

	public static class IconButtonStyle extends ButtonStyle {

		/**
		 * {@link IconButtonStyle#disabledImageColor} is used to change the
		 * {@link Color} of the image used in {@link IconButton} when the button
		 * is disabled (it does not include an alternative image).
		 */
		public Color disabledImageColor;

		/**
		 * {@link IconButtonStyle#enabledImageColor} is used to change the
		 * {@link Color} of the image used in {@link IconButton} when the button
		 * is activated (it does not include an alternative image).
		 */
		public Color enabledImageColor;

		/**
		 * Default constructor used for reflection
		 */
		public IconButtonStyle() {
		}

		public IconButtonStyle(Color enabledImageColor, Color disabledImageColor) {
			this.enabledImageColor = enabledImageColor;
			this.disabledImageColor = disabledImageColor;
		}

		public IconButtonStyle(IconButtonStyle iconButtonStyle) {
			super(iconButtonStyle);
			this.enabledImageColor = iconButtonStyle.enabledImageColor;
			this.disabledImageColor = iconButtonStyle.disabledImageColor;
		}

	}
}
