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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SerializationException;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.actions.editor.ImportEntityResources;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.repo.I18NString;
import es.eucm.ead.schema.editor.components.repo.I18NStrings;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schema.editor.components.repo.RepoLibrary;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;

/**
 * <p>
 * The server's directory should have the following layout:
 * </p>
 * 
 * <pre>
 * |libraries.json	<- A file listing the available libraries in the repository. This file is <strong>required</strong>.
 * |{library1}.zip	<- A file with all the resources in that library.
 * |{library1}.png  <- A thumbnail for previewing the library (the actual path of the thumbnail is is specified in {@link RepoLibrary#thumbnail}).
 * |{library1}.json <- A file with general metadata of the library for previewing (see {@link RepoLibrary}).
 * |{library2}.zip
 * ...
 * </pre>
 * 
 * <p>
 * A possible structure for the <strong>{library1 ... n}.zip</strong> files
 * could be:
 * </p>
 * 
 * <pre>
 * |entities.json 	<- Correctly formated to be parsed as a {@link List} of {@link ModelEntity ModelEntities}. This file is <strong>required</strong>.
 * |<strong>thumbnails/</strong>		<- All the thumbnails (recommended .png files).
 * |resources/		<- {@link ModelEntity ModelEntities} resources folder.
 * |	{NAME1}.png	<- File containing the resources of the element who's URI is "NAME1"(defined in entities.json).
 * |	{NAME2}.png
 * |	{NAME3}.png
 * </pre>
 * <p>
 * The <strong>thumbnails/</strong> contains the following structure:
 * </p>
 * 
 * <pre>
 * |file1.png		<- This is a small image that will be displayed as a thumbnail of the element whose {@link RepoElement} is linked to this thumbnail.
 * |file2.png		<- As a thumbnail, it's size shoudn't be too high.
 * |file3.png
 * </pre>
 */
public class RepositoryManager {

	/**
	 * 25 s.
	 */
	private static final int TIMEOUT = 25000;

	/*
	 * Server-side resources URLs.
	 */

	/**
	 * Is the root URL of our repository.
	 */
	private static final String ROOT_URL = "http://e-adventure.e-ucm.es/repo";

	/**
	 * Located at the root folder of our server: {@link #ROOT_URL}. The
	 * libraries.json file stores the names of all our libraries. This file is
	 * <strong>required</strong>.
	 */
	private static final String LIBRARIES_FILE_NAME = "/libraries.json";

	/**
	 * Since {@value #LIBRARIES_FILE_NAME} is <strong>required</strong> and it's
	 * located at the root of our repository, we know it's location.
	 */
	private static final String LIBRARIES_FILE_URL = ROOT_URL
			+ LIBRARIES_FILE_NAME;

	private static final String ONLINE_REPO_TAG = "RepositoryManager";

	/*
	 * Client-side cached resources paths.
	 */

	/**
	 * Path to the eAdventureMockup projects folder. The root folder where all
	 * the editor projects will be located. This will also be the location of
	 * our {@value #REPOSITORY_FOLDER_NAME} folder. E.g.
	 * 
	 * <pre>
	 * |{project1}/		<- A simple user-created project.
	 * |{project1}/
	 * |{project1}/
	 * |{@value #REPOSITORY_FOLDER_NAME}
	 * </pre>
	 */
	private static final String MOCKUP_PROJECTS_PATH = "";

	/**
	 * Name of the folder storing all the online data. Used to cache locally the
	 * repository resources.
	 */
	public static final String REPOSITORY_FOLDER_NAME = "/Online repository";
	private static final String REPOSITORY_FOLDER_PATH = MOCKUP_PROJECTS_PATH
			+ REPOSITORY_FOLDER_NAME;

	/**
	 * Name of the folder storing all the libraries metadata. Used to cache
	 * locally the repository resources.
	 */
	public static final String LIBRARIES_METADATA_FOLDER_NAME = "/Libraries metadata";
	private static final String LIBRARIES_METADATA_FOLDER_PATH = REPOSITORY_FOLDER_PATH
			+ LIBRARIES_METADATA_FOLDER_NAME;

