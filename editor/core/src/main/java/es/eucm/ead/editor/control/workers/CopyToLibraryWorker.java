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
package es.eucm.ead.editor.control.workers;

import java.io.FileNotFoundException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.SerializationException;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ModelStructure;

/**
 * Copies to the library folder the contents of a {@link ModelEntity} defined by
 * a .json file. Note that the result is executed on UI thread.
 * <dl>
 * <dt><strong>The input arguments are</strong></dt>
 * <dd><strong>args[0]</strong> <em>FileHandle</em> Directory to the contents of
 * the element that will be copied. E.g. "contents.zip" that has already been
 * unzipped somewhere. The contents must contain one .json file where the
 * {@link ModelEntity} is defined.</dd>
 * <dd><strong>args[1]</strong> <em>RepoElement</em> The element that will be
 * added to the library.</dd>
 * <dd><strong>args[1]</strong> <em>FileHandle</em> The thumbnail of the element
 * </dd>
 * </dl>
 * <dl>
 * <dt><strong>The result argument is</strong></dt>
 * <dd><strong>if</strong> everything went well, result(true) is invoked.</dd>
 * </dl>
 */
public class CopyToLibraryWorker extends Worker {

	private EditorGameAssets gameAssets;

	private FileHandle entityFolder;

	private FileHandle contentsFolder;

	private RepoElement element;

	private FileHandle thumbnail;

	public CopyToLibraryWorker() {
		super(true);
	}

	@Override
	protected void prepare() {
		contentsFolder = (FileHandle) args[0];
		element = (RepoElement) args[1];
		thumbnail = (FileHandle) args[2];

		gameAssets = controller.getEditorGameAssets();
		entityFolder = controller.getLibraryManager()
				.getRepoElementLibraryFolder(element);
	}

	@Override
	protected boolean step() {
		if (!thumbnail.exists()) {
			error(new FileNotFoundException("No thumbnail file found at: "
					+ thumbnail.path()));
			return true;
		}
		entityFolder.mkdirs();
		FileHandle entityContents = entityFolder
				.child(ModelStructure.CONTENTS_FOLDER);
		this.contentsFolder.copyTo(entityContents);
		FileHandle entityJson = entityContents
				.child(ModelStructure.ENTITY_FILE);
		if (!entityJson.exists()) {
			FileHandle[] list = entityContents.list(".json");
			if (list.length > 0) {
				list[0].copyTo(entityJson);
				list[0].delete();
			} else {
				error(new FileNotFoundException(
						"Entity json file not found at "
								+ entityContents.path()));
				return true;
			}
		}

		if (!thumbnail.exists()) {
			error(new FileNotFoundException("Thumbnail image not found at "
					+ thumbnail.path()));
			return true;
		}

		FileHandle thumbnailFile = entityFolder
				.child(ModelStructure.THUMBNAIL_FILE);

		thumbnail.copyTo(thumbnailFile);
		String json = null;
		try {
			json = gameAssets.toJson(element, RepoElement.class);
		} catch (SerializationException se) {
			error(se);
			return true;
		}
		entityFolder.child(ModelStructure.DESCRIPTOR_FILE).writeString(json,
				false);
		result(true);
		return true;
	}

	@Override
	protected void error(Throwable t) {
		deleteEntityFolder();
		super.error(t);
	}

	@Override
	protected void cancelled() {
		deleteEntityFolder();
		super.cancelled();
	}

	private void deleteEntityFolder() {
		if (entityFolder != null && entityFolder.exists()
				&& entityFolder.isDirectory()) {
			entityFolder.delete();
		}
	}
}