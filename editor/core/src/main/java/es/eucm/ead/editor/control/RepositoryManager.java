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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.builders.mockup.menu.InitialScreen;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ElementButton;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Image;

/**
 * <p>
 * The server's directory should have the following layout:
 * </p>
 * 
 * <pre>
 * |elements.json 	<- Correctly formated to parse a {@link List} of {@link ModelEntity ModelEntities}
 * |<strong>thumbnails.zip</strong>	<- All the thumbnails as .png, plus a bindings.properties file.
 * |
 * |resources/		<- Elements resources folder.
 * |	{NAME1}.zip	<- ZIP file containing the resources of the element who's title is "NAME1"(defined in elements.json).
 * |	{NAME2}.zip
 * |	{NAME3}.zip
 * </pre>
 * <p>
 * The <strong>thumbnails.zip</strong> contains the following structure:
 * </p>
 * 
 * <pre>
 * |bindings.properties	<- Java {@link Properties} file where is specified which element(key, title from elements.json) should display which thumbnail(value, e.g. file1.png, file2.png, file3.png).
 * |file1.png		<- This is a small image that will be displayed as a thumbnail of the element with title "X" if bindings.properties has the following line: X=file1.png.
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
	 * Client-side cached resources paths.
	 */

	private static final String MOCKUP_PROJECTS_PATH = InitialScreen.MOCKUP_PROJECT_FILE
			.file().getAbsolutePath();

	private static final String REPOSITORY_FOLDER_NAME = "/onlineRepository";
	private static final String REPOSITORY_FOLDER_PATH = MOCKUP_PROJECTS_PATH
			+ REPOSITORY_FOLDER_NAME;

	private static final String RESOURCES_FOLDER_NAME = "/resources";
	private static final String RESOURCES_FOLDER_PATH = REPOSITORY_FOLDER_PATH
			+ RESOURCES_FOLDER_NAME;

	private static final String THUMBNAILS_FOLDER_NAME = "/thumbnails";
	private static final String THUMBNAILS_FOLDER_PATH = REPOSITORY_FOLDER_PATH
			+ THUMBNAILS_FOLDER_NAME;

	private static final String THUMBNAIL_BINDINGS_FILE_NAME = "/bindings.properties";
	private static final String THUMBNAIL_BINDINGS_FILE_PATH = THUMBNAILS_FOLDER_PATH
			+ THUMBNAIL_BINDINGS_FILE_NAME;

	private static final String ELEMENTS_FILE = REPOSITORY_FOLDER_PATH
			+ "/elements.json";

	/*
	 * Server-side resources URLs.
	 */

	/**
	 * Used to download elements.json file that will be parsed as a {@link List}
	 * of {@link ModelEntity ModelEntities}.
	 */
	private static final String REPOSITORY_ELEMENTS_URL = "http://repo-justusevim.rhcloud.com/elements.json";
	/**
	 * Used to download the thumbnails.zip file.
	 */
	private static final String REPOSITORY_THUMBNAILS_URL = "http://repo-justusevim.rhcloud.com/thumbnails.zip";
	/**
	 * Used to download resources, if not aviable locally, when the user decides
	 * to import a {@link ModelEntity} into a scene. The {@link HttpRequest}
	 * will be sent to the following URL: {@value #REPOSITORY_RESOURCES_URL} +
	 * "ELEMENT_TITLE" + ".zip"
	 */
	private static final String REPOSITORY_RESOURCES_URL = "http://repo-justusevim.rhcloud.com/resources/";

	private static final String ONLINE_REPO_TAG = "RepositoryGallery";

	private String previousElements = "";

	/**
	 * Key {@link ModelEntity}'s title, value the {@link ElementButton} that
	 * displays the {@link ModelEntity}. Used to process correctly
	 * {@value #THUMBNAIL_BINDINGS_FILE_NAME}.
	 */
	private final ObjectMap<String, ElementButton> onlineElements = new ObjectMap<String, ElementButton>();

	public RepositoryManager() {

	}

	public void setPreviousElements(String newVal) {
		this.previousElements = newVal;
	}

	public Values<ElementButton> getElements() {
		return this.onlineElements.values();
	}

	// ///////////////////////////
	// CLIENT
	// ///////////////////////////

	/**
	 * Imports the element to the project by fetching it in the local cache or
	 * downloading it, if necessary.
	 * 
	 * @param target
	 * @param controller
	 */
	public void importElement(final ElementButton target,
			final Controller controller,
			final OnEntityImportedListener importListener) {

		final byte data[] = new byte[4096];
		final EditorGameAssets gameAssets = controller.getEditorGameAssets();
		final String elemTitle = target.getTitle();
		final String resourceElementPath = RESOURCES_FOLDER_PATH + "/"
				+ elemTitle;
		if (gameAssets.absolute(resourceElementPath).exists()) {
			importElementFromLocal(target, resourceElementPath, gameAssets,
					controller, importListener);
			return;
		}
		final FileHandle zipFile = gameAssets.absolute(resourceElementPath
				+ ".zip");
		sendDownloadRequest(
				REPOSITORY_RESOURCES_URL + elemTitle.replace(" ", "%20")
						+ ".zip", zipFile, controller, data,
				new ProgressListener() {

					@Override
					public void finished(boolean succeeded,
							Controller controller) {
						if (succeeded) {
							FileHandle unzippedResource = gameAssets
									.absolute(resourceElementPath);

							unzipFile(zipFile, unzippedResource, data, true);

							importElementFromLocal(target, resourceElementPath,
									gameAssets, controller, importListener);
						} else {
							importListener.entityImported(null, controller);
						}
					}
				});
	}

	/**
	 * Imports the target into the current edit scene from local path. This
	 * assumes that the file from the local path exists.
	 * 
	 * @param target
	 *            the {@link ElementButton} that contains the
	 *            {@link ModelEntity} that will be imported.
	 * @param resourceElementPath
	 *            local cached path.
	 * @param gameAssets
	 * @param controller
	 */
	private void importElementFromLocal(ElementButton target,
			String resourceElementPath, EditorGameAssets gameAssets,
			Controller controller, OnEntityImportedListener importListener) {

		// Take special care in order to import correctly the
		// elements
		// from the
		// "/onlineRepository/resource/{elemTitle}/{elem_image.png}"
		// to the project directory.
		ModelEntity elem = copyModelEntity(target.getSceneElement(), gameAssets);
		List<ModelComponent> comps = elem.getComponents();
		for (int i = 0, length = comps.size(); i < length; ++i) {
			ModelComponent comp = comps.get(i);
			if (comp.getClass() == Image.class) {
				Image renderer = (Image) comp;
				String uri = renderer.getUri();
				uri = resourceElementPath + uri.substring(uri.lastIndexOf("/"));
				String newUri = gameAssets.copyToProject(uri, Texture.class);
				renderer.setUri(newUri == null ? uri : newUri);
			}
		}
		importListener.entityImported(elem, controller);
	}

	/**
	 * Creates a new {@link ModelEntity} from another {@link ModelEntity}.
	 * 
	 * @param entity
	 * @return
	 */
	private ModelEntity copyModelEntity(ModelEntity entity,
			EditorGameAssets assets) {
		return assets.fromJson(ModelEntity.class,
				assets.toJson(entity, ModelEntity.class));
	}

	/**
	 * Tries to update the repository either by downloading new information when
	 * changes are detected or by loading from local cache.
	 * 
	 * @param controller
	 */
	public void update(final Controller controller,
			final ProgressListener progressListener) {
		HttpRequest httpRequest = new HttpRequest(Net.HttpMethods.GET);
		httpRequest.setUrl(REPOSITORY_ELEMENTS_URL);
		httpRequest.setContent(null);
		httpRequest.setTimeOut(TIMEOUT);
		Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(final HttpResponse httpResponse) {
				final int statusCode = httpResponse.getStatus().getStatusCode();
				// We are not in main thread right now so we
				// need to post to main thread for UI updates

				if (statusCode != HttpStatus.SC_OK) {
					Gdx.app.log(ONLINE_REPO_TAG,
							"An error ocurred since statusCode is not OK, "
									+ httpResponse);
					return;
				}

				final String res = httpResponse.getResultAsString();
				Gdx.app.log(ONLINE_REPO_TAG, "Success");

				if (("".equals(previousElements) && !loadFromLocal(controller,
						res)) || !previousElements.equals(res)) {
					EditorGameAssets gameAssets = controller
							.getEditorGameAssets();
					gameAssets.absolute(ELEMENTS_FILE).writeString(res, false);
					createFromString(res, controller);

					final byte data[] = new byte[4096];
					final FileHandle zipFile = gameAssets
							.absolute(REPOSITORY_FOLDER_PATH
									+ "/thumbnails.zip");
					sendDownloadRequest(REPOSITORY_THUMBNAILS_URL, zipFile,
							controller, data, new ProgressListener() {

								@Override
								public void finished(boolean succeeded,
										Controller controller) {
									if (succeeded) {
										FileHandle unzippedThumbnails = controller
												.getEditorGameAssets()
												.absolute(
														THUMBNAILS_FOLDER_PATH);

										unzipFile(zipFile, unzippedThumbnails,
												data, true);

										FileHandle bindings = unzippedThumbnails
												.child(THUMBNAIL_BINDINGS_FILE_NAME);
										processBindings(bindings, controller);
									}
									progressListener.finished(succeeded,
											controller);
								}
							});
				} else {
					progressListener.finished(true, controller);
				}

				previousElements = res;
			}

			@Override
			public void failed(Throwable t) {
				Gdx.app.log(ONLINE_REPO_TAG,
						"Failed to perform the HTTP Request: ", t);
				boolean succeeded = loadFromLocal(controller, previousElements);
				progressListener.finished(succeeded, controller);

			}

			@Override
			public void cancelled() {
				Gdx.app.log(ONLINE_REPO_TAG, "HTTP request cancelled");
				progressListener.finished(false, controller);

			}
		});
	}

	/**
	 * 
	 * @param controller
	 * @param updatedJson
	 *            the most updated {@link #ELEMENTS_FILE} info. Usually the most
	 *            recently downloaded.
	 * @return true if we could load the elements from local path.
	 */
	private boolean loadFromLocal(Controller controller, String updatedJson) {
		EditorGameAssets gameAssets = controller.getEditorGameAssets();
		FileHandle elementsFile = gameAssets.absolute(ELEMENTS_FILE);
		if (elementsFile.exists()) {
			String localJson = elementsFile.readString();
			if (!"".equals(updatedJson) && !localJson.equals(updatedJson)) {
				return false;
			}
			createFromString(localJson, controller);
			processBindings(gameAssets.absolute(THUMBNAIL_BINDINGS_FILE_PATH),
					controller);
			previousElements = updatedJson;
			return true;
		}
		return false;
	}

	/**
	 * Tries to fill the {@link #onlineElements} by creating {@link ModelEntity
	 * ModelEntities} from the jsonString.
	 * 
	 * @param jsonString
	 *            must be correctly formated as a {@link List list of
	 *            ModelEntities}.
	 * @param controller
	 */
	private void createFromString(final String jsonString,
			final Controller controller) {
		EditorGameAssets gameAssets = controller.getEditorGameAssets();

		@SuppressWarnings("unchecked")
		ArrayList<ModelEntity> elems = gameAssets.fromJson(ArrayList.class,
				jsonString);

		onlineElements.clear();
		ApplicationAssets appAssets = controller.getApplicationAssets();
		I18N i18n = appAssets.getI18N();
		Skin skin = appAssets.getSkin();
		for (ModelEntity elem : elems) {
			onlineElements.put(Model.getComponent(elem, Note.class).getTitle(),
					new ElementButton(controller.getPlatform().getSize(), i18n,
							elem, null, skin, controller));
		}
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
					return;
				}

				download(dstFile, controller, httpResponse, data);

				listener.finished(true, controller);

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

	private void download(FileHandle dstFile, Controller controller,
			HttpResponse httpResponse, byte[] data) {
		int count = -1;

		InputStream input = null;
		OutputStream output = null;
		try {
			input = httpResponse.getResultAsStream();
			output = dstFile.write(false);

			// long total = 0;
			while ((count = input.read(data)) != -1) {
				// allow canceling with back button
				/*
				 * if (isCancelled()) { input.close(); return; }
				 */
				// total += count;
				// publishing the progress....
				/*
				 * if (fileLength > 0) // only if total length is known
				 * publishProgress((int) (total * 100 / fileLength));
				 */
				output.write(data, 0, count);
			}

		} catch (Exception e) {
			Gdx.app.error(ONLINE_REPO_TAG, "Exception while downloading file "
					+ dstFile.toString(), e);
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
	}

	/**
	 * Unzips a .zip file.
	 * 
	 * @param zipFile
	 * @param outDir
	 * @param data
	 * @param deleteZipFile
	 */
	private void unzipFile(FileHandle zipFile, FileHandle outDir, byte data[],
			boolean deleteZipFile) {
		if (!outDir.exists()) {
			outDir.mkdirs();
		}
		String outPath = outDir.file().getAbsolutePath() + "/";

		ZipEntry ze = null;
		ZipInputStream zin = null;
		FileOutputStream fout = null;
		int count = -1;
		try {
			zin = new ZipInputStream(zipFile.read());
			while ((ze = zin.getNextEntry()) != null) {
				fout = new FileOutputStream(outPath + ze.getName());
				while ((count = zin.read(data)) != -1) {
					fout.write(data, 0, count);
				}

				zin.closeEntry();
				fout.close();
				fout = null;
			}
		} catch (Exception e) {
			Gdx.app.error(ONLINE_REPO_TAG, "Exception while unzipping file "
					+ zipFile.toString(), e);
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
	}

	/**
	 * Reads and loads bindingsFile to display the thumbnails.
	 * 
	 * @param bindingsFile
	 * @param controller
	 */
	private void processBindings(FileHandle bindingsFile, Controller controller) {
		InputStream is = null;
		Properties props = null;
		try {
			props = new Properties();

			props.load(is = bindingsFile.read());
		} catch (IOException ioe) {
			Gdx.app.error(ONLINE_REPO_TAG, "Error loading bindings properties",
					ioe);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ignored) {
				Gdx.app.error(ONLINE_REPO_TAG,
						"This exception should be ignored", ignored);
			}
		}

		String thumbnailsPath = ".." + REPOSITORY_FOLDER_NAME
				+ THUMBNAILS_FOLDER_NAME + "/";
		EditorGameAssets gameAssets = controller.getEditorGameAssets();
		Set<java.util.Map.Entry<Object, Object>> propsSet = props.entrySet();
		for (java.util.Map.Entry<Object, Object> prop : propsSet) {
			gameAssets.get(thumbnailsPath + prop.getValue().toString(),
					Texture.class, new ThumbnailLoadedListener(prop.getKey()
							.toString()));
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
	 * This listener sets the thumbnail icon to the linked {@link ElementButton}
	 * . The binding relation is defined via {@link #onlineElements}.
	 */
	private class ThumbnailLoadedListener implements
			AssetLoadedCallback<Texture> {
		private String title;

		/**
		 * This listener sets the thumbnail icon to the linked
		 * {@link ElementButton}. The binding relation is defined via
		 * {@link #onlineElements}.
		 */
		public ThumbnailLoadedListener(String title) {
			this.title = title;
		}

		@Override
		public void loaded(String fileName, Texture asset) {
			onlineElements.get(this.title).setIcon(asset);
		}
	}
}
