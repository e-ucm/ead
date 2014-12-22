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

package es.eucm.ead.repobuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.demobuilder.EditorDemoBuilder;
import es.eucm.ead.editor.demobuilder.ImgUtils;
import es.eucm.ead.editor.exporter.ExportCallback;
import es.eucm.ead.editor.exporter.Exporter;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.engine.utils.ZipUtils;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.data.Dimension;
import es.eucm.ead.schema.data.shape.Circle;
import es.eucm.ead.schema.data.shape.Rectangle;
import es.eucm.ead.schema.editor.components.Thumbnail;
import es.eucm.ead.schema.editor.components.repo.*;
import es.eucm.ead.schema.editor.components.repo.licenses.DefaultLicenses;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Extend this class to create libraries for the mokap repo.
 * 
 * Created by Javier Torrente on 23/09/14.
 */
public abstract class RepoLibraryBuilder extends EditorDemoBuilder {

	/*
	 * Properties that can be set
	 */
	private static final String THUMBNAIL_QUALITIES = "72;128;256;512";

	private static final String DEFAULT_THUMBNAILS_FOLDER = "thumbnails/";

	private static final String DEFAULT_RESOURCES_FOLDER = "resources/";

	private static final String ZIP_THUMBNAILS_SUBPATH = "thumbnails/";

	private static final String ZIP_DESCRIPTOR_SUBPATH = "descriptor.json";

	private static final String ZIP_CONTENTS_SUBPATH = "contents.zip";

	private static final String ENTITIES_JSON = "entities.json";

	private static final String ELEMENTS_SUBFOLDER = "elements/";

	private static final String LIBRARIES_SUBFOLDER = "libraries/";

	private static final String PREVIEW_GAMES_SUBFOLDER = "preview/";

	public static final String OUTPUT = "Output";

	public static final String THUMBNAILS = "thumbnails";

	public static final String RESOURCES = "resources";

	public static final String VERSION = "Version";

	public static final String TAGS = "Tags";

	public static final String LICENSE = "License";

	public static final String AUTHOR_NAME = "aname";

	public static final String AUTHOR_URL = "aurl";

	public static final String MAX_WIDTH = "mwidth";

	public static final String MAX_HEIGHT = "mheight";

	public static final String LIB_NAME = "LibName";

	public static final String AUTO_IDS = "AutoIds";

	public static final String PUBLISHER = "Publisher";

	public static final String CATEGORIES = "Categories";

	public static final String DEFAULT = "default";

	public static final String PREVIEW_WIDTH = "PreviewWidth";

	public static final String PREVIEW_HEIGHT = "PreviewHeight";

	public static final String ENGINE_JAR_FOR_PREVIEW = "EngineJarWithDependencies";

	protected Map<String, String> properties = new HashMap<String, String>();

	protected RepoLibrary lastLibrary;

	protected RepoElement lastElement;

	protected List<ModelEntity> repoEntities = new ArrayList<ModelEntity>();

	/**
	 * Creates the object but does not actually build the game. Just creates the
	 * temp folder and unzips the the contents of the file specified by the
	 * relative path {@code root}
	 * 
	 * @param root
	 */
	public RepoLibraryBuilder(String root) {
		super(root);
		setCommonProperty(THUMBNAIL_QUALITIES, "512,256,128,72");
		setCommonProperty(THUMBNAILS, DEFAULT_THUMBNAILS_FOLDER);
		setCommonProperty(RESOURCES, DEFAULT_RESOURCES_FOLDER);
		setCommonProperty(PREVIEW_WIDTH, "1800");
		setCommonProperty(PREVIEW_HEIGHT, "900");
		Calendar calendar = Calendar.getInstance();
		String defaultVersion = Integer.toString(calendar.get(Calendar.YEAR))
				+ Integer.toString(calendar.get(Calendar.MONTH))
				+ Integer.toString(calendar.get(Calendar.DAY_OF_MONTH))
				+ Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		setCommonProperty(VERSION, defaultVersion);
		convertPNGs = true;
	}

	public String getRoot() {
		return root;
	}

	public int getNumberOfItems() {
		return repoEntities.size();
	}

	public String resource(String resource) {
		return resource.startsWith(properties.get(RESOURCES)) ? resource
				: properties.get(RESOURCES) + resource;
	}

