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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SerializationException;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Copies to a given folder the binaries of a {@link ModelEntity} defined by a
 * .json file. Note that the result is executed on UI thread.
 * <dl>
 * <dt><strong>The input arguments are</strong></dt>
 * <dd><strong>args[0]</strong> <em>FileHandle</em> Directory to the contents of
 * the element that will be copied. E.g. "contents.zip" that has already been
 * unzipped somewhere. The contents must contain one .json file where the
 * {@link ModelEntity} is defined.</dd>
 * <dd><strong>args[1]</strong> <em>FileHandle</em> Directory where the binaries
 * referenced by the {@link ModelEntity} will be copied.</dd>
 * </dl>
 * <dl>
 * <dt><strong>The result argument is</strong></dt>
 * <dd><strong>if</strong> everything went well, result({@link ModelEntity}) is
 * invoked. <strong>args[0]</strong> the <em>{@link ModelEntity}</em> whose
 * assets have been imported to the project path.</dd> Otherwise,
 * <dd><strong>if</strong> the .json file with the definition of the
 * {@link ModelEntity} isn't found then error({@link FileNotFoundException}) is
 * invoked.</dd>
 * <dd><strong>if</strong> the .json file with the definition of the
 * {@link ModelEntity} is found but couldn't be parsed as a correct
 * {@link ModelEntity} then error({@link SerializationException}) is invoked.</dd>
 * <dd><strong>if</strong> a binary reference doesn't exist error(
 * {@link FileNotFoundException}) is invoked.</dd>
 * </dl>
 */
public class CopyEntityResources extends Worker {

	private static final String IMPORT_TAG = "ImportEntity";

	private ModelEntity entity;

	private FileHandle contentsFile;

	private FileHandle outputFolder;

	private EditorGameAssets gameAssets;

	public CopyEntityResources() {
		super(true);
	}

	@Override
	protected void prepare() {
		entity = null;
		contentsFile = (FileHandle) args[0];
		outputFolder = (FileHandle) args[1];
		FileHandle[] jsons = contentsFile.list(".json");
		if (jsons.length == 1) {
			FileHandle modelEntityFile = jsons[0];
			if (modelEntityFile.exists()) {
				try {
					gameAssets = controller.getEditorGameAssets();
					entity = gameAssets.fromJson(ModelEntity.class,
							modelEntityFile);
				} catch (SerializationException se) {
					Gdx.app.error(IMPORT_TAG,
							"Exception parsing model entity .json "
									+ modelEntityFile.file().getAbsolutePath(),
							se);
					entity = null;
					error(se);
				}
			} else {
				FileNotFoundException entityJsonNotFound = new FileNotFoundException(
						"Couldn't find the model entity definition in "
								+ contentsFile.file().getAbsolutePath());
				Gdx.app.error(IMPORT_TAG, "Entity .json not found",
						entityJsonNotFound);
				error(entityJsonNotFound);
			}
		}
	}

	@Override
	protected boolean step() {
		if (entity != null) {
			Array<String> binaries = ProjectUtils.listRefBinaries(entity);
			for (String binaryPath : binaries) {
				FileHandle binFile = contentsFile.child(binaryPath);
				if (!binFile.exists()) {
					FileNotFoundException binaryNotFoundEx = new FileNotFoundException(
							"A binary reference  (" + binaryPath
									+ ") doesn't exist. Contents folder: "
									+ contentsFile.path());
					Gdx.app.error(IMPORT_TAG, "Binary not found",
							binaryNotFoundEx);
					error(binaryNotFoundEx);
					return true;
				}
			}
			String outputFolderPath = outputFolder.file().getAbsolutePath();
			if (!outputFolderPath.endsWith("/")) {
				outputFolderPath += "/";
			}
			for (String binaryPath : binaries) {
				FileHandle binFile = contentsFile.child(binaryPath);
				FileHandle unusedFile = ProjectUtils.getNonExistentFile(
						outputFolder, binFile.nameWithoutExtension(),
						binFile.extension());
				if (!unusedFile.file().getAbsolutePath().endsWith(binaryPath)) {
					String newRef = unusedFile.file().getAbsolutePath()
							.substring(outputFolderPath.length());
					ProjectUtils.replaceBinaryRef(entity, binaryPath, newRef);
				}
				binFile.copyTo(unusedFile);
			}
			result(entity);
		}
		return true;
	}
}