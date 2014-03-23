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
package es.eucm.ead.android.platform;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Helper class for camera control in different platforms
 */
public interface DevicePictureControl {

	/**
	 * Prepares camera preview asynchronously.
	 * 
	 * @param listener
	 */
	void prepareCameraAsync(CameraPreparedListener listener);

	/**
	 * Stops camera preview asynchronously.
	 */
	void stopPreviewAsync();

	/**
	 * Takes picture asynchronously without blocking the main thread.
	 * 
	 * @param saving_path
	 * @param listener
	 */
	void takePictureAsync(String saving_path, PictureTakenListener listener);

	/**
	 * Sets the picture size to be displayed on the next surface view update.
	 * 
	 * @param width
	 * @param height
	 */
	void setPictureSize(int width, int height);

	/**
	 * @return a list of supported picture sizes.
	 */
	Array<Vector2> getSupportedPictureSizes();

	/**
	 * @return the current picture size.
	 */
	Vector2 getCurrentPictureSize();

	interface CameraPreparedListener {
		/**
		 * Invoked when the camera has finished preparing.
		 */
		void onCameraPrepared();
	}

	interface PictureTakenListener {
		/**
		 * Invoked when the picture was taken.
		 * 
		 * @param success
		 */
		void onPictureTaken(boolean success);
	}
}
