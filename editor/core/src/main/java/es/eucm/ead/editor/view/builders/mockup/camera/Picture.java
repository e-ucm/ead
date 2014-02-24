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
package es.eucm.ead.editor.view.builders.mockup.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ChangeView;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.mockup.Navigation;
import es.eucm.ead.editor.view.widgets.mockup.buttons.IconButton;

public class Picture implements ViewBuilder {

	public static final String NAME = "mockup_picture";
	private static final String RESOURCES = "images";
	private static final String IC_PHOTO = "ic_photocamera";

	private static final float DEFAULT_PAD = 10f;

	private Button takePicButton;

	private Controller controller;
	private SelectBox<String> resolution;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Actor build(Controller controller) {
		this.controller = controller;
		Skin skin = controller.getEditorAssets().getSkin();

		takePicButton = new IconButton(skin, IC_PHOTO);
		takePicButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				takePic();
			}
		});

		resolution = new SelectBox<String>(skin);
		resolution.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				String[] sels = resolution.getSelected().split("x");
				Gdx.app.log("Picture", "Changing resolution to " + sels[0]
						+ "x" + sels[1]);
				Picture.this.controller.action(ChangeView.NAME, Picture.NAME);
			}
		});

		Table window = new Table(skin).debug().pad(DEFAULT_PAD);
		window.setFillParent(true);
		window.add(resolution).right().top();
		window.row();
		window.add(takePicButton).bottom().expand().padBottom(DEFAULT_PAD);
		window.addActor(new Navigation(controller, skin));
		return window;
	}

	private void takePic() {
		Gdx.app.log("Picture",
				"Taking picture, path is " + this.controller.getLoadingPath()
						+ RESOURCES);
	}

	@Override
	public void initialize(Controller controller) {
		Gdx.app.log("Picture", "Preparing camera");
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		String[] sizesStr = { "1920x1080", "1280x720", "800x600" };
		this.resolution.setItems(sizesStr);
		this.resolution.setSelected("1920x1080");
	}

	@Override
	public void release(Controller controller) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		Gdx.app.log("Picture", "Stopping camera");
	}
}