	public RepoLibraryBuilder frame(String frameUri, float duration) {
		return (RepoLibraryBuilder) super.frame(resource(frameUri), duration);
	}

	public RepoLibraryBuilder frame(ModelEntity modelEntity, String frameUri,
			float duration, Frames.Sequence sequence) {
		return (RepoLibraryBuilder) super.frame(modelEntity,
				resource(frameUri), duration, sequence);
	}

	public RepoLibraryBuilder blinkFrameAnimation(String... frames) {
		for (int i = 0; i < frames.length; i++) {
			frames[i] = resource(frames[i]);
		}
		return (RepoLibraryBuilder) super.blinkFrameAnimation(frames);
	}

	public void createJarBundle(FileHandle destiny, FileHandle previewZip) {
		Exporter exporter = new Exporter(gameAssets);
		FileHandle tempFolder = FileHandle.tempDirectory("jarbundle");
		tempFolder.mkdirs();
		ZipUtils.unzip(previewZip, tempFolder);
		Map<String, Object> allEntities = new HashMap<String, Object>();
		for (String key : entities.keySet()) {
			allEntities.put(key, entities.get(key));
		}
		for (ModelEntity entity : repoEntities) {
			String key = null;
			for (ModelComponent component : entity.getComponents()) {
				if (component instanceof RepoElement) {
					if (component.getId() != null) {
						allEntities.put(
								component.getId().endsWith(".json") ? component
										.getId() : component.getId() + ".json",
								entity);
					}
				}
			}
		}

		if (properties.get(ENGINE_JAR_FOR_PREVIEW) != null) {
			exporter.exportAsJar(destiny.path(), tempFolder.path(),
					properties.get(ENGINE_JAR_FOR_PREVIEW),
					allEntities.entrySet(), new ExportCallback() {
						@Override
						public void error(String errorMessage) {

						}

						@Override
						public void progress(int percentage, String currentTask) {

						}

						@Override
						public void complete(String completionMessage) {

						}
					});
		} else {
			System.err
					.println("The path to the engine jar with dependencies file was not set. Jar previewer will not be produced");
		}
	}

	public FileHandle createPreviewGame() {
		int width = Integer.parseInt(properties.get(PREVIEW_WIDTH));
		int height = Integer.parseInt(properties.get(PREVIEW_HEIGHT));
		String libId = makeLibraryId();

		game(width, height);

		// Create a scene for each element
		for (int i = 0; i < repoEntities.size(); i++) {
			scene(null);

			// Background
			ShapeRenderer renderer = new ShapeRenderer();
			Rectangle rectangle = new Rectangle();
			rectangle.setWidth(width);
			rectangle.setHeight(height);
			renderer.setShape(rectangle);
			renderer.setPaint("000000;000000");
			ModelEntity background = new ModelEntity();
			background.getComponents().add(renderer);
			getLastScene().getChildren().add(background);

			// Load element to preview
			initBehavior(getLastScene(), makeAddEntity(libId + (i + 1)
					+ ".json"));

			// Next scene
			if (i < repoEntities.size() - 1) {
				entity(getLastScene(), null, width - 200, 80);
				ShapeRenderer shapeRenderer = new ShapeRenderer();
				Circle circle = new Circle();
				circle.setRadius(20);
				shapeRenderer.setShape(circle);
				shapeRenderer.setPaint("ffffff;ffffff");
				getLastEntity().getComponents().add(shapeRenderer);
				GoScene goScene = new GoScene();
				goScene.setSceneId(DEFAULT_SCENE_PREF + (i + 1) + JSON);
				touchBehavior(goScene);
			}

			// Previous scene
			if (i > 0) {
				entity(getLastScene(), null, 50, 80);
				ShapeRenderer shapeRenderer = new ShapeRenderer();
				shapeRenderer.setPaint("ffffff;ffffff");
				Circle circle = new Circle();
				circle.setRadius(20);
				shapeRenderer.setShape(circle);
				getLastEntity().getComponents().add(shapeRenderer);
				GoScene goScene = new GoScene();
				goScene.setSceneId(DEFAULT_SCENE_PREF + (i - 1) + JSON);
				touchBehavior(goScene);
			}
		}

		FileHandle prevRootFolder = rootFolder;
		rootFolder = FileHandle.tempDirectory("previewgame");
		rootFolder.mkdirs();
		save();
		FileHandle toReturn = rootFolder;
		rootFolder = prevRootFolder;
		return toReturn;
	}

