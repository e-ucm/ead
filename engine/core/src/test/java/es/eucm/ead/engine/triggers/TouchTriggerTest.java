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

import es.eucm.ead.engine.Engine;
import es.eucm.ead.engine.mock.engineobjects.MockActor;
import es.eucm.ead.engine.mock.schema.MockEmpty;
import es.eucm.ead.schema.behaviors.Behavior;
import es.eucm.ead.schema.behaviors.Touch;
import es.eucm.ead.schema.behaviors.Touch.Type;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class TouchTriggerTest extends TriggerTest {

	@Test
	public void testPressRelease() {

		Touch press = new Touch();
		press.setType(Type.PRESS);

		Touch release = new Touch();
		release.setType(Type.RELEASE);

		MockEmpty pressAction = new MockEmpty();
		MockEmpty releaseAction = new MockEmpty();

		// Press behavior
		Behavior pressBehavior = new Behavior();
		pressBehavior.setTrigger(press);
		pressBehavior.setAction(pressAction);

		sceneElement.getBehaviors().add(pressBehavior);
		sceneElement.getBehaviors().add(pressBehavior);

		// Release behavior
		Behavior releaseBehavior = new Behavior();
		releaseBehavior.setTrigger(release);
		releaseBehavior.setAction(releaseAction);

		sceneElement.getBehaviors().add(releaseBehavior);

		Engine.gameController.loadSceneElement(sceneElement);
		mockGame.act();

		MockActor mockActor = (MockActor) Engine.gameController
				.getSceneElement(sceneElement);

		mockActor.expectAction(pressAction).expectAction(pressAction)
				.expectAction(releaseAction);

		mockGame.act();
		mockGame.press(20, 580);
		mockGame.release(20, 580);

		assertFalse(mockActor.expectingActions());

	}

}
