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
package es.eucm.ead.editor.control;

import com.badlogic.gdx.Gdx;
import es.eucm.ead.editor.EditorTest;
import es.eucm.ead.editor.control.appdata.OS;
import es.eucm.ead.editor.control.appdata.ReleaseInfo;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * This class testes {@link es.eucm.ead.editor.control.appdata.ReleaseInfo}:
 * 
 * Checks that ...: - ... the release file can be reached and loaded into
 * memory; - ... the appVersion and releaseType loaded are not null, etc.
 * 
 * Created by Javier Torrente on 14/03/14.
 */
public class ReleaseInfoTest extends EditorTest {
	@Test
	/**
	 * Tests that a valid release.json file is loaded correctly
	 */
	public void testValidReleaseInfo() {
		setReleasePath("appdata/validrelease.json");
		ReleaseInfo releaseInfo = controller.getApplicationAssets()
				.loadReleaseInfo();
		assertNotNull("The release info cannot be null", releaseInfo);
		assertNotNull("The release info must have a not null appVersion",
				releaseInfo.getAppVersion());
		assertNotNull("The release info must have a not null releaseType",
				releaseInfo.getReleaseType().toString());
		Gdx.app.debug("appVersion read from appdata/release.json",
				releaseInfo.getAppVersion());
		assertNotNull(
				"The release info must have a not null os (if it is not defined in the file, multiplatform should be returned)",
				releaseInfo.getOs().toString());
	}

	@Test
	/**
	 * Tests that {@link es.eucm.ead.editor.assets.EditorAssets} is able to create default release objects
	 * when the release.json file is not well formed or some attributes are missing
	 */
	public void testInvalidReleaseInfoHandling() {
		testNotValidRelaseFile("appdata/invalidrelease.json");
		testNotValidRelaseFile("appdata/invalidrelease_02.json");
	}

	public void testNotValidRelaseFile(String filePath) {
		setReleasePath(filePath);
		ReleaseInfo releaseInfo = controller.getApplicationAssets()
				.loadReleaseInfo();
		assertTrue("Default releaseInfo should have appVersion=0.0.0",
				releaseInfo.getAppVersion().equals("0.0.0"));
		assertTrue("Default releaseInfo should have os=multiplatform",
				releaseInfo.getOs() == OS.MULTIPLATFORM);
		assertFalse("Default releaseInfo should have dev=false",
				releaseInfo.isDev());

	}

	/**
	 * Sets editorAsset's internal path for loading the
	 * {@link es.eucm.ead.editor.control.appdata.ReleaseInfo} object via
	 * reflection. This allows checking the loading process works and fails
	 * properly without needing to provide a "set" method in
	 * {@link es.eucm.ead.editor.assets.ApplicationAssets}
	 * 
	 * @param newPath
	 *            The new path to resolve the file (e.g.
	 *            "test/appdata/notvalidrelease.json")
	 */
	private void setReleasePath(String newPath) {
		try {
			Field releaseLocationField = controller.getApplicationAssets()
					.getClass().getDeclaredField("releaseFile");
			releaseLocationField.setAccessible(true);
			releaseLocationField
					.set(controller.getApplicationAssets(), newPath);
			releaseLocationField.setAccessible(false);
		} catch (NoSuchFieldException e) {
			handleUnexpectedReflectionException(e);
		} catch (IllegalAccessException e) {
			handleUnexpectedReflectionException(e);
		}
	}

	/**
	 * This method is meant to be called within this test whenever an unexpected
	 * exception is thrown due to reflection access. It just makes the test fail
	 * and logs the exception.
	 * 
	 * @param e
	 *            The exception thrown.
	 */
	private void handleUnexpectedReflectionException(Exception e) {
		Gdx.app.error(
				this.getClass().getCanonicalName(),
				"Error setting the path of the release file via reflection. Check the field is accessible and its"
						+ "name is correct", e);
		fail("Error setting the path of the release file via reflection. Check the field is accessible and its"
				+ "name is correct");
	}
}
