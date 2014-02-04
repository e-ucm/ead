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
package es.eucm.ead.editor.control.commands;

import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.editor.model.events.SceneElementEvent;
import es.eucm.ead.editor.model.events.SceneElementEvent.Type;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.components.Transformation;

public class MoveSceneElement extends Command {

	private SceneElement sceneElement;

	private float x;

	private float y;

	private float oldX;

	private float oldY;

	private boolean combine;

	private SceneElementEvent event;

	public MoveSceneElement(SceneElement sceneElement, float x, float y,
			boolean combine) {
		this.sceneElement = sceneElement;
		if (sceneElement.getTransformation() == null) {
			sceneElement.setTransformation(new Transformation());
		}
		oldX = sceneElement.getTransformation().getX();
		oldY = sceneElement.getTransformation().getY();
		this.x = x;
		this.y = y;
		this.combine = combine;
		event = new SceneElementEvent(Type.MOVE, sceneElement);
	}

	@Override
	public ModelEvent doCommand(Model model) {
		Transformation transformation = sceneElement.getTransformation();
		transformation.setX(x);
		transformation.setY(y);
		return event;
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public ModelEvent undoCommand(Model model) {
		Transformation transformation = sceneElement.getTransformation();
		transformation.setX(oldX);
		transformation.setY(oldY);
		return event;
	}

	@Override
	public boolean combine(Command other) {
		if (this.combine && other instanceof MoveSceneElement) {
			MoveSceneElement move = (MoveSceneElement) other;
			this.x = ((MoveSceneElement) other).x;
			this.y = ((MoveSceneElement) other).y;
			this.combine = move.combine;
			return true;
		}
		return false;
	}
}
