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
package es.eucm.ead.editor.view.builders.scene;

import es.eucm.ead.editor.control.Commands;
import es.eucm.ead.editor.control.Commands.CommandListener;
import es.eucm.ead.editor.control.Commands.CommandsStack;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.Selection.Context;
import es.eucm.ead.editor.control.actions.editor.ShowInfoPanel;
import es.eucm.ead.editor.control.actions.editor.ShowInfoPanel.TypePanel;
import es.eucm.ead.editor.control.actions.editor.ShowToast;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.SelectionCommand;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.builders.scene.draw.BrushStrokes;
import es.eucm.ead.editor.view.builders.scene.play.TestGameView;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.MultiWidget;
import es.eucm.ead.editor.view.widgets.baseview.BaseView;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.Layer;

public class SceneEditor extends BaseView implements ModelView,
		SelectionListener, CommandListener {

	public static final int INSERT = 0, TRANSFORM = 1, PAINT = 2, FX = 3,
			INTERACTION = 4;

	public enum Mode {
		COMPOSE, FX, INTERACTION, PLAY, DRAW,
	}

	private Controller controller;

	private Selection selection;

	private EntitiesLoader entitiesLoader;

	private Mode mode;

	private Mode oldMode;

	private MultiWidget toolbar;

	private SceneGroupEditor sceneGroupEditor;

	private TestGameView gameView;

	private BrushStrokes brushStrokes;

	public SceneEditor(Controller controller) {
		super(controller.getApplicationAssets().getSkin());
		this.controller = controller;
		this.selection = controller.getModel().getSelection();
		this.entitiesLoader = controller.getEngine().getEntitiesLoader();

		mode = Mode.COMPOSE;

		setNavigation(new ProjectNavigation(controller));

		AbstractWidget container = new AbstractWidget();
		sceneGroupEditor = new SceneGroupEditor(controller, this);
		sceneGroupEditor.setFillParent(true);
		brushStrokes = new BrushStrokes(controller, container, this);
		gameView = new TestGameView(controller.getEngine().getGameLoop());
		gameView.setVisible(false);

		container.addActor(sceneGroupEditor);
		container.addActor(gameView);

		setToolbar(toolbar = new GroupEditorToolbar(controller, this,
				brushStrokes));
		setContent(container);
	}

	public SceneGroupEditor getGroupEditor() {
		return sceneGroupEditor;
	}

	@Override
	public void prepare() {
		setMode(mode);
		controller.getEngine().setGameView(gameView);
		controller.getModel().addSelectionListener(this);
		controller.getCommands().addCommandListener(this);

		GameData gameData = Q.getComponent(controller.getModel().getGame(),
				GameData.class);
		gameView.updateWorldSize(gameData.getWidth(), gameData.getHeight());
	}

	@Override
	public void release() {
		controller.getModel().removeSelectionListener(this);
		controller.getCommands().removeCommandListener(this);
	}

	@Override
	public boolean listenToContext(String contextId) {
		return Selection.SCENE_ELEMENT.equals(contextId);
	}

	@Override
	public void modelChanged(SelectionEvent event) {
		setMode(mode);
	}

	/**
	 * @return true if the back was processed
	 */
	public boolean backPressed() {
		if (isNavigationVisible()) {
			toggleNavigation();
			return true;
		} else if (mode == Mode.PLAY) {
			setMode(oldMode);
			return true;
		} else if (mode == Mode.DRAW) {
			setMode(Mode.COMPOSE);
			brushStrokes.hide(true);
			return true;
		} else if (mode == Mode.COMPOSE
				&& selection.get(Selection.SCENE_ELEMENT).length > 0) {
			controller.action(SetSelection.class, Selection.EDITED_GROUP,
					Selection.SCENE_ELEMENT);
			return true;
		}
		return false;
	}

	public void setMode(Mode mode) {
		if (this.mode == Mode.PLAY && mode != Mode.PLAY) {
			controller.getEngine().stop();
			showToolbar();
			gameView.setVisible(false);
			sceneGroupEditor.setVisible(true);
			sceneGroupEditor.prepare();
		}

		this.oldMode = this.mode;
		this.mode = mode;
		switch (mode) {
		case COMPOSE:
			Context context = controller.getModel().getSelection()
					.getContext(Selection.SCENE_ELEMENT);
			if (context == null || context.getSelection().length == 0) {
				toolbar.setSelectedWidget(INSERT);
			} else {
				toolbar.setSelectedWidget(TRANSFORM);
			}

			controller.action(ShowInfoPanel.class, TypePanel.COMPOSE,
					Preferences.HELP_MODE_COMPOSE);
			break;
		case DRAW:
			toolbar.setSelectedWidget(PAINT);
			brushStrokes.show();
			break;
		case INTERACTION:
			toolbar.setSelectedWidget(INTERACTION);
			break;
		case FX:
			toolbar.setSelectedWidget(FX);
			break;
		case PLAY:
			hideToolbar();
			gameView.setVisible(true);
			sceneGroupEditor.setVisible(false);
			controller.getEngine().play();
			sceneGroupEditor.release();

			ModelEntity scene = (ModelEntity) controller.getModel()
					.getSelection().getSingle(Selection.SCENE);
			gameView.addEntityToLayer(Layer.SCENE,
					entitiesLoader.toEngineEntity(scene));

			controller.action(ShowToast.class, controller
					.getApplicationAssets().getI18N().m("play.back"));

			controller.action(ShowInfoPanel.class, TypePanel.PLAY,
					Preferences.HELP_MODE_PLAY);
			break;
		}
	}

	@Override
	public void doCommand(Commands commands, Command command) {
		if (!(command instanceof SelectionCommand)) {
			ModelEntity scene = (ModelEntity) controller.getModel()
					.getSelection().getSingle(Selection.SCENE);
			if (scene != null) {
				Q.getThumbnail(controller, scene);
				String resource = controller.getModel().getIdFor(scene);
				if (resource != null) {
					controller.getEditorGameAssets().save(resource, scene);
				}
			}
		}
	}

	@Override
	public void undoCommand(Commands commands, Command command) {
	}

	@Override
	public void redoCommand(Commands commands, Command command) {
	}

	@Override
	public void savePointUpdated(Commands commands, Command savePoint) {
	}

	@Override
	public void cleared(Commands commands) {
	}

	@Override
	public void contextPushed(Commands commands) {
	}

	@Override
	public void contextPopped(Commands commands, CommandsStack poppedContext,
			boolean merge) {
	}

}
