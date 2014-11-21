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

	protected Image iconImage;

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

	public IconButton(String name, String icon, float padding, Skin skin) {
		this(name, skin.getDrawable(icon), padding, skin);
	}

	public IconButton(String icon, float padding, Skin skin, String styleName) {
		this(icon, skin.getDrawable(icon), padding, skin, styleName);
	}

	public IconButton(String icon, Skin skin, String styleName) {
		this(icon, icon, 0, skin, styleName);
	}

	public IconButton(String name, String icon, float padding, Skin skin,
			String styleName) {
		this(name, skin.getDrawable(icon), padding, skin, styleName);
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
		this(null, icon, padding, skin, "default");
	}

	public IconButton(String name, Drawable icon, float padding, Skin skin) {
		this(name, icon, padding, skin, "default");
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
	public IconButton(String name, Drawable icon, float padding, Skin skin,
			String styleName) {
		super(skin);
		setStyle(skin.get(styleName, IconButtonStyle.class));
		init(icon, padding, skin);
		setName(name);
	}

	public IconButton(Drawable drawable, float padding, Skin skin,
			String styleName) {
		this(null, drawable, padding, skin, styleName);
	}

	protected void init(Drawable icon, float padding, Skin skin) {
		iconImage = new Image(icon);
		iconImage.setScaling(Scaling.fit);
		iconImage.setTouchable(Touchable.disabled);
		add(iconImage).pad(padding);
		setDisabled(false);
	}

	/**
	 * Change the {@link Button#isDisabled} attribute and accordingly the color
	 * of the image.
	 * <p/>
	 * The background color when disabled is managed with
	 * {@link ButtonStyle#disabled} attribute in {@link Button#draw} method.
	 * 
	 * @param isDisabled
	 */
	public void setDisabled(boolean isDisabled) {
		super.setDisabled(isDisabled);
		if (isDisabled) {
			if (style.disabledColor != null) {
				iconImage.setColor(style.disabledColor);
			}
		} else {
			if (style.color != null) {
				iconImage.setColor(style.color);
			}
		}
	}

	@Override
	public void setChecked(boolean isChecked) {
		super.setChecked(isChecked);
		if (isChecked && style.checkedColor != null) {
			iconImage.setColor(style.checkedColor);
		} else {
			iconImage.setColor(style.color);
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
		 * {@link IconButtonStyle#disabledColor} is used to change the
		 * {@link Color} of the image used in {@link IconButton} when the button
		 * is disabled (it does not include an alternative image).
		 */
		public Color disabledColor;

		/**
		 * {@link IconButtonStyle#color} is used to change the {@link Color} of
		 * the image used in {@link IconButton} when the button is activated (it
		 * does not include an alternative image).
		 */
		public Color color;

		public Color checkedColor;

		/**
		 * Default constructor used for reflection
		 */
		public IconButtonStyle() {
		}

		public IconButtonStyle(Color color, Color disabledColor) {
			this.color = color;
			this.disabledColor = disabledColor;
		}

		public IconButtonStyle(IconButtonStyle iconButtonStyle) {
			super(iconButtonStyle);
			this.color = iconButtonStyle.color;
			this.disabledColor = iconButtonStyle.disabledColor;
		}

	}

	public Image getIcon() {
		return iconImage;
	}
}
