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
package es.eucm.ead.engine.tests.systems.tweens;

import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.systems.tweens.tweencreators.MoveTweenCreator;
import es.eucm.ead.engine.systems.tweens.tweencreators.TweenCreator;
import es.eucm.ead.schema.components.tweens.MoveTween;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MoveTweenTest extends TweenTest {

	@Override
	public Class getTweenClass() {
		return MoveTween.class;
	}

	@Override
	public TweenCreator getTweenCreator() {
		return new MoveTweenCreator();
	}

	@Test
	public void testXYTween() {
		MoveTween moveTween = new MoveTween();
		moveTween.setDuration(1);
		moveTween.setX(10);
		moveTween.setY(20);

		EngineEntity entity = addEntityWithTweens(moveTween);

		gameLoop.update(0.5f);

		assertTrue("Entity x position is " + entity.getGroup().getX()
				+ ". Should be 5.0",
				Math.abs(entity.getGroup().getX() - 5.0f) < 0.0001f);

		assertTrue("Entity y position is " + entity.getGroup().getY()
				+ ". Should be 10.0",
				Math.abs(entity.getGroup().getY() - 10.0f) < 0.0001f);
		gameLoop.update(1.0f);

		assertTrue("Entity x position is " + entity.getGroup().getX()
				+ ". Should be 10.0",
				Math.abs(entity.getGroup().getX() - 10.0f) < 0.0001f);

		assertTrue("Entity y position is " + entity.getGroup().getY()
				+ ". Should be 20.0",
				Math.abs(entity.getGroup().getY() - 20.0f) < 0.0001f);

	}

	@Test
	public void testIndependentXYTween() {
		MoveTween moveTweenX = new MoveTween();
		moveTweenX.setDuration(2);
		moveTweenX.setRelative(true);
		moveTweenX.setX(2);

		MoveTween moveTweenY = new MoveTween();
		moveTweenY.setDuration(0.5f);
		moveTweenY.setRelative(true);
		moveTweenY.setY(2);

		EngineEntity entity = addEntityWithTweens(moveTweenX, moveTweenY);

		gameLoop.update(0.5f);
		assertTrue("Entity x position is " + entity.getGroup().getX()
				+ ". Should be 0.5",
				Math.abs(entity.getGroup().getX() - 0.5f) < 0.0001f);
		assertTrue("Entity y position is " + entity.getGroup().getY()
				+ ". Should be 2.0",
				Math.abs(entity.getGroup().getY() - 2.0f) < 0.0001f);

	}

	@Test
	public void testAbsuleVerticalTween() {
		MoveTween moveTween = new MoveTween();
		moveTween.setDuration(1);
		moveTween.setX(Float.NaN);
		moveTween.setY(20);

		EngineEntity entity = addEntityWithTweens(moveTween);
		float x = entity.getGroup().getX();

		gameLoop.update(0.5f);

		assertTrue("Entity x position is " + entity.getGroup().getX()
				+ ". Should be " + x, entity.getGroup().getX() == x);

		assertTrue("Entity y position is " + entity.getGroup().getY()
				+ ". Should be 10.0",
				Math.abs(entity.getGroup().getY() - 10.0f) < 0.0001f);
		gameLoop.update(1.0f);

		assertTrue("Entity x position is " + entity.getGroup().getX()
				+ ". Should be " + x, entity.getGroup().getX() == x);

		assertTrue("Entity y position is " + entity.getGroup().getY()
				+ ". Should be 20.0",
				Math.abs(entity.getGroup().getY() - 20.0f) < 0.0001f);

	}

}
