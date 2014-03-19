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
import es.eucm.ead.editor.control.appdata.ReleasePlatformInfo;
import es.eucm.ead.editor.control.appdata.UpdateInfo;
import es.eucm.ead.editor.control.updatesystem.UpdateSystem;
import es.eucm.network.requests.Request;
import es.eucm.network.requests.RequestCallback;
import es.eucm.network.requests.RequestHelper;
import es.eucm.network.requests.ResourceCallback;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests {@link es.eucm.ead.editor.control.updatesystem.UpdateSystem}
 * Limitations: since UpdateSystem requires user confirmation to proceed with
 * the actual download of the file, its functionally cannot be fully tested.
 * From the 4 phases
 * {@link es.eucm.ead.editor.control.updatesystem.UpdateSystem} goes through,
 * only the two first are tested (downloading update.json and checking if an
 * update is needed).
 * 
 * Created by Javier Torrente on 17/03/14.
 */
public class UpdateSystemTest extends EditorTest {

	/**
	 * Stuff related to the update.json generated for testing: The String
	 * version of the json-contents, the uri where the file is supposed to live,
	 * and the actual object
	 */
	private String updateJSONContent;
	private String updateJSONuri;
	private UpdateInfo updateInfo;

	/**
	 * The object being tested (a new one is created each time instead of using
	 * Controller's)
	 */
	private UpdateSystem updateSystem;

	/**
	 * For suspending the test until
	 * {@link es.eucm.ead.editor.control.updatesystem.UpdateSystem} tries
	 * returns the json file
	 */
	private Object monitor;

	/**
	 * True if {@link es.eucm.ead.editor.control.updatesystem.UpdateSystem}
	 * requests to retrieve the UpdateInfo object remotely
	 */
	private boolean invoked = false;

	/**
	 * Indicates if {@code invoked} is expected to be true
	 */
	private boolean shouldGetUpdateGetInvoked = false;

	@Test
	/**
	 * Tests the normal case (everything's OK, update needed)
	 */
	public void testNeedsUpdate() {
		String remoteVersion = "1.1.1";
		String localVersion = "1.1.0";
		String expectedInstallerURL = testDownloadUpdateInfo(remoteVersion,
				localVersion, true, true);
		// Give UpdateSystem a little so it can update installerURL
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		testCheckUpdateNeeded(expectedInstallerURL, updateSystem);
	}

	@Test
	/**
	 * Tests the normal case (everything's OK, update not needed)
	 */
	public void testUpdateNotNeeded() {
		String remoteVersion = "1.1.1";
		String localVersion = "1.1.1";
		testDownloadUpdateInfo(remoteVersion, localVersion, true, true);
		// Give UpdateSystem a little so it can update installerURL
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		testCheckUpdateNeeded(null, updateSystem);
	}

	@Test
	/**
	 * Tests {@link es.eucm.ead.editor.control.updatesystem.UpdateSystem#downloadUpdateInfo()}
	 */
	public void testNotValidUpdateURL() {
		String remoteVersion = "1.1.1";
		String localVersion = "1.1.0";
		testDownloadUpdateInfo(remoteVersion, localVersion, false, false);
	}

	@Test
	/**
	 * Tests {@link es.eucm.ead.editor.control.updatesystem.UpdateSystem#downloadUpdateInfo()}
	 */
	public void testNotValidUpdateJSON() {
		String remoteVersion = "1.1.1";
		String localVersion = "1.1.0";
		testDownloadUpdateInfo(remoteVersion, localVersion, true, false);
	}

	/**
	 * Tests
	 * {@link es.eucm.ead.editor.control.updatesystem.UpdateSystem#downloadUpdateInfo()}
	 * 
	 * @return The path that simulates the location of the new installer
	 */
	private String testDownloadUpdateInfo(String remoteVersion,
			String localVersion, boolean validUpdateURL, boolean validUpdateJSON) {
		// Reset fields
		updateJSONContent = null;
		updateJSONuri = null;
		updateInfo = null;
		invoked = false;
		shouldGetUpdateGetInvoked = validUpdateURL;

		// Create a temp file to simulate the url that hosts the zip file to
		// download
		File appBundle = mockPlatform.createTempFile(false);
		String appBundleURI = appBundle.toURI().toString();

		// Create the object that simulates the contents of the remote
		// updateInfo json
		if (validUpdateJSON) {
			updateInfo = new UpdateInfo();
			updateInfo.setVersion(remoteVersion);
			ReleasePlatformInfo releasePlatformInfo = new ReleasePlatformInfo();
			releasePlatformInfo.setOs(ReleasePlatformInfo.Os.MULTIPLATFORM);
			releasePlatformInfo.setUrl(appBundleURI);
			updateInfo.getPlatforms().add(releasePlatformInfo);
			updateJSONContent = mockController.getApplicationAssets().toJson(
					updateInfo, UpdateInfo.class);
		} else {
			updateInfo = null;
			updateJSONContent = "{os:XXX,url:YYY}";
		}

		// Create the release info object
		ReleaseInfo releaseInfo = new ReleaseInfo();
		releaseInfo.setAppVersion(localVersion);
		releaseInfo.setDev(false);
		releaseInfo.setOs(ReleaseInfo.Os.MULTIPLATFORM);

		// Simulate storing the updateInfo object to disk
		if (validUpdateURL) {
			File updateJSON = mockPlatform.createTempFile(false);
			updateJSONuri = updateJSON.toURI().toString();
			releaseInfo.setUpdateURL(updateJSONuri);
		} else {
			releaseInfo.setUpdateURL(null);
		}

		// Create the update system
		updateSystem = new UpdateSystem(releaseInfo, new MockRequestHelper(), mockController.getApplicationAssets().getI18N(),
				mockController);
		updateSystem.start();

		/**
		 * Wait until
		 * {@link es.eucm.ead.editor.control.UpdateSystemTest.MockRequestHelper#get(es.eucm.network.requests.Request, String, es.eucm.network.requests.ResourceCallback, Class, boolean)}
		 * is called. This means
		 * {@link es.eucm.ead.editor.control.updatesystem.UpdateSystem} is
		 * trying to retrieve update.json from the uri provided in
		 * {@link es.eucm.ead.editor.control.appdata.ReleaseInfo} (
		 * {@code updateJSONuri}).
		 */
		try {
			monitor = new Object();
			synchronized (monitor) {
				monitor.wait(500);
			}
		} catch (InterruptedException e) {
			Gdx.app.error(this.getClass().getCanonicalName(),
					"Something went wrong. Test failed", e);
			fail();
		}

		if (shouldGetUpdateGetInvoked) {
			assertTrue(
					"The get method used to retrieve update.json should have been invoked",
					invoked);
			// Check that UpdateSystem.updateInfo was updated
			UpdateInfo updateInfo1 = this
					.getUpdateInfoFieldFromUpdateSystem(updateSystem);

			if (validUpdateJSON) {
				assertTrue(
						"UpdateInfo generated and UpdateInfo read should be equals",
						updateInfo.getVersion()
								.equals(updateInfo1.getVersion()));
			} else {
				assertNull("The update system should get disabled", updateInfo1);
			}
		} else {
			assertTrue(
					"The get method used to retrieve update.json should have NOT been invoked",
					!invoked);
		}

		return appBundleURI;

	}

