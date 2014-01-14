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
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.mockup.core.control.screens.AbstractScreen;
import es.eucm.ead.mockup.core.utils.Constants;
import es.eucm.ead.mockup.core.view.UIAssets;
import es.eucm.ead.mockup.core.view.ui.Panel;

public class DeleteComponent {

	private DeletePanel panel;
	private TextButton button;
	private Color color;

	public DeleteComponent(Skin skin) {
		button = new TextButton("Borrar", skin);
		panel = new DeletePanel(skin, "opaque");
		button.addListener(new ClickListener() {
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

	private class DeletePanel extends Panel {

		private final int WIDTH=350;
		private final int HEIGHT=250;
		private Slider slider;
		private Label rubberSize;

		public DeletePanel(Skin skin) {
			super(skin, "default");
		}

		public DeletePanel(Skin skin, String styleName) {
			super(skin, styleName);
			setHeight(HEIGHT);
			setWidth(WIDTH);
			setVisible(false);
			setColor(Color.DARK_GRAY);
			setModal(false);

			defaults().fill().expand();
			
			Label label=new Label("Herramienta de borrado", skin, "default-thin-opaque");
			label.setWrap(true);
			label.setAlignment(Align.center);

			rubberSize=new Label("1", skin, "default");
			rubberSize.setAlignment(Align.center);
			rubberSize.setFontScale(0.7f);
			rubberSize.setColor(Color.LIGHT_GRAY);
			
			slider = new Slider(1, 60, 0.5f, false, skin, "left-horizontal");
			slider.addListener(new InputListener(){
				@Override
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					actState();
					return true;
				}
				
				@Override
				public void touchDragged(InputEvent event, float x, float y,
						int pointer) {
					actState();
				}
				
				@Override
				public void touchUp(InputEvent event, float x, float y,
						int pointer, int button) {
					actState();
				}
			});
			
			add(label);
			row();
			add("Tamaño de goma");
			row();
			add(rubberSize);
			row();
			add(slider);
			//debug();
			
		}
		
		public void actCoordinates(){
			setX(button.getX() + (button.getWidth() / 2) - (WIDTH / 2));
			setY(Constants.SCREENH - UIAssets.TOOLBAR_HEIGHT - HEIGHT - 10);
		}
		
		public void actState(){
			if((""+slider.getValue())!=rubberSize.getText()){
				rubberSize.setText(""+slider.getValue());
			}
		}

		@Override
		public void show() {
			super.show();
			// addAction(Actions.moveTo(0, y, fadeDuration));
		}

		@Override
		public void hide() {
			super.hide();
			//addAction(Actions.moveTo(x, y, fadeDuration));
		}

		public float getSize() {
			return slider.getValue();
		}
	}

	public DeletePanel getPanel() {
		return panel;
	}

	public TextButton getButton() {
		return button;
	}

	public Color getColor() {
		return color;
	}

	public float getPincelSize() {
		return panel.getSize();
	}
	
	public void actCoordinates(){
		panel.actCoordinates();
	}
}
