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
package es.eucm.ead.engine.tests;

import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.engine.GameAssets;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockFiles;
import es.eucm.ead.engine.mock.engineobjects.EngineObjectMock;
import es.eucm.ead.engine.mock.schema.SchemaObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GameAssetsTest {

	public static final String CONTENT = "{}";

	private GameAssets gameAssets;

	private FileHandle gameFolder;

	private FileHandle gameFile;

	@Before
	public void setUp() throws IOException {
		new MockApplication();
		gameAssets = new GameAssets(new MockFiles());
		gameFolder = new FileHandle(File.createTempFile("eadtests",
				System.currentTimeMillis() % 1000 + ""));
		// This delete is necessary to create the directory
		gameFolder.delete();
		gameFolder.mkdirs();
		gameFile = gameFolder.child("game.json");
		gameFile.writeString(CONTENT, false);
		gameAssets.setLoadingPath(gameFolder.file().getAbsolutePath(), false);
	}

	@Test
	public void testAbsolutePath() {
		FileHandle gameResolved = gameAssets.resolve(gameFile.file()
				.getAbsolutePath());
		assertTrue(gameResolved.exists());
		assertEquals(CONTENT, gameResolved.readString());
	}

	@Test
	public void testGameProjectPath() {
		FileHandle gameResolved = gameAssets.resolve("game.json");
		assertTrue(gameResolved.exists());
		assertEquals(CONTENT, gameResolved.readString());
	}

	@Test
	public void testGameProjectPathFallback() {
		// bindings.json doesn't exist in the game folder, but exists in assets.
		// It should fallback to it
		FileHandle fileResolved = gameAssets.resolve("bindings.json");
		assertTrue(fileResolved.exists());
	}

	@Test
	public void testOverwriteInternalPath() {
		FileHandle bindingsFile = gameFolder.child("bindings.json");
		bindingsFile.writeString(CONTENT, false);
		FileHandle fileResolved = gameAssets.resolve("bindings.json");
		assertTrue(fileResolved.exists());
		assertEquals(bindingsFile.readString(), CONTENT);
	}

	@Test
	public void testDefaultFont() {
		assertNotNull(gameAssets.getDefaultFont());
	}

	@Test
	public void testGamePathNull() {
		// Assets must be able to access files with game path set to null
		gameAssets.setLoadingPath(null, false);
		assertTrue(gameAssets.resolve("bindings.json").exists());
	}

	@Test
	public void testGamePathCorrected() {
		String path = "path";
		gameAssets.setLoadingPath(path, false);
		assertEquals(gameAssets.getLoadingPath(), path + "/");
		assertFalse(gameAssets.isGamePathInternal());
	}

	@Test
	public void testGetEngineObject() {
		gameAssets.bind("schemaobject", SchemaObject.class,
				EngineObjectMock.class);
		SchemaObject schemaObject = new SchemaObject();
		assertEquals(gameAssets.getEngineObject(schemaObject).getClass(),
				EngineObjectMock.class);
		assertNull(gameAssets.getEngineObject(Object.class));
	}

	@Test
	public void testLoadBindings() {
		try {
			gameAssets.loadBindings(gameAssets.resolve("bindings.json"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

	@After
	public void tearDown() {
		gameFolder.deleteDirectory();
	}

}
