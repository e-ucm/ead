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
package es.eucm.ead.engine.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import es.eucm.ead.engine.Engine;
import es.eucm.ead.engine.EngineObject;
import es.eucm.ead.schema.actions.Action;
import es.eucm.ead.schema.actors.SceneElement;

public abstract class AbstractActor<T> extends Group implements EngineObject<T> {

	protected T element;

	protected float accTime;

	public final void setSchema(T schemaObject) {
		this.element = schemaObject;
		initialize(schemaObject);
	}

	public T getSchema() {
		return element;
	}

	public void dispose() {
		Engine.factory.free(this);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		accTime += delta;
	}

	/**
	 * Adds an schema action to the actor. The action is automatically converted
	 * to an engine action
	 * 
	 * @param action
	 *            the action schema
	 */
	public void addAction(Action action) {
		addAction((com.badlogic.gdx.scenes.scene2d.Action) Engine.factory
				.getEngineObject(action));
	}

	/**
	 * @param sceneElement
	 *            the target scene element
	 * @return Returns the actor that wraps the given scene element
	 */
	public Actor getSceneElement(SceneElement sceneElement) {
		if (sceneElement == element) {
			return this;
		}
		for (Actor a : this.getChildren()) {
			if (a instanceof AbstractActor) {
				Actor actor = ((AbstractActor) a).getSceneElement(sceneElement);
				if (actor != null) {
					return actor;
				}
			}
		}
		return null;
	}
}