	public void exportByLibrary(String outputDir) {
		setCommonProperty(OUTPUT, outputDir);

		createOutputFolder();
		// Copy

		doBuild();

		// Update version code in entities
		for (ModelEntity modelEntity : repoEntities) {
			for (ModelComponent modelComponent : modelEntity.getComponents()) {
				if (modelComponent instanceof RepoElement) {
					((RepoElement) modelComponent).setVersion(properties
							.get(VERSION));
				}
			}
		}

		// Save entities.json
		FileHandle fh = rootFolder.child(ENTITIES_JSON);
		Gdx.app.debug(LOG_TAG, "Saving " + ENTITIES_JSON + " to: "
				+ fh.file().getAbsolutePath());
		gameAssets.toJson(repoEntities, null, fh);

		// Zip entities.json (+resources & thumbnails) to output folder
		FileHandle outputZip = new FileHandle(outputDir);
		outputZip.mkdirs();
		outputZip = outputZip.child(root + ".zip");
		ZipUtils.zip(rootFolder, outputZip);

		// Update Mb
		float sizeInMb = outputZip.length() / 1048576F;
		if (sizeInMb > 0) {
			lastLibrary.setSize(sizeInMb);
		} else {
			lastLibrary.setSize(-1F);
		}

		// Update version for library
		lastLibrary.setVersion(properties.get(VERSION));

		// Create json for library
		FileHandle outputJson = new FileHandle(outputDir);
		outputJson = outputJson.child(root + ".json");
		gameAssets.toJson(lastLibrary, null, outputJson);
	}

	public FileHandle export(String outputDir) {
		setCommonProperty(OUTPUT, outputDir);

		// Copy and unzip resources
		createOutputFolder();
		FileHandle outputFH = new FileHandle(outputDir);
		outputFH.mkdirs();
		FileHandle entitiesDir = outputFH.child(ELEMENTS_SUBFOLDER);
		entitiesDir.mkdirs();

		doBuild();

		// Iterate through entities
		List<FileHandle> entityTempFolders = new ArrayList<FileHandle>();
		for (ModelEntity modelEntity : repoEntities) {
			entityTempFolders.add(exportElement(modelEntity, outputFH));
		}

		exportRepoLibrary(lastLibrary, outputFH);
		FileHandle previewGameFH = null;
		entityTempFolders.add(previewGameFH = createPreviewGame());

		// Create preview game
		FileHandle previewZip = outputFH.child(PREVIEW_GAMES_SUBFOLDER).child(
				root + ".zip");
		previewZip.parent().mkdirs();

		FileHandle[] sourcesForPreviewGame = new FileHandle[entityTempFolders
				.size()];
		for (int i = 0; i < entityTempFolders.size(); i++) {
			FileHandle fh = entityTempFolders.get(i);
			if (fh == previewGameFH) {
				sourcesForPreviewGame[i] = fh;
			} else {
				sourcesForPreviewGame[i] = fh.child(ZIP_CONTENTS_SUBPATH);
			}
		}
		ZipUtils.mergeZipsAndDirsToFile(previewZip, sourcesForPreviewGame);

		// Create jar bundle
		FileHandle exportFolder = outputFH.child("export/");
		exportFolder.mkdirs();
		createJarBundle(exportFolder.child(root + ".jar"), previewZip);

		// Delete temp folders
		for (FileHandle temp : entityTempFolders) {
			temp.deleteDirectory();
		}

		return previewZip;
	}

