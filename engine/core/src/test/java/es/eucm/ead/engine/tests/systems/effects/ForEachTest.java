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

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.mock.schema.MockEffect;
import es.eucm.ead.engine.mock.schema.MockEffect.MockEffectListener;
import es.eucm.ead.engine.systems.effects.controlstructures.ForEachExecutor;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.controlstructures.ForEach;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ForEachTest extends EffectTest implements MockEffectListener {

	private int result;

	@Test
	public void testForEach() {
		Array<Object> list = new Array<Object>();
		for (int i = 0; i < 10; i++) {
			list.add(3);
		}

		variablesManager.setValue("list", list);

		ForEach forEach = new ForEach();
		forEach.setIteratorVar("i");
		forEach.setListExpression("$list");

		MockEffect effect = new MockEffect(this);
		forEach.getEffects().add(effect);

		effectsSystem.registerEffectExecutor(ForEach.class,
				new ForEachExecutor(effectsSystem, variablesManager));

		result = 0;
		effectsSystem.executeEffectList(Arrays.<Effect> asList(forEach));

		assertEquals(30, result);
	}

	@Override
	public void executed() {
		Integer value = (Integer) variablesManager.getValue("i");
		result += value;
	}
}
