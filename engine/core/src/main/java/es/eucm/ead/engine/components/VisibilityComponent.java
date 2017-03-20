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
package es.eucm.ead.engine.components;

import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.variables.VariablesManager;

/**
 * Engine equivalent for {@link es.eucm.ead.schema.components.Visibility}
 * Created by Javier Torrente on 17/04/14.
 */
public class VisibilityComponent extends ConditionedComponent {

	private VariablesManager variablesManager;

	public void setVariablesManager(VariablesManager variablesManager) {
		this.variablesManager = variablesManager;
	}

	/*
	 * Method init() is overriden to make sure that entity's visibility is
	 * updated BEFORE it is attached to the gameView. Otherwise a nasty
	 * "flickering" effect happens when visibility is false, as the entity is
	 * added and then hidden, leaving it visible for a fraction of a second
	 */
	public void init() {
		update();
	}

	/**
	 * Evaluates condition and applies its changes to the parent entity's group
	 */
	public void update() {
		if (parent instanceof EngineEntity) {
			boolean condition = variablesManager.evaluateCondition(
					getCondition(), true);
			// Change the visibility
			EngineEntity engineEntity = (EngineEntity) parent;
			engineEntity.getGroup().setVisible(condition);
		}
	}
}
