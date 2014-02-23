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

	private static final int MAX_PREVIEW_PIXELS = 1500000;
	private static final int MAX_PHOTO_PIXELS = 2100000;

	private final EditorActivity activity;

	private Size pictureSize, previewSize;
	private Camera camera;

	public CameraSurfaceCallback(EditorActivity activity) {
		this.activity = activity;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// Once the surface is created, simply open a handle to the camera
		// hardware.
		Gdx.app.log("Picture", "CameraSurfaceCallback.surfaceCreated");
		if (camera == null) {
			camera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
			if (camera != null) {
				prepareCamera(holder);
			}
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	private void prepareCamera(SurfaceHolder holder) {
		Gdx.app.log("Picture", "CameraSurfaceCallback.prepareCamera");
		// This method is called when the surface changes, e.g. when it's size
		// is set.
		// We use the opportunity to initialize the camera preview display
		// dimensions.
		setCameraDisplayOrientation(this.activity,
				CameraInfo.CAMERA_FACING_BACK, camera);

		Camera.Parameters parameters = camera.getParameters();

		if (pictureSize == null) {
			// First time we start the app...

			List<Camera.Size> pictureSizes = parameters
					.getSupportedPictureSizes();
			// You need to choose the most appropriate photoSize for your app
			for (Size size : pictureSizes) {
				if (wantToUseThisPhotoResolution(size)) {
					pictureSize = size;
					break;
				}
			}
		}
		Gdx.app.log("Picture", "Selected picture size: " + pictureSize.width
				+ "x" + pictureSize.height);
		parameters.setPictureSize(pictureSize.width, pictureSize.height);

		// You need to choose the most appropriate previewSize for your app
		// ... select one of previewSizes here
		List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
		previewSize = null;
		Map<Float, Camera.Size> possibleSizes = new HashMap<Float, Camera.Size>();
		final float pictureAspectRatio = pictureSize.width
				/ Float.valueOf(pictureSize.height);
		Gdx.app.log("Picture", "Picture aspect ratio: " + pictureAspectRatio);
		Float bestAspectRatio = Float.MAX_VALUE;
		for (Size size : previewSizes) {
			if (wantToUseThisPreviewResolution(size)) {
				Float currentSizeAspectRatio = size.width
						/ Float.valueOf(size.height);
				Float deltaAspectRatio = Math.abs(pictureAspectRatio
						- currentSizeAspectRatio);
				possibleSizes.put(deltaAspectRatio, size);
				if (deltaAspectRatio < bestAspectRatio) {
					bestAspectRatio = deltaAspectRatio;
				}
			}
		}
		if (previewSize == null) {
			previewSize = possibleSizes.get(bestAspectRatio);
		}
		Gdx.app.log("Picture", "Selected preview size: " + previewSize.width
				+ "x" + previewSize.height);
		parameters.setPreviewSize(previewSize.width, previewSize.height);
		possibleSizes.clear();

		camera.setParameters(parameters);

		// We also assign the preview display to this surface...
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException ex) {
			Gdx.app.error("Picture", "Prepare camera failed!", ex);
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
		Gdx.app.log("Picture", "CameraSurfaceCallback.surfaceDestroyed");
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	public Camera getCamera() {
		return camera;
	}

	public Size getPictureSize() {
		return pictureSize;
	}

	public Size getPreviewSize() {
		return previewSize;
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
			Gdx.app.log("Picture", "Selected picture size: "
					+ this.pictureSize.width + "x" + this.pictureSize.height);
		}
	}

	/**
	 * Returns an array of Strings of supported picture resolutions
	 */
	public Array<Vector2> getSupportedPictureSizes() {
		Camera.Parameters parameters = camera.getParameters();
		List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
		Array<Vector2> sizes = new Array<Vector2>(false, pictureSizes.size());
		for (Size size : pictureSizes) {
			if (size.width * size.height < MAX_PHOTO_PIXELS) {
				sizes.add(new Vector2(size.width, size.height));
			}
		}
		return sizes;
	}

	public Vector2 getCurrentPictureSize() {
		return new Vector2(pictureSize.width, pictureSize.height);
	}
}
