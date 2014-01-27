package es.eucm.ead.engine.demos;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import es.eucm.ead.engine.EngineDesktop;

public class Parity2Demo {

	public static void main(String args[]) {
		EngineDesktop engine = new EngineDesktop(1920, 1080);
		engine.run("parity2.0",true);
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}
}
