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
package es.eucm.ead.editor.control.background;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.async.AsyncTask;

import java.util.concurrent.Semaphore;

/**
 * This class represents a background task in the editor, intended to run in an
 * independent thread heavy weight editor processes.
 * 
 * Created by angel on 25/03/14.
 */
public abstract class BackgroundTask<T> implements AsyncTask<T> {

	private Semaphore semaphore;

	public BackgroundTask() {
		semaphore = new Semaphore(1);
	}

	/**
	 * Completion percentage. To avoid concurrency problems, it should be set
	 * only through {@link BackgroundTask#setCompletionPercentage(float)}
	 */
	private Float completionPercentage = 0.0f;

	/**
	 * 
	 * @return a float between {@code 0.0f} and {@code 1.0f}, representing the
	 *         completion percentage of the task
	 */
	public float getCompletionPercentage() {
		float percentage = 0.0f;
		try {
			semaphore.acquire();
			percentage = this.completionPercentage;
			semaphore.release();
		} catch (InterruptedException e) {
			Gdx.app.error("BackgroundTask.getCompletionPercentage",
					"Thread interrupted", e);
		}
		return percentage;
	}

	/**
	 * Sets the completion percentage. This method is protected and should be
	 * called only from {@link BackgroundTask#call()}
	 * 
	 * @param completionPercentage
	 *            a float between {@code 0.0f} and {@code 1.0f}, representing
	 *            the completion percentage of the task
	 */
	protected void setCompletionPercentage(float completionPercentage) {
		try {
			semaphore.acquire();
			this.completionPercentage = completionPercentage;
			semaphore.release();
		} catch (InterruptedException e) {
			Gdx.app.error(
					"BackgroundTask.setCompletionPercentage",
					"Thread interrupted. completion percentage was not updated",
					e);
		}
	}
}
