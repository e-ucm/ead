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

import com.badlogic.gdx.files.FileHandle;

import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schemax.ModelStructure;

/**
 * Controls the downloaded {@link RepoElement}s.
 */
public class LibraryManager {

	private Controller controller;

	public LibraryManager(Controller controller) {
		this.controller = controller;
	}

	/**
	 * 
	 * @param element
	 * @return the {@link FileHandle} where a given {@link RepoElement} should
	 *         be downloaded and stored.
	 */
	public FileHandle getRepoElementLibraryFolder(RepoElement element) {
		FileHandle libraryFolder = controller.getApplicationAssets().absolute(
				controller.getPlatform().getDefaultLibraryFolder());
		FileHandle entityFolder = libraryFolder.child(element.getEntityRef());
		return entityFolder;
	}

	/**
	 * 
	 * @param element
	 * @return true if the minimum required files of a given {@link RepoElement}
	 *         have been downloaded and are stored in the correct place.
	 */
	public boolean isDownloaded(RepoElement element) {
		FileHandle elemFolder = getRepoElementLibraryFolder(element);
		if (elemFolder.isDirectory()) {
			if (elemFolder.child(ModelStructure.THUMBNAIL_FILE).file().isFile()
					&& elemFolder.child(ModelStructure.DESCRIPTOR_FILE).file()
							.isFile()) {
				FileHandle contentsFolder = elemFolder
						.child(ModelStructure.CONTENTS_FOLDER);
				if (contentsFolder.isDirectory()
						&& contentsFolder.child(ModelStructure.ENTITY_FILE)
								.file().isFile()) {
					return true;
				}
			}
		}
		return false;
	}
}
