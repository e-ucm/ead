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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.engine.wrappers.transformer.SelectedOverlay;

public class SelectionListener extends InputListener {

	private SelectedOverlay selectedOverlay;

	private Vector2 auxVector = new Vector2();

	public SelectionListener(Controller controller,
			SelectedOverlay selectedOverlay) {
		this.selectedOverlay = selectedOverlay;
	}

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer,
			int button) {
		Actor a = event.getListenerActor();
		if (a instanceof Group) {
			((Group) a).addActor(selectedOverlay);
			auxVector.set(event.getStageX(), event.getStageY());
			event.getStage().stageToScreenCoordinates(auxVector);
			event.getStage().touchDown((int) auxVector.x, (int) auxVector.y,
					event.getPointer(), event.getButton());
		}
		event.cancel();
		return false;
	}

	public void setPlaying(boolean playing) {
		selectedOverlay.setVisible(!playing);
	}
}
