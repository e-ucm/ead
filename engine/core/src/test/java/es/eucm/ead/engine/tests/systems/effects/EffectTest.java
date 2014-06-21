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

import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.schema.MockEffect;
import es.eucm.ead.engine.mock.schema.MockEffectExecutor;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.variables.VarsContext;
import org.junit.Before;

import es.eucm.ead.engine.variables.VariablesManager;
import org.junit.BeforeClass;

public abstract class EffectTest {

	protected EffectsSystem effectsSystem;

	protected VariablesManager variablesManager;

	@BeforeClass
	public static void setUpClass() {
		MockApplication.initStatics();
	}

	@Before
	public void setUp() {
		variablesManager = new VariablesManager(null, null, null);
		GameLoop gameLoop = new GameLoop();
		effectsSystem = new EffectsSystem(gameLoop, variablesManager);
		effectsSystem.registerEffectExecutor(MockEffect.class,
				new MockEffectExecutor());
		variablesManager
				.setValue(VarsContext.THIS_VAR, gameLoop.createEntity());
	}
}
