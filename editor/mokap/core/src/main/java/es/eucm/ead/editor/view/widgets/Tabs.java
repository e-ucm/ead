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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
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

	private TabsStyle style;

	private Image overlayImage;

	private TextButton selectedTabe;

	public Tabs(Skin skin) {
		this(skin.get(TabsStyle.class));
	}

	public Tabs(TabsStyle style) {
		this.style = style;
		overlayImage = new Image(style.selectedDrawable);
		if (style.selectedDrawableColor != null) {
			overlayImage.setColor(style.selectedDrawableColor);
		}
	}

	public void setItems(String... items) {
		clearChildren();
		addActor(overlayImage);
		float veticalPad = WidgetBuilder.dpToPixels(16);
		float horizontalPad = WidgetBuilder.dpToPixels(8);
		for (int i = 0; i < items.length; ++i) {
			String item = items[i];
			TextButton textButton = new TabButton(item);
			textButton.setName(item);
			textButton.setUserObject(i);
			if (i == 0) {
				selectedTabe = textButton;
			} else {
				textButton.setChecked(false);
			}
			textButton
					.pad(veticalPad, horizontalPad, veticalPad, horizontalPad);
			add(textButton).left();
		}
	}

	@Override
	public void layout() {
		super.layout();
		if (selectedTabe != null) {
			overlayImage.setBounds(selectedTabe.getX(), selectedTabe.getY(),
					selectedTabe.getWidth(), overlayImage.getHeight());
		}
	}

	public TextButton getSelectedTab() {
		return selectedTabe;
	}

	public int getSelectedTabIndex() {
		return (Integer) selectedTabe.getUserObject();
	}

	/**
	 * The style for {@link Tabs} widget.
	 */
	static public class TabsStyle {

		public TextButtonStyle textButtonStyle;

		public Color activeTextColor, disablexTextColor, selectedDrawableColor;

		public Drawable selectedDrawable;

	}

	private class TabButton extends TextButton {

		public TabButton(String text) {
			super(text, style.textButtonStyle);
		}

		@Override
		public void setChecked(boolean isChecked) {
			if (overlayImage.getActions().size > 0) {
				return;
			}
			if (isChecked) {
				if (selectedTabe == this) {
					return;
				}
				selectedTabe = this;
				for (Cell cell : Tabs.this.getCells()) {
					TextButton btn = (TextButton) cell.getActor();
					if (btn != this) {
						btn.setChecked(false);
					}
				}
			} else if (selectedTabe == this) {
				return;
			}

			Color color;
			if (isChecked) {
				float targetX = getX();
				if (targetX < overlayImage.getX()) {
					overlayImage.addAction(Actions.moveTo(targetX, getY(),
							SELECTION_ANIMATION, Interpolation.pow2Out));
					overlayImage.addAction(Actions.sequence(Actions.sizeTo(
							getWidth() + overlayImage.getWidth(),
							overlayImage.getHeight(), SELECTION_ANIMATION,
							Interpolation.pow2Out), Actions.sizeTo(getWidth(),
							overlayImage.getHeight(), SELECTION_ANIMATION,
							Interpolation.pow2Out)));
				} else {
					overlayImage.addAction(Actions.sequence(Actions.sizeTo(
							getWidth() + overlayImage.getWidth(),
							overlayImage.getHeight(), SELECTION_ANIMATION,
							Interpolation.pow2Out),
							Actions.parallel(
									Actions.sizeTo(getWidth(),
											overlayImage.getHeight(),
											SELECTION_ANIMATION,
											Interpolation.pow2Out), Actions
											.moveTo(targetX, getY(),
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
