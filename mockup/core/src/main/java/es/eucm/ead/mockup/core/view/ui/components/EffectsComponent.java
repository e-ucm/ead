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
package es.eucm.ead.mockup.core.view.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.mockup.core.control.screens.AbstractScreen;
import es.eucm.ead.mockup.core.utils.Constants;
import es.eucm.ead.mockup.core.view.UIAssets;
import es.eucm.ead.mockup.core.view.ui.Panel;
import es.eucm.ead.mockup.core.view.ui.ToolbarButton;

public class EffectsComponent {

	private EffectsPanel panel;
	private Button button;

	public enum Type {
		BRUSH, RUBBER, TEXT
	}

	public EffectsComponent( String imageUp,  String name, Skin skin,String description, float width, float height) {
		this.button = new ToolbarButton(skin.getDrawable(imageUp), name, skin);
		this.panel = new EffectsPanel(skin, "opaque", description, width, height);
		this.button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				event.cancel();
				if (!panel.isVisible()) {
					AbstractScreen.mockupController.show(panel);
				} else {
					AbstractScreen.mockupController.hide(panel);
				}
			}
		});
	}

	private class EffectsPanel extends Panel {

		private float width;
		private float height;

		public EffectsPanel(Skin skin, String styleName, String description, float width, float height) {
			super(skin, styleName);

			this.height = height;
			this.width = width;
			
			setHeight(height);
			setWidth(width);
			
			setVisible(false);
			setModal(false);
			setColor(Color.DARK_GRAY);
			
			//FIXME *repeated code*
			Label label = new Label(description, skin, "default-thin-opaque");
			label.setWrap(true);
			label.setAlignment(Align.center);
			label.setFontScale(0.7f);
					
			Image backImg1 = new Image(skin.getRegion("icon-blitz")); //edit element img
			final Button prop1 = new Button(skin, "default");
			prop1.add(backImg1);
			prop1.scale(0.2f);
			
			Image backImg2 = new Image(skin.getRegion("icon-blitz")); //edit element img
			final Button prop2 = new Button(skin, "default");
			prop2.add(backImg2);
			prop2.scale(0.2f);
			
			Image backImg3 = new Image(skin.getRegion("icon-blitz")); //edit element img
			final Button prop3 = new Button(skin, "default");
			prop3.add(backImg3);
			prop3.scale(0.2f);
			//END FIXME
			
			Table table = new Table(skin);
			
			CheckBox cb1 = new CheckBox("Efecto 1", skin);
			CheckBox cb2 = new CheckBox("Efecto 2", skin);
			CheckBox cb3 = new CheckBox("Efecto 3", skin);
		
			table.add(prop1).left();
			table.add(cb1).left().expand().fill();
			table.row();
			table.add(prop2).left();
			table.add(cb2);
			table.row();
			table.add(prop3).left();
			table.add(cb3);
			table.debug();
			
			defaults().fill().expand();
			add(label);
			row();
			add(table);
		}

		/**
		 * Set the panel'coordinates according to the button's coordinates
		 */
		public void actCoordinates() {
			if((button.getX() + (button.getWidth() / 2) - (width / 2) + width)<AbstractScreen.stagew){
				setX(button.getX() + (button.getWidth() / 2) - (width / 2));
			} else {
				setX(AbstractScreen.stagew-width-5);
			}
				setY(Constants.SCREENH - UIAssets.TOOLBAR_HEIGHT - height - 10);
		}

		@Override
		public void show() {
			super.show();
		}

		@Override
		public void hide() {
			super.hide();
		}

	}

	public EffectsPanel getPanel() {
		return panel;
	}

	public Button getButton() {
		return button;
	}

	public void actCoordinates() {
		panel.actCoordinates();
	}
}
