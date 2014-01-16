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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.mockup.core.control.screens.AbstractScreen;
import es.eucm.ead.mockup.core.utils.Constants;
import es.eucm.ead.mockup.core.view.UIAssets;
import es.eucm.ead.mockup.core.view.ui.GridPanel;
import es.eucm.ead.mockup.core.view.ui.Panel;

public class DrawComponent {

	private PaintPanel panel;
	private TextButton button;
	private Color color;

	/**
	 * Create a panel without palette.
	 */
	public DrawComponent(Skin skin, String name, String description, float width, float height) {
		button = new TextButton(name, skin);
		panel = new PaintPanel(skin, "opaque", description, width, height);
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
	
	/**
	 * Create a panel with palette.
	 */
	public DrawComponent(Skin skin, String name, String description, Color color, float width, float height){
		button = new TextButton(name, skin);
		this.color=color; //the color default
		panel = new PaintPanel(skin, "opaque", description, width, height);
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

	private class PaintPanel extends Panel {

		private float width; //350
		private float height; //550
		private Slider slider;
		private Label brushSize;
		private GridPanel<Actor> gridPanel;
		
		private Image cir;

		//TODO: Need changes for show the size of brush or text with a circle o letter.
		public PaintPanel(Skin skin, String styleName, String description, float width, float height) {
			
			super(skin, styleName);
			
			this.height=height;
			this.width=width;
			setHeight(height);
			setWidth(width);
			
			setVisible(false);
			setModal(false);
			setColor(Color.DARK_GRAY);

			if(color!=null){
				createPalette(skin);
				Pixmap aux2 = new Pixmap(500, 500, Format.RGBA8888);
				Texture pixTex;
				
				Blending b = Pixmap.getBlending();
				Pixmap.setBlending(Blending.None);
				aux2.fill();
				Pixmap.setBlending(b);

				aux2.setColor(color);
				aux2.fillCircle(250, 250, 245);
				pixTex = new Texture(aux2);
				pixTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				cir=new Image(pixTex);//cir.setScaleX(0.2f);
			}

			defaults().fill().expand();

			Label label = new Label(description, skin,
					"default-thin-opaque");
			label.setWrap(true);
			label.setAlignment(Align.center);

			brushSize = new Label("1", skin, "default");
			brushSize.setAlignment(Align.center);
			brushSize.setFontScale(0.7f);
			brushSize.setColor(Color.LIGHT_GRAY);

			slider = new Slider(1, 60, 0.5f, false, skin, "left-horizontal");
			slider.addListener(new InputListener() {
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
			add("Tamaño de pincel");
			row();
			add(slider);
			row();
			if(color!=null){
				add(cir).align(Align.center).expand(false, false).fill(false).size(60, 60);
				row();
				add("Color");
				row();
				add(gridPanel);
			}
			else{
				row();
				add(brushSize);
			}
			//debug();

		}

		/**
		 * Set the panel'coordinates according to the button's coordinates
		 */
		public void actCoordinates() {
			setX(button.getX() + (button.getWidth() / 2) - (width / 2));
			setY(Constants.SCREENH - UIAssets.TOOLBAR_HEIGHT - height - 10);
		}

		/**
		 * Show the value of slider
		 */
		public void actState() {
			if (("" + slider.getValue()) != brushSize.getText()) {
				brushSize.setText("" + slider.getValue());
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
		
		private void createPalette(Skin skin){
			Pixmap aux = new Pixmap(50, 50, Format.RGB888);
			final int COLS = 4, ROWS = 3;
			final Color[][] colrs = {
					{ Color.BLACK, Color.BLUE, Color.CYAN,
							new Color(.5f, .75f, .32f, 1f) },
					{ Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK },
					{ Color.RED, Color.LIGHT_GRAY, Color.YELLOW, Color.WHITE } };
			gridPanel = new GridPanel<Actor>(skin, ROWS, COLS, 20);
			ClickListener colorListener = new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					event.cancel();
					Image list = (Image) event.getListenerActor();
					color = list.getColor();
					System.out.println("color seteado " + list.getName() + " "
							+ list.getColor().toString());
				}
			};
			for (int i = 0; i < ROWS; i++) {
				for (int j = 0; j < COLS; j++) {
					Color c = colrs[i][j];
					aux.setColor(c);
					aux.fill();
					final Image colorB = new Image(new Texture(aux)); // FIXME unmanaged upenGL textures, TODO reload onResume (after pause)
					colorB.setColor(c);
					colorB.setName("" + i + j);
					colorB.addListener(colorListener);
					gridPanel.addItem(colorB, i, j).fill();
				}
			}
			aux.dispose();
		}
	}

	public PaintPanel getPanel() {
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

	public void actCoordinates() {
		panel.actCoordinates();
	}
}
