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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Tabs widget that fires {@link ChangeEvent}s when the selected item changes.
 * 
 */
public class Tabs extends Table {

	private static final float SELECTION_ANIMATION = .2f;

	private Image selectedImage;
	private TextButton checked;
	private TabsStyle style;

	public Tabs(Skin skin) {
		this(skin.get(TabsStyle.class));
	}

	public Tabs(TabsStyle style) {
		background(style.background);
		this.style = style;
		if (style.color != null) {
			setColor(style.color);
		}
		selectedImage = new Image(style.selectedDrawable);
		if (style.selectedDrawableColor != null) {
			selectedImage.setColor(style.selectedDrawableColor);
		}
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		selectedImage.act(delta);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		selectedImage.draw(batch, parentAlpha);
	}

	public void setItems(String... items) {
		clearChildren();
		float veticalPad = WidgetBuilder.dpToPixels(style.buttonsVerticalPad);
		float horizontalPad = WidgetBuilder
				.dpToPixels(style.buttonsHorizontalPad);
		for (int i = 0; i < items.length; ++i) {
			String item = items[i];
			TextButton textButton = new TabButton(item);
			textButton.setName(item);
			if (i == 0) {
				checked = textButton;
			} else {
				textButton.setChecked(false);
			}
			textButton
					.pad(veticalPad, horizontalPad, veticalPad, horizontalPad);
			add(textButton).expand();
		}
	}

	@Override
	public void layout() {
		super.layout();
		if (checked != null) {
			selectedImage.setBounds(getX() + checked.getX(),
					getY() + checked.getY(), checked.getWidth(),
					selectedImage.getHeight());
		}
	}

	public TextButton getSelected() {
		return checked;
	}

	/**
	 * The style for {@link Tabs} widget.
	 */
	static public class TabsStyle {

		public TextButtonStyle textButtonStyle;

		public Color activeTextColor, disablexTextColor, color,
				selectedDrawableColor;

		public Drawable selectedDrawable;

		public float buttonsVerticalPad, buttonsHorizontalPad;

		/** Optional */
		public Drawable background;

		public TabsStyle() {
		}

		public TabsStyle(TabsStyle style) {
			this.textButtonStyle = style.textButtonStyle;
			this.background = style.background;
		}
	}

	private class TabButton extends TextButton {

		public TabButton(String text) {
			super(text, style.textButtonStyle);
		}

		@Override
		public void setChecked(boolean isChecked) {
			if (selectedImage.getActions().size > 0) {
				return;
			}
			if (isChecked) {
				if (checked == this) {
					return;
				}
				checked = this;
				for (Actor actor : Tabs.this.getChildren()) {
					TextButton btn = (TextButton) actor;
					if (btn != this) {
						btn.setChecked(false);
					}
				}
			} else if (checked == this) {
				return;
			}

			Color color = null;
			if (isChecked) {
				float targetX = Tabs.this.getX() + getX();
				if (targetX < selectedImage.getX()) {
					selectedImage.addAction(Actions.moveTo(targetX, getY()
							+ Tabs.this.getY(), SELECTION_ANIMATION,
							Interpolation.pow2Out));
					selectedImage.addAction(Actions.sequence(Actions.sizeTo(
							getWidth() + selectedImage.getWidth(),
							selectedImage.getHeight(), SELECTION_ANIMATION,
							Interpolation.pow2Out), Actions.sizeTo(getWidth(),
							selectedImage.getHeight(), SELECTION_ANIMATION,
							Interpolation.pow2Out)));
				} else {
					selectedImage.addAction(Actions.sequence(Actions.sizeTo(
							getWidth() + selectedImage.getWidth(),
							selectedImage.getHeight(), SELECTION_ANIMATION,
							Interpolation.pow2Out),
							Actions.parallel(
									Actions.sizeTo(getWidth(),
											selectedImage.getHeight(),
											SELECTION_ANIMATION,
											Interpolation.pow2Out), Actions
											.moveTo(targetX,
													getY() + Tabs.this.getY(),
													SELECTION_ANIMATION,
													Interpolation.pow2Out))));
				}
				color = style.activeTextColor;
			} else {
				color = style.disablexTextColor;
			}
			getLabel().setColor(color);
			super.setChecked(isChecked);
		}
	}
}
