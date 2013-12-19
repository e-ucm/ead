/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.tests;

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
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.Json;
import org.junit.Test;

import static org.junit.Assert.fail;

public class ActionsConfigFileTest {

	@Test
	public void testFile() {
		Gdx.files = new LwjglFiles();
		Gdx.app = new Application() {
			@Override
			public ApplicationListener getApplicationListener() {
				return null;
			}

			@Override
			public Graphics getGraphics() {
				return null;
			}

			@Override
			public Audio getAudio() {
				return null;
			}

			@Override
			public Input getInput() {
				return null;
			}

			@Override
			public Files getFiles() {
				return null;
			}

			@Override
			public Net getNet() {
				return null;
			}

			@Override
			public void log(String tag, String message) {
			}

			@Override
			public void log(String tag, String message, Throwable exception) {
			}

			@Override
			public void error(String tag, String message) {
			}

			@Override
			public void error(String tag, String message, Throwable exception) {
			}

			@Override
			public void debug(String tag, String message) {
			}

			@Override
			public void debug(String tag, String message, Throwable exception) {
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
				return null;
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

			@Override
			public Preferences getPreferences(String name) {
				return null;
			}

			@Override
			public Clipboard getClipboard() {
				return null;
			}

			@Override
			public void postRunnable(Runnable runnable) {
			}

			@Override
			public void exit() {
			}

			@Override
			public void addLifecycleListener(LifecycleListener listener) {
			}

			@Override
			public void removeLifecycleListener(LifecycleListener listener) {
			}
		};
		Json json = new Json();
		try {
			json.fromJson(Array.class, Gdx.files.classpath("actions.json"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed loading actions");
		}
	}
}