	private FileHandle exportElement(ModelEntity modelEntity,
			FileHandle outputFH) {
		// Make a list of all binaries referenced by this modelEntity
		Array<String> binaryPaths = ProjectUtils.listRefBinaries(modelEntity);
		// Copy all binaries to a temp folder
		FileHandle tempFolder = FileHandle.tempDirectory("modelentity");
		tempFolder.mkdirs();
		for (String binary : binaryPaths) {
			FileHandle destinyFH = tempFolder.child(binary);
			rootFolder.child(binary).copyTo(destinyFH);
		}
		// Update version code in entity and get id
		String id = null;
		RepoElement repoElement = null;
		for (ModelComponent modelComponent : modelEntity.getComponents()) {
			if (modelComponent instanceof RepoElement) {
				repoElement = (RepoElement) modelComponent;
				repoElement.setVersion(properties.get(VERSION));
				id = modelComponent.getId();
			}
		}
		// Write model entity to json, if id is not null
		if (repoElement != null) {
			FileHandle fh = tempFolder.child(id + ".json");
			Gdx.app.debug(LOG_TAG, "Saving " + id + " to: "
					+ fh.file().getAbsolutePath());
			gameAssets.toJson(modelEntity, null, fh);

			// Zip contents
			FileHandle contentsZip = FileHandle.tempFile(id);
			ZipUtils.zip(tempFolder, contentsZip);
			// Clear temp folder
			tempFolder.emptyDirectory();
			// Move contents back to temp file
			contentsZip.moveTo(tempFolder.child(ZIP_CONTENTS_SUBPATH));
			// Write descriptor
			gameAssets.toJson(repoElement, null,
					tempFolder.child(ZIP_DESCRIPTOR_SUBPATH));
			// Copy thumbnail paths
			FileHandle zipThumbnails = tempFolder.child(ZIP_THUMBNAILS_SUBPATH);
			zipThumbnails.mkdirs();
			for (FileHandle thumbnail : rootFolder.child(id).list()) {
				thumbnail.moveTo(zipThumbnails);
			}

			// Zip temp folder to destiny
			FileHandle entitiesFolder = outputFH.child(ELEMENTS_SUBFOLDER);
			entitiesFolder.mkdirs();
			FileHandle elementZipFH = entitiesFolder.child(id + ".zip");
			ZipUtils.zip(tempFolder, elementZipFH);
		} else {
			Gdx.app.error(LOG_TAG,
					"Null id for element, it will be skipped while exporting:  "
							+ gameAssets.toJson(modelEntity));
		}

		return tempFolder;
	}

	private void exportRepoLibrary(RepoLibrary repoLibrary, FileHandle outputFH) {
		// Update version for library
		repoLibrary.setVersion(properties.get(VERSION));

		// Make temp folder
		FileHandle tempFolder = FileHandle.tempDirectory("repolibrary");
		tempFolder.mkdirs();
		// Update version code in entity and get id
		String id = repoLibrary.getId();
		// Write model entity to json, if id is not null
		if (id != null) {
			FileHandle fh = tempFolder.child(id + ".json");
			Gdx.app.debug(LOG_TAG, "Saving " + id + " to: "
					+ fh.file().getAbsolutePath());
			gameAssets.toJson(repoLibrary, null, fh);

			// Copy thumbnail paths
			FileHandle zipThumbnails = tempFolder.child(ZIP_THUMBNAILS_SUBPATH);
			zipThumbnails.mkdirs();
			for (FileHandle thumbnail : rootFolder.child(id).list()) {
				thumbnail.moveTo(zipThumbnails);
			}

			// Zip temp folder to destiny
			FileHandle libsFolder = outputFH.child(LIBRARIES_SUBFOLDER);
			libsFolder.mkdirs();
			FileHandle libZipFH = libsFolder.child(id + ".zip");
			ZipUtils.zip(tempFolder, libZipFH);
		} else {
			Gdx.app.error(LOG_TAG,
					"Null id for repo library, it will be skipped while exporting:  "
							+ gameAssets.toJson(repoLibrary));
		}
		// Delete temp folder
		tempFolder.deleteDirectory();
	}

	public RepoLibraryBuilder setCommonProperty(String property, String value) {
		properties.put(property, value);
		return this;
	}

	public RepoLibraryBuilder repoLib(String nameEn, String nameEs,
			String descriptionEn, String descriptionEs, String thumbnail) {
		lastLibrary = makeRepoLibrary(nameEn, nameEs, descriptionEn,
				descriptionEs, thumbnail);
		String libraryId = null;
		if (properties.get(AUTO_IDS) != null) {
			libraryId = makeLibraryId();
		} else {
			libraryId = nameEn;
		}
		lastLibrary.setId(libraryId);

		if (thumbnail == null) {
			thumbnail = root + ".png";
		}
		// Copy thumbnail source file to temp folder
		FileHandle libraryFolder = rootFolder.child(makeLibraryId());
		libraryFolder.mkdirs();
		gameAssets.resolve(thumbnail).copyTo(rootFolder);
		getThumbnailPaths(rootFolder, thumbnail, libraryFolder);

		return this;
	}

