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
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.mock.schema.MockEffect;
import es.eucm.ead.engine.mock.schema.MockEffect.MockEffectListener;
import es.eucm.ead.engine.variables.VarsContext;
import es.eucm.ead.schema.data.Parameter;
import es.eucm.ead.schema.effects.Effect;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class EffectWithParametersTest extends EffectTest {

	private boolean executed;

	@Test
	public void testAddEffect() {
		MockEffect dynamicEffect = new MockEffect();

		Parameter parameter = new Parameter();
		parameter.setName("effectListener");
		parameter.setValue("$_this");

		dynamicEffect.getParameters().add(parameter);

		variablesManager.registerVar(VarsContext.THIS_VAR,
				new ListenerEngineEntity(gameLoop), true);
		executed = false;

		effectsSystem.execute(Arrays.<Effect> asList(dynamicEffect));

		assertTrue(executed);
	}

	public class ListenerEngineEntity extends EngineEntity implements
			MockEffectListener {

		public ListenerEngineEntity(GameLoop gameLoop) {
			super(gameLoop);
		}

		@Override
		public void executed() {
			executed = true;
		}

	}
}
