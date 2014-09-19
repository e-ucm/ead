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
package es.eucm.ead.editor.platform;

import java.io.File;
import java.io.InputStream;

import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;

import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.data.Dimension;
import es.eucm.network.requests.RequestHelper;

public abstract class MockupPlatform extends AbstractPlatform {

	private final Vector2 screenDimensions = new Vector2(1280f, 800f);
	private String importProjectPath;

	@Override
	public void askForFile(FileChooserListener listener) {

	}

	@Override
	public void askForFolder(FileChooserListener listener) {

	}

	public void setImportProjectPath(String importProjectPath) {
		this.importProjectPath = importProjectPath;
	}

	/**
	 * 
	 * @return the path of a file that was exported by an editor and should be
	 *         imported in this editor's working space.
	 */
	public String getImportProjectPath() {
		return importProjectPath;
	}

	@Override
	public void setTitle(String title) {

	}

	@Override
	public void editImage(I18N i18n, String image, FileChooserListener listener) {

	}

	public void askForAudio(FileChooserListener listener) {

	}

	@Override
	public void setSize(int width, int height) {

	}

	@Override
	public Vector2 getSize() {
		return screenDimensions;
	}

	public void getMultilineTextInput(TextInputListener listener,
			final String title, final String text, I18N i18n) {

	}

	public void sendProject(FileHandle projectHandle, I18N i18n,
			ProjectSentListener listener) {

	}

	@Override
	public RequestHelper getRequestHelper() {
		return null;
	}

	@Override
	public Dimension getImageDimension(InputStream imageInputStream) {
		return null;
	}

	public abstract void captureImage(File photoFile,
			ImageCapturedListener listener);

	public interface ImageCapturedListener {

		public static enum Result {
			SUCCES, UNKOWN("error.unknown"), NO_CAMERA("error.noCamera"), NO_APP(
					"error.noApp");

			private String i18nKey;

			private Result() {
				this(null);
			}

			private Result(String i18nKey) {
				this.i18nKey = i18nKey;
			}

			public String getI18nKey() {
				return i18nKey;
			}
		}

		void imageCaptured(Result result);
	}

	public interface ProjectSentListener {

		void projectSent(boolean success);
	}
}
