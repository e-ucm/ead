/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2014 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          CL Profesor Jose Garcia Santesmases 9,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.eucm.ead.engine.mock;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxNativesLoader;
import es.eucm.ead.engine.EngineApplicationListener;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a no-GUI wrapper for the engine, intended for testing
 */
public class MockApplication implements Application {

	private ApplicationListener listener;
	private Input input;
	private Files files;
	private Graphics graphics;
	private Audio audio;
	private Clipboard clipboard;

	protected final Array<Runnable> runnables = new Array<Runnable>();
	private boolean ended;

	public MockApplication() {
		this(new EngineApplicationListener(new MockImageUtils()));
	}

	public MockApplication(ApplicationListener listener) {
		this(listener, 800, 600);
	}

	public MockApplication(ApplicationListener listener, int width, int height) {
		this.listener = listener;
		// Create stub objects
		files = new MockFiles();
		audio = new MockAudio();
		input = new MockInput();
		graphics = new MockGraphics(width, height);

		Gdx.app = this;
		Gdx.graphics = graphics;
		Gdx.files = files;
		Gdx.audio = audio;
		Gdx.input = input;
		Gdx.gl = graphics.getGL20();
		Gdx.gl20 = graphics.getGL20();
		GdxNativesLoader.load();
		clipboard = new Clipboard() {

			private String contents;

			@Override
			public String getContents() {
				return contents;
			}

			@Override
			public void setContents(String content) {
				this.contents = content;
			}
		};
		start();
	}

	/**
	 * Start the mock
	 */
	public void start() {
		if (listener != null) {
			listener.create();
			listener.resize(graphics.getWidth(), graphics.getHeight());
		}
	}

	/**
	 * Make an update
	 */
	public void act() {
		for (int i = 0; i < runnables.size; i++) {
			runnables.get(i).run();
		}
		runnables.clear();
		listener.render();
	}

	@Override
	public ApplicationListener getApplicationListener() {
		return listener;
	}

	@Override
	public Graphics getGraphics() {
		return Gdx.graphics;
	}

	@Override
	public Audio getAudio() {
		return audio;
	}

	@Override
	public Input getInput() {
		return input;
	}

	@Override
	public Files getFiles() {
		return files;
	}

	@Override
	public Net getNet() {
		return null;
	}

	@Override
	public void log(String tag, String message) {
		System.out.println(tag + ": " + message);
	}

	@Override
	public void log(String tag, String message, Throwable exception) {
		log(tag, message);
		exception.printStackTrace();
	}

	@Override
	public void error(String tag, String message) {
		log("[ERROR] " + tag, message);
	}

	@Override
	public void error(String tag, String message, Throwable exception) {
		log("[ERROR] " + tag, message, exception);
	}

	@Override
	public void debug(String tag, String message) {
		log("[DEBUG] " + tag, message);
	}

	@Override
	public void debug(String tag, String message, Throwable exception) {
		log("[DEBUG] " + tag, message, exception);
	}

	@Override
	public void setLogLevel(int logLevel) {
	}

	@Override
	public int getLogLevel() {
		return 0;
	}

	@Override
	public ApplicationType getType() {
		return ApplicationType.Desktop;
	}

	@Override
	public int getVersion() {
		return 0;
	}

	@Override
	public long getJavaHeap() {
		return 0;
	}

	@Override
	public long getNativeHeap() {
		return 0;
	}

	Map<String, Preferences> preferences = new HashMap<String, Preferences>();

	@Override
	public Preferences getPreferences(String name) {
		if (preferences.containsKey(name)) {
			return preferences.get(name);
		} else {
			Preferences prefs = new MockPreferences(name, "testpreferences");
			preferences.put(name, prefs);
			return prefs;
		}
	}

	@Override
	public Clipboard getClipboard() {
		return clipboard;
	}

	@Override
	public void postRunnable(Runnable runnable) {
		synchronized (runnables) {
			runnables.add(runnable);
		}
	}

	@Override
	public void exit() {
		ended = true;
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
	}

	public boolean isEnded() {
		return ended;
	}

	public static void initStatics() {
		if (Gdx.app == null) {
			Gdx.app = new MockApplication();
		}
	}
}
