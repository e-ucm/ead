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
package es.eucm.ead.editor.assets;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import es.eucm.ead.editor.platform.MockPlatform;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockFiles;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EditorGameAssetsTest {

	private EditorGameAssets editorGameAssets;

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
		editorGameAssets = new EditorGameAssets(files, new ApplicationAssets(
				files));
		projectFolder = platform.createTempFile(true);
		editorGameAssets.setLoadingPath(projectFolder.getAbsolutePath(), true);
	}

	@Test
	public void testCopyAndLoad() {
		File externalFile = platform.createTempFile(false);

		assertEquals(editorGameAssets.copyToProject(
				externalFile.getAbsolutePath(), Texture.class),
				EditorGameAssets.IMAGES_FOLDER + externalFile.getName());

		String newPath = EditorGameAssets.IMAGES_FOLDER
				+ externalFile.getName();
		assertTrue(editorGameAssets.resolve(newPath).exists());

		AssetManager am = getAssetManagerForTesting();

		assertEquals(am.getQueuedAssets(), 1);

		for (int i = 1; i < 10; i++) {
			newPath = EditorGameAssets.IMAGES_FOLDER + externalFile.getName()
					+ i;
			assertEquals(
					editorGameAssets.copyToProject(
							externalFile.getAbsolutePath(), Texture.class),
					newPath);
			assertTrue(editorGameAssets.resolve(newPath).exists());
			assertEquals(am.getQueuedAssets(), i + 1);
		}
	}

	/**
	 * Retrieves LibGDX's {@link com.badlogic.gdx.assets.AssetManager} declared
	 * in {@link es.eucm.ead.engine.assets.Assets#assetManager} from
	 * {@link #editorGameAssets} by reflection.
	 * 
	 * This should only be done for testing.
	 * 
	 * To access the assetManager through an {@link EditorGameAssets}, it first
	 * retrieves its superclass ({@link es.eucm.ead.engine.assets.GameAssets}),
	 * and then also its superclass ({@link es.eucm.ead.engine.assets.Assets}),
	 * where the class where assetManager is declared.
	 * 
	 * @return The {@link com.badlogic.gdx.assets.AssetManager} for testing. If
	 *         anything goes wrong along this process, the test that invoke this
	 *         method will fail throwing an {@link AssertionError} exception.
	 */
	private AssetManager getAssetManagerForTesting() {
		AssetManager am = null;
		try {
			Field assetManager = EditorGameAssets.class.getSuperclass()
					.getSuperclass().getDeclaredField("assetManager");
			assetManager.setAccessible(true);
			am = (AssetManager) assetManager.get(editorGameAssets);
			assetManager.setAccessible(false);
		} catch (NoSuchFieldException e) {
			assertFalse(
					"An exception was thrown while retrieving the Assets.assetManager for testing. Exception: "
							+ e.toString(), true);
			Gdx.app.debug("Test failed: ", this.getClass().getCanonicalName(),
					e);
		} catch (IllegalAccessException e) {
			assertFalse(
					"An exception was thrown while retrieving the Assets.assetManager for testing. Exception: "
							+ e.toString(), true);
			Gdx.app.debug("Test failed: ", "EditorGameAssetsTest", e);
		}
		return am;
	}
}
