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
package es.eucm.ead.android.picture;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.android.EditorActivity;

public class CameraSurfaceCallback implements SurfaceHolder.Callback {

	private static final int MAX_PREVIEW_PIXELS = 2100000;
	private static final int MAX_PHOTO_PIXELS = 2100000;
	private static final String PICTURE_TAG = "Picture";

	private final Vector2 pictureSizeVector;
	private final EditorActivity activity;

	private Size pictureSize, previewSize;
	private Camera camera;

	public CameraSurfaceCallback(EditorActivity activity) {
		this.pictureSizeVector = new Vector2();
		this.activity = activity;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// Once the surface is created, simply open a handle to the camera
		// hardware.
		Gdx.app.log(PICTURE_TAG, "CameraSurfaceCallback.surfaceCreated");
		if (this.camera == null) {
			this.camera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
			if (this.camera != null) {
				prepareCamera(holder);
			}
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	private void prepareCamera(SurfaceHolder holder) {
		Gdx.app.log(PICTURE_TAG, "CameraSurfaceCallback.prepareCamera");
		// This method is called when the surface changes, e.g. when it's size
		// is set.
		// We use the opportunity to initialize the camera preview display
		// dimensions.
		setCameraDisplayOrientation(this.activity,
				CameraInfo.CAMERA_FACING_BACK, camera);

		Camera.Parameters parameters = camera.getParameters();

		if (this.pictureSize == null) {
			// First time we start the app...
			List<Camera.Size> pictureSizes = parameters
					.getSupportedPictureSizes();
			// You need to choose the most appropriate photoSize for your app
			for (Size size : pictureSizes) {
				if (wantToUseThisPhotoResolution(size)) {
					this.pictureSize = size;
					break;
				}
			}
		}
		Gdx.app.log(PICTURE_TAG, "Selected picture size: " + pictureSize.width
				+ "x" + pictureSize.height);
		parameters.setPictureSize(pictureSize.width, pictureSize.height);

		// You need to choose the most appropriate previewSize for your app
		// ... select one of previewSizes here
		final List<Camera.Size> previewSizes = parameters
				.getSupportedPreviewSizes();
		this.previewSize = null;
		final Map<Float, Camera.Size> possibleSizes = new HashMap<Float, Camera.Size>();
		final float pictureAspectRatio = this.pictureSize.width
				/ Float.valueOf(this.pictureSize.height);
		Gdx.app.log(PICTURE_TAG, "Picture aspect ratio: " + pictureAspectRatio);
		Float bestAspectRatio = Float.MAX_VALUE;
		for (Size size : previewSizes) {
			if (wantToUseThisPreviewResolution(size)) {
				final Float currentSizeAspectRatio = size.width
						/ Float.valueOf(size.height);
				final Float deltaAspectRatio = Math.abs(pictureAspectRatio
						- currentSizeAspectRatio);
				possibleSizes.put(deltaAspectRatio, size);
				if (deltaAspectRatio < bestAspectRatio) {
					bestAspectRatio = deltaAspectRatio;
				}
			}
		}
		if (this.previewSize == null) {
			this.previewSize = possibleSizes.get(bestAspectRatio);
		}
		Gdx.app.log(PICTURE_TAG, "Selected preview size: "
				+ this.previewSize.width + " x " + this.previewSize.height);
		parameters.setPreviewSize(this.previewSize.width,
				this.previewSize.height);
		possibleSizes.clear();

		this.camera.setParameters(parameters);

		// We also assign the preview display to this surface...
		try {
			this.camera.setPreviewDisplay(holder);
		} catch (IOException ex) {
			Gdx.app.error(PICTURE_TAG, "Prepare camera failed!", ex);
		}
	}

	private void setCameraDisplayOrientation(Activity activity, int cameraId,
			android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	private boolean wantToUseThisPreviewResolution(Size size) {
		return size.width * size.height < MAX_PREVIEW_PIXELS;
	}

	private boolean wantToUseThisPhotoResolution(Size size) {
		int w = size.width, h = size.height, pixels = w * h;
		return pixels < MAX_PHOTO_PIXELS;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Once the surface gets destroyed, we stop the preview mode and release
		// the whole camera since we no longer need it.
		Gdx.app.log(PICTURE_TAG, "CameraSurfaceCallback.surfaceDestroyed");
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	public Camera getCamera() {
		return this.camera;
	}

	public Size getPictureSize() {
		return this.pictureSize;
	}

	public Size getPreviewSize() {
		return this.previewSize;
	}

	/**
	 * Used if the user changes the picture size.
	 * 
	 * @param width
	 * @param height
	 */
	public void setPictureSize(int width, int height) {
		if (this.pictureSize.width != width
				|| this.pictureSize.height != height) {
			this.pictureSize.width = width;
			this.pictureSize.height = height;
			Gdx.app.log(PICTURE_TAG, "Selected picture size: "
					+ this.pictureSize.width + "x" + this.pictureSize.height);
		}
	}

	/**
	 * Returns an array of Strings of supported picture resolutions
	 */
	public Array<Vector2> getSupportedPictureSizes() {
		final Camera.Parameters parameters = this.camera.getParameters();
		final List<Camera.Size> pictureSizes = parameters
				.getSupportedPictureSizes();
		final Array<Vector2> sizes = new Array<Vector2>(false,
				pictureSizes.size());
		for (final Size size : pictureSizes) {
			if (size.width * size.height < MAX_PHOTO_PIXELS) {
				sizes.add(new Vector2(size.width, size.height));
			}
		}
		return sizes;
	}

	public Vector2 getCurrentPictureSize() {
		return this.pictureSizeVector.set(this.pictureSize.width,
				this.pictureSize.height);
	}
}
