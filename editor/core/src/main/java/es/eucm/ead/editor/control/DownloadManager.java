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

import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.workers.DownloadFile;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;

/**
 * Manages the active downloads and also allows to add new ones or cancel active
 * ones.
 * 
 */
public class DownloadManager {

	private Controller controller;

	public DownloadManager(Controller controller) {
		this.controller = controller;
	}

	public void download(DownloadWork work) {
		work.listener.queued();
		work.workerListener = new DownloadWorkListener(work.listener);
		controller.action(ExecuteWorker.class, DownloadFile.class, false,
				work.workerListener, work.uri, work.outputFile);
	}

	public void cancel(DownloadWork work) {
		controller.getWorkerExecutor().cancel(DownloadFile.class,
				work.workerListener);
	}

	/**
	 * Listens to events about the state of a specified {@link DownloadWork}.
	 * 
	 */
	public interface DownloadListener {

		/**
		 * The {@link DownloadWork} has been sent to be processed.
		 */
		void queued();

		/**
		 * The {@link DownloadWork} has started the download.
		 */
		void started();

		/**
		 * While downloading, returns the completion of the download from 0 to
		 * 1.
		 */
		void completion(float completion);

		/**
		 * The download has finished correctly.
		 */
		void downloaded();

		/**
		 * The download has been cancelled.
		 */
		void cancelled();

		/**
		 * An error has occurred.
		 */
		void error();
	}

	/**
	 * The basic work unit that can be sent do be downloaded. Must have:
	 * 
	 * 1) A correct {@link FileHandle} specifying where the downloaded file will
	 * be stored. 2) A String that is the url of the download. 3) A
	 * {@link DownloadListener} that will be notified about the state of this
	 * work.
	 * 
	 */
	public static class DownloadWork {
		private DownloadListener listener;
		private FileHandle outputFile;
		private String uri;
		private DownloadWorkListener workerListener;

		public DownloadWork(DownloadListener listener, String uri,
				FileHandle outputFile) {
			this.listener = listener;
			this.uri = uri;
			this.outputFile = outputFile;
		}

		public DownloadListener getListener() {
			return listener;
		}

		public void setListener(DownloadListener listener) {
			this.listener = listener;
		}

		public FileHandle getOutputFile() {
			return outputFile;
		}

		public void setOutputFile(FileHandle outputFile) {
			this.outputFile = outputFile;
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

	}

	private static class DownloadWorkListener implements WorkerListener {

		private DownloadListener listener;

		public DownloadWorkListener(DownloadListener listener) {
			this.listener = listener;
		}

		@Override
		public void start() {
			listener.started();
		}

		@Override
		public void result(Object... results) {
			Float completion = (Float) results[0];
			listener.completion(completion);
			if (completion == 1f) {
				listener.downloaded();
			}
		}

		@Override
		public void cancelled() {
			listener.cancelled();
		}

		@Override
		public void error(Throwable ex) {
			listener.error();
		}

		@Override
		public void done() {

		}
	}
}