	public RepoLibraryBuilder repoEntity(String nameEn, String nameEs,
			String descriptionEn, String descriptionEs, String thumbnail,
			String image) {
		return repoEntity(nameEn, nameEs, descriptionEn, descriptionEs,
				thumbnail, null, image);
	}

	public RepoLibraryBuilder repoEntity(String nameEn, String nameEs,
			String descriptionEn, String descriptionEs, String thumbnail,
			String categories, String image) {
		if (image != null
				&& !(image.toLowerCase().endsWith(".png")
						|| image.toLowerCase().endsWith(".jpg") || image
						.toLowerCase().endsWith(".jpeg"))) {
			image += ".png";
		}
		// Create holding entity and component
		ModelEntity parent = entity(null,
				image == null ? null : properties.get(RESOURCES) + image, 0, 0)
				.getLastEntity();
		repoEntities.add(parent);
		lastElement = makeRepoElement(nameEn, nameEs, descriptionEn,
				descriptionEs, categories);
		parent.getComponents().add(lastElement);

		// Create default id, based on library name, if the option is selected
		String id = null;
		if (properties.get(AUTO_IDS) != null) {
			String libraryId = makeLibraryId();
			id = libraryId + repoEntities.size();
			lastElement.setId(id);
			lastElement.setLibraryId(libraryId);
		} else {
			id = nameEn;
		}
		// Add publisher, if present
		if (properties.get(PUBLISHER) != null) {
			lastElement.setPublisher(properties.get(PUBLISHER));
		}

		adjustEntity(parent);

		// Write thumbnails
		FileHandle thumbnailsTempPathForElement = rootFolder.child(id + "/");
		thumbnailsTempPathForElement.mkdirs();
		getThumbnailPaths(rootFolder,
				thumbnail == null ? null : properties.get(THUMBNAILS)
						+ thumbnail, thumbnailsTempPathForElement);
		return this;
	}

	public RepoLibraryBuilder frameState(int nTags, float duration,
			String... tagsAndUris) {
		for (int i = nTags; i < tagsAndUris.length; i++) {
			tagsAndUris[i] = properties.get(RESOURCES) + tagsAndUris[i];
		}
		super.frameState(getLastEntity(), nTags, duration, tagsAndUris);
		return this;
	}

	public RepoLibraryBuilder adjustEntity(ModelEntity parent) {
		// /////// Entity adjustments
		// Calculate current dimension
		Dimension actualDim = null;
		for (ModelComponent component : parent.getComponents()) {
			if (component instanceof Renderer) {
				actualDim = getRendererDimension((Renderer) component);
				break;
			}
		}

		if (actualDim == null || actualDim.getWidth() == 0
				|| actualDim.getHeight() == 0) {
			return this;
		}

		float actualHeight = actualDim.getHeight();
		float actualWidth = actualDim.getWidth();

		for (ModelComponent component : parent.getComponents()) {
			if (component instanceof RepoElement) {
				((RepoElement) component).setWidth(actualWidth);
				((RepoElement) component).setHeight(actualHeight);
				break;
			}
		}

		// Center origin
		parent.setOriginX(actualWidth / 2.0F);
		parent.setOriginY(actualHeight / 2.0F);

		// Update scale in case there is a max width or max height declared
		float sy = 1.0F, sx = 1.0F;
		if (properties.get(MAX_HEIGHT) != null) {
			try {
				float maxHeight = Float.parseFloat(properties.get(MAX_HEIGHT));
				if (actualHeight > maxHeight) {
					sy = maxHeight / actualHeight;
				}
			} catch (NumberFormatException e) {
				// Just log it
				Gdx.app.log("RepoLibrary", "could not parse MAX_HEIGHT="
						+ properties.get(MAX_HEIGHT));
			}
		}

		if (properties.get(MAX_WIDTH) != null) {
			try {
				float maxWidth = Float.parseFloat(properties.get(MAX_WIDTH));
				if (actualWidth > maxWidth) {
					sx = maxWidth / actualWidth;
				}
			} catch (NumberFormatException e) {
				// Just log it
				Gdx.app.log("RepoLibrary", "could not parse MAX_HEIGHT="
						+ properties.get(MAX_HEIGHT));
			}
		}

		sx = sy = Math.min(sx, sy);

		if (sx != 1.0) {
			parent.setScaleX(sx);
		}

		if (sy != 1.0) {
			parent.setScaleY(sy);
		}

		return this;
	}

