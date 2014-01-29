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
package es.eucm.ead.editor.view.widgets.scene;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.ModelEvent;

public class ScenePreview extends WidgetGroup implements ModelListener {

	private Controller controller;

	private EditorGameLoop gameController;

	public ScenePreview(Controller controller) {
		this.controller = controller;
		gameController = new EditorGameLoop();
		addActor(gameController.getSceneView());
		reloadGame();
		controller.getModel().addListener(this);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		gameController.act(delta);
	}

	@Override
	public float getPrefWidth() {
		return gameController.getGame() != null ? gameController.getGame()
				.getWidth() : 100;
	}

	@Override
	public float getPrefHeight() {
		return gameController.getGame() != null ? gameController.getGame()
				.getHeight() : 100;
	}

	@Override
	public void modelChanged(ModelEvent event) {
		reloadGame();
	}

	public void reloadGame() {
		gameController.setGamePath(controller.getLoadingPath(), false);
		invalidateHierarchy();
	}
}
