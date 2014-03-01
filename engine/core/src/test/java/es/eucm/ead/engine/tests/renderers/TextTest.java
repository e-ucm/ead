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
package es.eucm.ead.engine.tests.renderers;

import com.badlogic.gdx.graphics.Color;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.actors.SceneElementEngineObject;
import es.eucm.ead.engine.mock.MockGame;
import es.eucm.ead.engine.renderers.TextEngineObject;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.renderers.Text;
import es.eucm.ead.schema.renderers.TextStyle;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertTrue;

/**
 * Created by Javier Torrente on 24/02/14.
 */
public class TextTest {

	private MockGame mockGame;

	private GameLoop gameLoop;

	@Before
	public void setUp() {
		mockGame = new MockGame();
		gameLoop = mockGame.getGameLoop();
	}

	@Test
	public void testDefaultTextWithoutAnyStyle() {
		Text text1 = new Text();
		text1.setText("text1");
		testText(text1, 1.0F, Color.WHITE);

	}

	@Test
	public void testEmbeddedStyleOnly() {
		Text text1 = new Text();
		text1.setText("text1");

		TextStyle embedded = new TextStyle();
		es.eucm.ead.schema.components.Color color = new es.eucm.ead.schema.components.Color();
		color.setG(0.5F);
		color.setR(0.5F);
		color.setB(0.5F);
		color.setA(0.5F);
		embedded.setColor(color);
		embedded.setScale(2);
		text1.setStyle(embedded);

		testText(text1, 2.0F, new Color(0.5F, 0.5F, 0.5F, 0.5F));

	}

	@Test
	public void testRefStyleOnly() {
		String styleRefPath = "textstyles/teststyle.json";
		Scene sceneContainingText = gameLoop.getAssets().fromJsonPath(
				Scene.class, "testgame/scenes/texttestscene.json");

		Text text1 = (Text) sceneContainingText.getChildren().get(0)
				.getRenderer();
		// text1.setText("text1");
		// text1.setStyleref(styleRefPath);
		// gameLoop.getAssets().addDependency(styleRefPath, TextStyle.class);

		testText(text1, 0.1F, new Color(0.25F, 0.25F, 0.25F, 0.25F));

	}

	@Test
	public void testMissingRefStyle() {
		Text text1 = new Text();
		text1.setText("text1");
		// This file does not exist
		text1.setStyleref("textstyles/teststyle_doesnotexist.json");
		// Default values should be set up
		testText(text1, 1.0F, Color.WHITE);

	}

	@Test
	public void testEmbeddedAndRefStyles() {
		Text text1 = new Text();
		text1.setText("text1");

		TextStyle embedded = new TextStyle();
		es.eucm.ead.schema.components.Color color = new es.eucm.ead.schema.components.Color();
		color.setG(0.5F);
		color.setR(0.5F);
		color.setB(0.5F);
		color.setA(0.5F);
		embedded.setColor(color);
		embedded.setScale(2);
		text1.setStyle(embedded);

		text1.setStyleref("textstyles/teststyle.json");

		// If both embedded and ref styles are present, then the embedded should
		// be used
		testText(text1, 2.0F, new Color(0.5F, 0.5F, 0.5F, 0.5F));
	}

	@Test
	public void testIncompleteStyle() {
		Text text1 = new Text();
		text1.setText("text1");

		TextStyle embedded = new TextStyle();
		embedded.setScale(2);
		text1.setStyle(embedded);

		testText(text1, 2.0F, Color.WHITE);

	}

	/**
	 * Creates a text renderer with the given text and adds it to the scene.
	 * Then it retrieves the TextEngineObject created and checks its color and
	 * scale match those given as params
	 * 
	 * @param textToApply
	 *            The text object to be tested
	 * @param expectedScale
	 *            The scale TextEngineObject should have
	 * @param expectedColor
	 *            The color TextEngineObject should have
	 */
	private void testText(Text textToApply, float expectedScale,
			Color expectedColor) {
		// Load scene
		mockGame.act();
		// Creates a scene element.
		SceneElement sceneElement = new SceneElement();
		sceneElement.setRenderer(textToApply);
		// Adds sceneElement to the game and retrieves the reference to
		// SceneElementActor
		gameLoop.getGameView().getCurrentScene().addActor(sceneElement);
		mockGame.act();

		SceneElementEngineObject sceneElementActor = ((SceneElementEngineObject) (gameLoop
				.getSceneElement(sceneElement)));
		TextEngineObject textRenderer = (TextEngineObject) sceneElementActor
				.getRenderer();

		for (Field field : textRenderer.getClass().getDeclaredFields()) {

			field.setAccessible(true); // You might want to set modifier to
										// public first.
			Object value = null;
			try {
				value = field.get(textRenderer);
				if (field.getName().toLowerCase().equals("scale")) {
					float scale = ((Float) value).floatValue();
					assertTrue(scale == expectedScale);
				} else if (field.getName().toLowerCase().equals("color")) {
					Color color = ((Color) value);
					assertTrue(EqualsBuilder.reflectionEquals(color,
							expectedColor));
				}

			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}
