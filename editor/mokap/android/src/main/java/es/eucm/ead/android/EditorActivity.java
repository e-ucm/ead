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
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import es.eucm.ead.editor.MokapApplicationListener;
import es.eucm.ead.editor.platform.MokapPlatform;
import es.eucm.mokap.R;

public class EditorActivity extends AndroidApplication {

	private final String SAVED_INSTANCE_STATE_CONSUMED_INTENT = "SAVED_INSTANCE_STATE_CONSUMED_INTENT";
	private Map<Integer, ActivityResultListener> listeners;
	private boolean consumedIntent = false;
	private AndroidPlatform platform;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(SAVED_INSTANCE_STATE_CONSUMED_INTENT,
				consumedIntent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			consumedIntent = savedInstanceState
					.getBoolean(SAVED_INSTANCE_STATE_CONSUMED_INTENT);
		}

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useImmersiveMode = false;
		config.hideStatusBar = true;
		config.useWakelock = true;
		config.useCompass = false;

		this.listeners = new HashMap<Integer, ActivityResultListener>();
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
		Tracker tracker = analytics.newTracker(R.xml.tracker);
		analytics.reportActivityStart(this);
		platform = new AndroidPlatform(getContext(), tracker);
		handleIntent();
		initialize(new MokapApplicationListener(platform), config);
	}

	private void handleIntent() {
		if (consumedIntent) {
			return;
		}
		Intent intent = getIntent();
		if (intent != null) {
			boolean launchedFromHistory = (intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0;
			if (!launchedFromHistory) {
				String action = intent.getAction();
				if (Intent.ACTION_VIEW.equals(action)) {
					Uri data = intent.getData();
					if (data != null) {
						String path = data.getPath();
						platform.setApplicationArguments(path);
						consumedIntent = true;
					}
				} else if (Intent.ACTION_MAIN.equals(action)) {
					consumedIntent = true;
				}
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		consumedIntent = false;
	}

	public void startActivityForResult(Intent intent, int requestCode,
			ActivityResultListener l) {
		this.listeners.put(requestCode, l);
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ActivityResultListener listener = this.listeners.get(requestCode);
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

	@Override
	protected void onResume() {
		handleIntent();
		super.onResume();
		// This is necessary because we are using non-continuous rendering and
		// sometimes the screen stops rendering after onResume(). Probably a
		// libGDX bug.
		Gdx.graphics.requestRendering();
	}
}