	/**
	 * Tests
	 * {@link es.eucm.ead.editor.control.updatesystem.UpdateSystem#checkUpdateNeeded()}
	 * 
	 * @param expectedInstallerURL
	 *            The expected value for
	 *            {@link es.eucm.ead.editor.control.updatesystem.UpdateSystem#installerURL}
	 *            . Makes the test fail if this value is not matched to the
	 *            update systems'
	 */
	private void testCheckUpdateNeeded(String expectedInstallerURL,
			UpdateSystem updateSystem) {
		String installerURL = this
				.getInstallerURLFromUpdateSystem(updateSystem);
		assertTrue("Expected installerURL and actual one don't match",
				expectedInstallerURL == null && installerURL == null
						|| expectedInstallerURL.equals(installerURL));
	}

	/**
	 * Retrieves the
	 * {@link es.eucm.ead.editor.control.updatesystem.UpdateSystem#updateInfo}
	 * field via reflection. This is the object representation of the
	 * update.json file fetched remotely
	 * 
	 * @param updateSystem
	 *            The object from where the field should be retrieved
	 * @return The "updateInfo" field contained in this object, or null if an
	 *         exception is thrown or the field is not available
	 */
	private UpdateInfo getUpdateInfoFieldFromUpdateSystem(
			UpdateSystem updateSystem) {
		return getFieldByReflectionFromUpdateSystem(updateSystem, "updateInfo");
	}

	/**
	 * Similar to
	 * {@link #getUpdateInfoFieldFromUpdateSystem(es.eucm.ead.editor.control.updatesystem.UpdateSystem)}
	 * 
	 * @param updateSystem
	 *            The object from where the field should be retrieved
	 * @return The "installerURL" field contained in this object, or null if an
	 *         exception is thrown or the field is not available
	 */
	private String getInstallerURLFromUpdateSystem(UpdateSystem updateSystem) {
		return getFieldByReflectionFromUpdateSystem(updateSystem,
				"installerURL");
	}

	/**
	 * Returns a field from the {@code updateSystem} object via reflection. The
	 * current test fails if any exception is thrown
	 */
	private <T> T getFieldByReflectionFromUpdateSystem(
			UpdateSystem updateSystem, String fieldName) {
		try {
			Field field = UpdateSystem.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			T object = (T) field.get(updateSystem);
			field.setAccessible(false);
			return object;
		} catch (NoSuchFieldException e) {
			Gdx.app.debug(this.getClass().getCanonicalName(),
					"Error while retrieving " + fieldName
							+ " field from UpdateSystem via reflection.", e);
			fail();
		} catch (IllegalAccessException e) {
			Gdx.app.debug(this.getClass().getCanonicalName(),
					"Error while retrieving " + fieldName
							+ " field from UpdateSystem via reflection.", e);
			fail();
		}
		return null;
	}

	/**
	 * This RequestHelper is used to simulate network traffic. When the
	 * {@link es.eucm.ead.editor.control.updatesystem.UpdateSystem} invokes its
	 * {@link #get(es.eucm.network.requests.Request, String, es.eucm.network.requests.ResourceCallback, Class, boolean)}
	 * method, it returns the String with the update.json file generated
	 * previously by the test and notifies the test to go on.
	 */
	private class MockRequestHelper extends RequestHelper {

		@Override
		public void send(Request request, String uriWithParameters,
				RequestCallback callback) {

		}

		@Override
		public <S, T> void get(Request request, String uriWithParameters,
				ResourceCallback<T> callback, Class<S> clazz,
				boolean isCollection) {
			assertTrue(
					"The parameters provided for accessing the update.json file remotely are not valid",
					uriWithParameters != null
							&& uriWithParameters.equals(updateJSONuri));
			callback.success((T) updateJSONContent);
			invoked = true;
			synchronized (monitor) {
				monitor.notify();
			}
		}

		@Override
		public String encode(String string, String charset) {
			return null;
		}

		@Override
		public String getJsonData(Object element) {
			return null;
		}
	}

}
