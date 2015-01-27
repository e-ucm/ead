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
package es.eucm.ead.engine.android;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.surfaceview.FixedResolutionStrategy;
import es.eucm.ead.engine.EngineApplicationListener;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class EngineActivity extends AndroidApplication {

	/**
	 * Key corresponding to the meta-data value that determines the fixed width
	 * of the GLSurfaceView. If the value for this meta-data key is -1, or it is
	 * not present in the activity, the surface will be stretched full screen.
	 */
	public static final String CANVAS_WIDTH = "CanvasWidth";
	/**
	 * Key corresponding to the meta-data value that determines the fixed height
	 * of the GLSurfaceView. If the value for this meta-data key is -1, or it is
	 * not present in the activity, the surface will be stretched full screen.
	 */
	public static final String CANVAS_HEIGHT = "CanvasHeight";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Read canvas width and height
		int canvasWidth = -1;
		int canvasHeight = -1;

		try {
			ActivityInfo ai = getPackageManager().getActivityInfo(
					getComponentName(),
					PackageManager.GET_ACTIVITIES
							| PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			canvasWidth = bundle.getInt(CANVAS_WIDTH);
			canvasHeight = bundle.getInt(CANVAS_HEIGHT);
		} catch (Exception e) {
			errorReadingCanvasSize(e);
		}

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		if (canvasHeight > 0 && canvasWidth > 0) {
			config.resolutionStrategy = new FixedResolutionStrategy(
					canvasWidth, canvasHeight);
		}

		config.useAccelerometer = false;
		config.useImmersiveMode = false;
		config.hideStatusBar = true;
		config.useWakelock = true;
		config.useCompass = false;

		final EngineApplicationListener engineApplicationListener = new EngineAndroidApplicationListener();
		initialize(engineApplicationListener, config);
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				engineApplicationListener.getGameLoader().loadGame("", true);
			}
		});
	}

	private void errorReadingCanvasSize(Exception e) {
		System.err
				.println("Error reading canvas size. Either it is not accessible or it is bad formatted.");
		e.printStackTrace();
	}
}
