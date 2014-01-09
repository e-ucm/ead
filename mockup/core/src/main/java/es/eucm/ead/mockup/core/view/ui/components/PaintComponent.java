package es.eucm.ead.mockup.core.view.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.mockup.core.control.screens.AbstractScreen;
import es.eucm.ead.mockup.core.view.ui.Panel;

public class PaintComponent{

	public PaintPanel panel;
	public TextButton button;
	
	public PaintComponent(Skin skin){
		panel=new PaintPanel(skin, "default");
		button=new TextButton("pintar", skin);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				event.cancel();
				System.out.println("hola");
				if (!panel.isVisible()) {
					AbstractScreen.mockupController.show(panel);
				} else {
					AbstractScreen.mockupController.hide(panel);
				}
			}
		});
	}

	private class PaintPanel extends Panel{
		
		private float x, y;
		
		public PaintPanel(Skin skin) {
			super(skin, "default");
		}
	
		public PaintPanel(Skin skin, String styleName) {
			super(skin, styleName);
			float w = AbstractScreen.stagew * .3f;
			this.x = -w;
			this.y = AbstractScreen.stageh * .03f;
			setBounds(100, 100, 400, 400);
			setVisible(false);
			setColor(Color.ORANGE);
			setModal(false);
	
			Label cbs1 = new Label("TODO", skin);
	
			Table t = new Table();
			t.add(cbs1);
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
}
