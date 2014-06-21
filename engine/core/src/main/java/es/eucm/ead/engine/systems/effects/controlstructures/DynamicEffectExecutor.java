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

import ashley.core.Entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.systems.effects.EffectExecutor;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.effects.DynamicEffect;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.EffectField;

public class DynamicEffectExecutor extends EffectExecutor<DynamicEffect> {

	private GameAssets gameAssets;

	private VariablesManager variablesManager;

	private EffectsSystem effectsSystem;

	private Array<Effect> auxEffects = new Array<Effect>();

	public DynamicEffectExecutor(GameAssets gameAssets,
			EffectsSystem effectsSystem, VariablesManager variablesManager) {
		this.gameAssets = gameAssets;
		this.effectsSystem = effectsSystem;
		this.variablesManager = variablesManager;
	}

	@Override
	public void execute(Entity target, DynamicEffect effect) {
		Class effectClass = gameAssets.getClass(effect.getEffectAlias());
		try {
			Effect addedEffect = (Effect) ClassReflection
					.newInstance(effectClass);
			for (EffectField effectField : effect.getFields()) {
				Field field = ClassReflection.getDeclaredField(effectClass,
						effectField.getFieldName());
				Object fieldValue = variablesManager
						.evaluateExpression(effectField.getExpression());
				field.setAccessible(true);
				field.set(addedEffect, fieldValue);
			}
			auxEffects.clear();
			auxEffects.add(addedEffect);
			effectsSystem.executeEffectList(auxEffects);
		} catch (ReflectionException e) {
			Gdx.app.error("DynamicEffectExecutor",
					"Impossible to create effect " + effect.getEffectAlias());
		}
	}
}
