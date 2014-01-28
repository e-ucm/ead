package es.eucm.ead.editor;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import es.eucm.editor.DesktopPlatform;

public class EditorDesktop extends Editor {

	public EditorDesktop() {
		super(new DesktopPlatform());
	}

	@Override
	public void create() {
		super.create();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL20 = true;
		config.forceExit = true;
		LwjglFrame frame = new LwjglFrame(new EditorDesktop(), config);
		frame.setVisible(true);
	}
}
