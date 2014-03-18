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
package es.eucm.ead.editor.view.widgets.engine.wrappers.transformer.listeners;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.engine.wrappers.transformer.SelectedOverlay;
import es.eucm.ead.engine.actors.SceneElementEngineObject;
import es.eucm.ead.schema.actors.SceneElement;

public abstract class DragListener extends InputListener {

	protected Controller controller;

	protected SceneElementEngineObject actor;

	protected SceneElement sceneElement;

	protected Vector2 touch;

	protected Vector2 current;

	protected SelectedOverlay selectedOverlay;

	public DragListener(Controller controller, SelectedOverlay selectedOverlay) {
		this.controller = controller;
		this.selectedOverlay = selectedOverlay;
		touch = new Vector2();
		current = new Vector2();
	}

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer,
			int button) {
		Actor a = selectedOverlay.getParent();
		if (a instanceof SceneElementEngineObject) {
			actor = (SceneElementEngineObject) a;
			sceneElement = ((SceneElementEngineObject) a).getSchema();
			touch.set(event.getStageX(), event.getStageY());
			actor.getParent().stageToLocalCoordinates(touch);
			readInitialsValues(actor);
			event.cancel();
			return true;
		} else {
			actor = null;
			sceneElement = null;
			return false;
		}
	}

	@Override
	public boolean touchDragged(InputEvent event, float x, float y, int pointer) {
		process(event, true);
		return true;
	}

	@Override
	public boolean touchUp(InputEvent event, float x, float y, int pointer,
			int button) {
		process(event, false);
		return true;
	}

	private void process(InputEvent event, boolean combine) {
		if (actor != null && sceneElement != null) {
			current.set(event.getStageX(), event.getStageY());
			actor.getParent().stageToLocalCoordinates(current);
			process(combine);
		}
	}

	public abstract void readInitialsValues(SceneElementEngineObject actor);

	public abstract void process(boolean combine);

}
