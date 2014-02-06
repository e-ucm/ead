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
import es.eucm.ead.engine.mock.engineobjects.SceneElementMock;
import es.eucm.ead.engine.mock.schema.Empty;
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

		Empty timeEffect = new Empty();

		// Time behavior
		Behavior timeBehavior = new Behavior();
		timeBehavior.setTrigger(time);
		timeBehavior.setEffect(timeEffect);

		this.sceneElement.getBehaviors().add(timeBehavior);

		gameLoop.getSceneView().getCurrentScene().addActor(this.sceneElement);
		mockGame.act();

		SceneElementMock sceneElement = (SceneElementMock) gameLoop
				.getSceneElement(this.sceneElement);

		sceneElement.expectEffect(timeEffect);

		assertTrue(sceneElement.expectingEffect());
		mockGame.act();
		assertTrue(sceneElement.expectingEffect());
		mockGame.act();
		assertFalse(sceneElement.expectingEffect());

	}

	/**
	 * Helper method to test repeating time events.
	 * 
	 * @param deltas
	 *            between rings
	 * @param repeats
	 *            to set
	 * @param timeEffect
	 *            to trigger
	 */
	private SceneElementMock prepareActorWithTimer(int deltas, int repeats,
			Empty timeEffect) {
		Time time = new Time();
		time.setTime(Gdx.graphics.getDeltaTime() * deltas);
		time.setRepeat(repeats);

		// Time behavior
		Behavior timeBehavior = new Behavior();
		timeBehavior.setTrigger(time);
		timeBehavior.setEffect(timeEffect);

		sceneElement.getBehaviors().add(timeBehavior);

		gameLoop.getSceneView().getCurrentScene().addActor(sceneElement);
		mockGame.act();

		return (SceneElementMock) gameLoop.getSceneElement(sceneElement);
	}

	@Test
	public void testTimeRepeat100() {
		Empty timeEffect = new Empty();
		int deltas = 3;
		int repeats = 100;
		SceneElementMock sceneElement = prepareActorWithTimer(deltas, repeats,
				timeEffect);
		for (int i = 0; i < repeats; i++) {
			sceneElement.expectEffect(timeEffect);
		}
		for (int i = 1; i < deltas * repeats; i++) {
			assertTrue(sceneElement.expectingEffect());
			mockGame.act();
		}
		assertFalse(sceneElement.expectingEffect());
	}

	@Test
	public void testTimeRepeatOnce() {
		Empty timeEffect = new Empty();
		int deltas = 3;
		SceneElementMock sceneElement = prepareActorWithTimer(deltas, 1,
				timeEffect);
		sceneElement.expectEffect(timeEffect);
		for (int i = 1; i < deltas; i++) {
			assertTrue(sceneElement.expectingEffect());
			mockGame.act();
		}
		assertFalse(sceneElement.expectingEffect());
	}

	@Test
	public void testTimeRepeat0() {
		Empty timeEffect = new Empty();
		int deltas = 3;
		SceneElementMock sceneElement = prepareActorWithTimer(deltas, 0,
				timeEffect);
		sceneElement.expectEffect(timeEffect);
		for (int i = 1; i < deltas; i++) {
			assertTrue(sceneElement.expectingEffect());
			mockGame.act();
		}
		assertFalse(sceneElement.expectingEffect());
	}

	@Test
	public void testTimeRepeatForever() {
		Empty timeEffect = new Empty();
		int deltas = 3;
		int lots = 100; // arbitrarily high
		// passing -1 as "forever"
		SceneElementMock sceneElement = prepareActorWithTimer(deltas, -1,
				timeEffect);
		for (int i = 0; i < lots; i++) {
			sceneElement.expectEffect(timeEffect);
		}
		sceneElement.expectEffect(timeEffect);
		for (int i = 0; i < deltas * (lots - 1); i++) {
			assertTrue(sceneElement.expectingEffect());
			mockGame.act();
		}

		assertTrue(sceneElement.expectingEffect());
	}
}
