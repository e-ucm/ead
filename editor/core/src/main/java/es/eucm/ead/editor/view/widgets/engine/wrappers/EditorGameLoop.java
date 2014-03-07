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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.EditScene;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.view.widgets.engine.wrappers.transformer.SelectedOverlay;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.game.Game;

import java.util.List;

public class EditorGameLoop extends GameLoop implements
		ModelListener<ListEvent> {

	private Game game;

	private Skin skin;

	private SelectionListener selectionListener;

	private Controller controller;

	private boolean playing;

	private Model model;

	private String currentSceneName;

	private SelectedOverlay selectedOverlay;

	private List<SceneElement> children;

	public EditorGameLoop(Controller controller, Skin skin,
			EditorGameView sceneView) {
		super(controller.getProjectAssets(), sceneView);
		this.controller = controller;
		this.skin = skin;
		this.selectedOverlay = new SelectedOverlay(controller, skin);
		this.selectionListener = new SelectionListener(this);

		model = controller.getModel();
		model.addLoadListener(new ModelListener<LoadEvent>() {

			@Override
			public void modelChanged(LoadEvent event) {
				addModelListeners();
				startSubgame(null, null);
				updateEditScene();
				addChildrenListener();
			}
		});

		sceneView.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				selectedOverlay.remove();
				return false;
			}
		});

	}

	private void addChildrenListener() {
		if (children != null) {
			model.removeListener(children, this);
		}
		children = model.getEditScene().getChildren();
		model.addListListener(children, this);
	}

	private void updateEditScene() {
		String newScene = model.getGameMetadata().getEditScene();
		if (newScene != null && !newScene.equals(currentSceneName)) {
			currentSceneName = newScene;
			loadScene(currentSceneName);
		}
	}

	private void addModelListeners() {
		model.addFieldListener(model.getGameMetadata(), new FieldListener() {

			@Override
			public void modelChanged(FieldEvent event) {
				updateEditScene();
			}

			@Override
			public boolean listenToField(String fieldName) {
				return EditScene.FIELD_NAME.equals(fieldName);
			}
		});
	}

	@Override
	protected void setGame(Game game) {
		this.game = game;
		super.setGame(game);
	}

	public SelectionListener getSelectionListener() {
		return selectionListener;
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
		selectedOverlay.setVisible(!playing);
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

	public void setSelected(Group selected) {
		selected.addActor(selectedOverlay);
		controller.getViews().requestKeyboardFocus(selectedOverlay);
	}

	@Override
	public void modelChanged(ListEvent event) {
		switch (event.getType()) {
		case ADDED:
			setSelected(gameView.getCurrentScene().addActorAt(event.getIndex(),
					(SceneElement) event.getElement()));
			break;
		case REMOVED:
			Actor actor = gameView.getCurrentScene().getSceneElement(
					(SceneElement) event.getElement());
			actor.remove();
			break;
		}
	}
}
