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
package es.eucm.ead.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class EngineStage extends Stage {

	private Group ui;

	private Group scene;

	private int gameWidth;

	private int gameHeight;

	public EngineStage(int width, int height, boolean keepAspectRatio) {
		super(width, height, keepAspectRatio);
		ui = new Group();
		scene = new Group();
		this.addActor(scene);
		this.addActor(ui);
		initUI();
	}

	private void initUI() {
	}

	public void addUi(Actor a) {
		ui.addActor(a);
	}

	public void setScene(Actor s) {
		scene.clear();
		scene.addActor(s);
	}

	public void setGameSize(int width, int height) {
		this.gameWidth = width;
		this.gameHeight = height;
		this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void resize(int windowWidth, int windowHeight) {
		this.setViewport(gameWidth, gameHeight, false, 0, 0, windowWidth,
				windowHeight);
	}

	public int getGameWidth() {
		return gameWidth;
	}

	public int getGameHeight() {
		return gameHeight;
	}
}
