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
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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

public class PaintComponent {

	private PaintPanel panel;
	private TextButton button;
	private Color color;

	public PaintComponent(Skin skin) {
		button = new TextButton("Pintar", skin);
		panel = new PaintPanel(skin, "opaque");
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

		private final int WIDTH=350;
		private final int HEIGHT=550;
		private Slider slider;
		private GridPanel<Actor> gridPanel;

		public PaintPanel(Skin skin) {
			super(skin, "default");
		}

		public PaintPanel(Skin skin, String styleName) {
			super(skin, styleName);
			setHeight(HEIGHT);
			setWidth(WIDTH);
			setVisible(false);
			setColor(Color.DARK_GRAY);
			setModal(false);
			//setTouchable(Touchable.childrenOnly);

			Pixmap aux = new Pixmap(50, 50, Format.RGB888);
			final int COLS = 4, ROWS = 3;
			final Color[][] colrs= {  
					{Color.BLACK, Color.BLUE, Color.CYAN, new Color(.5f, .75f, .32f, 1f)},
					{Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK}, 
					{Color.RED, Color.LIGHT_GRAY, Color.YELLOW, Color.WHITE}
			};
			gridPanel = new GridPanel<Actor>(skin, ROWS, COLS, 20);
			ClickListener colorListener = new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					event.cancel();
					Image list = (Image) event.getListenerActor();
					color = list.getColor();
					System.out.println("color seteado "
							+ list.getName() +  " " 
							+ list.getColor().toString());
				}
			};
			for (int i = 0; i < ROWS; i++) {
				for (int j = 0; j < COLS; j++) {
					Color c = colrs[i][j];
					aux.setColor(c);
					aux.fill();
					final Image colorB = new Image(new Texture(aux));
					colorB.setColor(c);
					colorB.setName("" + i + j);
					colorB.addListener(colorListener);
					gridPanel.addItem(colorB, i, j).fill();
				}
			}

			defaults().fill().expand();

			Label l=new Label("Herramienta de pincel", skin, "default-thin-opaque");
			l.setWrap(true);
			l.setAlignment(Align.center);

			slider = new Slider(1, 60, 0.5f, false, skin, "left-horizontal");
			add(l);
			row();
			add("Tamaño de pincel");
			row();
			add(slider);
			row();
			add("Color");
			row();
			add(gridPanel);
			//debug();

		}

		public void actCoordinates(){
			setX(button.getX() + (button.getWidth() / 2) - (WIDTH / 2));
			setY(Constants.SCREENH - UIAssets.TOOLBAR_HEIGHT - HEIGHT - 10);
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

	public void actCoordinates(){
		panel.actCoordinates();
	}
}
