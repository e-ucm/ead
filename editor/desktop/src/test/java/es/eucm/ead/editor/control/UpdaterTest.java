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

/**
 * Tests {@link es.eucm.ead.editor.control.updatesystem.Updater}
 * 
 * Created by Javier Torrente on 17/03/14.
 */
public class UpdaterTest {

	// @BeforeClass
	// public static void setUpClass() {
	// MockApplication.initStatics();
	// }
	//
	// @Test
	// /**
	// * Tests the normal case (everything's OK, update needed).
	// * In this case, the update system should undergo all the
	// * four phases:
	// * download Update Info > check versions > ask user
	// * confirmation (simulated) > open browser (simulated through
	// MockPlatform)
	// */
	// public void testNeedsUpdate() {
	// UpdateSystemTestPlatform updateSystemTestPlatform = new
	// UpdateSystemTestPlatform(
	// "1.1.0");
	// updateSystemTestPlatform.startValidUpdateTest("1.1.1");
	// }
	//
	// @Test
	// /**
	// * Tests the case where localVersion == remoteVersion and therefore no
	// update is needed.
	// * In this case, the update system undergoes the two first phases:
	// * Download Update Info > Check Versions
	// */
	// public void testUpdateNotNeeded() {
	// UpdateSystemTestPlatform updateSystemTestPlatform = new
	// UpdateSystemTestPlatform(
	// "1.1.1");
	// updateSystemTestPlatform.startNotNeedToUpdateTest();
	// }
	//
	// @Test
	// /**
	// * Tests the case when the update.json file retrieved in the first phase
	// is not valid.
	// * In this case, the system only takes the first phase.
	// */
	// public void testNotValidUpdateJSON() {
	// UpdateSystemTestPlatform updateSystemTestPlatform = new
	// UpdateSystemTestPlatform(
	// "1.1.0");
	// updateSystemTestPlatform.startInvalidUpdateTest();
	// }
	//
	// /**
	// * A specific {@link es.eucm.ead.editor.platform.Platform} is needed for
	// * this test to: 1) detect and simulate the request for getting the remote
	// * update.json file, 2) detect and simulate the opening of the web browser
	// * to get the new app bundle downloaded.
	// */
	// private class UpdateSystemTestPlatform extends MockPlatform {
	//
	// /**
	// * The whole process should never last more than 5 seconds, at least in
	// * test
	// */
	// private static final long TIMEOUT = 5000;
	//
	// // Objects dynamically created during each test
	// private String updateJSONuri;
	// private String updateJSONContent;
	// private String installerURL;
	// private UpdateInfo updateInfo;
	// private ReleaseInfo releaseInfo;
	// private FileHandle releaseFileHandle;
	// /**
	// * The object being tested (a new one is created each time instead of
	// * using Controller's)
	// */
	// private Updater updater;
	// private Controller controller;
	//
	// /**
	// * To ensure temp files can be deleted once the test is done.
	// */
	// private Array<FileHandle> tempFiles;
	//
	// /**
	// * For making final assertions. Is set to true if the {@link #updater}
	// * requests opening a web browser (this means the whole process has
	// * completed successfully).
	// */
	// private boolean browser;
	//
	// /**
	// * For making final assertions. Set to true if the {@link #updater}
	// * requests downloading the update.json file.
	// */
	// private boolean getJson;
	//
	// /**
	// * @param localVersion
	// * The app version contained in ReleaseInfo. (e.g. "1.1.1")
	// */
	// public UpdateSystemTestPlatform(String localVersion) {
	// super();
	// tempFiles = new Array<FileHandle>();
	// createReleaseInfo(localVersion);
	// getJson = false;
	// browser = false;
	//
	// /**
	// * This RequestHelper is used to simulate network traffic. When the
	// * {@link es.eucm.ead.editor.control.updatesystem.Updater} invokes
	// * its
	// * {@link #get(es.eucm.network.requests.Request, String,
	// es.eucm.network.requests.ResourceCallback, Class, boolean)}
	// * method, it returns the String with the update.json file generated
	// * and makes assertions
	// */
	//
	// requestHelper = new RequestHelper() {
	//
	// @Override
	// public void send(Request request, String uriWithParameters,
	// RequestCallback callback) {
	//
	// }
	//
	// @Override
	// // This should be invoked by Updater#downloadUpdateInfo
	// public <S, T> void get(Request request,
	// String uriWithParameters, ResourceCallback<T> callback,
	// Class<S> clazz, boolean isCollection) {
	// assertTrue(
	// "The parameters provided for accessing the update.json file remotely are not valid",
	// uriWithParameters != null
	// && uriWithParameters.equals(updateJSONuri));
	// callback.success((T) updateJSONContent);
	// getJson = true;
	// }
	//
	// @Override
	// public String encode(String string, String charset) {
	// return null;
	// }
	//
	// @Override
	// public String getJsonData(Object element) {
	// return null;
	// }
	// };
	//
	// }
	//
	// @Override
	// // This should be invoked by Updater#update
	// public boolean browseURL(String URL) {
	// browser = true;
	// assertTrue("The installer url is not the expected", URL != null
	// && URL.equals(installerURL));
	// return true;
	// }
	//
	// public void startValidUpdateTest(String remoteVersion) {
	// File appBundle = createTempFile(false);
	// tempFiles.add(new FileHandle(appBundle));
	// installerURL = appBundle.toURI().toString();
	//
	// updateInfo = new UpdateInfo();
	// updateInfo.setVersion(remoteVersion);
	// UpdatePlatformInfo releasePlatformInfo = new UpdatePlatformInfo();
	// releasePlatformInfo.setOs(OS.MULTIPLATFORM);
	// releasePlatformInfo.setUrl(installerURL);
	// updateInfo.getPlatforms().add(releasePlatformInfo);
	// updateJSONContent = new Json().toJson(updateInfo, UpdateInfo.class);
	// createUpdateInfoFile();
	// saveReleaseInfoAndInitController();
	// createAndStartUpdater();
	// waitForUpdateSystemToComplete();
	// assertTrue(getJson);
	// assertTrue(browser);
	// clearTempFiles();
	// }
	//
	// public void startInvalidUpdateTest() {
	// createUpdateInfoFile();
	// installerURL = null;
	// updateInfo = null;
	// updateJSONContent = "{os:XXX,url:YYY}";
	// saveReleaseInfoAndInitController();
	// createAndStartUpdater();
	// waitForUpdateSystemToComplete();
	// assertTrue(getJson);
	// assertFalse(browser);
	// clearTempFiles();
	// }
	//
	// public void startNotNeedToUpdateTest() {
	// createUpdateInfoFile();
	// installerURL = null;
	// updateInfo = new UpdateInfo();
	// updateInfo.setVersion(releaseInfo.getAppVersion());
	// UpdatePlatformInfo releasePlatformInfo = new UpdatePlatformInfo();
	// releasePlatformInfo.setOs(OS.MULTIPLATFORM);
	// releasePlatformInfo.setUrl(installerURL);
	// updateInfo.getPlatforms().add(releasePlatformInfo);
	// updateJSONContent = new Json().toJson(updateInfo, UpdateInfo.class);
	// saveReleaseInfoAndInitController();
	// createAndStartUpdater();
	// waitForUpdateSystemToComplete();
	// assertTrue(getJson);
	// assertFalse(browser);
	// clearTempFiles();
	// }
	//
	// private void createUpdateInfoFile() {
	// File updateJSON = createTempFile(false);
	// tempFiles.add(new FileHandle(updateJSON));
	// updateJSONuri = updateJSON.toURI().toString();
	// releaseInfo.setUpdateURL(updateJSONuri);
	// }
	//
	// private void createReleaseInfo(String localVersion) {
	// // Create the release info object
	// releaseInfo = new ReleaseInfo();
	// releaseInfo.setAppVersion(localVersion);
	// releaseInfo.setDev(false);
	// releaseInfo.setOs(OS.MULTIPLATFORM);
	// }
	//
	// private void saveReleaseInfoAndInitController() {
	// File releaseInfoFile = createTempFile(false);
	// releaseFileHandle = new FileHandle(releaseInfoFile);
	// tempFiles.add(releaseFileHandle);
	// try {
	// FileWriter writer = new FileWriter(releaseInfoFile);
	// writer.write(new Json().toJson(releaseInfo));
	// } catch (IOException e) {
	// e.printStackTrace();
	// fail();
	// }
	//
	// controller = new Controller(this, new MockFiles() {
	// // This is needed to ensure the Controller reads release.json
	// // from the temp location this test creates, instead of
	// // the default one (appdata/release.json)
	// @Override
	// public FileHandle internal(String path) {
	// if (path.equals(ApplicationAssets.RELEASE_FILE)) {
	// return releaseFileHandle;
	// } else {
	// return super.internal(path);
	// }
	// }
	// }, new Group(), new Group());
	//
	// }
	//
	// private void createAndStartUpdater() {
	// // Create the update system
	// updater = new Updater(releaseInfo, controller, true);
	// updater.startUpdateProcess();
	// }
	//
	// private void waitForUpdateSystemToComplete() {
	// long waited = 0;
	// while (waited <= TIMEOUT && !updater.isDone()) {
	// try {
	// Thread.sleep(500);
	// waited += 500;
	// } catch (InterruptedException e) {
	// fail("An unexpected error occurred");
	// Gdx.app.error("UpdaterTest", "Unexpected error", e);
	// }
	// }
	//
	// if (waited > TIMEOUT) {
	// fail("Something went wrong. The update system should have terminated already");
	// }
	// }
	//
	// private void clearTempFiles() {
	// for (FileHandle fileHandle : tempFiles) {
	// fileHandle.delete();
	// }
	// }
	// }
}
