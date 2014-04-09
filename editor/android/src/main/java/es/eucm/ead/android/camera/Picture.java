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
package es.eucm.ead.android.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.GameStructure;
import es.eucm.ead.android.AndroidController;
import es.eucm.ead.android.platform.DevicePictureControl;
import es.eucm.ead.android.platform.DevicePictureControl.PictureTakenListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.mockup.Navigation;
import es.eucm.ead.editor.view.widgets.mockup.buttons.IconButton;

public class Picture implements ViewBuilder,
		DevicePictureControl.CameraPreparedListener, PictureTakenListener {

	public static final String NAME = "mockup_picture";
	private static final String IC_PHOTO = "ic_photocamera";
	private static final float DEFAULT_PAD = 10f;

	private DevicePictureControl pictureControl;
	private AndroidController controller;
	private SelectBox<String> resolution;
	private String previousResolution;
	private boolean cameraPrepared;
	private Button takePicButton;

	private final Runnable resolutionSelectedRunnable = new Runnable() {
		@Override
		public void run() {
			if (!Picture.this.cameraPrepared)
				return;
			final String selected = Picture.this.resolution.getSelected();
			if (Picture.this.previousResolution.equals(selected))
				return;
			final String[] sels = selected.split("x");
			Picture.this.pictureControl.setPictureSize(
					Integer.valueOf(sels[0]), Integer.valueOf(sels[1]));
			Picture.this.resolution.setDisabled(true);
			Picture.this.controller.action(ChangeView.class, Picture.NAME);
		}
	};

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Actor build(Controller controller) {
		if (controller instanceof AndroidController)
			this.controller = (AndroidController) controller;
		final Skin skin = controller.getApplicationAssets().getSkin();
		this.pictureControl = this.controller.getPictureControl();
		final Vector2 viewport = this.controller.getPlatform().getSize();

		this.takePicButton = new IconButton(viewport, skin, IC_PHOTO);
		this.takePicButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				takePic();
			}
		});

		this.resolution = new SelectBox<String>(skin);
		this.resolution.setDisabled(true);
		this.resolution.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.postRunnable(Picture.this.resolutionSelectedRunnable);
			}
		});

		final Table window = new Table(skin).debug().pad(DEFAULT_PAD);
		window.setFillParent(true);
		window.add(this.resolution).right().top();
		window.row();
		window.add(this.takePicButton).bottom().expand().padBottom(DEFAULT_PAD);
		window.addActor(new Navigation(viewport, controller, skin));
		return window;
	}

	private void takePic() {
		Picture.this.takePicButton.setDisabled(true);
		this.pictureControl.takePictureAsync(this.controller.getLoadingPath()
				+ GameStructure.IMAGES_FOLDER, this);
	}

	@Override
	public void initialize(Controller controller) {
		this.pictureControl.prepareCameraAsync(this);
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		this.takePicButton.setDisabled(false);
		this.cameraPrepared = false;
	}

	@Override
	public void release(Controller controller) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		this.pictureControl.stopPreviewAsync();
	}

	@Override
	public void onCameraPrepared() {
		this.cameraPrepared = true;

		final Array<Vector2> sizes = this.pictureControl
				.getSupportedPictureSizes();
		final String[] sizesStr = new String[sizes.size];
		int i = 0;
		for (final Vector2 size : sizes) {
			sizesStr[i] = String.valueOf((int) size.x) + "x"
					+ String.valueOf((int) size.y);
			++i;
		}
		this.resolution.setItems(sizesStr);
		final Vector2 pictureSize = this.pictureControl.getCurrentPictureSize();
		final String currRes = String.valueOf((int) pictureSize.x) + "x"
				+ String.valueOf((int) pictureSize.y);
		this.resolution.setSelected(currRes);
		this.previousResolution = currRes;
		this.resolution.setDisabled(false);
	}

	@Override
	public void onPictureTaken(boolean success) {
		this.takePicButton.setDisabled(false);
	}

}
