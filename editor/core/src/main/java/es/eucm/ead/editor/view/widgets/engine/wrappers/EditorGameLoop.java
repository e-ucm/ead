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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.model.FieldNames;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.view.widgets.TextField;
import es.eucm.ead.editor.view.widgets.engine.wrappers.transformer.SelectedOverlay;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.game.Game;

import java.util.ArrayList;
import java.util.Arrays;
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

	private SceneElementEditorObject selected;

	private TextField tagsTextfield;

	public EditorGameLoop(Controller c, Skin skin, EditorGameView sceneView) {
		super(c.getProjectAssets(), sceneView);
		this.controller = c;
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

		tagsTextfield = new TextField("", skin);
		tagsTextfield.addListener(new InputListener() {
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				String tags[] = tagsTextfield.getText().split(",");
				List<String> tagsList = new ArrayList<String>();
				tagsList.addAll(Arrays.asList(tags));
				controller.command(new FieldCommand(selected.getSchema(),
						FieldNames.TAGS, tagsList, false));
				return true;
			}
		});
		tagsTextfield.setPosition(200, 10);
		sceneView.getParent().addActor(tagsTextfield);

	}

	private void updateEditScene() {
		String newScene = model.getGameMetadata().getEditScene();
		if (newScene != null && !newScene.equals(currentSceneName)) {
			currentSceneName = newScene;
			loadScene(currentSceneName);
		}
		addChildrenListener();
	}

	private void addChildrenListener() {
		if (children != null) {
			model.removeListener(children, this);
		}
		children = model.getEditScene().getChildren();
		model.addListListener(children, this);
	}

	private void addModelListeners() {
		model.addFieldListener(model.getGameMetadata(), new FieldListener() {

			@Override
			public void modelChanged(FieldEvent event) {
				updateEditScene();
			}

			@Override
			public boolean listenToField(FieldNames fieldName) {
				return FieldNames.EDIT_SCENE == fieldName;
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

	public void setSelected(SceneElementEditorObject selected) {
		this.selected = selected;
		updateTags(selected);
		selected.addActor(selectedOverlay);
		controller.getViews().requestKeyboardFocus(selectedOverlay);
	}

	private void updateTags(SceneElementEditorObject sceneElement) {
		String tagsString = null;
		List<String> tags = sceneElement.getSchema().getTags();
		for (String t : tags) {
			if (tagsString == null) {
				tagsString = t;
			} else {
				tagsString += "," + t;
			}
		}
		tagsTextfield.setText(tagsString == null ? "" : tagsString);
	}

	@Override
	public void modelChanged(ListEvent event) {
		switch (event.getType()) {
		case ADDED:
			setSelected((SceneElementEditorObject) gameView.getCurrentScene()
					.addActorAt(event.getIndex(),
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
