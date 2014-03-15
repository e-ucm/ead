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
import es.eucm.ead.editor.control.appdata.ReleaseInfo;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This class testes {@link es.eucm.ead.editor.control.appdata.ReleaseInfo}:
 * 
 * Checks that ...: - ... the release file can be reached and loaded into
 * memory; - ... the appVersion and releaseType loaded are not null - ... the
 * appVersion matches Digit.Digit.Digit
 * 
 * Created by Javier Torrente on 14/03/14.
 */
public class ReleaseInfoTest extends EditorTest {
	@Test
	/**
	 * Tests that the release.json file this application contains is valid
	 */
	public void testLoadReleaseInfo() {
		try {
			Field releaseInfoField = mockController.getClass()
					.getDeclaredField("releaseInfo");
			// Set the field accessible so its value can be retrieved by
			// reflection
			releaseInfoField.setAccessible(true);
			ReleaseInfo releaseInfo = (ReleaseInfo) (releaseInfoField
					.get(mockController));
			assertNotNull("The release info cannot be null", releaseInfo);
			assertNotNull("The release info must have a not null appVersion",
					releaseInfo.getAppVersion());
			assertNotNull("The release info must have a not null releaseType",
					releaseInfo.getReleaseType().toString());
			Gdx.app.debug("appVersion read from appdata/release.json",
					releaseInfo.getAppVersion());
			assertTrue(
					"The appVersion field must have three numbers and two dots. Total length: 5 characters. Pattern: Digit.Digit.Digit",
					releaseInfo.getAppVersion()
							.matches("[0-9]\\.[0-9]\\.[0-9]"));
			// Reset accessibility
			releaseInfoField.setAccessible(false);
		} catch (NoSuchFieldException e) {
			fail("A releaseInfo attribute could not be found in class "
					+ mockController.getClass().getCanonicalName());
			Gdx.app.debug(this.getClass().getCanonicalName(), "Exception", e);
		} catch (IllegalAccessException e) {
			fail("A releaseInfo attribute could not be accessed from class "
					+ mockController.getClass().getCanonicalName());
			Gdx.app.debug(this.getClass().getCanonicalName(), "Exception", e);
		}
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
		ReleaseInfo releaseInfo = mockController.getEditorAssets()
				.getReleaseInfo();
		assertTrue("Default releaseInfo should have appVersion=0.0.0",
				releaseInfo.getAppVersion().equals("0.0.0"));
		assertTrue("Default releaseInfo should have releaseType=dev",
				releaseInfo.getReleaseType() == ReleaseInfo.ReleaseType.DEV);
	}

	/**
	 * Sets editorAsset's internal path for loading the
	 * {@link es.eucm.ead.editor.control.appdata.ReleaseInfo} object via
	 * reflection. This allows checking the loading process works and fails
	 * properly without needing to provide a "set" method in
	 * {@link es.eucm.ead.editor.assets.EditorAssets}
	 * 
	 * @param newPath
	 *            The new path to resolve the file (e.g.
	 *            "test/appdata/notvalidrelease.json")
	 */
	private void setReleasePath(String newPath) {
		try {
			Field releaseLocationField = mockController.getEditorAssets()
					.getClass().getDeclaredField("releaseFile");
			releaseLocationField.setAccessible(true);
			releaseLocationField.set(mockController.getEditorAssets(), newPath);
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
