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
package es.eucm.ead.engine.systems.effects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import es.eucm.ead.engine.systems.GleanerSystem;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.effects.ChangeVar;

/**
 * Executes {@link ChangeVar} effects
 */
public class ChangeVarExecutor extends EffectExecutor<ChangeVar> {

	private VariablesManager variablesManager;
	private GleanerSystem gleanerSystem;

	public ChangeVarExecutor(VariablesManager variablesManager,
			GleanerSystem gleanerSystem) {
		this.variablesManager = variablesManager;
		this.gleanerSystem = gleanerSystem;
	}

	@Override
	public void execute(Entity target, ChangeVar effect) {
		if (effect.getVariable() == null) {
			Gdx.app.debug("ChangeVarExecutor",
					"The name of the variable cannot be null. The effect will be skipped.");
			return;
		}

		variablesManager.setVarToExpression(effect.getVariable(),
				effect.getExpression(),
				effect.getContext() == ChangeVar.Context.GLOBAL);

		gleanerSystem.var(effect.getVariable(),
				variablesManager.getValue(effect.getVariable()));
	}
}
