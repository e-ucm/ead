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
package es.eucm.ead.android;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import es.eucm.ead.android.picture.AndroidDevicePictureController;
import es.eucm.ead.android.video.AndroidDeviceVideoController;

public class EditorActivity extends AndroidApplication {

	private Map<Integer, ActivityResultListener> listeners;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		final AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.hideStatusBar = true;
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useImmersiveMode = true;
		config.useWakelock = false;
		// We need to change the default pixel format - since it does not
		// include an alpha channel.
		// We need the alpha channel so the camera preview will be seen behind
		// the GL scene.
		config.r = 8;
		config.g = 8;
		config.b = 8;
		config.a = 8;

		this.listeners = new HashMap<Integer, ActivityResultListener>();
		final AndroidDeviceVideoController videoControl = new AndroidDeviceVideoController(
				this);
		final AndroidDevicePictureController pictureControl = new AndroidDevicePictureController(
				this);
		initialize(new AndroidEditor(new AndroidPlatform(), pictureControl,
				videoControl), config);
		if (super.graphics.getView() instanceof SurfaceView) {
			// Force alpha channel.
			final SurfaceView glView = (SurfaceView) this.graphics.getView();
			// If we don't set the format to PixelFormat.TRANSLUCENT we won't
			// see the camera preview through our OpenGL ES view.
			glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		}
	}

	public void startActivityForResult(Intent intent, int requestCode,
			ActivityResultListener l) {
		this.listeners.put(requestCode, l);
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		final ActivityResultListener listener = this.listeners.get(requestCode);
		if (listener != null) {
			listener.result(resultCode, data);
		}
	}

	public void post(Runnable run) {
		super.handler.post(run);
	}

	public interface ActivityResultListener {
		void result(int resultCode, Intent data);
	}
}
