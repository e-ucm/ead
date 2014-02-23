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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;

import es.eucm.ead.android.EditorActivity;
import es.eucm.ead.editor.platform.mockup.DevicePictureControl;

public class AndroidDevicePictureController implements DevicePictureControl,
		Camera.PictureCallback, Camera.AutoFocusCallback {

	private static final long PICTURE_PREVIEW_TIME = 1000;

	private final EditorActivity activity;
	private CameraSurface cameraSurface;
	private final LayoutParams mLayoutParams;
	private final Runnable prepareCameraAsyncRunnable;
	private final Runnable startPreviewAsyncRunnable;
	private final Runnable stopPreviewAsyncRunnable;
	private final Runnable takePictureAsyncRunnable;

	public AndroidDevicePictureController(EditorActivity activity) {
		this.activity = activity;
		this.mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
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
		if (cameraSurface == null) {
			Gdx.app.log("Picture", "camera, is null retrieving device's camera");
			this.cameraSurface = new CameraSurface(activity);
		}
		this.activity.addContentView(this.cameraSurface, this.mLayoutParams);
		startPreviewAsync();
	}

	private synchronized void startPreview() {
		Gdx.app.log("Picture", "startPreviw");
		// ...and start previewing. From now on, the camera keeps pushing
		// preview
		// images to the surface.
		Camera cam = this.cameraSurface.getCamera();
		if (this.cameraSurface != null && cam != null) {
			cam.startPreview();
		}
	}

	private synchronized void stopPreview() {
		Gdx.app.log("Picture", "stopPreview");
		// stop previewing.
		if (this.cameraSurface != null) {
			ViewParent parentView = this.cameraSurface.getParent();
			if (parentView instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) parentView;
				viewGroup.removeView(this.cameraSurface);
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

	private static int resourceID = 0;

	@Override
	public synchronized void onPictureTaken(byte[] data, Camera camera) {
		// We got the picture data - keep it
		Gdx.app.log("Picture", "onPictureTaken");
		/*
		 * String oriPath =
		 * FileHandler.getOriginalsFileHandle().file().getAbsolutePath() +
		 * File.separator; String halfPath =
		 * FileHandler.getHalfSizedFileHandle().file().getAbsolutePath() +
		 * File.separator; String thumbPath =
		 * FileHandler.getThumbnailsFileHandle().file().getAbsolutePath() +
		 * File.separator;
		 */
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String oriPath = path + File.separator;
		String halfPath = path + File.separator;
		String thumbPath = path + File.separator;
		int resID = 1 + resourceID;
		String num = String.valueOf(resID) + ".jpg";

		String originalFileName = oriPath + "Original" + num;
		String halfSizedFileName = halfPath + "HalfSized" + num;
		String thumbnailFileName = thumbPath + "Thumbnail" + num;

		try {
			// Original
			OutputStream fos = new FileOutputStream(new File(originalFileName));
			fos.write(data);
			fos.close();

			Bitmap imageBitmap = BitmapFactory.decodeByteArray(data, 0,
					data.length);

			Size photoSize = this.cameraSurface.getPhotoSize();
			int w = photoSize.width;
			int h = photoSize.height;

			// Thumbnail
			Bitmap aux = Bitmap.createScaledBitmap(imageBitmap, w / 10, h / 10,
					false);

			fos = new FileOutputStream(new File(thumbnailFileName));
			aux.compress(Bitmap.CompressFormat.JPEG, 75, fos);
			fos.flush();
			fos.close();
			if (aux != imageBitmap && aux != null) {
				aux.recycle();
				aux = null;
			}

			// HalfScaled image
			aux = Bitmap.createScaledBitmap(imageBitmap, w / 2, h / 2, true);

			fos = new FileOutputStream(new File(halfSizedFileName));
			aux.compress(Bitmap.CompressFormat.JPEG, 95, fos);
			fos.flush();
			fos.close();
			if (aux != imageBitmap && aux != null) {
				aux.recycle();
				aux = null;
			}

			if (imageBitmap != null) {
				imageBitmap.recycle();
				imageBitmap = null;
			}

			fos = null;

			Toast.makeText(activity, "New Image saved, id: " + resID,
					Toast.LENGTH_SHORT).show();

			++resourceID;

		} catch (Exception error) {
			Gdx.app.error("Picture", "File not saved! ", error);
			Toast.makeText(activity, "Image could not be saved.",
					Toast.LENGTH_LONG).show();
		}
		try {
			Thread.sleep(PICTURE_PREVIEW_TIME);
		} catch (InterruptedException e) {
		}
		camera.startPreview();
	}

	@Override
	public void prepareCameraAsync() {
		Gdx.app.log("Picture", "prepareCameraAsync");
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
	public synchronized void takePictureAsync() {
		Gdx.app.log("Picture", "takePictureAsync");
		this.activity.post(this.takePictureAsyncRunnable);
	}
}