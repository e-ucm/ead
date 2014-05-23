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
package es.eucm.ead.editor.view.widgets.mockup.buttons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.view.widgets.mockup.panels.HiddenPanel;

/**
 * A button displayed in the tool bar of the Edition screens.
 */
public class ToolbarButton extends IconButton {
	private static final String DEFAULT_TOOLBAR_BUTTON_FONT = "default-font";
	private static final float DEFAULT_SCALE_PROGRESSION = .65f;
	private static final float DEFAULT_FONT_SCALE = .5f;
	private static final float IMAGE_PAD_TOP = 2f;
	private static final float LABEL_PAD_BOTTOM = 1f;
	private static float LABEL_CELL_HEIGHT;

	private Label name;

	/**
	 * Represents the panel that is expected to be shown when this button is
	 * touched.
	 */
	private HiddenPanel boundPanel;

	public ToolbarButton(Vector2 viewport, Skin skin, String image) {
		super(viewport, skin, image);
		initialize(skin, true);
	}

	public ToolbarButton(Vector2 viewport, Drawable imageUp, String name,
			Skin skin) {
		super(viewport, imageUp);
		initializeLabel(name, skin, DEFAULT_FONT_SCALE);
		initialize(skin, true);
	}

	public ToolbarButton(Vector2 viewport, String imageUp, String name,
			boolean toggle, Skin skin) {
		super(viewport, skin, imageUp);
		initializeLabel(name, skin, DEFAULT_FONT_SCALE);
		initialize(skin, toggle);
	}

	public ToolbarButton(Vector2 viewport, String imageUp, String name,
			Skin skin) {
		super(viewport, skin, imageUp);
		initializeLabel(name, skin, DEFAULT_FONT_SCALE);
		initialize(skin, true);
	}

	private void initializeLabel(String name, Skin skin, float fontScale) {
		this.bottom();
		this.getImageCell().expand().padTop(IMAGE_PAD_TOP);
		this.row();
		LABEL_CELL_HEIGHT = skin.getFont(DEFAULT_TOOLBAR_BUTTON_FONT)
				.getBounds("A").height;
		this.name = new Label(name, skin);
		this.name.setFontScale(fontScale);
		final float labelCellHeight = LABEL_CELL_HEIGHT
				* DEFAULT_SCALE_PROGRESSION;
		this.add(this.name).height(labelCellHeight).bottom()
				.padBottom(LABEL_PAD_BOTTOM);
	}

	private void initialize(Skin skin, boolean toggle) {
		final ImageButtonStyle mStyle = getStyle();

		final Drawable btn_default_pressed = skin
				.getDrawable("blueBlackMedium");
		final Drawable btn_default_disabled = skin
				.getDrawable("dialogDimObscure");
		final Drawable btn_default_focused = btn_default_disabled;

		mStyle.up = null;
		mStyle.down = btn_default_pressed;
		mStyle.over = btn_default_focused;
		mStyle.checked = btn_default_pressed;
		if (toggle) {
			mStyle.checkedOver = btn_default_focused;
			mStyle.checked = btn_default_focused;
		}
		mStyle.disabled = btn_default_disabled;

		setStyle(mStyle);
	}

	/**
	 * Automatically hides it's focus listener if isChecked is false.
	 */
	@Override
	public void setChecked(boolean isChecked) {
		if (!isChecked && this.boundPanel != null && boundPanel.isVisible()) {
			this.boundPanel.hide();
		}
		super.setChecked(isChecked);
	}

	public void setFocusListener(HiddenPanel boundPanel) {
		this.boundPanel = boundPanel;
	}

	@Override
	public float getPrefWidth() {
		if (this.name != null
				&& super.getPrefWidth() < this.name.getPrefWidth())
			return this.name.getPrefWidth();
		else
			return super.getPrefWidth();
	}
}
