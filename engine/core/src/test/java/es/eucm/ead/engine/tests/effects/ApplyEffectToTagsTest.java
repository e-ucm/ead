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
package es.eucm.ead.engine.tests.effects;

import es.eucm.ead.schema.effects.ApplyEffectToTags;
import org.junit.Test;

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.engine.actors.SceneEngineObject;
import es.eucm.ead.engine.actors.SceneElementEngineObject;
import es.eucm.ead.engine.mock.MockGame;
import es.eucm.ead.schema.effects.Transform;
import es.eucm.ead.schema.components.Transformation;

import static org.junit.Assert.assertEquals;

public class ApplyEffectToTagsTest {

	@Test
	public void test() {

		float x = 10;
		float y = 10;
		float scaleX = 2;
		float scaleY = 2;
		float rotation = 60;

		MockGame game = new MockGame();
		// Load game
		game.act();
		game.getGameLoop().loadScene("tags");
		// Load scene
		game.act();

		ApplyEffectToTags applyEffectToTags = new ApplyEffectToTags();
		applyEffectToTags.getTags().add("tag3");

		Transformation transformation = new Transformation();
		transformation.setX(x);
		transformation.setY(y);
		transformation.setScaleX(scaleX);
		transformation.setScaleY(scaleY);
		transformation.setRotation(rotation);

		Transform transform = new Transform();
		transform.setTransformation(transformation);
		// Instant
		transform.setDuration(0.0f);

		applyEffectToTags.setEffect(transform);

		SceneEngineObject scene = game.getGameLoop().getSceneView()
				.getCurrentScene();

		scene.addEffect(applyEffectToTags);

		game.act();

		Array<SceneElementEngineObject> sceneElements = scene.findByTag("tag3");

		for (SceneElementEngineObject sceneElement : sceneElements) {
			assertEquals((int) sceneElement.getX(), (int) x);
			assertEquals((int) sceneElement.getY(), (int) y);
			assertEquals((int) sceneElement.getScaleX(), (int) scaleX);
			assertEquals((int) sceneElement.getScaleY(), (int) scaleY);
			assertEquals((int) sceneElement.getRotation(), (int) rotation);
		}

	}
}
