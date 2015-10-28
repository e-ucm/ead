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
package es.eucm.ead.engine.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import es.eucm.ead.engine.variables.ReservedVariableNames;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.engine.variables.VarsContext;

/**
 * This system makes available to {@link VariablesManager} several internal
 * properties that are useful, like the time elapsed since the game was launched
 * ({@link ReservedVariableNames#TIME}), or the width and height of the display
 * surface ({@link ReservedVariableNames#FRAME_WIDTH},
 * {@link ReservedVariableNames#FRAME_HEIGHT})
 */
public class UpdateVarsEachCycleSystem extends EntitySystem {

	public VariablesManager variablesManager;

	public UpdateVarsEachCycleSystem(VariablesManager variablesManager) {
		this.variablesManager = variablesManager;
		variablesManager.registerVar(ReservedVariableNames.TIME, 0f, true);
		variablesManager.registerVar(ReservedVariableNames.FRAME_WIDTH,
				Gdx.graphics.getWidth(), true);
		variablesManager.registerVar(ReservedVariableNames.FRAME_HEIGHT,
				Gdx.graphics.getHeight(), true);
	}

	@Override
	public void update(float delta) {
		variablesManager.setValue(ReservedVariableNames.FRAME_WIDTH,
				Gdx.graphics.getWidth(), true);
		variablesManager.setValue(ReservedVariableNames.FRAME_HEIGHT,
				Gdx.graphics.getHeight(), true);

		Float value = (Float) variablesManager
				.getValue(ReservedVariableNames.TIME);
		variablesManager.setValue(ReservedVariableNames.TIME, value + delta,
				true);
	}
}
