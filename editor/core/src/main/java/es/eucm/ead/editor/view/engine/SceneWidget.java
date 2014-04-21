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
package es.eucm.ead.editor.view.engine;

import com.badlogic.gdx.Gdx;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.engine.GameLoop;

public class SceneWidget extends AbstractWidget {

	private EditorGameLayers layers;

	private GameLoop gameLoop;

	private EditorGameAssets assets;

	public SceneWidget(Controller controller, EditorGameAssets editorGameAssets) {
		this.assets = editorGameAssets;
		addActor(layers = new EditorGameLayers());
		gameLoop = new GameLoop();
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		gameLoop.update(delta);
	}

	@Override
	public float getPrefWidth() {
		return Gdx.graphics.getWidth();
	}

	@Override
	public float getPrefHeight() {
		return Gdx.graphics.getHeight();
	}

	@Override
	public void layout() {
		float scaleX = getWidth() / layers.getWidth();
		float scaleY = getHeight() / layers.getHeight();
		float scale = Math.min(Math.min(scaleX, scaleY), 1.0f);
		layers.setScale(scale);

		float xOffset = (getWidth() - layers.getWidth() * scale) / 2;
		float yOffset = (getHeight() - layers.getHeight() * scale) / 2;
		layers.setPosition(xOffset, yOffset);
	}
}
