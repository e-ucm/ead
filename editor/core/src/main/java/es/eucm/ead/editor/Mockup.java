package es.eucm.ead.editor;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.editor.control.actions.ChangeSkin;
import es.eucm.ead.editor.control.actions.ChangeView;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.view.builders.mockup.menu.InitialScreen;

public class Mockup extends Editor {
	
	private static final int WIDTH = 1100;
	private static final int HEIGHT = 700;	

	public Mockup(Platform platform) {
		super(platform);
	}
	
	@Override
	public void render() {
		super.render();
		Table.drawDebug(super.stage);
	}
	
	@Override
	public void resize(int width, int height) {
		super.stage.setViewport(WIDTH, HEIGHT, true);
	}
	
	@Override
	protected Stage createStage() {
		return new Stage(WIDTH, HEIGHT, true);
	}
	
	@Override
	protected void initialize() {
		super.controller.action(ChangeSkin.NAME, "mockup");
		super.controller.action(ChangeView.NAME, InitialScreen.NAME);
	}
}
