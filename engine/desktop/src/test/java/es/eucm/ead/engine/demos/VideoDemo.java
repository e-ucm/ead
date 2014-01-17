package es.eucm.ead.engine.demos;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import es.eucm.ead.engine.EngineDesktop;

public class VideoDemo {

	public static void main(String args[]) {
		EngineDesktop engine = new EngineDesktop(1066, 600);
		engine.run("@videodemo");
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}
}
