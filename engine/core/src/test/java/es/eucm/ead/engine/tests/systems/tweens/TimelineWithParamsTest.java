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

import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.systems.tweens.TweenSystem;
import es.eucm.ead.engine.systems.tweens.tweencreators.BaseTweenCreator;
import es.eucm.ead.engine.systems.tweens.tweencreators.MoveTweenCreator;
import es.eucm.ead.engine.systems.tweens.tweencreators.TimelineCreator;
import es.eucm.ead.schema.components.tweens.BaseTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.Timeline;
import es.eucm.ead.schema.components.tweens.Tween;
import es.eucm.ead.schema.data.Parameter;
import org.junit.Test;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests that tweens with nested tweens get all parameters executed as expected
 * Created by jtorrente on 23/10/2015.
 */
public class TimelineWithParamsTest extends TweenTest {
	@Override
	public Class getTweenClass() {
		return Timeline.class;
	}

	@Override
	public BaseTweenCreator getTweenCreator() {
		tweenSystem.registerBaseTweenCreator(MoveTween.class,
				new MoveTweenCreator());
		return new TimelineCreator(gameAssets, variablesManager,
				tweenSystem.getBaseTweenCreators());
	}

	@Test
	public void testSingleTweenParams() {
		MoveTween moveTween = new MoveTween();
		moveTween.setDuration(0);
		moveTween.setEaseType(Tween.EaseType.IN);
		moveTween.setRelative(false);
		moveTween.setYoyo(false);
		moveTween.setRepeat(1);
		addParam(moveTween, "x", "i10");
		addParam(moveTween, "y", "(+ i10 i20)");
		addParam(moveTween, "duration", "(* i2 i4)");
		addParam(moveTween, "yoyo", "btrue");
		addParam(moveTween, "easeType", "sINOUT");
		addParam(moveTween, "easeEquation", "i6");

		EngineEntity engineEntity = addEntityWithTweens(moveTween);

		try {
			Field tweenManagerField = TweenSystem.class
					.getDeclaredField("tweenManager");
			tweenManagerField.setAccessible(true);
			TweenManager tweenManager = (TweenManager) tweenManagerField
					.get(tweenSystem);
			gameLoop.update(0);
			aurelienribon.tweenengine.Tween baseTweenAur = (aurelienribon.tweenengine.Tween) tweenManager
					.getObjects().get(0);
			testProperty(TweenEquations.easeInOutSine, baseTweenAur.getEasing());
			testProperty(true, baseTweenAur.isYoyo());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		gameLoop.update(8);
		testProperty(10.0F, engineEntity.getGroup().getX());
		testProperty(30.0F, engineEntity.getGroup().getY());
	}

	private void testProperty(Object expected, Object real) {
		assertTrue(
				"Value is " + real.toString() + ". Should be "
						+ expected.toString(), real.equals(expected));
	}

	@Test
	public void testNestedTweensAndParams() {
		MoveTween moveTween1 = new MoveTween();
		MoveTween moveTween2 = new MoveTween();
		moveTween2.setDelay(1F);
		MoveTween moveTween3 = new MoveTween();
		moveTween3.setDelay(1F);
		addParam(moveTween1, "x", "i1");
		addParam(moveTween2, "x", "i2");
		addParam(moveTween3, "x", "i3");

		Timeline timeline = new Timeline();
		timeline.setMode(Timeline.Mode.SEQUENCE);
		timeline.getChildren().add(moveTween1);
		Timeline innerTimeline = new Timeline();
		innerTimeline.setMode(Timeline.Mode.SEQUENCE);
		innerTimeline.getChildren().add(moveTween2);
		innerTimeline.getChildren().add(moveTween3);
		timeline.getChildren().add(innerTimeline);

		EngineEntity engineEntity = addEntityWithTweens(timeline);
		gameLoop.update(1);
		testProperty(1F, engineEntity.getGroup().getX());
		gameLoop.update(1);
		testProperty(2F, engineEntity.getGroup().getX());
		gameLoop.update(1);
		testProperty(3F, engineEntity.getGroup().getX());
		gameLoop.update(1);
		testProperty(3F, engineEntity.getGroup().getX());
	}

	private void addParam(BaseTween baseTween, String name, String exp) {
		Parameter param = new Parameter();
		param.setName(name);
		param.setValue(exp);
		baseTween.getParameters().add(param);
	}
}
