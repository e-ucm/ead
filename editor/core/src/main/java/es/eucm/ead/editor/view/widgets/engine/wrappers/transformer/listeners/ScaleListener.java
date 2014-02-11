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
package es.eucm.ead.editor.view.widgets.engine.wrappers.transformer.listeners;

import com.badlogic.gdx.math.Vector2;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.Scale;
import es.eucm.ead.editor.view.widgets.engine.wrappers.transformer.SelectedOverlay;
import es.eucm.ead.engine.actors.SceneElementEngineObject;

public class ScaleListener extends DragListener {

	private float xMultiplier;

	private float yMultiplier;

	private Vector2 start = new Vector2();

	private Vector2 scale = new Vector2();

	private Vector2 size = new Vector2();

	private Vector2 origin = new Vector2();

	public ScaleListener(Controller controller,
			SelectedOverlay selectedOverlay, float xMultiplier,
			float yMultiplier) {
		super(controller, selectedOverlay);
		this.xMultiplier = xMultiplier;
		this.yMultiplier = yMultiplier;
	}

	@Override
	public void readInitialsValues(SceneElementEngineObject actor) {
		start.set(actor.getX(), actor.getY());
		scale.set(actor.getScaleX(), actor.getScaleY());
		size.set(actor.getWidth(), actor.getHeight());
		origin.set(actor.getOriginX(), actor.getOriginY());
	}

	@Override
	public void process(boolean combine) {
		current.sub(touch);
		float scaleX = scale.x + xMultiplier * (current.x / size.x);
		float scaleY = scale.y + yMultiplier * (current.y / size.y);

		// actor.parentToLocalCoordinates(current);
		// float newX = xMultiplier < 0 ? start.x + current.x : start.x;
		// float newY = yMultiplier < 0 ? start.y + current.y : start.y;
		/*
		 * float newX = 0; if ( xMultiplier > 0){ newX = start.x + current.x *
		 * ((xMultiplier * origin.x) / size.x); } else if (xMultiplier < 0){
		 * newX = start.x + current.x * ((size.x + xMultiplier * origin.x) /
		 * size.x); }
		 * 
		 * float newY = start.y - current.y * yMultiplier * (size.y - origin.y)
		 * / size.y;
		 */
		float newX = start.x;
		float newY = start.y;
		controller.action(Scale.NAME, sceneElement.getTransformation(), scaleX,
				scaleY, newX, newY, combine);
	}
}
