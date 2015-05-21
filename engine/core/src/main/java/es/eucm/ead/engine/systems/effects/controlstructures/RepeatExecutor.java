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
package es.eucm.ead.engine.systems.effects.controlstructures;

import com.badlogic.ashley.core.Entity;

import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.effects.controlstructures.Repeat;

/**
 * Created by Javier Torrente on 16/06/14.
 */
public class RepeatExecutor extends ControlStructureExecutor<Repeat> {

	public RepeatExecutor(EffectsSystem effectsSystem,
			VariablesManager variablesManager) {
		super(effectsSystem, variablesManager);
	}

	@Override
	public void execute(Entity target, Repeat effect) {
		// Calculate condition
		String condition = "(lt $" + effect.getCounter() + " i"
				+ effect.getTimes() + ")";
		String increment = "(+ $" + effect.getCounter() + " i1)";
		variablesManager.setVarToExpression(effect.getCounter(), "i0");
		while (variablesManager.evaluateCondition(condition, false)) {
			effectsSystem.execute(effect.getEffects());
			variablesManager.setVarToExpression(effect.getCounter(), increment);
		}
	}
}
