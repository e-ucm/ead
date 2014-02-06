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
package es.eucm.ead.editor.view.widgets.engine.wrappers;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.schema.game.Game;

public class EditorGameLoop extends GameLoop {

	private Game game;

	private Skin skin;

	private DragListener dragListener;

	private Controller controller;

	private boolean playing;

	private Model model;

	private String currentSceneName;

	public EditorGameLoop(Controller controller, Skin skin,
			EditorSceneView sceneView) {
		super(controller.getProjectAssets(), sceneView);
		this.controller = controller;
		this.skin = skin;
		this.dragListener = new DragListener(controller, this);

		model = controller.getModel();
		model.addLoadListener(new ModelListener<LoadEvent>() {

			@Override
			public void modelChanged(LoadEvent event) {
				addModelListeners();
				startSubgame(null, null);
				updateEditScene();
			}
		});

	}

	private void updateEditScene() {
		String newScene = model.getProject().getEditScene();
		if (newScene != null && !newScene.equals(currentSceneName)) {
			currentSceneName = newScene;
			loadScene(currentSceneName);
		}
	}

	private void addModelListeners() {
		model.addFieldListener(model.getProject(), new FieldListener() {

			@Override
			public void modelChanged(FieldEvent event) {
				updateEditScene();
			}

			@Override
			public boolean listenToField(String fieldName) {
				return "editScene".equals(fieldName);
			}
		});
	}

	@Override
	protected void setGame(Game game) {
		this.game = game;
		super.setGame(game);
	}

	public DragListener getDragListener() {
		return dragListener;
	}

	public Game getGame() {
		return game;
	}

	public Skin getSkin() {
		return skin;
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	@Override
	protected void updateTriggerSources(float delta) {
		if (isPlaying()) {
			super.updateTriggerSources(delta);
		}
	}

	public Controller getController() {
		return controller;
	}
}
