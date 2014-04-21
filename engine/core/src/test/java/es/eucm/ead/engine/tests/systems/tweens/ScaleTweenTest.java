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

import es.eucm.ead.engine.entities.ActorEntity;
import es.eucm.ead.engine.systems.tweens.tweencreators.ScaleTweenCreator;
import es.eucm.ead.engine.systems.tweens.tweencreators.TweenCreator;
import es.eucm.ead.schema.components.tweens.ScaleTween;
import es.eucm.ead.schema.components.tweens.Tweens;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ScaleTweenTest extends TweenTest {

	@Override
	public Class getTweenClass() {
		return ScaleTween.class;
	}

	@Override
	public TweenCreator getTweenCreator() {
		return new ScaleTweenCreator();
	}

	@Test
	public void testXYTween() {
		ScaleTween scaleTween = new ScaleTween();
		scaleTween.setDuration(1);
		scaleTween.setScaleX(10);
		scaleTween.setScaleY(20);

		ActorEntity entity = addEntityWithTweens(scaleTween);
		entity.getGroup().setScale(0.0f, 0.0f);

		gameLoop.update(0.5f);

		assertTrue("Entity x scale is " + entity.getGroup().getScaleX()
				+ ". Should be 5.0",
				Math.abs(entity.getGroup().getScaleX() - 5.0f) < 0.0001f);

		assertTrue("Entity y scale is " + entity.getGroup().getScaleY()
				+ ". Should be 10.0",
				Math.abs(entity.getGroup().getScaleY() - 10.0f) < 0.0001f);
		gameLoop.update(1.0f);

		assertTrue("Entity x scale is " + entity.getGroup().getScaleX()
				+ ". Should be 10.0",
				Math.abs(entity.getGroup().getScaleX() - 10.0f) < 0.0001f);

		assertTrue("Entity y scale is " + entity.getGroup().getScaleY()
				+ ". Should be 20.0",
				Math.abs(entity.getGroup().getScaleY() - 20.0f) < 0.0001f);

	}

	@Test
	public void testIndependentXYTween() {
		Tweens tweens = new Tweens();
		ScaleTween scaleTweenX = new ScaleTween();
		scaleTweenX.setDuration(2);
		scaleTweenX.setRelative(true);
		scaleTweenX.setScaleX(2);

		ScaleTween scaleTweenY = new ScaleTween();
		scaleTweenY.setDuration(0.5f);
		scaleTweenY.setRelative(true);
		scaleTweenY.setScaleY(2);

		tweens.getTweens().add(scaleTweenX);
		tweens.getTweens().add(scaleTweenY);

		addEntityWithTweens(tweens);

		ActorEntity entity = addEntityWithTweens(tweens);
		entity.getGroup().setScale(0.0f, 0.0f);

		gameLoop.update(0.5f);
		assertTrue("Entity x scale is " + entity.getGroup().getScaleX()
				+ ". Should be 0.5",
				Math.abs(entity.getGroup().getScaleX() - 0.5f) < 0.0001f);
		assertTrue("Entity y scale is " + entity.getGroup().getScaleY()
				+ ". Should be 2.0",
				Math.abs(entity.getGroup().getScaleY() - 2.0f) < 0.0001f);

	}
}
