package es.eucm.ead.mockup.core.view.renderers;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MenuRenderer extends ScreenRenderer {

	
	@Override
	public void create() {
		TextButton t = new TextButton("hola mundo", skin);
		stage.addActor(t);
	}
	
	@Override
	public void draw() {
		stage.draw();
	}
}
