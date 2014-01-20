/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.mockup.core.view.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * A button displayed in the MainMenu and PanelMenu Screens.
 */
public class ToolbarButton extends ImageButton {
	private static final float DEFAULT_SCALE_PROGRESSION = 1.8f;	
	private static final float DEFAULT_FONT_SCALE = .4f;	
	private static final float IMAGE_PAD_TOP = 5f;
	private static float LABEL_CELL_HEIGHT;	

	private static Drawable btn_default_pressed;
	private static Drawable btn_default_focused;
	private static Drawable btn_default_disabled;

	public ToolbarButton(Drawable imageUp, Skin skin) {
		super(imageUp);
		initialize(skin);
	}
	
	public ToolbarButton(Drawable imageUp, String name, Skin skin) {
		super(imageUp);
		initializeLabel(name, skin, DEFAULT_FONT_SCALE);
		initialize(skin);
	}
	
	public ToolbarButton(Drawable imageUp, String name, Skin skin, float fontScale) {
		super(imageUp);
		initializeLabel(name, skin, fontScale);
		initialize(skin);
	}
	
	/**
	 * Experimental constructor, draws the label under the cell.
	 * Usage: toolbar specific icons.
	 * 
	 * TODO ---under construction---
	 * 
	 * @param imageUp
	 * @param name
	 * @param skin
	 * @param fontScale
	 */
	public ToolbarButton(Drawable imageUp, String name, Skin skin, float fontScale, int experimental) {
		super(imageUp);		
		this.row();
		Label mName = new Label(name, skin);
		mName.setFontScale(fontScale);
		/*mName.setAlignment(Align.bottom);		
		float labelCellHeight = LABEL_CELL_HEIGHT * fontScale * DEFAULT_SCALE_PROGRESSION; */
		this.add(mName);
		initialize(skin);
		//getImageCell().size(getPrefWidth(), getPrefHeight());
		//debug();
	}
	
	private void initializeLabel(String name, Skin skin, float fontScale){
		this.getImageCell().expand().padTop(IMAGE_PAD_TOP);
		this.row();
		Label mName = new Label(name, skin);
		mName.setFontScale(fontScale);
		mName.setAlignment(Align.bottom);		
		float labelCellHeight = LABEL_CELL_HEIGHT * fontScale * DEFAULT_SCALE_PROGRESSION; 
		this.add(mName).height(labelCellHeight).bottom();
	}

	private void initialize(Skin skin){
		ImageButtonStyle mStyle = getStyle();	

		mStyle.down = btn_default_pressed;
		mStyle.over = btn_default_focused;
		mStyle.checked = btn_default_pressed;
		mStyle.checkedOver = btn_default_focused;
		mStyle.disabled = btn_default_disabled;
	}

	/**
	 * We initialize our styles. 
	 * This method should be called right after the skin was loaded 
	 * and initialized.
	 */
	public static void loadStyle(Skin skin){
		btn_default_pressed = skin.getDrawable("blueBlackMedium");
		btn_default_focused = btn_default_pressed;
		btn_default_disabled = skin.getDrawable("dialogDimObscure");
		
		LABEL_CELL_HEIGHT = skin.getFont("default-font").getBounds("A").height;
	}
}
