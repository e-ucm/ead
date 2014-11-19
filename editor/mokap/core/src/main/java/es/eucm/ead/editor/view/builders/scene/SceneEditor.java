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
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.Selection.Context;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.SelectionCommand;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.builders.scene.draw.BrushStrokes;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.MultiWidget;
import es.eucm.ead.editor.view.widgets.baseview.BaseView;
import es.eucm.ead.schema.entities.ModelEntity;

public class SceneEditor extends BaseView implements ModelView,
		SelectionListener, CommandListener {

	public static final int INSERT = 0, TRANSFORM = 1, PAINT = 2, FX = 3,
			INTERACTION = 4;

	public enum Mode {
		COMPOSE, FX, INTERACTION, PLAY, DRAW,
	}

	private Controller controller;

	private Mode mode;

	private MultiWidget toolbar;

	private SceneGroupEditor sceneGroupEditor;

	private BrushStrokes brushStrokes;

	public SceneEditor(Controller controller) {
		super(controller.getApplicationAssets().getSkin());
		this.controller = controller;
		mode = Mode.COMPOSE;

		setNavigation(new ProjectNavigation(controller));

		AbstractWidget container = new AbstractWidget();
		sceneGroupEditor = new SceneGroupEditor(controller);
		sceneGroupEditor.setFillParent(true);
		brushStrokes = new BrushStrokes(container, controller);
		container.addActor(sceneGroupEditor);
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
		controller.getModel().addSelectionListener(this);
		controller.getCommands().addCommandListener(this);
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

	public void setMode(Mode mode) {
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
				controller.getEditorGameAssets().save(resource, scene);
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
