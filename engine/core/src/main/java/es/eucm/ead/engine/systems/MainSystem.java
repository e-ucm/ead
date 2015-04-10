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
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.engine.variables.VarsContext;

/**
 * A system in which variables can get initialized and updated every frame if
 * needed.
 */
public class MainSystem extends EntitySystem {

	/**
	 * Counts in seconds the elapsed time from the moment we've started playing
	 */
	public static final String TIME = VarsContext.RESERVED_VAR_PREFIX + "time";

	/**
	 * The width in pixels of the display surface
	 */
	public static final String FRAME_WIDTH = VarsContext.RESERVED_VAR_PREFIX
			+ "width";

	/**
	 * The height in pixels of the display surface
	 */
	public static final String FRAME_HEIGHT = VarsContext.RESERVED_VAR_PREFIX
			+ "height";

	public VariablesManager variablesManager;

	public MainSystem(VariablesManager variablesManager) {
		this.variablesManager = variablesManager;
		variablesManager.registerVar(TIME, 0f, true);
		variablesManager
				.registerVar(FRAME_WIDTH, Gdx.graphics.getWidth(), true);
		variablesManager.registerVar(FRAME_HEIGHT, Gdx.graphics.getHeight(),
				true);
	}

	@Override
	public void update(float delta) {
		variablesManager.setValue(FRAME_WIDTH, Gdx.graphics.getWidth(), true);
		variablesManager.setValue(FRAME_HEIGHT, Gdx.graphics.getHeight(), true);

		Float value = (Float) variablesManager.getValue(TIME);
		variablesManager.setValue(TIME, value + delta, true);
	}
}
