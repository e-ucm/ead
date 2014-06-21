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
package es.eucm.ead.engine.tests.systems.effects;

import ashley.core.Entity;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockFiles;
import es.eucm.ead.engine.mock.schema.MockEffect;
import es.eucm.ead.engine.mock.schema.MockEffect.MockEffectListener;
import es.eucm.ead.engine.mock.schema.MockEffectExecutor;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.systems.effects.controlstructures.DynamicEffectExecutor;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.engine.variables.VarsContext;
import es.eucm.ead.schema.effects.DynamicEffect;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.EffectField;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class DynamicEffectTest extends Entity implements MockEffectListener {

	private EffectsSystem effectsSystem;

	private VariablesManager variablesManager;

	private boolean executed;

	@BeforeClass
	public static void setUpClass() {
		MockApplication.initStatics();
	}

	@Before
	public void setUp() {
		variablesManager = new VariablesManager(null, null, null);
		effectsSystem = new EffectsSystem(new GameLoop(), variablesManager);
		GameAssets gameAssets = new GameAssets(new MockFiles());
		gameAssets.addClassTag("mockeffect", MockEffect.class);

		effectsSystem.registerEffectExecutor(DynamicEffect.class,
				new DynamicEffectExecutor(gameAssets, effectsSystem,
						variablesManager));
		effectsSystem.registerEffectExecutor(MockEffect.class,
				new MockEffectExecutor());
	}

	@Test
	public void testAddEffect() {
		DynamicEffect dynamicEffect = new DynamicEffect();
		dynamicEffect.setEffectAlias("mockeffect");

		EffectField effectField = new EffectField();
		effectField.setFieldName("effectListener");
		effectField.setExpression("$_this");

		dynamicEffect.getFields().add(effectField);

		variablesManager.registerVar(VarsContext.THIS_VAR, this, true);
		executed = false;

		effectsSystem.executeEffectList(Arrays.<Effect> asList(dynamicEffect));

		assertTrue(executed);
	}

	@Override
	public void executed() {
		executed = true;
	}
}
