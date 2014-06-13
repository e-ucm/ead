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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import es.eucm.ead.editor.model.Model;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * This asset manager is meant to deal with the game's assets in the editor.
 * That is, for example, the images, game.json and any scene.json file in the
 * game.
 * 
 * This asset manager should only be used in the editor
 * 
 * For managing the own application's assets (e.g. the skin and preferences),
 * use {@link es.eucm.ead.editor.assets.ApplicationAssets} instead.
 */
public class EditorGameAssets extends GameAssets {

	public static final String EDITOR_BINDINGS = "editor-bindings.json";

	/**
	 * Creates an assets handler
	 * 
	 * @param files
	 *            object granting access to files
	 */
	public EditorGameAssets(Files files) {
		super(files);
		setOutputType(OutputType.json);
		setIgnores();
	}

	/**
	 * Sets components that should not be saved
	 */
	private void setIgnores() {
		IgnoreSerializer ignoreSerializer = new IgnoreSerializer();
		setSerializer(Parent.class, ignoreSerializer);
	}

	@Override
	protected FileHandle[] resolveBindings() {
		return new FileHandle[] { files.internal(ENGINE_BINDINGS),
				files.internal(EDITOR_BINDINGS) };
	}

	public void toJsonPath(Object object, String path) {
		toJson(object, resolve(path));
	}

	@Override
	public FileHandle resolve(String path) {
		FileHandle internal = files.internal(path);
		if (internal.exists()) {
			return internal;
		}
		return files.absolute((getLoadingPath() == null
				|| path.startsWith(getLoadingPath()) ? "" : getLoadingPath())
				+ path);
	}

	/**
	 * Copy and loads the asset in the given path to the project folder
	 * 
	 * @param path
	 *            the path
	 * @param type
	 *            the asset type associated to the file
	 * @return the path of the project in which the file was copied
	 */
	private String copyToProject(String path, Class<?> type) {
		FileHandle fh = files.absolute(path);
		if (fh.exists()) {
			String folderPath = getFolder(type);
			FileHandle folder = absolute(getLoadingPath() + folderPath);
			String extension = fh.extension();
			if (!"".equals(extension)) {
				extension = "." + extension;
			}
			String name = fh.nameWithoutExtension();
			String fileName = name + extension;
			int count = 1;
			FileHandle dst;
			while ((dst = folder.child(fileName)).exists()) {
				fileName = name + count++ + extension;
			}

			fh.copyTo(dst);

			return folderPath + fileName;
		} else {
			return null;
		}
	}

	/**
	 * Copies and loads the asset in the given path to the project folder only
	 * if they weren't already loaded.
	 * 
	 * @param path
	 *            the path
	 * @param type
	 *            the asset type associated to the file
	 * @return the path of the project in which the file was copied, may be null
	 *         if the path doesn't exist
	 */
	public String copyToProjectIfNeeded(String path, Class<?> type) {
		// If resource path is not loaded
		if (!path.startsWith(getLoadingPath()) || !isLoaded(path, type)) {
			return copyToProject(path, type);
		}
		return path;
	}

	private String getFolder(Class<?> clazz) {
		if (clazz == Texture.class) {
			return IMAGES_FOLDER;
		} else {
			return null;
		}
	}

	/**
	 * Creates a new {@link Object} from another {@link Object}. This creates a
	 * deep memory copy through JSON serialization of the specified parameter.
	 * 
	 * @param entity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T copy(T entity) {
		Class<T> clazz = (Class<T>) entity.getClass();
		T ret = fromJson(clazz, toJson(entity, clazz));

		// Since Parent.class isn't serialized we must take
		// special care to copy it to the new element.
		if (entity instanceof ModelEntity) {
			ModelEntity entityRet = (ModelEntity) entity;
			if (Model.hasComponent(entityRet, Parent.class)) {
				Parent entityParent = Model.getComponent(entityRet,
						Parent.class);

				Parent retParent = new Parent();
				retParent.setParent(entityParent.getParent());
				((ModelEntity) ret).getComponents().add(retParent);
			}
		}
		return ret;
	}

	/**
	 * Loads the pixmap contained in the given file handle. Remember to call
	 * {@link com.badlogic.gdx.graphics.Pixmap#dispose()} once you are done with
	 * it
	 * 
	 * @param fh
	 *            the file handle
	 * @return a pixmap
	 */
	public Pixmap loadPixmap(FileHandle fh) {
		return new Pixmap(fh);
	}

	// ////////////////////////////////////////////
	// Method for adding assets. THIS SHOULD BE REVISED
	// ////////////////////////////////////////////

	/**
	 * Currently, this method is only invoked from
	 * {@link es.eucm.ead.editor.control.actions.model.AddScene}. We may want to
	 * rethink if this should be kept.
	 */
	public <T> void addAsset(String fileName, Class<T> type, T asset) {
		assetManager.addAsset(fileName, type, asset);
	}

	/**
	 * Serializer to ignore classes that shouldn't be saved
	 */
	public class IgnoreSerializer implements Serializer {

		@Override
		public void write(Json json, Object object, Class knownType) {
		}

		@Override
		public Object read(Json json, JsonValue jsonData, Class type) {
			return null;
		}
	}
}
