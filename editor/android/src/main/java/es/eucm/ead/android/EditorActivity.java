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
package es.eucm.ead.android;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import es.eucm.ead.android.picture.AndroidDevicePictureController;
import es.eucm.ead.editor.Editor;

public class EditorActivity extends AndroidApplication {

	private Map<Integer, ActivityResultListener> listeners;
	private View libGDXview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Do the stuff that initialize() would do for you
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.hideStatusBar = true;
		config.useGL20 = true;
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useGLSurfaceViewAPI18 = false;
		config.useImmersiveMode = true;
		config.useWakelock = false;
		// we need to change the default pixel format - since it does not
		// include an alpha channel
		// we need the alpha channel so the camera preview will be seen behind
		// the GL scene
		config.r = 8;
		config.g = 8;
		config.b = 8;
		config.a = 8;

		listeners = new HashMap<Integer, ActivityResultListener>();
		AndroidDevicePictureController pictureControl = new AndroidDevicePictureController(
				this);
		libGDXview = initializeForView(new Editor(new AndroidPlatform(),
				pictureControl), config);
		setContentView(libGDXview);
		if (graphics.getView() instanceof SurfaceView) {
			SurfaceView glView = (SurfaceView) graphics.getView();
			// force alpha channel
			glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		}
	}

	public void startActivityForResult(Intent intent, int requestCode,
			ActivityResultListener l) {
		listeners.put(requestCode, l);
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ActivityResultListener l = listeners.get(requestCode);
		if (l != null) {
			l.result(resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void post(Runnable run) {
		handler.post(run);
	}

	public interface ActivityResultListener {
		void result(int resultCode, Intent data);
	}

	public View getLibGDXview() {
		return libGDXview;
	}
}
