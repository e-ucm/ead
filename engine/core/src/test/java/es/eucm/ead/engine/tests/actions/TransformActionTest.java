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
package es.eucm.ead.engine.tests.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.ead.engine.mock.MockGame;
import es.eucm.ead.schema.actions.Transform;
import es.eucm.ead.schema.components.Color;
import es.eucm.ead.schema.components.Transformation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransformActionTest {

	private static MockGame mockGame;

	@BeforeClass
	public static void setUp() {
		mockGame = new MockGame();
	}

	@Test
	public void testInstant() {
		Transformation transformation = new Transformation();
		transformation.setX(10);
		transformation.setY(10);
		transformation.setScaleY(2);
		transformation.setScaleX(2);
		transformation.setRotation(60);
		Color color = new Color();
		color.setR(0);
		color.setG(0);
		color.setB(0);
		color.setA(0);
		transformation.setColor(color);

		Transform action = new Transform();
		action.setTransformation(transformation);
		// Instant
		action.setDuration(0.0f);

		mockGame.addActionToDummyActor(action);
		mockGame.act();

		Actor actor = mockGame.getDummyActor();
		assertEquals((int) actor.getX(), 10);
		assertEquals((int) actor.getY(), 10);
		assertEquals((int) actor.getScaleX(), 2);
		assertEquals((int) actor.getScaleY(), 2);
		assertEquals((int) actor.getRotation(), 60);
		assertEquals((int) actor.getColor().r, 0);
		assertEquals((int) actor.getColor().g, 0);
		assertEquals((int) actor.getColor().b, 0);
		assertEquals((int) actor.getColor().a, 0);
	}

	@Test
	public void testRelative() {
		mockGame.resetDummyActor();
		// Dummy is in (0,0) with 0x0, 0º and #FFFFFFFF
		// Relative transformation (10,10) with 2x2, 60º and #00000000
		Transformation transformation = new Transformation();
		transformation.setX(10);
		transformation.setY(10);
		transformation.setScaleY(2);
		transformation.setScaleX(2);
		transformation.setRotation(60);
		Color color = new Color();
		color.setR(0);
		color.setG(0);
		color.setB(0);
		color.setA(0);
		transformation.setColor(color);

		Transform action = new Transform();
		action.setTransformation(transformation);
		// Instant
		action.setDuration(0.0f);
		// Relative
		action.setRelative(true);

		mockGame.addActionToDummyActor(action);
		mockGame.act();

		Actor actor = mockGame.getDummyActor();
		assertEquals((int) actor.getX(), 10); // 0 + 10
		assertEquals((int) actor.getY(), 10); // 0 + 10
		assertEquals((int) actor.getScaleX(), 3); // 1 + 2
		assertEquals((int) actor.getScaleY(), 3); // 1 + 2
		assertEquals((int) actor.getRotation(), 60); // 0 + 60
		assertEquals((int) actor.getColor().r, 1); // 1 + 0
		assertEquals((int) actor.getColor().g, 1); // 1 + 0
		assertEquals((int) actor.getColor().b, 1); // 1 + 0
		assertEquals((int) actor.getColor().a, 1); // 1 + 0

		// Test clamp in color
		color.setR(-2);
		color.setG(-2);
		color.setB(-2);
		color.setA(-2);
		mockGame.addActionToDummyActor(action);
		mockGame.act();
		assertEquals((int) actor.getColor().r, 0); // clamp(1 - 2)
		assertEquals((int) actor.getColor().g, 0); // clamp(1 - 2)
		assertEquals((int) actor.getColor().b, 0); // clamp(1 - 2)
		assertEquals((int) actor.getColor().a, 0); // clamp(1 - 2)

		// Test other relative attributes
		assertEquals((int) actor.getX(), 20); // 0 + 10 x 2
		assertEquals((int) actor.getY(), 20); // 0 + 10 x 2
		assertEquals((int) actor.getScaleX(), 5); // 1 + 2 x 2
		assertEquals((int) actor.getScaleY(), 5); // 1 + 2 x 2
		assertEquals((int) actor.getRotation(), 120); // 0 + 60 x 2
	}

	@Test
	public void testTimed() {
		mockGame.resetDummyActor();

		Transformation transformation = new Transformation();
		transformation.setX(10);
		transformation.setY(10);
		transformation.setScaleY(2);
		transformation.setScaleX(2);
		transformation.setRotation(60);
		Color color = new Color();
		color.setR(-0.5f);
		color.setG(-0.5f);
		color.setB(-0.5f);
		color.setA(-0.5f);
		transformation.setColor(color);

		Transform action = new Transform();
		action.setTransformation(transformation);
		// Action converted in 2 ticks
		action.setDuration(Gdx.graphics.getDeltaTime() * 2);
		action.setRelative(true);

		mockGame.addActionToDummyActor(action);
		mockGame.act();

		Actor actor = mockGame.getDummyActor();
		assertEquals((int) actor.getX(), 5);
		assertEquals((int) actor.getY(), 5);
		// Multiply by 10-100 to compare integers, which is safer than floats or
		// doubles
		assertEquals((int) (actor.getScaleX() * 10), 20);
		assertEquals((int) (actor.getScaleY() * 10), 20);
		assertEquals((int) actor.getRotation(), 30);
		assertEquals((int) (actor.getColor().r * 100), 75);
		assertEquals((int) (actor.getColor().g * 100), 75);
		assertEquals((int) (actor.getColor().b * 100), 75);
		assertEquals((int) (actor.getColor().a * 100), 75);

		mockGame.act();

		assertEquals((int) actor.getX(), 10);
		assertEquals((int) actor.getY(), 10);
		// Multiply by 10-100 to compare integers, which is safer than floats or
		// doubles
		assertEquals((int) (actor.getScaleX() * 10), 30);
		assertEquals((int) (actor.getScaleY() * 10), 30);
		assertEquals((int) actor.getRotation(), 60);
		assertEquals((int) (actor.getColor().r * 100), 50);
		assertEquals((int) (actor.getColor().g * 100), 50);
		assertEquals((int) (actor.getColor().b * 100), 50);
		assertEquals((int) (actor.getColor().a * 100), 50);
	}

	@AfterClass
	public static void tearDown() {
		Gdx.app.exit();
	}
}
