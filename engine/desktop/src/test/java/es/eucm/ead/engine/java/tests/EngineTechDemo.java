package es.eucm.ead.engine.java.tests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import es.eucm.ead.engine.Engine;

public class EngineTechDemo {

	public static void main(String args[]) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL20 = true;
		config.width = 800;
		config.height = 600;
		new LwjglApplication(new Engine("@techdemo"), config);
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}
}
