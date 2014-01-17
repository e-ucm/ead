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
	
	public enum Type {
		BRUSH,
		RUBBER,
		TEXT
	}

	public DrawComponent(Skin skin, String name, String description, Type type, float width, float height) {
		this.color=Color.BLACK;
		this.button = new TextButton(name, skin);
		this.panel = new PaintPanel(skin, "opaque", description, type, width, height);
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
	
	private class PaintPanel extends Panel {

		private float width;
		private float height;
		private Type type;
		
		private Slider slider;
		private Label brushSize;
		private GridPanel<Actor> gridPanel;
		
		private Label textSample;
		private Image cir;
		private Texture pixTex;
		private Pixmap circleSample;
		
		private String tSize = "Tamaño de ";
		
		private final float maxPixRadius = 50f;		
		private final int pixmapWidthHeight = 100, center = pixmapWidthHeight/2;

		//TODO: Need changes for show the size of brush or text with a circle o letter.
		public PaintPanel(Skin skin, String styleName, String description, Type t, float width, float height) {
			
			super(skin, styleName);
			
			this.height=height;
			this.width=width;
			
			this.type=t;
			
			if(type==Type.TEXT)
			{
				tSize+="texto";
				this.textSample=new Label("AaBbCc...", skin);
			}else{
				tSize+="pincel";
			}
							
			setHeight(height);
			setWidth(width);
			
			setVisible(false);
			setModal(false);
			setColor(Color.DARK_GRAY);

			defaults().fill().expand();

			Label label = new Label(description, skin,
					"default-thin-opaque");
			label.setWrap(true);
			label.setAlignment(Align.center);

			if(type==Type.RUBBER){
				brushSize = new Label("1", skin, "default");
				brushSize.setAlignment(Align.center);
				brushSize.setFontScale(0.7f);
				brushSize.setColor(Color.LIGHT_GRAY);
			}

			slider = new Slider(1, 60, 0.5f, false, skin, "left-horizontal");
			slider.setValue(30);
			slider.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					if(type==Type.RUBBER){
						actState();
					} else {//Type.BRUSH o Type.TEXT
						updateDemoColor();
					}
					return true;
				}

				@Override
				public void touchDragged(InputEvent event, float x, float y,
						int pointer) {
					if(type==Type.RUBBER){
						actState();
					} else {//Type.BRUSH o Type.TEXT
						updateDemoColor();
					}
				}

				@Override
				public void touchUp(InputEvent event, float x, float y,
						int pointer, int button) {
					if(type==Type.RUBBER){
						actState();
					} else {//Type.BRUSH o Type.TEXT
						updateDemoColor();
					}
				}
			});
			
			if(type!=Type.RUBBER){
				createPalette(skin);
				circleSample = new Pixmap(pixmapWidthHeight, pixmapWidthHeight, Format.RGBA8888);
				
				Blending b = Pixmap.getBlending();
				Pixmap.setBlending(Blending.None);
				circleSample.fill();
				Pixmap.setBlending(b);

				circleSample.setColor(color);
				int radius = (int)getCurrentRadius();
				circleSample.fillCircle(center, center, radius);
				pixTex = new Texture(circleSample); // FIXME unmanaged upenGL textures, TODO reload onResume (after pause)
				pixTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				cir=new Image(pixTex);//cir.setScaleX(0.2f);
			}

			add(label);
			row();
			add(tSize);
			row();
			add(slider);
			row();
			add(textSample);
			row();
			if(type!=Type.RUBBER){
				if(type==Type.BRUSH){
					add(cir).align(Align.center).expand(false, false).fill(false).size(60, 60);
				}
				else{
					textSample.setColor(color);
					add(textSample).align(Align.left).size(60, 60);
				}
				row();
				add("Colores:");
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
			if ((String.valueOf(slider.getValue())) != brushSize.getText()) {
				brushSize.setText(String.valueOf(slider.getValue()));
			}
		}

		@Override
		public void show() {
			super.show();
		}

		@Override
		public void hide() {
			super.hide();
		}

		public float getSize() {
			return slider.getValue();
		}
		
		/**
		 * Updates the texture that displays the 
		 * visual representation of our draw component.
		 */
		private void updateDemoColor(){
			if(type==Type.TEXT){
				updateTextSample();
			}else if(type==Type.BRUSH){
				updateCircleSample();
			}
		}
		
		private void updateCircleSample(){
			Blending b = Pixmap.getBlending();
			Pixmap.setBlending(Blending.None);
			circleSample.setColor(0f, 0f, 0f, 0f);
			circleSample.fill();
			Pixmap.setBlending(b);

			circleSample.setColor(color);
			int radius = (int)getCurrentRadius();
			circleSample.fillCircle(center, center, radius);
			pixTex.draw(circleSample, 0, 0);
		}
		
		private float getCurrentRadius(){
			return maxPixRadius * slider.getValue()/slider.getMaxValue();
		}
		
		private void updateTextSample(){
			textSample.setColor(color);
			textSample.setFontScale((slider.getValue()+1)/slider.getMaxValue());
		}
		
		private void createPalette(Skin skin){
			Pixmap auxPixmap = new Pixmap(50, 50, Format.RGB888);
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
					updateDemoColor();
				}
			};
			for (int i = 0; i < ROWS; i++) {
				for (int j = 0; j < COLS; j++) {
					Color c = colrs[i][j];
					auxPixmap.setColor(c);
					auxPixmap.fill();
					final Image colorB = new Image(new Texture(auxPixmap)); // FIXME unmanaged upenGL textures, TODO reload onResume (after pause)
					colorB.setColor(c);
					colorB.addListener(colorListener);
					gridPanel.addItem(colorB, i, j).expand().fill();
				}
			}
			auxPixmap.dispose();
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
