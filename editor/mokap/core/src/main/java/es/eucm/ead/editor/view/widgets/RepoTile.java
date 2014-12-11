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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.DownloadManager.DownloadListener;
import es.eucm.ead.editor.control.DownloadManager.DownloadWork;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.workers.CopyEntityResources;
import es.eucm.ead.editor.control.workers.UnzipFile;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.SceneView;
import es.eucm.ead.editor.view.listeners.workers.CopyEntityResourcesListener;
import es.eucm.ead.editor.view.listeners.workers.UnzipFileListener;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schema.entities.ModelEntity;

public class RepoTile extends Tile implements DownloadListener {

	public static enum State {
		DOWNLOADABLE, DOWNLOADING, IN_QUEUE, DOWNLOADED, CANCELLED
	}

	private static final String TAG = "RepoTile";

	private Skin skin;

	private LoadingBar loadingBar;
	private ProgressBar progressBar;
	private ImageButton marker;

	private State state;

	private DownloadWork work;

	private FileHandle tempDownloadFolder;

	public RepoTile(final Controller controller, final RepoElement elem) {
		super(controller.getApplicationAssets().getSkin());

		this.skin = controller.getApplicationAssets().getSkin();
		state = State.DOWNLOADABLE;

		final FileHandle loadingPath = controller.getApplicationAssets()
				.absolute(controller.getPlatform().getDefaultProjectsFolder());
		tempDownloadFolder = ProjectUtils.getNonExistentFile(loadingPath,
				MathUtils.random(1000) + "", "");
		FileHandle downloadFile = tempDownloadFolder.child("contents");
		work = new DownloadWork(RepoTile.this, elem.getContentsUrl(),
				downloadFile);
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				switch (state) {
				case CANCELLED:
				case DOWNLOADABLE:
					tempDownloadFolder.mkdirs();
					controller.getDownloadManager().download(work);
					break;
				case DOWNLOADED:
					controller.action(ExecuteWorker.class, UnzipFile.class,
							new UnzipAndCopyEntityListener(tempDownloadFolder,
									controller), work.getOutputFile(),
							tempDownloadFolder);
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
			progressBar.setValue(completion);
		} else {
			error("Receiving results in an invalid state: " + state);
		}
	}

	@Override
	public void downloaded() {
		if (state == State.DOWNLOADING) {
			state = State.DOWNLOADED;
			setBottom(null);
			setMarker(getMarker(SkinConstants.IC_CLOUD_DONE));
		} else {
			error("Downloaded from an invalid state: " + state);
		}
	}

	@Override
	public void cancelled() {
		if (state == State.IN_QUEUE || state == State.DOWNLOADING) {
			state = State.CANCELLED;
			if (tempDownloadFolder.exists() && tempDownloadFolder.isDirectory()) {
				tempDownloadFolder.deleteDirectory();
			}
			setBottom(null);
		} else {
			error("Trying to cancel from an invalid state: " + state);
		}
	}

	@Override
	public void error() {
		if (state == State.IN_QUEUE || state == State.DOWNLOADING) {
			state = State.DOWNLOADABLE;
			if (tempDownloadFolder.exists() && tempDownloadFolder.isDirectory()) {
				tempDownloadFolder.deleteDirectory();
			}
			setBottom(null);
			setMarker(getMarker(SkinConstants.IC_ERROR));
		} else {
			error("Received an error in an invalid state: " + state);
		}
	}

	private void error(String msg) {
		Gdx.app.error(TAG, msg);
	}

	private Actor getProgressBar(boolean determinate) {
		if (determinate) {
			if (progressBar == null) {
				progressBar = new ProgressBar(0, 1, .05f, false, skin);
			}
			return progressBar;
		} else {
			if (loadingBar == null) {
				loadingBar = new LoadingBar(skin);
			}
			return loadingBar;
		}
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

	private static class UnzipAndCopyEntityListener extends UnzipFileListener {

		private Controller controller;

		public UnzipAndCopyEntityListener(FileHandle outputFolder,
				Controller controller) {
			super(outputFolder);
			this.controller = controller;
		}

		@Override
		public void unzipped() {
			EditorGameAssets assets = controller.getEditorGameAssets();
			FileHandle projectFolder = assets.absolute(assets.getLoadingPath());
			controller.action(ExecuteWorker.class, CopyEntityResources.class,
					new CopyEntityResourcesListener(outputFolder) {

						@Override
						public void entityCopied(ModelEntity entity) {
							controller.action(AddSceneElement.class, entity);
							controller
									.action(ChangeView.class, SceneView.class);
						}
					}, outputFolder, projectFolder);
		}

	}
}
