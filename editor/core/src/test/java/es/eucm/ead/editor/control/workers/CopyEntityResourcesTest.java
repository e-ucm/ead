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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schemax.ModelStructure;

public class CopyEntityResourcesTest extends WorkerTest implements
		WorkerListener {

	private FileHandle contentsFolder;
	private FileHandle thumbnailFile;
	private RepoElement element;

	private boolean success;

	@Before
	public void buildTestContents() {

		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		EditorGameAssets gameAssets = controller.getEditorGameAssets();
		gameAssets.setLoadingPath("", true);
		contentsFolder = Gdx.files.external("tempContents");
		if (contentsFolder.exists() && contentsFolder.isDirectory()) {
			contentsFolder.deleteDirectory();
		}
		contentsFolder.mkdirs();
		FileHandle medicPng = Gdx.files.internal("import_entity/medic.png");
		thumbnailFile = medicPng;
		element = new RepoElement();
		element.setEntityRef("restID");
		medicPng.copyTo(contentsFolder);
		Gdx.files.internal("import_entity/my_medic_def.json").copyTo(
				contentsFolder);
	}

	@Override
	public void testWorker() {
		success = false;
		controller.action(ExecuteWorker.class,
				CopyToLibraryEntityResources.class, this, contentsFolder,
				element, thumbnailFile);
	}

	@Override
	public void asserts() {
		assertTrue(success);
		contentsFolder.deleteDirectory();
	}

	@Override
	public void start() {

	}

	@Override
	public void result(Object... results) {
		success = (Boolean) results[0];
		if (success) {
			EditorGameAssets gameAssets = controller.getEditorGameAssets();
			FileHandle mockLibsFolder = gameAssets.absolute(controller
					.getPlatform().getDefaultLibraryFolder());
			assertTrue(mockLibsFolder.exists() && mockLibsFolder.isDirectory());

			FileHandle entityFolder = mockLibsFolder.child(element
					.getEntityRef());
			assertTrue(entityFolder.exists() && entityFolder.isDirectory());
			assertTrue(entityFolder.child(ModelStructure.THUMBNAIL_FILE)
					.exists());
			assertTrue(entityFolder.child(ModelStructure.DESCRIPTOR_FILE)
					.exists());

			FileHandle contentsFolder = entityFolder
					.child(ModelStructure.CONTENTS_FOLDER);
			assertTrue(contentsFolder.exists() && contentsFolder.isDirectory());
			assertTrue(contentsFolder.child(ModelStructure.ENTITY_FILE)
					.exists());

			mockLibsFolder.deleteDirectory();
		}
	}

	@Override
	public void done() {
	}

	@Override
	public void error(Throwable ex) {
		fail("Exception thrown" + ex);
	}

	@Override
	public void cancelled() {

	}
}