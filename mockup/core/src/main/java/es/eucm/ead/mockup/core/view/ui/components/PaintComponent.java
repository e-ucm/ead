package es.eucm.ead.mockup.core.view.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.mockup.core.control.screens.AbstractScreen;
import es.eucm.ead.mockup.core.utils.Constants;
import es.eucm.ead.mockup.core.view.UIAssets;
import es.eucm.ead.mockup.core.view.ui.GridPanel;
import es.eucm.ead.mockup.core.view.ui.Panel;

public class PaintComponent{

	private PaintPanel panel;
	private TextButton button;
	private Color color;
	
	
	public PaintComponent(Skin skin){
		panel=new PaintPanel(skin, "default");
		button=new TextButton("pintar", skin);
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

	private class PaintPanel extends Panel{
		
		public PaintPanel(Skin skin) {
			super(skin, "default");
		}
	
		public PaintPanel(Skin skin, String styleName) {
			super(skin, styleName);
			setBounds(380, Constants.SCREENH-UIAssets.TOOLBAR_HEIGHT-610, 350, 600); //Change the size and coordinates.
			setVisible(false);
			setColor(Color.ORANGE);
			setModal(false);
	
			Label cbs1 = new Label("TODO", skin);
			
			final int COLS = 5, ROWS = 4 ;
			GridPanel<Actor> gridPanel = new GridPanel<Actor>(skin, ROWS, COLS, 40);
			for (int i=0; i<ROWS; i++){
				for(int j=0; j<COLS; j++){
					final Button colorB = new Button(skin);
					colorB.setName(""+i+j);
					colorB.addListener(new ClickListener(){
						@Override
						public void clicked(InputEvent event, float x, float y) {
							event.cancel();
							color=colorB.getColor();
							System.out.println("color seteado "+colorB.getName());
						}
					});
					gridPanel.addItem(colorB, i, j);
				}
			}
			
			Slider slider = new Slider(1, 60, 0.5f, false, skin);
			Table table = new Table(skin);
			slider.setColor(Color.RED);
			table.add("Tamaño de pincel");
			table.row();
			table.add(slider);
			table.row();
			table.add("Color");
			table.row();
			table.add(gridPanel);
			table.debug();
			//table.add(cbs1).expand().center();
			
			this.add(table).fill();
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
}
