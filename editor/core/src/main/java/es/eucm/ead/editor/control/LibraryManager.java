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

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.schema.components.Reference;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schemax.ModelStructure;

import java.io.File;
import java.io.FileFilter;

/**
 * Controls the downloaded {@link RepoElement}s.
 */
public class LibraryManager {

	private FileFilter onlyFolders = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	};

	private Controller controller;

	public LibraryManager(Controller controller) {
		this.controller = controller;
	}

	private String getRepoElementFolder(RepoElement element) {
		Array<RepoCategories> categoryList = element.getCategoryList();
		if (categoryList.size == 0) {
			return element.getEntityRef();
		}
		return getSuperCategory(categoryList.first()) + "/"
				+ element.getEntityRef();
	}

	private String getSuperCategory(RepoCategories category) {
		String[] aux = category.toString().split("-");
		return aux[0];
	}

	/**
	 * 
	 * @return an array with all the downloaded elements in the library
	 */
	public Array<FileHandle> listDownloadedElements() {
		Array<FileHandle> elements = new Array<FileHandle>();
		listDownloadedElements(getLibraryFolder(), elements);
		return elements;
	}

	/**
	 * Adds to the array all the elements available in the folder and all the
	 * sub-folders that aren't {@link RepoElement}s.
	 * 
	 * @param folder
	 * @param elements
	 */
	public void listDownloadedElements(FileHandle folder,
			Array<FileHandle> elements) {
		FileHandle[] list = folder.list(onlyFolders);
		for (FileHandle file : list) {
			if (file.child(ModelStructure.DESCRIPTOR_FILE).exists()) {
				elements.add(file);
			} else {
				listDownloadedElements(file, elements);
			}
		}
	}

	/**
	 * 
	 * @param element
	 * @return a {@link es.eucm.ead.schema.components.Reference} to a
	 *         {@link es.eucm.ead.schema.editor.components.repo.RepoElement} in
	 *         the library.
	 */
	public Reference buildReference(RepoElement element) {
		Reference ref = new Reference();
		ref.setFolder(getRepoElementFolder(element) + "/");
		ref.setEntity(ModelStructure.CONTENTS_FOLDER
				+ ModelStructure.ENTITY_FILE);
		return ref;
	}

	/**
	 * 
	 * @return the {@link FileHandle} where the {@link RepoElement}s should be
	 *         downloaded and stored.
	 */
	public FileHandle getLibraryFolder() {
		FileHandle libraryFolder = controller.getApplicationAssets().absolute(
				controller.getPlatform().getLibraryFolder());
		return libraryFolder;
	}

	/**
	 * 
	 * @param element
	 * @return the {@link FileHandle} where a given {@link RepoElement} should
	 *         be downloaded and stored.
	 */
	public FileHandle getRepoElementLibraryFolder(RepoElement element) {
		FileHandle libraryFolder = getLibraryFolder();
		FileHandle entityFolder = libraryFolder
				.child(getRepoElementFolder(element));
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
				if (contentsFolder.isDirectory()) {

					// Distinguish if the element is a mokap
					if (isMokap(element)) {
						return contentsFolder.child(ModelStructure.GAME_FILE)
								.file().isFile();
					}
					return contentsFolder.child(ModelStructure.ENTITY_FILE)
							.file().isFile();
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param element
	 * @return true if the element is a Mokap, false otherwise. This is achieved
	 *         by asking if any of its categories is
	 *         {@link RepoCategories#MOKAPS}.
	 */
	public boolean isMokap(RepoElement element) {
		for (RepoCategories category : element.getCategoryList()) {
			if (category == RepoCategories.MOKAPS) {
				return true;
			}
		}
		return false;
	}
}