	public RepoLibraryBuilder authorName(String name) {
		if (lastLibrary != null) {
			lastLibrary.setAuthorName(name);
		} else if (lastElement != null) {
			lastElement.setAuthorName(name);
		}
		return this;
	}

	public RepoLibraryBuilder authorUrl(String authorUrl) {
		if (lastLibrary != null) {
			lastLibrary.setAuthorUrl("'" + authorUrl + "'");
		} else if (lastElement != null) {
			lastElement.setAuthorUrl("'" + authorUrl + "'");
		}
		return this;
	}

	public RepoLibraryBuilder tag(String tagEn, String tagEs) {
		if (lastLibrary != null) {
			if (!lastLibrary.getTagList().contains(tagEn, false)) {
				lastLibrary.getTagList().add(tagEn);
			}
			if (!lastLibrary.getTagList().contains(tagEs, false)) {
				lastLibrary.getTagList().add(tagEs);
			}
		} else if (lastElement != null) {
			if (!lastElement.getTagList().contains(tagEn, false)) {
				lastElement.getTagList().add(tagEn);
			}
			if (!lastElement.getTagList().contains(tagEs, false)) {
				lastElement.getTagList().add(tagEs);
			}
		}
		return this;
	}

	public RepoLibraryBuilder tagFullyAnimatedCharacter() {
		return tagAnimatedCharacter().tag("walk", "andar")
				.tag("grab", "agarrar").tag("talk", "hablar")
				.tag("use", "usar");
	}

	public RepoLibraryBuilder tagAnimatedCharacter() {
		return tagCharacter().tag("animated", "animado");
	}

	public RepoLibraryBuilder tagCharacter() {
		return tag("character", "personaje");
	}

	public RepoLibraryBuilder license(String strLicense) {
		DefaultLicenses license = StaticLicenses.get(strLicense);
		if (license != null) {
			if (lastLibrary != null) {
				if (!lastLibrary.getLicenseNameList().contains(strLicense,
						false)) {
					lastLibrary.getLicenseNameList().add(strLicense);
				}
			} else if (lastElement != null) {
				lastElement.setLicenseName(strLicense);
			}
		} else {
			System.err.println("Invalid license: " + strLicense);
		}
		return this;
	}

	public RepoLibraryBuilder category(RepoCategories category) {
		if (lastLibrary != null) {
			if (!lastLibrary.getCategoryList().contains(category, false)) {
				lastLibrary.getCategoryList().add(category);
			}
		} else if (lastElement != null) {
			if (!lastElement.getCategoryList().contains(category, false)) {
				lastElement.getCategoryList().add(category);
			}
		}
		return this;
	}

	public RepoLibraryBuilder category(String strCategory) {
		try {
			RepoCategories category = RepoCategories.fromValue(strCategory);
			return category(category);
		} catch (IllegalArgumentException e) {
			System.err.println("Invalid category: " + strCategory);
		}
		return this;
	}

	public RepoElement makeRepoElement(String nameEn, String nameEs,
			String descriptionEn, String descriptionEs, String categories) {
		// Infer w,h
		int width = 0, height = 0;
		ModelEntity modelEntity = getLastEntity();
		for (ModelComponent component : modelEntity.getComponents()) {
			if (component instanceof Renderer) {
				Dimension dimension = getRendererDimension((Renderer) component);
				width = Math.max(width, dimension.getWidth());
				height = Math.max(height, dimension.getHeight());
			}
		}

		return makeRepoElement(nameEn, nameEs, descriptionEn, descriptionEs,
				categories, width, height);
	}

