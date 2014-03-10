package es.eucm.ead.editor.platform;

import com.badlogic.gdx.Gdx;

public abstract class AbstractPlatform implements Platform {

	protected AbstractPlatform() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				Gdx.app.error("Editor",
						"Fatal error: " + t.getName() + "(" + t.getId() + ")",
						e);
			}
		});
	}
}
