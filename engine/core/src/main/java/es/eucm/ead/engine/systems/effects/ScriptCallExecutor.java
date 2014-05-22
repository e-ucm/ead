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

import ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.effects.ScriptCall;

/**
 * Created by Javier Torrente on 22/05/14.
 */
public class ScriptCallExecutor extends EffectExecutor<ScriptCall> {

	// To trigger the effects
	private EffectsSystem effectsSystem;
	// To parse expressions
	private VariablesManager variablesManager;

	public ScriptCallExecutor(EffectsSystem effectsSystem,
			VariablesManager variablesManager) {
		this.effectsSystem = effectsSystem;
		this.variablesManager = variablesManager;
	}

	@Override
	public void execute(Entity target, ScriptCall effect) {
		pushInputArguments(effect);
		effectsSystem
				.executeEffectList(target, effect.getScript().getEffects());
		popInputArguments();

	}

	private void pushInputArguments(ScriptCall effect) {
		// Create local context with input arguments
		if (effect.getInputArgumentValues().size() != effect.getScript()
				.getInputArguments().size()) {
			Gdx.app.debug("ScriptCallExecutor",
					"The number of arguments passed ("
							+ effect.getInputArgumentValues().size()
							+ ") does not match the expected ("
							+ effect.getScript().getInputArguments().size()
							+ ") for this script ");
		}

		variablesManager.push().registerVariables(
				effect.getScript().getInputArguments());
		for (int i = 0; i < Math.min(effect.getScript().getInputArguments()
				.size(), effect.getInputArgumentValues().size()); i++) {
			variablesManager.setValue(effect.getScript().getInputArguments()
					.get(i).getName(), effect.getInputArgumentValues().get(i));
		}

	}

	private void popInputArguments() {
		variablesManager.pop();
	}

}