	public RepoElement makeRepoElement(String nameEn, String nameEs,
			String descriptionEn, String descriptionEs, String categories,
			int width, int height) {
		// Create repo element with default options, if necessary
		RepoElement repoElement = lastElement = new RepoElement();
		enEsString(lastElement.getNameList(), lastElement.getNameI18nList(),
				nameEn, nameEs);
		enEsString(repoElement.getDescriptionList(),
				repoElement.getDescriptionI18nList(), descriptionEn,
				descriptionEs);
		repoElement.setWidth(width);
		repoElement.setHeight(height);

		// Add default author, if any
		if (properties.get(AUTHOR_NAME) != null) {
			repoElement.setAuthorName(properties.get(AUTHOR_NAME));
		}

		if (properties.get(AUTHOR_URL) != null) {
			repoElement.setAuthorUrl("'" + properties.get(AUTHOR_URL) + "'");
		}

		// Add default tags, if any
		if (properties.get(TAGS) != null) {
			String commonTags = properties.get(TAGS);
			for (String tag : commonTags.split(",")) {
				String en = tag.split(";")[0];
				String es = tag.split(";").length > 1 ? tag.split(";")[1] : tag
						.split(";")[0];
				if (!repoElement.getTagList().contains(en, false)) {
					repoElement.getTagList().add(en);
				}
				if (!repoElement.getTagList().contains(es, false)) {
					repoElement.getTagList().add(es);
				}
			}
		}

		// Add default license, if any
		if (properties.get(LICENSE) != null) {
			for (String commonLicense : properties.get(LICENSE).split(",")) {
				license(commonLicense);
			}
		}

		// Add default & custom categories
		if (properties.get(CATEGORIES) != null) {
			for (String category : properties.get(CATEGORIES).split(",")) {
				category(category);
			}
		}
		if (categories != null) {
			for (String category : categories.split(",")) {
				category(category);
			}
		}
		return repoElement;
	}

	public RepoLibrary makeRepoLibrary(String nameEn, String nameEs,
			String descriptionEn, String descriptionEs, String thumbnail) {
		RepoLibrary repoLibrary = new RepoLibrary();
		enEsString(repoLibrary.getNameList(), repoLibrary.getNameI18nList(),
				nameEn, nameEs);
		enEsString(repoLibrary.getDescriptionList(),
				repoLibrary.getDescriptionI18nList(), descriptionEn,
				descriptionEs);

		// Collect license & author info, number of elements
		NoRepetitionList<String> uniqueAuthors = new NoRepetitionList<String>();
		NoRepetitionList<String> uniqueUrls = new NoRepetitionList<String>();
		NoRepetitionList<String> uniqueLicenses = new NoRepetitionList<String>();
		NoRepetitionList<RepoCategories> uniqueCategories = new NoRepetitionList<RepoCategories>();
		int nItems = 0;
		for (ModelEntity entity : repoEntities) {
			for (ModelComponent component : entity.getComponents()) {
				if (component instanceof RepoElement) {
					nItems++;
					RepoElement repoElement = (RepoElement) component;
					if (repoElement.getAuthorName() != null) {
						uniqueAuthors.add(repoElement.getAuthorName());
					}
					if (repoElement.getAuthorUrl() != null) {
						uniqueUrls.add(repoElement.getAuthorUrl());
					}
					if (repoElement.getLicenseName() != null) {
						uniqueLicenses.add(repoElement.getLicenseName());
					}
					for (RepoCategories repoCat : repoElement.getCategoryList()) {
						uniqueCategories.add(repoCat);
					}
					break;
				}
			}
		}
		repoLibrary.setAuthorName(uniqueAuthors.toString());
		repoLibrary.setAuthorUrl("'" + uniqueUrls.toString() + "'");
		for (String license : uniqueLicenses) {
			repoLibrary.getLicenseNameList().add(license);
		}
		for (RepoCategories repoCategory : uniqueCategories) {
			repoLibrary.getCategoryList().add(repoCategory);
		}

		// Set number of elements
		repoLibrary.setNumberOfElements(nItems);
		// Set path (root)
		// repoLibrary.setPath(root);
		// Set default tags, if any
		if (properties.get(TAGS) != null) {
			for (String parsedTag : parseI18NTag(properties.get(TAGS))) {
				if (!repoLibrary.getTagList().contains(parsedTag, false)) {
					repoLibrary.getTagList().add(parsedTag);
				}
			}
		}
		return repoLibrary;
	}

	protected List<String> parseI18NTag(String string) {
		List<String> parsedTags = new ArrayList<String>();
		for (String tag : string.split(",")) {
			String en = tag.split(";")[0];
			String es = tag.split(";").length > 1 ? tag.split(";")[1] : tag
					.split(";")[0];
			parsedTags.add(en);
			parsedTags.add(es);
		}
		return parsedTags;
	}

	/*
	 * Returns a valid id (0-9 and a-z lowercase only) from the given libName.
	 * Unsupported characters are replaced by _
	 */
	private String makeLibraryId() {
		String libName = properties.get(LIB_NAME) != null ? properties
				.get(LIB_NAME) : root;
		String id = "";
		for (int i = 0; i < libName.length(); i++) {
			if (Character.isLetterOrDigit(libName.charAt(i))) {
				id += libName.charAt(i);
			} else {
				id += "_";
			}
		}
		return id;
	}

