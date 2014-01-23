/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.engine.triggers;

import com.badlogic.gdx.Gdx;
import es.eucm.ead.engine.Engine;
import es.eucm.ead.engine.mock.engineobjects.MockActor;
import es.eucm.ead.engine.mock.schema.MockEmpty;
import es.eucm.ead.schema.behaviors.Behavior;
import es.eucm.ead.schema.behaviors.Time;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TimeTriggerTest extends TriggerTest {

	@Test
	public void testTime() {
		int deltas = 3;
		Time time = new Time();
		time.setTime(Gdx.graphics.getDeltaTime() * deltas);

		MockEmpty timeAction = new MockEmpty();

		// Time behavior
		Behavior timeBehavior = new Behavior();
		timeBehavior.setTrigger(time);
		timeBehavior.setAction(timeAction);

		sceneElement.getBehaviors().add(timeBehavior);

		Engine.sceneManager.loadSceneElement(sceneElement);
		mockGame.act();

		MockActor mockActor = (MockActor) Engine.sceneManager
				.getSceneElement(sceneElement);

		mockActor.expectAction(timeAction);

		mockGame.act();
		assertTrue(mockActor.expectingActions());
		mockGame.act();
		assertTrue(mockActor.expectingActions());
		mockGame.act();
		assertFalse(mockActor.expectingActions());

	}

	/**
	 * Helper method to test repeating time events.
	 * @param deltas between rings
	 * @param repeats to set
	 * @param timeAction to trigger
	 */
	private MockActor prepareActorWithTimer(int deltas, int repeats,
			MockEmpty timeAction) {
		Time time = new Time();
		time.setTime(Gdx.graphics.getDeltaTime() * deltas);
		time.setRepeat(repeats);

		// Time behavior
		Behavior timeBehavior = new Behavior();
		timeBehavior.setTrigger(time);
		timeBehavior.setAction(timeAction);

		sceneElement.getBehaviors().add(timeBehavior);

		Engine.sceneManager.loadSceneElement(sceneElement);
		mockGame.act();

		return (MockActor) Engine.sceneManager.getSceneElement(sceneElement);
	}

	@Test
	public void testTimeRepeat100() {
		MockEmpty timeAction = new MockEmpty();
		int deltas = 3;
		int repeats = 100;
		MockActor mockActor = prepareActorWithTimer(deltas, repeats, timeAction);
		for (int i = 0; i < repeats; i++) {
			mockActor.expectAction(timeAction);
		}
		for (int i = 0; i < deltas * repeats; i++) {
			assertTrue(mockActor.expectingActions());
			mockGame.act();
		}
		assertFalse(mockActor.expectingActions());
	}

	@Test
	public void testTimeRepeatOnce() {
		MockEmpty timeAction = new MockEmpty();
		int deltas = 3;
		MockActor mockActor = prepareActorWithTimer(deltas, 1, timeAction);
		mockActor.expectAction(timeAction);
		for (int i = 0; i < deltas; i++) {
			assertTrue(mockActor.expectingActions());
			mockGame.act();
		}
		assertFalse(mockActor.expectingActions());
	}

	@Test
	public void testTimeRepeat0() {
		MockEmpty timeAction = new MockEmpty();
		int deltas = 3;
		MockActor mockActor = prepareActorWithTimer(deltas, 0, timeAction);
		mockActor.expectAction(timeAction);
		for (int i = 0; i < deltas; i++) {
			assertTrue(mockActor.expectingActions());
			mockGame.act();
		}
		assertFalse(mockActor.expectingActions());
	}

	@Test
	public void testTimeRepeatForever() {
		MockEmpty timeAction = new MockEmpty();
		int deltas = 3;
		int lots = 100; // arbitrarily high
		// passing -1 as "forever"
		MockActor mockActor = prepareActorWithTimer(deltas, -1, timeAction);
		for (int i = 0; i < lots; i++) {
			mockActor.expectAction(timeAction);
		}
		mockActor.expectAction(timeAction);
		for (int i = 0; i < deltas * (lots - 1); i++) {
			assertTrue(mockActor.expectingActions());
			mockGame.act();
		}

		assertTrue(mockActor.expectingActions());
	}
}
