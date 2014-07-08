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
package es.eucm.ead.editor.nogui;

import com.badlogic.gdx.Input.Buttons;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.engine.mock.MockApplication;

public abstract class EditorGUITest {

	protected MockApplication app;

	protected NoGUIEditorDesktop editorDesktop;

	protected Controller controller;

	protected Stage stage;

	@Before
	public void setUp() {
		editorDesktop = new NoGUIEditorDesktop();
		app = new MockApplication(editorDesktop);
		controller = editorDesktop.getController();
		stage = editorDesktop.getStage();
		controller.getCommands().pushStack();
	}

	@Test
	public void testEditorDesktop() {
		Array<Runnable> runnables = new Array<Runnable>();
		collectRunnables(runnables);
		for (Runnable runnable : runnables) {
			app.postRunnable(runnable);
		}
		app.act();
	}

	protected void press(String actorName) {
		inputEvent(actorName, Type.touchDown);
	}

	protected void release(String actorName) {
		inputEvent(actorName, Type.touchUp);
	}

	protected void click(String actorName) {
		press(actorName);
		release(actorName);
	}

	public void setSelection(Object... selection) {
		controller.action(SetSelection.class, selection);
	}

	private void inputEvent(String actorName, Type type) {
		Actor actor = stage.getRoot().findActor(actorName);
		InputEvent inputEvent = Pools.obtain(InputEvent.class);
		inputEvent.setType(type);
		inputEvent.setButton(Buttons.LEFT);
		actor.fire(inputEvent);
		Pools.free(inputEvent);
	}

	protected abstract void collectRunnables(Array<Runnable> runnables);
}
