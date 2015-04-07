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

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import es.eucm.ead.engine.components.ShaderComponent;
import es.eucm.ead.engine.components.assets.SoundComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.processors.BackgroundShaderProcessor;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.engine.variables.VarsContext;
import es.eucm.ead.schema.assets.Sound;

/**
 * A system that sets up variables used by the shaders.
 */
public class ShaderSystem extends IteratingSystem {

	public static final String TIME = VarsContext.RESERVED_VAR_PREFIX + "time";
	public static final String WIDTH = VarsContext.RESERVED_VAR_PREFIX
			+ "width";
	public static final String HEIGHT = VarsContext.RESERVED_VAR_PREFIX
			+ "height";

	private boolean initialized = false;

	public VariablesManager variablesManager;

	public ShaderSystem(VariablesManager variablesManager) {
		super(Family.all(ShaderComponent.class).get());
		this.variablesManager = variablesManager;
	}

	private void checkInitialization() {
		if (!initialized) {
			variablesManager.registerVar(WIDTH,
					Float.valueOf(Gdx.graphics.getWidth()), true);
			variablesManager.registerVar(HEIGHT,
					Float.valueOf(Gdx.graphics.getHeight()), true);
			variablesManager.registerVar(TIME, 0f, true);
			initialized = true;
		}
	}

	@Override
	public void processEntity(Entity entity, float delta) {
		checkInitialization();

		Float value = (Float) variablesManager.getValue(TIME);
		variablesManager.setValue(TIME, value + delta, true);
	}
}