	/**
	 * Since {@value #LIBRARIES_FILE_NAME} is <strong>required</strong> and it's
	 * located at the root of our repository, we know it's location.
	 */
	private static final String LIBRARIES_FILE_PATH = REPOSITORY_FOLDER_PATH
			+ LIBRARIES_FILE_NAME;

	/**
	 * The entities.json file that stores all the entities as a serialized
	 * {@link List}. This file will be parsed as a {@link List} of
	 * {@link ModelEntity ModelEntities}. This file is <strong>required</strong>
	 * to exist at the root of each library in the repository (lives into the
	 * file each library is ZIPPED into).
	 */
	private static final String ENTITIES_FILE_NAME = "/entities.json";

	/**
	 * Convenience method that chooses the best String value in a
	 * {@link I18NStrings object} passed as argument according to the current
	 * language settings of the application. If there is no valid string for the
	 * language selected, the first one is returned. If there are no strings,
	 * blank string is returned
	 * 
	 * @param strings
	 *            Object with the string values
	 * @param i18N
	 *            Object that determines current app settings
	 * @return The value in the current language, or "" if nothing available
	 */
	public static final String i18nString(I18NStrings strings, I18N i18N) {
		for (I18NString i18NString : strings.getStrings()) {
			if (i18NString.getLang().equals(i18N.getLang())) {
				return i18NString.getValue();
			}
		}

		if (strings.getStrings().size > 0) {
			return strings.getStrings().get(0).getValue();
		}

		return "";
	}

	/**
	 * Used to know from which library we're fetching.
	 */
	private String currentLibrary = "";

	/**
	 * A temporal byte array used to write to disk efficiently.
	 */
	private final byte data[] = new byte[4096];

	/**
	 * Used to store the {@link ModelEntity ModelEntities} of a library.
	 */
	private Array<ModelEntity> onlineElements;

	/**
	 * Stores metadata associated to each library
	 */
	private Array<RepoLibrary> libraries;

	private boolean copyThumbnailToProject;

	public RepositoryManager() {
		copyThumbnailToProject = false;
		libraries = new Array<RepoLibrary>();
	}

	public Array<ModelEntity> getElements() {
		return this.onlineElements;
	}

	/**
	 * @return An Array with all the libraries available, ordered by
	 *         {@link RepoLibrary#name}
	 */
	public Array<RepoLibrary> getRepoLibraries() {
		// First, order them by name
		sortRepoLibraries(0, libraries.size);
		return libraries;
	}

	/*
	 * Quicksort
	 */
	private void sortRepoLibraries(int initial, int end) {
		int pivot = (initial + end) / 2;
		for (int j = initial; j < end; j++) {
			if (j == pivot) {
				continue;
			}
			int comparison = libraries.get(j).getPath()
					.compareTo(libraries.get(pivot).getPath());
			if (comparison > 0 && j < pivot) {
				libraries.add(libraries.removeIndex(j));
				pivot--;
			} else if (comparison < 0 && j > pivot) {
				libraries.insert(0, libraries.removeIndex(j));
				pivot++;
			}
		}
		if (initial < pivot) {
			sortRepoLibraries(initial, pivot);
		}
		if (pivot < end) {
			sortRepoLibraries(pivot + 1, end);
		}
	}

	// ///////////////////////////
	// CLIENT
	// ///////////////////////////

	/**
	 * @see #currentLibrary
	 */
	public void setCurrentLibrary(String currentLibrary) {
		if (!currentLibrary.startsWith("/"))
			currentLibrary = "/" + currentLibrary;
		this.currentLibrary = currentLibrary;
	}

	/**
	 * Imports the element to the project by fetching it in the local cache or
	 * downloading it, if necessary.
	 * 
	 * @param target
	 * @param controller
	 */
	public ModelEntity importElement(ModelEntity target, Controller controller) {

		return importElementFromLocal(target, controller.getEditorGameAssets(),
				controller);
	}

