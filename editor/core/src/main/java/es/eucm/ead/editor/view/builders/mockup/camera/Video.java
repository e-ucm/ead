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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.GameStructure;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.mockup.Navigation;
import es.eucm.ead.editor.view.widgets.mockup.buttons.IconButton;

public class Video implements ViewBuilder {

	public static final String NAME = "mockup_video";
	private static final String IC_RECORD = "ic_record";
	private static final String IC_RECORDING = "ic_recording";

	private static final float DEFAULT_PAD = 10f;

	private SelectBox<String> resolution;
	private Button recordingButton;
	private Controller controller;
	private Table recInfoButton;
	private float elapsedMilis;
	private int elapsedSecs;
	private Label recLabel;
	private boolean recording;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Actor build(Controller controller) {

		this.recording = false;
		this.controller = controller;
		final Skin skin = controller.getApplicationAssets().getSkin();
		final Vector2 viewport = controller.getPlatform().getSize();

		this.recordingButton = new IconButton(viewport, skin, IC_RECORD);
		this.recordingButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				record();
			}
		});

		this.resolution = new SelectBox<String>(skin);
		this.resolution.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				final String sel = Video.this.resolution.getSelected();
				Gdx.app.log("Video", "Changing recording profile to " + sel);
				Video.this.controller.action(ChangeView.class, Video.NAME);
			}
		});

		this.recInfoButton = new Table();
		final Image recImg = new Image(skin.getRegion(IC_RECORDING));
		recImg.setTouchable(Touchable.disabled);
		this.recLabel = new Label("", skin) {
			@Override
			public void act(float delta) {
				super.act(delta);
				if (Video.this.recording) {
					Video.this.elapsedMilis += delta;
					if (Video.this.elapsedMilis >= 1f) {
						Video.this.elapsedMilis = 0f;
						++Video.this.elapsedSecs;
						Video.this.recLabel.setText(String
								.valueOf(Video.this.elapsedSecs));
					}
				}
			}
		};
		final String[] quals = { "1080p", "720p", "480p" };
		this.resolution.setItems(quals);
		this.recLabel.setColor(Color.RED);
		this.recInfoButton.add(recImg);
		this.recInfoButton.add(this.recLabel).padLeft(20f);

		final Table window = new Table(skin).debug().pad(DEFAULT_PAD);
		window.setFillParent(true);

		window.add(this.resolution).right().top();
		window.row();
		window.add(this.recInfoButton).right().top();
		window.row();
		window.add(this.recordingButton).bottom().expand()
				.padBottom(DEFAULT_PAD);
		window.addActor(new Navigation(viewport, controller, skin));
		return window;
	}

	private void record() {
		if (this.recording) {
			this.recording = false;
			Gdx.app.log("Video", "Stopping recording!");
		} else {
			this.recording = true;
			Gdx.app.log("Video",
					"Starting recording to" + this.controller.getLoadingPath()
							+ GameStructure.VIDEOS_FOLDER);
		}
		this.elapsedSecs = 0;
		this.elapsedMilis = 0f;
		this.resolution.setDisabled(this.recording);
		this.recInfoButton.setVisible(this.recording);
		this.recordingButton.setChecked(this.recording);
		this.recLabel.setText(String.valueOf(this.elapsedSecs));
	}

	@Override
	public void initialize(Controller controller) {
		this.resolution.setSelected("1080p");
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.app.log("Video", "Preparing video surface.");
		this.recInfoButton.setVisible(false);
		this.elapsedMilis = 0f;
		this.elapsedSecs = 0;
		this.recording = false;
	}

	@Override
	public void release(Controller controller) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		Gdx.app.log("Video", "Stopping video preview.");
	}
}