	private String nameToPath(String name) {
		if (name == null || name.equals("")) {
			return "undef" + new Random().nextInt(100000000);
		}

		String path = "";
		for (int i = 0; i < name.length(); i++) {
			if (Character.isLetterOrDigit(name.charAt(i))) {
				path += name.charAt(i);
			} else {
				path += "_";
			}
		}

		return path;
	}

	private String[] enEquivalents = { "en", "en_EN", "en_US", "en_UK" };
	private String[] esEquivalents = { "es", "es_ES" };

	protected void enEsString(Array<String> values, Array<String> langs,
			String enValue, String esValue) {
		for (String enLang : enEquivalents) {
			values.add(enValue);
			langs.add(enLang);
		}
		for (String esLang : esEquivalents) {
			values.add(esValue);
			langs.add(esLang);
		}
	}

	private/* RepoThumbnail */void getThumbnailPaths(FileHandle sourceDir,
			String thumbnailPath, FileHandle targetDir) {
		if (!thumbnailPath.contains(".")) {
			thumbnailPath += ".png";
		}

		FileHandle origin = sourceDir.child(thumbnailPath);
		int originalWidth = -1;
		int originalHeight = -1;
		float originalRate = -1;
		BufferedImage originalImage = null;
		try {
			originalImage = ImageIO.read(origin.read());
			originalWidth = originalImage.getWidth();
			originalHeight = originalImage.getHeight();
			originalRate = (float) originalHeight / (float) originalWidth;
		} catch (IOException e) {
			System.err.println("[Error] Could not read original thumbnail: "
					+ thumbnailPath);
		}

		// RepoThumbnail repoThumbnail = new RepoThumbnail();

		if (originalWidth == -1) {
			Thumbnail thumbnail = new Thumbnail();
			thumbnail.setWidth(-1);
			thumbnail.setHeight(-1);
			thumbnail.setPath(thumbnailPath);
		} else {
			for (String quality : properties.get(THUMBNAIL_QUALITIES)
					.split(",")) {
				int width = Integer.parseInt(quality);
				int height = Math.round(width * originalRate);

				if (originalWidth < width) {
					continue;
				}

				String path;
				if (width == originalWidth) {
					// path = thumbnailPath;
					path = originalWidth + "x" + originalHeight + ".png";
					if (!targetDir.child(path).exists()) {
						origin.copyTo(targetDir.child(path));
					}
				} else {
					// targetDir.child(quality).mkdirs();
					// path = quality + "/" + thumbnailPath;
					path = width + "x" + height + ".png";
					String originPath = origin.path();
					String outputPath = targetDir.child(path).path();
					targetDir.child(path).parent().mkdirs();
					ImgUtils.thumbnail(originPath, outputPath, width, height);
				}

				/*
				 * Thumbnail thumbnail = new Thumbnail();
				 * thumbnail.setWidth(width); thumbnail.setHeight(height);
				 * thumbnail.setPath(path);
				 * 
				 * repoThumbnail.getThumbnails().add(thumbnail);
				 */

			}

			if (targetDir.list().length == 0) {
				origin.copyTo(targetDir.child(originalWidth + "x"
						+ originalHeight + ".png"));
			}

			/*
			 * if (repoThumbnail.getThumbnails().size == 0) { Thumbnail
			 * thumbnail = new Thumbnail(); thumbnail.setWidth(originalWidth);
			 * thumbnail.setHeight(originalHeight);
			 * thumbnail.setPath(thumbnailPath);
			 * 
			 * repoThumbnail.getThumbnails().add(thumbnail); }
			 */
		}

		// return repoThumbnail;
	}

	private static class NoRepetitionList<T> extends ArrayList<T> {
		@Override
		public boolean add(T t) {
			boolean exists = false;
			for (int i = 0; i < size(); i++) {
				exists |= get(i) == null && t == null || get(i) == t
						|| get(i).equals(t);
			}
			if (!exists) {
				return super.add(t);
			}
			return false;
		}

		public String toString() {
			String str = "";
			for (T t : this) {
				str += t.toString() + ",";
			}
			if (str.endsWith(",")) {
				str = str.substring(0, str.length() - 1);
			}
			return str;
		}
	}
}