	/**
	 * Imports the target into the current edit scene from local path. This
	 * assumes that the file from the local path exists.
	 * 
	 * @param target
	 *            the {@link ModelEntity} that will be imported.
	 * @param gameAssets
	 * @param controller
	 */
	private ModelEntity importElementFromLocal(ModelEntity target,
			EditorGameAssets gameAssets, Controller controller) {

		// Take special care in order to import correctly the
		// elements
		// from the
		// "/Online repository/{currentLibrary}/resources/{elemUri}"
		// to the project directory.
		// We must create a deep memory copy of the element, and import that to
		// the model.
		ModelEntity elem = gameAssets.copy(target);
		GameData gameData = Q.getComponent(controller.getModel().getGame(),
				GameData.class);
		elem.setX(gameData.getWidth() * .5f - elem.getOriginX());
		elem.setY(gameData.getHeight() * .5f - elem.getOriginY());
		try {
			importRenderers(elem, controller);
		} catch (Exception unexpectedException) {
			Gdx.app.log(ONLINE_REPO_TAG,
					"Exception while importing an element", unexpectedException);
			// If something goes wrong, our listener must receive a null element
			// indicating that we couldn't import the entity, as specified in
			// OnEntityImportedListener#entityImported(...) java documentation.
			elem = null;
		}
		if (elem != null && copyThumbnailToProject) {
			RepoElement repoElem = Q.getComponent(elem, RepoElement.class);
			String thumbnailName = repoElem.getThumbnail();
			if (thumbnailName != null && !thumbnailName.isEmpty()) {
				// We also must copy the thumbnail from the online
				// repository to our project
				// thumbnails folder.
				FileHandle projectThumbnails = gameAssets
						.resolve(GameStructure.THUMBNAILS_PATH);
				if (!projectThumbnails.exists()) {
					projectThumbnails.mkdirs();
				}

				FileHandle thumbnail = controller.getEditorGameAssets()
						.absolute(
								REPOSITORY_FOLDER_PATH + currentLibrary + "/"
										+ thumbnailName);

				if (thumbnail.exists()) {

					FileHandle child = projectThumbnails.child(thumbnailName
							.substring(thumbnailName.lastIndexOf("/")));
					int i = 0;
					while (child.exists()) {
						child = projectThumbnails.child(child
								.nameWithoutExtension()
								+ ++i
								+ "."
								+ child.extension());
					}

					repoElem.setThumbnail(child.name());

					thumbnail.copyTo(child);
				}
			}
		}
		controller.action(AddSceneElement.class, elem);
		return elem;
	}

	private void importRenderers(ModelEntity elem, Controller controller) {

		controller.action(ImportEntityResources.class, elem,
				REPOSITORY_FOLDER_PATH + currentLibrary + "/");
		Array<ModelEntity> children = elem.getChildren();
		for (int i = 0; i < children.size; ++i) {
			ModelEntity child = children.get(i);
			importRenderers(child, controller);
		}

	}

	/**
	 * Tries to obtain the elements from the current library and add them to
	 * {@link #onlineElements} either by downloading new information when
	 * changes are detected or by loading from local cache. Note that the
	 * {@link #currentLibrary} attribute must have been previously setted in
	 * order for this process to work correctly.
	 * 
	 * @param controller
	 */
	public void updateElements(final Controller controller,
			final ProgressListener progressListener) {

		final EditorGameAssets gameAssets = controller.getEditorGameAssets();

		if (!loadFromLocal(controller)) {
			String currZipLib = currentLibrary + ".zip";
			final FileHandle zipFile = gameAssets
					.absolute(REPOSITORY_FOLDER_PATH + currZipLib);
			sendDownloadRequest(ROOT_URL + currZipLib, zipFile, controller,
					data, new ProgressListener() {

						@Override
						public void finished(boolean downloaded,
								Controller controller) {

							Gdx.app.log(ONLINE_REPO_TAG,
									"Downloaded " + String.valueOf(downloaded));
							boolean unzipped = false, loaded = false;
							if (downloaded) {
								unzipped = unzipFile(zipFile, gameAssets
										.absolute(REPOSITORY_FOLDER_PATH
												+ currentLibrary), data, true);
								Gdx.app.log(ONLINE_REPO_TAG, "Unzipped "
										+ String.valueOf(unzipped));
								if (unzipped) {
									loaded = loadFromLocal(controller);
									Gdx.app.log(ONLINE_REPO_TAG, "Loaded "
											+ String.valueOf(loaded));
								}
							}
							progressListener.finished(downloaded && unzipped
									&& loaded, controller);
						}
					});
		} else
			progressListener.finished(true, controller);

	}

