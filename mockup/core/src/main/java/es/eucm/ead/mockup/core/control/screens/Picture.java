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
package es.eucm.ead.mockup.core.control.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.mockup.core.control.ScreenController;
import es.eucm.ead.mockup.core.view.UIAssets;

public class Picture extends AbstractScreen {

	private Group navigationGroup;
	private Table rootTable;
	private Button takePicButton;
	private Color previousClearColor, clearColor = new Color(0f, 0f, 0f, 0f);

	@Override
	public void create() {
		this.navigationGroup = UIAssets.getNavigationGroup();

		takePicButton = new ImageButton(skin);
		takePicButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				takePic();
			}
		});
		String[] res = { "800x600", "1280x720", "1920x1080", "4000x3000" };
		SelectBox resolution = new SelectBox(res, skin);
		resolution.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// TODO change resolution here
				super.clicked(event, x, y);
			}
		});

		rootTable = new Table();
		rootTable.setVisible(false);
		rootTable.setFillParent(true);
		rootTable.pad(10f);
		//rootTable.debug();

		rootTable.add(resolution).right().top();
		rootTable.row();
		rootTable.add(takePicButton).bottom().expand();

		stage.addActor(rootTable);
	}

	private void takePic() {
		//TODO take picture here
	}

	@Override
	public void show() {
		super.show();
		previousClearColor = ScreenController.CLEAR_COLOR;
		ScreenController.CLEAR_COLOR = clearColor;
		setPreviousScreen(mockupController.getPreviousScreen());
		rootTable.setVisible(true);
		navigationGroup.setVisible(true);
	}

	@Override
	public void act(float delta) {
		stage.act(delta);
	}

	@Override
	public void draw() {
		stage.draw();
		//Table.drawDebug(stage);
	}

	@Override
	public void pause() {
	}

	@Override
	public void hide() {
		ScreenController.CLEAR_COLOR = previousClearColor;
		rootTable.setVisible(false);
		navigationGroup.setVisible(false);
	}
}
