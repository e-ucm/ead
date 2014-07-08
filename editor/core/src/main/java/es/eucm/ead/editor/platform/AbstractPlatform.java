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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Tracker;

public abstract class AbstractPlatform implements Platform {

	private Batch batch;

	protected AbstractPlatform() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				Gdx.app.error("EditorApplicationListener",
						"Fatal error: " + t.getName() + "(" + t.getId() + ")",
						e);
			}
		});
	}

	@Override
	public Tracker createTracker(Controller controller) {
		return new Tracker(controller);
	}

	public boolean browseURL(String URL) {
		try {
			Gdx.net.openURI(URL);
			return true;
		} catch (Throwable t) {
			Gdx.app.debug("AbstractPlatform", "Error opening URL " + URL, t);
			return false;
		}
	}

	@Override
	public void setBatch(Batch batch) {
		this.batch = batch;
	}

	@Override
	public Batch getBatch() {
		return batch;
	}

	@Override
	public DeviceVideoControl getVideo() {
		return null;
	}

	@Override
	public DevicePictureControl getPicture() {
		return null;
	}
}
