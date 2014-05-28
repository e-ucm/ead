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
package es.eucm.ead.editor.view.builders.mockup.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.platform.DeviceVideoControl;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.mockup.Navigation;
import es.eucm.ead.editor.view.widgets.mockup.buttons.IconButton;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schemax.GameStructure;

public class Video implements ViewBuilder, DeviceVideoControl.RecordingListener {

	private static final String IC_RECORD = "ic_record";
	private static final String IC_RECORDING = "ic_recording";
	private static final float DEFAULT_PAD = 10f;

	private DeviceVideoControl videoControl;
	private SelectBox<String> resolution;
	private Controller controller;
	private String previousResolution;
	private Button recordingButton;
	private Table recInfoButton;
	private float elapsedMilis;
	private boolean recording;
	private int elapsedSecs;
	private Label recLabel;

	private Actor view;

	private final Runnable resolutionSelectedRunnable = new Runnable() {
		@Override
		public void run() {
			final String currSel = Video.this.resolution.getSelected();
			if (Video.this.previousResolution.equals(currSel))
				return;
			Video.this.videoControl.setRecordingProfile(currSel);
			Video.this.controller.action(ChangeView.class, Video.class);
			Video.this.previousResolution = currSel;
		}
	};

	@Override
	public Actor getView(Object... args) {
		this.resolution.setSelected(this.videoControl.getCurrentProfile());
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		this.recordingButton.setDisabled(false);
		this.videoControl.prepareVideoAsynk();
		this.resolution.setDisabled(false);
		this.recording = false;
		this.elapsedMilis = 0f;
		this.elapsedSecs = 0;
		updateUI();

		return view;
	}

	private void record() {
		if (this.recordingButton.isDisabled())
			return;
		this.recordingButton.setDisabled(true);
		if (this.videoControl.isRecording()) {
			this.videoControl.stopRecording(this);
		} else {
			this.videoControl.startRecording(this.controller.getLoadingPath()
					+ GameStructure.VIDEOS_FOLDER, this);
		}
	}

	@Override
	public void initialize(Controller controller) {
		this.controller = controller;

		Skin skin = controller.getApplicationAssets().getSkin();
		I18N i18n = controller.getApplicationAssets().getI18N();
		videoControl = controller.getPlatform().getVideo();
		Vector2 viewport = this.controller.getPlatform().getSize();

		recordingButton = new IconButton(viewport, skin, IC_RECORD);
		recordingButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				record();
			}
		});

		resolution = new SelectBox<String>(skin);
		resolution.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.postRunnable(resolutionSelectedRunnable);
			}
		});

		recInfoButton = new Table();
		Image recImg = new Image(skin.getRegion(IC_RECORDING));
		recImg.setTouchable(Touchable.disabled);
		recLabel = new Label("", skin) {
			@Override
			public void act(float delta) {
				super.act(delta);
				if (recording) {
					elapsedMilis += delta;
					if (elapsedMilis >= 1f) {
						elapsedMilis = 0f;
						++elapsedSecs;
						if (recordingButton.isDisabled() && elapsedSecs > 1) {
							recordingButton.setDisabled(false);
						}
						recLabel.setText(String.valueOf(elapsedSecs));
					}
				}
			}
		};
		Array<String> qualities = videoControl.getQualities();
		if (qualities.size == 0) {
			resolution.setItems(i18n.m("video.min-res"));
		} else {
			resolution.setItems(qualities);
		}
		previousResolution = resolution.getSelected();
		recLabel.setColor(Color.RED);
		recInfoButton.add(recImg);
		recInfoButton.add(recLabel).padLeft(DEFAULT_PAD * 2);

		Table window = new Table(skin).debug().pad(DEFAULT_PAD);
		window.setFillParent(true);

		window.add(resolution).right().top();
		window.row();
		window.add(recInfoButton).right().top();
		window.row();
		window.add(recordingButton).bottom().expand().padBottom(DEFAULT_PAD);
		window.addActor(new Navigation(viewport, controller, skin));
		view = window;
	}

	@Override
	public void release(Controller controller) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		if (this.videoControl.isRecording()) {
			this.videoControl.stopRecording(this);
		}
		this.videoControl.stopPreviewAsynk();
	}

	@Override
	public void onVideoFinishedRecording(boolean success) {
		this.recordingButton.setDisabled(recording = false);
		updateUI();
	}

	@Override
	public void onVideoStartedRecording(boolean success) {
		this.elapsedSecs = 0;
		this.recording = success;
		this.elapsedMilis = 0f;
		updateUI();
	}

	private void updateUI() {
		this.resolution.setDisabled(this.recording);
		this.recInfoButton.setVisible(this.recording);
		this.recordingButton.setChecked(this.recording);
		this.recLabel.setText(String.valueOf(this.elapsedSecs));
	}
}
