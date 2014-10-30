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
package es.eucm.ead.editor.view.widgets.editionview;

import java.util.Stack;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import es.eucm.ead.editor.control.Actions;
import es.eucm.ead.editor.control.Commands;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeMockupView;
import es.eucm.ead.editor.control.actions.editor.Redo;
import es.eucm.ead.editor.control.actions.editor.Undo;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.transitions.Transitions;
import es.eucm.ead.editor.view.builders.gallery.PlayView;
import es.eucm.ead.editor.view.listeners.ActionListener;
import es.eucm.ead.editor.view.widgets.Toolbar;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.iconwithpanel.IconWithLateralPanel;
import es.eucm.ead.engine.assets.GameAssets;

public class TopEditionToolbar extends Toolbar {

	private OthersWidget others;

	private IconButton debugPlay;

	private IconButton undo;

	private IconButton redo;

	private IconWithLateralPanel about;

	public TopEditionToolbar(final Controller controller, String style,
			float smallPad, float normalPad, Actor reference) {

		super(controller.getApplicationAssets().getSkin(), style);
		Skin skin = controller.getApplicationAssets().getSkin();

		debugPlay = new IconButton("play", "debugPlay", 0f, skin, "inverted");

		undo = new IconButton("undo", "undo80x80", 0f, skin);
		redo = new IconButton("redo", "redo80x80", 0f, skin);

		others = new OthersWidget(controller, reference);

		ChangeListener buttonsListener = new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Actor listenerActor = event.getListenerActor();
				if (listenerActor == debugPlay) {
					controller.action(ChangeMockupView.class, PlayView.class,
							Transitions.getSlideTransition(true),
							GameAssets.GAME_DEBUG);
				} else if (listenerActor == undo) {
					controller.action(Undo.class);
				} else if (listenerActor == redo) {
					controller.action(Redo.class);
				}
			}
		};

		debugPlay.addListener(buttonsListener);
		undo.addListener(buttonsListener);
		redo.addListener(buttonsListener);

		ActionListener undoRedo = new ActionListener() {

			@Override
			public void enableChanged(Class actionClass, boolean enable) {
				if (actionClass == Undo.class) {
					undo.setDisabled(!enable);
				} else if (actionClass == Redo.class) {
					redo.setDisabled(!enable);
				}
			}
		};
		Commands commands = controller.getCommands();
		Stack<Command> undoHistory = commands.getUndoHistory();
		undo.setDisabled(undoHistory == null ? true : undoHistory.isEmpty());
		Stack<Command> redoHistory = commands.getRedoHistory();
		redo.setDisabled(redoHistory == null ? true : redoHistory.isEmpty());
		Actions actions = controller.getActions();
		actions.addActionListener(Undo.class, undoRedo);
		actions.addActionListener(Redo.class, undoRedo);

		rightAdd(undo);
		rightAdd(redo);
		rightAdd(debugPlay);
		rightAdd(others);

		backgroundColor(Color.LIGHT_GRAY);
	}

	private void setDisabled(boolean disabled, Controller controller) {
		undo.setDisabled(disabled);
		redo.setDisabled(disabled);
	}
}