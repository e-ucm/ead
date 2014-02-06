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
package es.eucm.ead.editor.assets;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.graphics.Texture;
import es.eucm.ead.editor.platform.MockPlatform;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockFiles;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProjectAssetsTest {

	private ProjectAssets projectAssets;

	private File projectFolder;

	private static Files files;

	private static MockPlatform platform;

	@BeforeClass
	public static void setUpClass() {
		MockApplication.initStatics();
		platform = new MockPlatform();
		files = new MockFiles();
	}

	@Before
	public void setUp() {
		projectAssets = new ProjectAssets(files, new EditorAssets(files));
		projectFolder = platform.createTempFile(true);
		projectAssets.setLoadingPath(projectFolder.getAbsolutePath(), true);
	}

	@Test
	public void testCopyAndLoad() {
		File externalFile = platform.createTempFile(false);

		assertEquals(projectAssets.copyAndLoad(externalFile.getAbsolutePath(),
				Texture.class),
				ProjectAssets.IMAGES_FOLDER + externalFile.getName());

		String newPath = ProjectAssets.IMAGES_FOLDER + externalFile.getName();
		assertTrue(projectAssets.resolve(newPath).exists());
		assertEquals(projectAssets.getAssetManager().getQueuedAssets(), 1);

		for (int i = 1; i < 10; i++) {
			newPath = ProjectAssets.IMAGES_FOLDER + externalFile.getName() + i;
			assertEquals(projectAssets.copyAndLoad(
					externalFile.getAbsolutePath(), Texture.class), newPath);
			assertTrue(projectAssets.resolve(newPath).exists());
			assertEquals(projectAssets.getAssetManager().getQueuedAssets(),
					i + 1);
		}
	}
}
