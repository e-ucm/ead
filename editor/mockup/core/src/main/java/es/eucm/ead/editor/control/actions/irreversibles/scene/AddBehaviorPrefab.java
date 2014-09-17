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
package es.eucm.ead.editor.control.actions.irreversibles.scene;

import es.eucm.ead.editor.control.ComponentId;
import es.eucm.ead.editor.control.actions.irreversibles.IrreversibleAction;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * 
 * Adds a {@link Behavior}
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Behavior}</em> to add</dd>
 * <dd><strong>args[1]</strong> <em>{@link ComponentId}</em> of the
 * {@link Behavior}</dd>
 * </dl>
 */
public class AddBehaviorPrefab extends IrreversibleAction {

	public AddBehaviorPrefab() {
		super(ResourceCategory.SCENE, true, false, Behavior.class, String.class);
	}

	@Override
	protected void action(ModelEntity entity, Object[] args) {

		Behavior behavior = (Behavior) args[0];
		behavior.setEvent(new Touch());
		behavior.setId((String) args[1]);

		entity.getComponents().add(behavior);
	}

}
