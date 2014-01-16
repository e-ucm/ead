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
package es.eucm.ead.engine.java.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.engine.FileResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileResolverTest {

	public static final String CONTENT = "test";

	private FileResolver fileResolver;

	private FileHandle gameFolder;

	private FileHandle gameFile;

	@Before
	public void setUp() throws IOException {
		Gdx.files = new LwjglFiles();
		fileResolver = new FileResolver();
		gameFolder = new FileHandle(File.createTempFile("eadtests", System
				.currentTimeMillis()
				% 1000 + ""));
		// This delete is necessary to create the directory
		gameFolder.delete();
		gameFolder.mkdirs();
		gameFile = gameFolder.child("game.json");
		gameFile.writeString(CONTENT, false);
		fileResolver.setGamePath(gameFolder.file().getAbsolutePath());
	}

	@Test
	public void testAbsolutePath() {
		FileHandle gameResolved = fileResolver.resolve(gameFile.file()
				.getAbsolutePath());
		assertTrue(gameResolved.exists());
		assertEquals(CONTENT, gameResolved.readString());
	}

	@Test
	public void testGameProjectPath() {
		FileHandle gameResolved = fileResolver.resolve("game.json");
		assertTrue(gameResolved.exists());
		assertEquals(CONTENT, gameResolved.readString());
	}

	@Test
	public void testGameProjectPathFallback() {
		// bindings.json doesn't exist in the game folder, but exists in assets. It
		// should fallback to it
		FileHandle fileResolved = fileResolver.resolve("bindings.json");
		assertTrue(fileResolved.exists());
	}

	@Test
	public void testOverwriteInternalPath() {
		FileHandle bindingsFile = gameFolder.child("bindings.json");
		bindingsFile.writeString(CONTENT, false);
		FileHandle fileResolved = fileResolver.resolve("bindings.json");
		assertTrue(fileResolved.exists());
		assertEquals(bindingsFile.readString(), CONTENT);
	}

	@After
	public void tearDown() {
		gameFolder.deleteDirectory();
	}
}