	/**
	 * 
	 * @param controller
	 * @return true if we could load the elements from local path.
	 */
	private boolean loadFromLocal(Controller controller) {
		EditorGameAssets gameAssets = controller.getEditorGameAssets();
		FileHandle libFile = gameAssets.absolute(REPOSITORY_FOLDER_PATH
				+ currentLibrary);
		if (libFile.exists()) {
			FileHandle child = libFile.child(ENTITIES_FILE_NAME);
			if (child.exists()) {
				return createFromString(child.readString(), gameAssets);
			} else {
				libFile.deleteDirectory();
				return false;
			}
		}
		return false;
	}

	/**
	 * Sends a download request from an URL, saving it to dstFile.
	 * 
	 * @param fromURL
	 * @param dstFile
	 * @param controller
	 * @param data
	 *            a temporal byte array used to write to disk efficiently.
	 * @param listener
	 *            may not be null.
	 */
	private void sendDownloadRequest(String fromURL, final FileHandle dstFile,
			final Controller controller, final byte[] data,
			final ProgressListener listener) {
		Gdx.app.log(ONLINE_REPO_TAG, "Sending download request to " + fromURL);
		HttpRequest httpRequest = new HttpRequest(Net.HttpMethods.GET);
		httpRequest.setUrl(fromURL);
		httpRequest.setContent(null);
		httpRequest.setTimeOut(TIMEOUT);
		Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(final HttpResponse httpResponse) {
				final int statusCode = httpResponse.getStatus().getStatusCode();
				// We are not in main thread right now so we
				// need to post to main thread for UI updates

				if (statusCode != 200) {
					Gdx.app.log(ONLINE_REPO_TAG,
							"An error ocurred since statusCode(" + statusCode
									+ ") is not OK(200)");
					listener.finished(false, controller);
					return;
				}

				boolean succeeded = download(dstFile, controller, httpResponse,
						data);

				listener.finished(succeeded, controller);

			}

			@Override
			public void failed(Throwable t) {
				Gdx.app.log(ONLINE_REPO_TAG,
						"Failed to perform the HTTP Request: ", t);
				listener.finished(false, controller);

			}

			@Override
			public void cancelled() {
				Gdx.app.log(ONLINE_REPO_TAG, "HTTP request cancelled");
				listener.finished(false, controller);

			}
		});
	}

	private boolean download(FileHandle dstFile, Controller controller,
			HttpResponse httpResponse, byte[] data) {
		boolean succeeded = true;

		int count = -1;
		InputStream input = null;
		OutputStream output = null;
		try {
			input = httpResponse.getResultAsStream();
			output = dstFile.write(false);

			while ((count = input.read(data)) != -1)
				output.write(data, 0, count);

		} catch (Exception e) {
			Gdx.app.error(ONLINE_REPO_TAG, "Exception while downloading file "
					+ dstFile.toString(), e);
			succeeded = false;
		} finally {
			try {
				if (output != null)
					output.close();
				if (input != null)
					input.close();
			} catch (IOException ignored) {
				Gdx.app.error(ONLINE_REPO_TAG,
						"This exception should be ignored", ignored);
			}
		}
		return succeeded;
	}

	/**
	 * Unzips a .zip file.
	 * 
	 * @param zipFile
	 * @param outDir
	 * @param data
	 * @param deleteZipFile
	 */
	private boolean unzipFile(FileHandle zipFile, FileHandle outDir,
			byte data[], boolean deleteZipFile) {
		boolean succeeded = true;
		if (!outDir.exists()) {
			outDir.mkdirs();
		}

		ZipEntry ze = null;
		ZipInputStream zin = null;
		FileOutputStream fout = null;
		int count = -1;
		try {
			zin = new ZipInputStream(zipFile.read());
			while ((ze = zin.getNextEntry()) != null) {

				File child = outDir.child(ze.getName()).file();

				if (ze.isDirectory() && !child.exists()) {
					child.mkdirs();
					continue;
				}

				// Make sure all folders exists (they should, but the safer, the
				// better
				if (child.getParentFile() != null
						&& !child.getParentFile().exists()) {
					child.getParentFile().mkdirs();
				}

				// Create file on disk...
				if (!child.exists()) {
					child.createNewFile();
				}

				fout = new FileOutputStream(child);
				while ((count = zin.read(data)) != -1)
					fout.write(data, 0, count);

				zin.closeEntry();
				fout.flush();
				fout.close();
				fout = null;

			}
		} catch (Exception e) {
			Gdx.app.error(ONLINE_REPO_TAG, "Exception while unzipping file "
					+ zipFile.toString(), e);
			succeeded = false;
		} finally {
			try {
				if (fout != null)
					fout.close();
				if (zin != null)
					zin.close();
			} catch (IOException ignored) {
				Gdx.app.error(ONLINE_REPO_TAG,
						"This exception should be ignored", ignored);
			}
		}
		if (deleteZipFile)
			zipFile.delete();

		return succeeded;
	}

	/**
	 * Tries to fill the {@link #onlineElements} by creating {@link ModelEntity
	 * ModelEntities} from the jsonString.
	 * 
	 * @param jsonString
	 *            must be correctly formated as a {@link Array list of
	 *            ModelEntities}.
	 * @param gameAssets
	 */
	@SuppressWarnings("unchecked")
	private boolean createFromString(String jsonString,
			EditorGameAssets gameAssets) {
		try {
			this.onlineElements = gameAssets.fromJson(Array.class, jsonString);
			return true;
		} catch (SerializationException se) {
			Gdx.app.log(ONLINE_REPO_TAG, "Error parsing " + ENTITIES_FILE_NAME
					+ " file", se);
			return false;
		}
	}

	public String getCurrentLibraryPath() {
		return REPOSITORY_FOLDER_PATH + currentLibrary + "/";
	}

	/**
	 * Invokes
	 * {@link OnThumbnailAvailableListener#thumbnailAvailable(String, Controller)}
	 * when the thumbnail is available locally.
	 * 
	 * @param listener
	 * @param library
	 */
	public void getLibraryThumbnail(
			final OnThumbnailAvailableListener listener, RepoLibrary library,
			Controller controller) {
		EditorGameAssets gameAssets = controller.getEditorGameAssets();

		String libThumbnail = library.getThumbnail();

		if (libThumbnail == null) {
			listener.thumbnailAvailable(null, controller);
			return;
		}

		final FileHandle thumbFile = gameAssets
				.absolute(LIBRARIES_METADATA_FOLDER_PATH + "/" + libThumbnail);

		if (!thumbFile.exists()) {
			if (!libThumbnail.startsWith("/")) {
				libThumbnail = "/" + libThumbnail;
			}
			String URL = ROOT_URL + libThumbnail;
			sendDownloadRequest(URL, thumbFile, controller, data,
					new ProgressListener() {

						@Override
						public void finished(boolean succeeded,
								Controller controller) {
							if (succeeded) {
								listener.thumbnailAvailable(thumbFile.file()
										.getAbsolutePath(), controller);
							} else {
								listener.thumbnailAvailable(null, controller);
							}
						}
					});
		} else {
			listener.thumbnailAvailable(thumbFile.file().getAbsolutePath(),
					controller);
		}
	}

	/**
	 * Listens to progress events, while downloading or updating.
	 */
	public interface ProgressListener {
		/**
		 * Invoked when the operation finished.
		 * 
		 * @param succeeded
		 * @param controller
		 */
		void finished(boolean succeeded, Controller controller);
	}

	/**
	 * Listens to import events.
	 */
	public interface OnEntityImportedListener {
		/**
		 * Invoked when the entity has finished loading either from local or by
		 * downloading its resources from the online repository. If the entity
		 * is null, the operation failed (that's a good moment to show a
		 * notification to the user).
		 * 
		 * @param entity
		 *            may be null.
		 * @param controller
		 */
		void entityImported(ModelEntity entity, Controller controller);
	}

	/**
	 * Listens to library thumbnail events.
	 */
	public interface OnThumbnailAvailableListener {
		/**
		 * Invoked when the thumbnail is locally available.
		 * 
		 * @param thumbnailPath
		 *            (absolute path to the thumbnail) may be null, if something
		 *            went wrong.
		 * @param controller
		 */
		void thumbnailAvailable(String thumbnailPath, Controller controller);
	}

	/**
	 * Fetches the {@value #LIBRARIES_FILE_NAME} file, retrieves the libraries
	 * and places its contents into {@link #libraries}.
	 */
	public void updateLibraries(final ProgressListener progressListener,
			final Controller controller) {
		if (!loadLibrariesFromLocal(progressListener, controller)) {
			HttpRequest httpRequest = new HttpRequest(Net.HttpMethods.GET);
			httpRequest.setUrl(LIBRARIES_FILE_URL);
			httpRequest.setContent(null);
			httpRequest.setTimeOut(TIMEOUT);
			Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener() {

				@Override
				public void handleHttpResponse(final HttpResponse httpResponse) {
					final int statusCode = httpResponse.getStatus()
							.getStatusCode();
					// We are not in main thread right now so we
					// need to post to main thread for UI updates

					if (statusCode != HttpStatus.SC_OK) {
						Gdx.app.log(ONLINE_REPO_TAG,
								"An error ocurred since statusCode is not OK, "
										+ httpResponse);
						failed(null);
						return;
					}

					final String res = httpResponse.getResultAsString();
					Gdx.app.log(ONLINE_REPO_TAG, "Success");

					EditorGameAssets gameAssets = controller
							.getEditorGameAssets();
					gameAssets.absolute(LIBRARIES_FILE_PATH).writeString(res,
							false);
					createLibrariesFromString(progressListener, controller,
							res, gameAssets);
				}

				@Override
				public void failed(Throwable t) {
					if (t != null)
						Gdx.app.log(ONLINE_REPO_TAG,
								"Failed to perform the HTTP Request: ", t);
					if (!loadLibrariesFromLocal(progressListener, controller)) {
						progressListener.finished(false, controller);
					}

				}

				@Override
				public void cancelled() {
					Gdx.app.log(ONLINE_REPO_TAG, "HTTP request cancelled");
					if (!loadLibrariesFromLocal(progressListener, controller)) {
						progressListener.finished(false, controller);
					}
				}
			});
		}
	}

	private boolean loadLibrariesFromLocal(
			final ProgressListener progressListener, Controller controller) {
		EditorGameAssets gameAssets = controller.getEditorGameAssets();
		FileHandle librariesFile = gameAssets.absolute(LIBRARIES_FILE_PATH);
		if (librariesFile.exists()) {
			String localJson = librariesFile.readString();
			if (localJson.isEmpty()) {
				return false;
			}
			return createLibrariesFromString(progressListener, controller,
					localJson, gameAssets);
		}
		return false;
	}

	/**
	 * Tries to retrieve the {@link #LIBRARIES_FILE_NAME} first, then use it to
	 * download library metadata to populate {@link #libraries}
	 * 
	 * @param jsonString
	 *            must be correctly formatted as a {@link Array list of Strings}
	 *            .
	 */
	@SuppressWarnings("unchecked")
	private boolean createLibrariesFromString(
			final ProgressListener progressListener,
			final Controller controller, String jsonString,
			final EditorGameAssets gameAssets) {
		try {
			final Array<String> libraryPaths = gameAssets.fromJson(Array.class,
					jsonString);
			libraries.clear();
			final boolean[] failed = { false };
			final int[] nProcessed = { 0 };

			for (int i = 0; i < libraryPaths.size; i++) {
				final String libraryPath = libraryPaths.get(i);
				if (!loadLibraryMetadataFromLocal(controller, libraryPath)) {
					String libraryJsonUrl = libraryPath.startsWith("/")
							|| libraryPath.startsWith("\\") ? ROOT_URL
							+ libraryPath : ROOT_URL + "/" + libraryPath;
					if (!libraryJsonUrl.toLowerCase().endsWith(".json")) {
						libraryJsonUrl += ".json";
					}

					HttpRequest httpRequest = new HttpRequest(
							Net.HttpMethods.GET);
					httpRequest.setUrl(libraryJsonUrl);
					httpRequest.setContent(null);
					httpRequest.setTimeOut(TIMEOUT);
					Gdx.net.sendHttpRequest(httpRequest,
							new HttpResponseListener() {

								@Override
								public void handleHttpResponse(
										final HttpResponse httpResponse) {
									final int statusCode = httpResponse
											.getStatus().getStatusCode();
									// We are not in main thread right now so we
									// need to post to main thread for UI
									// updates

									if (statusCode != HttpStatus.SC_OK) {
										Gdx.app.log(ONLINE_REPO_TAG,
												"An error ocurred since statusCode is not OK, "
														+ httpResponse);
										failed(null);
										return;
									}

									final String libraryMetadata = httpResponse
											.getResultAsString();
									gameAssets
											.absolute(
													LIBRARIES_METADATA_FOLDER_PATH
															+ "/" + libraryPath)
											.writeString(libraryMetadata, false);

									if (createLibraryMetadataFromString(
											libraryMetadata, libraryPath,
											gameAssets)) {
										nProcessed[0] = nProcessed[0] + 1;
										if (nProcessed[0] >= libraryPaths.size) {
											progressListener.finished(
													!failed[0], controller);
										}
									} else {
										failed(null);
									}
								}

								@Override
								public void failed(Throwable t) {
									if (t != null)
										Gdx.app.log(
												ONLINE_REPO_TAG,
												"Failed to perform the HTTP Request -library could not be retrieved ",
												t);
									failed[0] = true;
									nProcessed[0] = nProcessed[0] + 1;
									if (nProcessed[0] >= libraryPaths.size) {
										progressListener.finished(!failed[0],
												controller);
									}

								}

								@Override
								public void cancelled() {
									Gdx.app.log(ONLINE_REPO_TAG,
											"HTTP request cancelled-library not retrieved");
									failed[0] = true;
									nProcessed[0] = nProcessed[0] + 1;
									if (nProcessed[0] >= libraryPaths.size) {
										progressListener.finished(!failed[0],
												controller);
									}

								}
							});
				} else {
					nProcessed[0] = nProcessed[0] + 1;
					if (nProcessed[0] >= libraryPaths.size) {
						progressListener.finished(!failed[0], controller);
					}
				}
			}
		} catch (SerializationException se) {
			Gdx.app.log(ONLINE_REPO_TAG, "Error parsing " + LIBRARIES_FILE_PATH
					+ " file", se);
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param library
	 * @return
	 */
	public FileHandle getRepoLibraryFile(String libraryPath, Assets assets) {
		FileHandle librariesFile = assets
				.absolute(LIBRARIES_METADATA_FOLDER_PATH + "/" + libraryPath);
		return librariesFile;
	}

	/**
	 * 
	 * @param lib
	 * @param assets
	 * @return true if the given {@link RepoLibrary} has been downloaded.
	 */
	public boolean isDownloaded(RepoLibrary lib, Assets assets) {
		return assets.absolute(REPOSITORY_FOLDER_PATH + "/" + lib.getPath())
				.exists();
	}

	/**
	 * 
	 * 
	 * @param controller
	 * @return true if we could load the libraryMetadataPaths from local path.
	 */
	private boolean loadLibraryMetadataFromLocal(Controller controller,
			String libraryPath) {
		EditorGameAssets gameAssets = controller.getEditorGameAssets();
		FileHandle librariesFile = getRepoLibraryFile(libraryPath, gameAssets);
		if (librariesFile.exists()) {
			String localJson = librariesFile.readString();
			if (localJson.isEmpty()) {
				return false;
			}
			return createLibraryMetadataFromString(localJson, libraryPath,
					gameAssets);
		}
		return false;
	}

	/**
	 * Tries to fill the {@link #libraries} by creating {@link RepoLibrary} from
	 * the libraryMetadata.
	 * 
	 * @param libraryMetadata
	 *            must be correctly formated as a {@link RepoLibrary}.
	 * @param gameAssets
	 */
	private boolean createLibraryMetadataFromString(String libraryMetadata,
			String libraryPath, EditorGameAssets gameAssets) {

		try {
			RepoLibrary repoLibrary = gameAssets.fromJson(RepoLibrary.class,
					libraryMetadata);
			if (repoLibrary == null || repoLibrary.getPath() == null) {
				return false;
			}
			libraries.add(repoLibrary);
			return true;
		} catch (SerializationException e) {
			Gdx.app.log(ONLINE_REPO_TAG,
					"Error parsing library metadata from: " + libraryPath
							+ " file", e);
			return false;
		}
	}
}