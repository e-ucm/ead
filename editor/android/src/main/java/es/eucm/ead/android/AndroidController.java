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

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import es.eucm.ead.android.platform.DevicePictureControl;
import es.eucm.ead.android.platform.DeviceVideoControl;
import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Views;
import es.eucm.ead.editor.platform.Platform;

public class AndroidController extends Controller {

	private DeviceVideoControl videoControl;

	private DevicePictureControl pictureControl;

	public AndroidController(Platform platform,
			DevicePictureControl pictureControl,
			DeviceVideoControl videoControl, Files files,
			final Group rootComponent) {
		super(platform, files, rootComponent);
		this.videoControl = videoControl;
		this.pictureControl = pictureControl;
		// This allows us to catch events related with
		// the back key in Android.
		Gdx.input.setCatchBackKey(true);
		rootComponent.addCaptureListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				// This hides the on screen keyboard
				// if we're writing in a text field and
				// touch down anywhere else but the text field.
				final Stage stage = rootComponent.getStage();
				if (!(event.getTarget() instanceof TextField)) {
					stage.unfocusAll();
					Gdx.input.setOnscreenKeyboardVisible(false);
				}
				return false;
			}

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.BACK) {
					((AndroidViews) AndroidController.this.views)
							.onBackPressed();
				}
				return false;
			}
		});
	}

	@Override
	protected ApplicationAssets createApplicationAssets(Files files) {
		return new ApplicationAssets(files, "skins/mockup/skin");
	}

	@Override
	protected Views createViews(Group rootView) {
		return new AndroidViews(this, rootView);
	}

	public DeviceVideoControl getVideoControl() {
		return this.videoControl;
	}

	public DevicePictureControl getPictureControl() {
		return this.pictureControl;
	}
}
