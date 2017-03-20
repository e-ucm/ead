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
package es.eucm.ead.engine;

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by jtorrente on 03/12/2015.
 */
public class EngineStage extends Stage {

	private Actor target;
	private EventListener listener;
	private Actor listenerActor;
	private int pointer;
	private int button;

	public EngineStage(Viewport viewport, PolygonSpriteBatch batch) {
		super(viewport, batch);
	}

	@Override
	public void addTouchFocus(EventListener listener, Actor listenerActor,
			Actor target, int pointer, int button) {
		super.addTouchFocus(listener, listenerActor, target, pointer, button);
		this.target = target;
		this.listener = listener;
		this.listenerActor = listenerActor;
		this.pointer = pointer;
		this.button = button;
	}

	public void updateTarget(Actor oldTarget, Actor newTarget) {
		if (target != null && oldTarget == target) {
			removeTouchFocus(listener, listenerActor, target, pointer, button);
			addTouchFocus(listener, listenerActor, newTarget, pointer, button);
		}
	}
}
