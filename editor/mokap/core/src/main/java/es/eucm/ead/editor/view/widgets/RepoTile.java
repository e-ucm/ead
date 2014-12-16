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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.DownloadManager.DownloadListener;
import es.eucm.ead.editor.control.DownloadManager.DownloadWork;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.actions.model.AddRepoElementReference;
import es.eucm.ead.editor.control.workers.CopyToLibraryEntityResources;
import es.eucm.ead.editor.control.workers.UnzipFile;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.SceneView;
import es.eucm.ead.editor.view.listeners.workers.UnzipFileListener;
import es.eucm.ead.schema.editor.components.repo.RepoElement;

public class RepoTile extends Tile implements DownloadListener {

	public static enum State {
		DOWNLOADABLE, IN_QUEUE, DOWNLOADING, DOWNLOADED, CANCELLED, IN_LIBRARY
	}

	private static final String TAG = "RepoTile";

	private Skin skin;

	private LoadingBar loadingBar;

	private ImageButton marker;

	private State state;

	private DownloadWork work;

	private FileHandle tempDownloadFolder;

	private FileHandle thumbnailFile;

	private RepoElement element;

	private Controller controller;

	public RepoTile(Controller control, RepoElement elem,
			final Pixmap thumbPixmap) {
		super(control.getApplicationAssets().getSkin());

		this.element = elem;
		this.controller = control;
		this.skin = controller.getApplicationAssets().getSkin();
		initState();
		FileHandle projctsFolder = controller.getApplicationAssets().absolute(
				controller.getPlatform().getDefaultProjectsFolder());
		tempDownloadFolder = ProjectUtils.getNonExistentFile(projctsFolder,
				MathUtils.random(1000) + "", "");
		FileHandle downloadFile = tempDownloadFolder.child("contents.zip");
		thumbnailFile = tempDownloadFolder.child("thumbnail");
		work = new DownloadWork(RepoTile.this, elem.getContentsUrl(),
				downloadFile);
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				switch (state) {
				case CANCELLED:
				case DOWNLOADABLE:
					tempDownloadFolder.mkdirs();
					PixmapIO.writePNG(thumbnailFile, thumbPixmap);
					controller.getDownloadManager().download(work);
					break;
				case DOWNLOADED:
					break;
				case IN_LIBRARY:
					controller.action(AddRepoElementReference.class, element);
					controller.action(ChangeView.class, SceneView.class);
					break;
				case DOWNLOADING:
				case IN_QUEUE:
					controller.getDownloadManager().cancel(work);
					break;
				default:
					error("Clicked in an unknown state");
					break;
				}
			}
		});
	}

	private void initState() {
		FileHandle entityFolder = ProjectUtils
				.getRepoElementLibraryFolder(element);
		if (entityFolder.exists()) {
			state = State.DOWNLOADED;
			inLibrary();
		} else {
			state = State.DOWNLOADABLE;
		}
	}

	@Override
	public void queued() {
		if (state == State.DOWNLOADABLE || state == State.CANCELLED) {
			state = State.IN_QUEUE;
			setMarker(null);
			setBottom(getProgressBar(false));
		} else {
			error("Trying to send to queue from an invalid state: " + state);
		}
	}

	@Override
	public void started() {
		if (state == State.IN_QUEUE) {
			state = State.DOWNLOADING;
		} else {
			error("Trying to start downloading from an invalid state: " + state);
		}
	}

	@Override
	public void completion(float completion) {
		if (state == State.DOWNLOADING) {
			setBottom(getProgressBar(true));
			loadingBar.setCompletion(completion);
		} else {
			error("Receiving results in an invalid state: " + state);
		}
	}

	@Override
	public void downloaded() {
		if (state == State.DOWNLOADING) {
			state = State.DOWNLOADED;
			FileHandle entityFolder = tempDownloadFolder.child("contents");
			controller.action(ExecuteWorker.class, UnzipFile.class,
					new UnzipAndCopyEntityListener(entityFolder), false,
					work.getOutputFile(), entityFolder);
		} else {
			error("Downloaded from an invalid state: " + state);
		}
	}

	@Override
	public void cancelled() {
		if (state == State.IN_QUEUE || state == State.DOWNLOADING
				|| state == State.DOWNLOADED) {
			state = State.CANCELLED;
			deleteTempDownloadFolder();
			setBottom(null);
		} else {
			error("Trying to cancel from an invalid state: " + state);
		}
	}

	@Override
	public void error() {
		if (state == State.IN_QUEUE || state == State.DOWNLOADING
				|| state == State.DOWNLOADED) {
			state = State.DOWNLOADABLE;
			deleteTempDownloadFolder();
			setBottom(null);
			setMarker(getMarker(SkinConstants.IC_ERROR));
		} else {
			error("Received an error in an invalid state: " + state);
		}
	}

	private void inLibrary() {
		if (state == State.DOWNLOADED) {
			state = State.IN_LIBRARY;
			setBottom(null);
			setMarker(getMarker(SkinConstants.IC_CLOUD_DONE));
		} else {
			error("Element added to library in an invalid state: " + state);
		}
	}

	private void deleteTempDownloadFolder() {
		if (tempDownloadFolder.exists() && tempDownloadFolder.isDirectory()) {
			tempDownloadFolder.deleteDirectory();
		}
	}

	private void error(String msg) {
		deleteTempDownloadFolder();
		Gdx.app.error(TAG, msg);
	}

	private Actor getProgressBar(boolean determinate) {
		if (loadingBar == null) {
			loadingBar = new LoadingBar(skin, WidgetBuilder.dpToPixels(8));
		}
		return loadingBar;
	}

	private ImageButton getMarker(String drawable) {
		if (marker == null) {
			ImageButtonStyle style = new ImageButtonStyle(skin.get(
					SkinConstants.STYLE_MARKER, ImageButtonStyle.class));

			marker = new ImageButton(style);
			marker.pad(WidgetBuilder.dpToPixels(8));
		}
		marker.getStyle().imageUp = skin.getDrawable(drawable);
		return marker;
	}

	private class UnzipAndCopyEntityListener extends UnzipFileListener {

		public UnzipAndCopyEntityListener(FileHandle outputFolder) {
			super(outputFolder);
		}

		@Override
		public void unzipped() {
			controller.action(ExecuteWorker.class,
					CopyToLibraryEntityResources.class,
					new CopyToLibraryListener(), false, outputFolder, element,
					thumbnailFile);
		}

		@Override
		public void error(Throwable ex) {
			RepoTile.this.error();
			super.error(ex);
		}

	}

	private class CopyToLibraryListener implements WorkerListener {

		@Override
		public void start() {

		}

		@Override
		public void result(Object... results) {
			if ((Boolean) results[0]) {
				inLibrary();
			}
		}

		@Override
		public void done() {
			deleteTempDownloadFolder();
		}

		@Override
		public void error(Throwable ex) {
			RepoTile.this.error();
		}

		@Override
		public void cancelled() {
			RepoTile.this.cancelled();
		}
	}
}
