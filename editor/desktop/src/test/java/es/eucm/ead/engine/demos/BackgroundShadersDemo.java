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
package es.eucm.ead.engine.demos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import es.eucm.ead.editor.demobuilder.EditorDemoBuilder;
import es.eucm.ead.schema.components.BackgroundShader;
import es.eucm.ead.schema.components.Shader;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.data.Color;
import es.eucm.ead.schema.data.shape.Rectangle;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.ShapeRenderer;

public class BackgroundShadersDemo extends EditorDemoBuilder {

	private String lastSceneId;

	public BackgroundShadersDemo() {
		super("backgroundShaders");
	}

	@Override
	public String getName() {
		return "BackgroundShaders";
	}

	@Override
	public String getDescription() {
		return "Background Shaders displayed for testing";
	}

	@Override
	protected void doBuild() {
		ShaderProgram.pedantic = false;
		game(800, 600);

		ModelEntity initialScene = scene(1);
		for (int i = 2; i < 34; ++i) {
			scene(i);
		}
		String lastSceneId = getLastSceneId();
		if (lastSceneId != null) {
			entity(initialScene, "back.png", 200, 0).touchBehavior(
					makeGoScene(lastSceneId, GoScene.Transition.SLIDE_RIGHT,
							1.0f, true));
		}
	}

	private ModelEntity scene(int i) {

		ModelEntity scene = scene(null).getLastEntity();

		ModelEntity background = new ModelEntity();

		ShapeRenderer shapeRenderer = new ShapeRenderer();
		Rectangle rectangle = new Rectangle();
		rectangle.setWidth(800);
		rectangle.setHeight(600);
		shapeRenderer.setShape(rectangle);

		background.getComponents().add(shapeRenderer);

		BackgroundShader shader = new BackgroundShader();
		shader.setUri("test" + i + ".fragment");

		background.getComponents().add(shader);

		scene.getChildren().add(background);

		ModelEntity label = new ModelEntity();
		Label labelComponent = new Label();
		labelComponent.setText("" + i);
		label.getComponents().add(labelComponent);
		Color color = new Color();
		label.setColor(color);

		scene.getChildren().add(label);

		if (lastSceneId != null) {
			entity(scene, "back.png", 200, 0).touchBehavior(
					makeGoScene(lastSceneId, GoScene.Transition.SLIDE_RIGHT,
							1.0f, true));
		}
		lastSceneId = getLastSceneId();

		return scene;
	}
}
