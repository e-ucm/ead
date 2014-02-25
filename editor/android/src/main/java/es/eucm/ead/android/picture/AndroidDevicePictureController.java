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

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.android.EditorActivity;
import es.eucm.ead.android.platform.DevicePictureControl;

public class AndroidDevicePictureController implements DevicePictureControl,
		Camera.PictureCallback, Camera.AutoFocusCallback {

	private static final long PICTURE_PREVIEW_TIME = 1000;

	private final EditorActivity activity;
	private final LayoutParams mLayoutParams;
	private final RelativeLayout previewLayout;
	private final RelativeLayout.LayoutParams previewParams;
	private final CameraSurface cameraSurface;
	private final Runnable prepareCameraAsyncRunnable;
	private final Runnable startPreviewAsyncRunnable;
	private final Runnable stopPreviewAsyncRunnable;
	private final Runnable takePictureAsyncRunnable;

	private CameraPreparedListener listener;
	private String savingPath;

	public AndroidDevicePictureController(EditorActivity activity) {
		this.activity = activity;
		this.cameraSurface = new CameraSurface(activity);
		this.mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		this.previewLayout = new RelativeLayout(activity);
		this.previewParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		this.previewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		this.previewLayout.setGravity(Gravity.CENTER);
		this.prepareCameraAsyncRunnable = new Runnable() {
			public void run() {
				prepareCamera();
			}
		};
		this.startPreviewAsyncRunnable = new Runnable() {
			public void run() {
				startPreview();
			}
		};
		this.stopPreviewAsyncRunnable = new Runnable() {
			public void run() {
				stopPreview();
			}
		};
		this.takePictureAsyncRunnable = new Runnable() {
			public void run() {
				takePicture();
			}
		};
	}

	private synchronized void prepareCamera() {
		Gdx.app.log("Picture", "prepareCamera");
		this.previewLayout.addView(this.cameraSurface, this.previewParams);
		this.activity.addContentView(this.previewLayout, this.mLayoutParams);
		startPreviewAsync();
	}

	private synchronized void startPreview() {
		Gdx.app.log("Picture", "startPreviw");
		// ...and start previewing. From now on, the camera keeps pushing
		// preview
		// images to the surface.
		Camera cam = this.cameraSurface.getCamera();
		if (this.cameraSurface != null && cam != null) {
			setUpLayoutParams();
			cam.startPreview();
			if (this.listener != null)
				this.listener.onCameraPrepared();
		}
	}

	private void setUpLayoutParams() {
		LayoutParams params = this.cameraSurface.getLayoutParams();
		Size prevSize = this.cameraSurface.getPreviewSize();

		float viewportWidth = prevSize.width;
		float viewportHeight = prevSize.height;
		float viewPortAspect = viewportWidth / viewportHeight;
		float physicalWidth = Gdx.graphics.getWidth();
		float physicalHeight = Gdx.graphics.getHeight();
		float physicalAspect = physicalWidth / physicalHeight;

		if (physicalAspect < viewPortAspect) {
			viewportHeight = viewportHeight * (physicalWidth / viewportWidth);
			viewportWidth = physicalWidth;
		} else {
			viewportWidth = viewportWidth * (physicalHeight / viewportHeight);
			viewportHeight = physicalHeight;
		}

		params.width = (int) viewportWidth;
		params.height = (int) viewportHeight;

		this.cameraSurface.setLayoutParams(params);
	}

	private synchronized void stopPreview() {
		Gdx.app.log("Picture", "stopPreview");
		// stop previewing.
		if (this.cameraSurface != null) {
			ViewParent parentView = this.previewLayout.getParent();
			if (parentView instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) parentView;
				viewGroup.removeView(this.previewLayout);
				this.previewLayout.removeView(this.cameraSurface);
			}
			Camera cam = this.cameraSurface.getCamera();
			if (cam != null) {
				cam.stopPreview();
			}
		}
	}

	private synchronized void takePicture() {
		Gdx.app.log("Picture", "takePicture");
		// the user request to take a picture - start the process by requesting
		// focus
		this.cameraSurface.getCamera().autoFocus(this);
		// If the camera does not support auto-focus and autoFocus is called,
		// onAutoFocus will be called immediately with a fake value of success
		// set to true.
	}

	@Override
	public synchronized void onAutoFocus(boolean success, Camera camera) {
		Gdx.app.log("Picture", "onAutoFocus");
		// Focus process finished, we now have focus (or not)
		if (success && camera != null) {
			// We now have focus take the actual picture
			camera.takePicture(null, null, null, this);
		}
	}

	@Override
	public synchronized void onPictureTaken(byte[] data, Camera camera) {
		// We got the picture data - keep it
		Gdx.app.log("Picture", "onPictureTaken");

		String path = this.savingPath;
		int resID = 1 + Gdx.files.absolute(path).list().length;
		String resIDstr = String.valueOf(resID);
		String finalPath = path + File.separator + resIDstr + File.separator;
		FileHandle finalPathHandle = Gdx.files.absolute(finalPath);
		if (!finalPathHandle.exists()) {
			finalPathHandle.mkdirs();
		}
		String oriPath = finalPath;
		String halfSizedPath = finalPath;
		String thumbPath = finalPath;
		String extension = ".jpg";

		String originalFileName = oriPath + "Original" + extension;
		String halfSizedFileName = halfSizedPath + "HalfSized" + extension;
		String thumbnailFileName = thumbPath + "Thumbnail" + extension;

		OutputStream originalFos = null;
		OutputStream thumbnailFos = null;
		OutputStream halfSizedFos = null;
		try {
			// Original
			originalFos = new FileOutputStream(new File(originalFileName));
			originalFos.write(data);
			originalFos.flush();

			Bitmap imageBitmap = BitmapFactory.decodeByteArray(data, 0,
					data.length);

			Size photoSize = this.cameraSurface.getPictureSize();
			int w = photoSize.width;
			int h = photoSize.height;

			// Thumbnail
			// will have a tenth of the original size
			Bitmap aux = Bitmap.createScaledBitmap(imageBitmap, w / 10, h / 10,
					false);

			thumbnailFos = new FileOutputStream(new File(thumbnailFileName));
			aux.compress(Bitmap.CompressFormat.JPEG, 75, thumbnailFos);
			thumbnailFos.flush();
			if (aux != imageBitmap && aux != null) {
				aux.recycle();
				aux = null;
			}

			// HalfScaled image
			aux = Bitmap.createScaledBitmap(imageBitmap, w / 2, h / 2, true);

			halfSizedFos = new FileOutputStream(new File(halfSizedFileName));
			aux.compress(Bitmap.CompressFormat.JPEG, 95, halfSizedFos);
			halfSizedFos.flush();
			if (aux != imageBitmap && aux != null) {
				aux.recycle();
				aux = null;
			}

			if (imageBitmap != null) {
				imageBitmap.recycle();
				imageBitmap = null;
			}

			Gdx.app.log("Picture", "New image saved, id: " + resID);
		} catch (FileNotFoundException fnfex) {
			// complain to user
			Gdx.app.error("Picture", "File not found ", fnfex);
			finalPathHandle.deleteDirectory();
		} catch (IOException ioex) {
			// notify user
			Gdx.app.error("Picture", "File not saved! ", ioex);
			finalPathHandle.deleteDirectory();
		} finally {
			close(originalFos);
			close(thumbnailFos);
			close(halfSizedFos);
		}
		try {
			Thread.sleep(PICTURE_PREVIEW_TIME);
		} catch (InterruptedException e) {
		}
		camera.startPreview();
	}

	private void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception ex) {
				// Ignore ...
				// any significant errors should already have been
				// reported via an IOException from the final flush.
			}
			closeable = null;
		}
	}

	@Override
	public void prepareCameraAsync(CameraPreparedListener listener) {
		Gdx.app.log("Picture", "prepareCameraAsync");
		this.listener = listener;
		this.activity.post(this.prepareCameraAsyncRunnable);
	}

	private synchronized void startPreviewAsync() {
		Gdx.app.log("Picture", "startPreviewAsync");
		this.activity.post(this.startPreviewAsyncRunnable);
	}

	@Override
	public synchronized void stopPreviewAsync() {
		Gdx.app.log("Picture", "stopPreviewAsync");
		this.activity.post(this.stopPreviewAsyncRunnable);
	}

	@Override
	public synchronized void takePictureAsync(String path) {
		Gdx.app.log("Picture", "takePictureAsync");
		this.savingPath = path;
		this.activity.post(this.takePictureAsyncRunnable);
	}

	@Override
	public void setPictureSize(int width, int height) {
		this.cameraSurface.setPictureSize(width, height);

	}

	@Override
	public Array<Vector2> getSupportedPictureSizes() {
		return this.cameraSurface.getSupportedPictureSizes();
	}

	@Override
	public Vector2 getCurrentPictureSize() {
		return this.cameraSurface.getCurrentPictureSize();
	}
}