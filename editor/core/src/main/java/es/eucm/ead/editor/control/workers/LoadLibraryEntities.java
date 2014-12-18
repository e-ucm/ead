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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schemax.ModelStructure;

import java.io.File;
import java.io.FileFilter;

/**
 * Receives a path (args[0]) to a folder with a library, and loads all its
 * children. If args[1] is set to an integer, it ignores the first args[1] in
 * the results.
 * <dl>
 * <dt><strong>The result arguments are</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link String}</em> entity reference.
 * <dd><strong>args[1]</strong> <em>{@link String}</em> name of the entity.</dd>
 * <dd><strong>args[2]</strong> <em>{@link String}</em> path to the thumbnail of
 * the element.</dd>
 * </dl>
 */
public class LoadLibraryEntities extends Worker {

	private int i = 0;

	private FileHandle[] libraryEntities;

	public LoadLibraryEntities() {
		super(true, true);
	}

	@Override
	protected void prepare() {
		FileHandle libraryFolder = controller.getApplicationAssets().absolute(
				(String) getArg(0));
		if (libraryFolder.exists()) {
			libraryEntities = libraryFolder.list(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isDirectory();
				}
			});
		}

		if (args.length == 2 && args[1] instanceof Integer) {
			i = (Integer) args[1];
		}
	}

	@Override
	protected boolean step() {
		if (libraryEntities == null || i >= libraryEntities.length) {
			return true;
		}
		FileHandle libraryEntity = libraryEntities[i++];
		try {
			RepoElement repoElement = controller
					.getEditorGameAssets()
					.fromJson(RepoElement.class,
							libraryEntity.child(ModelStructure.DESCRIPTOR_FILE));
			result(repoElement.getEntityRef(),
					repoElement.getNameList().get(0),
					libraryEntity.child(ModelStructure.THUMBNAIL_FILE).path());

		} catch (Exception e) {
			Gdx.app.error("LoadLibraryEntities",
					"Error loading library entity", e);
		}

		return false;
	}
}
