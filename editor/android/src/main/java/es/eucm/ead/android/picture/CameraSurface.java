/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.android.picture;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.android.EditorActivity;

public class CameraSurface extends SurfaceView {

	private final CameraSurfaceCallback callback;

	public CameraSurface(EditorActivity activity) {
		super(activity);

		this.callback = new CameraSurfaceCallback(activity);
		// We're implementing the Callback interface and want to get notified
		// about certain surface events.
		SurfaceHolder sh = getHolder();
		sh.addCallback(this.callback);
		// We're changing the surface to a PUSH surface, meaning we're receiving
		// all buffer data from another component - the camera, in this case.
		sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	public Camera getCamera() {
		return this.callback.getCamera();
	}

	public Size getPictureSize() {
		return this.callback.getPictureSize();
	}

	public Size getPreviewSize() {
		return this.callback.getPreviewSize();
	}

	public void setPictureSize(int width, int height) {
		this.callback.setPictureSize(width, height);
	}

	public Array<Vector2> getSupportedPictureSizes() {
		return this.callback.getSupportedPictureSizes();
	}

	public Vector2 getCurrentPictureSize() {
		return this.callback.getCurrentPictureSize();
	}
}