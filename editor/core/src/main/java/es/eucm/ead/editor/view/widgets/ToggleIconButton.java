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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * A button with two icons that automatically change between one or another when
 * clicked.
 */
public class ToggleIconButton extends IconButton {

	private Drawable icon1, icon2;

	/**
	 * @param icon
	 *            the identifier of the icon drawable inside the given skin
	 * @param skin
	 *            the skin
	 */
	public ToggleIconButton(String icon1, String icon2, Skin skin) {
		super(icon1, 0, skin);
		this.icon1 = iconImage.getDrawable();
		this.icon2 = skin.getDrawable(icon2);
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				buttonClicked();
			}
		});
	}

	/**
	 * Changes the drawable of the button. Convenience method that should be
	 * overridden if needed.
	 */
	protected void buttonClicked() {
		if (iconImage.getDrawable() == icon1) {
			iconImage.setDrawable(icon2);
		} else {
			iconImage.setDrawable(icon1);
		}
	}

}
