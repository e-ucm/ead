package es.eucm.ead.editor;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.editor.DesktopPlatform;

import javax.swing.JFrame;

public class EditorDesktop extends Editor {

	private LwjglFrame frame;

	public EditorDesktop(Platform platform) {
		super(platform);
	}

	@Override
	public void create() {
		super.create();
		Preferences preferences = controller.getPreferences();
		frame = ((DesktopPlatform) platform).getFrame();
		if (preferences.getBoolean(Preferences.WINDOW_MAXIMIZED)) {
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else {
			int width = preferences.getInteger(Preferences.WINDOW_WIDTH);
			int height = preferences.getInteger(Preferences.WINDOW_HEIGHT);
			frame.setSize(width, height);
		}
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		Preferences preferences = controller.getPreferences();
		preferences.putInteger(Preferences.WINDOW_WIDTH, width);
		preferences.putInteger(Preferences.WINDOW_HEIGHT, height);
		preferences.putBoolean(Preferences.WINDOW_MAXIMIZED,
				frame.getExtendedState() == JFrame.MAXIMIZED_BOTH);
		preferences.flush();
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL20 = true;
		config.forceExit = true;
		DesktopPlatform platform = new DesktopPlatform();
		LwjglFrame frame = new LwjglFrame(new EditorDesktop(platform), config);
		platform.setFrame(frame);
		// set visible calls create()
		frame.setVisible(true);
	}
}
