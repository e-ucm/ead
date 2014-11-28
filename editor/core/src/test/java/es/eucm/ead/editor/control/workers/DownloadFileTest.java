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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.junit.Before;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import es.eucm.ead.editor.utils.ProjectUtils;

public class DownloadFileTest extends WorkerTest implements WorkerListener {

	private static final String URL = "";

	private FileHandle image;

	private FileHandle dstFile;

	@Before
	public void buildInputStream() {

		EditorGameAssets gameAssets = controller.getEditorGameAssets();
		gameAssets.setLoadingPath("", true);
		image = gameAssets.resolve("blank.png");

		platform.putHttpResponse(URL, new MockConnection(image.read()));
	}

	@Override
	public void testWorker() {
		FileHandle directory = Gdx.files.external("");
		dstFile = ProjectUtils.getNonExistentFile(directory,
				image.nameWithoutExtension(), image.extension());
		controller.action(ExecuteWorker.class, DownloadFile.class, this, URL,
				dstFile.file().getAbsolutePath());
	}

	@Override
	public void asserts() {
		assertTrue(dstFile.exists());
		assertEquals(dstFile.length(), image.length());
		dstFile.delete();
	}

	@Override
	public void start() {

	}

	@Override
	public void result(Object... results) {
		assertTrue((Boolean) results[0]);
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

	public static class MockConnection extends HttpURLConnection {

		private InputStream stream;

		protected MockConnection(InputStream stream) {
			super(null);
			this.stream = stream;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return stream;
		}

		@Override
		public void disconnect() {
		}

		@Override
		public boolean usingProxy() {
			return false;
		}

		@Override
		public void connect() throws IOException {

		}
	}
}
