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
package es.eucm.ead.engine.actions;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.DelegateAction;
import es.eucm.ead.engine.Engine;
import es.eucm.ead.engine.EngineObject;
import es.eucm.ead.schema.actions.Action;
import es.eucm.ead.schema.behaviors.Trigger;

public abstract class AbstractAction<T extends Action> extends DelegateAction
		implements EngineObject<T> {

	protected T schema;

	private Trigger trigger;

	@Override
	public void setSchema(T schemaObject) {
		this.schema = schemaObject;
	}

	@Override
	public void setActor(Actor actor) {
		super.setActor(actor);
		if (actor == null) {
			dispose();
		} else {
			initialize(schema);
		}
	}

	@Override
	public T getSchema() {
		return schema;
	}

	/**
	 * 
	 * @return The trigger that originated the action. It could be
	 *         {@literal null}
	 */
	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public void dispose() {
		Engine.factory.free(this);
		trigger = null;
		schema = null;
		super.setActor(null);
	}
}
