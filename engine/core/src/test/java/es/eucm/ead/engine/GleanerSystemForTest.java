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
package es.eucm.ead.engine;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.engine.systems.GleanerSystem;
import es.eucm.gleaner.tracker.Tracker;
import es.eucm.gleaner.tracker.storage.LocalStorage;
import es.eucm.gleaner.tracker.storage.Storage;

import java.lang.reflect.Field;

import static org.junit.Assert.fail;

/**
 * Created by jtorrente on 03/11/2015.
 */
public class GleanerSystemForTest extends GleanerSystem {

	private DataSentListener listener = null;
	private FileHandle gleanerFile;
	public String data;

	public DataSentListener getListener() {
		return listener;
	}

	public void setListener(DataSentListener listener) {
		this.listener = listener;
	}

	public FileHandle getGleanerFile() {
		return gleanerFile;
	}

	public void setGleanerFile(FileHandle gleanerFile) {
		this.gleanerFile = gleanerFile;
	}

	@Override
	protected void init() {
		super.init();
		if (tracker != null) {
			setFlushListenerAdapter();
		}
	}

	private void setFlushListenerAdapter() {
		try {
			Field field = Tracker.class.getDeclaredField("flushListener");
			field.setAccessible(true);
			Tracker.FlushListener current = (Tracker.FlushListener) field
					.get(tracker);
			TrackerAdapter trackerAdpter = new TrackerAdapter(current);
			field.set(this.tracker, trackerAdpter.flushListenerAdapter);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Override
	protected FileHandle fileHandleForFolder() {
		FileHandle folder = FileHandle.tempDirectory("mokap-test-gleaner");
		folder.mkdirs();
		return folder;
	}

	@Override
	protected FileHandle fileHandleForLocalStorage(String prefix) {
		gleanerFile = super.fileHandleForLocalStorage(prefix);
		return gleanerFile;
	}

	@Override
	protected Storage buildGleanerStorage() {
		LocalStorage storage = new LocalStorage(
				fileHandleForLocalStorage("test")) {

			@Override
			public void send(String data, Net.HttpResponseListener flushListener) {
				GleanerSystemForTest.this.data = data;
				super.send(data, flushListener);
			}
		};
		return storage;
	}

	public void cleanup() {
		removedFromEngine(null);
		if (gleanerFile != null) {
			gleanerFile.delete();
		}
	}

	public interface DataSentListener {
		void dataSent(String data);
	}

	private class TrackerAdapter extends Tracker {

		FlushListenerAdapter flushListenerAdapter;

		public TrackerAdapter(Tracker.FlushListener flushListener) {
			super(new Storage() {
				@Override
				public void setTracker(Tracker tracker) {

				}

				@Override
				public void start(Net.HttpResponseListener startListener) {

				}

				@Override
				public void send(String data,
						Net.HttpResponseListener flushListener) {

				}

				@Override
				public void close() {

				}
			});
			this.flushListenerAdapter = new FlushListenerAdapter(flushListener);
		}

		public class FlushListenerAdapter extends Tracker.FlushListener {
			Tracker.FlushListener flushListener;

			public FlushListenerAdapter(Tracker.FlushListener flushListener) {
				this.flushListener = flushListener;
			}

			@Override
			public void handleHttpResponse(Net.HttpResponse httpResponse) {
				flushListener.handleHttpResponse(httpResponse);
				if (listener != null) {
					listener.dataSent(data);
				}
			}

			@Override
			public void failed(Throwable t) {
				flushListener.failed(t);
			}

			@Override
			public void cancelled() {
				flushListener.cancelled();
			}
		}
	}
}
