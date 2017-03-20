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

import es.eucm.ead.engine.demobuilder.ExecutableDemoBuilder;
import es.eucm.ead.schema.components.Shader;
import es.eucm.ead.schema.entities.ModelEntity;

public class ShadersDemo extends ExecutableDemoBuilder {

	public ShadersDemo() {
		super("shaders");
	}

	@Override
	public String getName() {
		return "Shaders";
	}

	@Override
	public String getDescription() {
		return "Shaders in action.";
	}

	@Override
	protected void doBuild() {
		ModelEntity scene = singleSceneGame(null, 800, 600).getLastScene();

		ModelEntity rectangle = new ModelEntity();
		rectangle.getComponents().add(makeRectangle(400, 300));
		scene.getChildren().add(rectangle);

		Shader shader = new Shader();
		shader.setUri("gradient.fragment");

		shader.getUniforms().add(makeParameter("point1", "f0,f0"));
		shader.getUniforms().add(makeParameter("point2", "f1,f1"));
		shader.getUniforms().add(makeParameter("color1", "f1,f1,f1,f1"));
		shader.getUniforms().add(makeParameter("color2", "f0,f0,f0,f1"));

		rectangle.getComponents().add(shader);

		rectangle = new ModelEntity();
		rectangle.getComponents().add(makeRectangle(400, 300));
		rectangle.setX(400);

		shader = new Shader();
		shader.setUri("gradient.fragment");

		shader.getUniforms().add(makeParameter("point1", "f0,f0"));
		shader.getUniforms().add(makeParameter("point2", "f1,f1"));
		shader.getUniforms().add(makeParameter("color1", "f1,f1,f1,f1"));
		shader.getUniforms().add(makeParameter("color2", "f0,f0,f0,f1"));

		rectangle.getComponents().add(shader);

		scene.getChildren().add(rectangle);

		rectangle = new ModelEntity();
		rectangle.getComponents().add(makeRectangle(400, 300));
		rectangle.setY(300);

		shader = new Shader();
		shader.setUri("gradient.fragment");

		shader.getUniforms().add(makeParameter("point1", "f0.25,f0.25"));
		shader.getUniforms().add(makeParameter("point2", "f0.75,f0.75"));
		shader.getUniforms().add(makeParameter("color1", "f1,f0,f0,f1"));
		shader.getUniforms().add(makeParameter("color2", "f0,f1,f0,f1"));

		rectangle.getComponents().add(shader);

		scene.getChildren().add(rectangle);

		rectangle = new ModelEntity();
		rectangle.getComponents().add(makeRectangle(400, 300));
		rectangle.setX(400);
		rectangle.setY(300);

		shader = new Shader();
		shader.setUri("gradient.fragment");

		shader.getUniforms().add(makeParameter("point1", "f0,f0"));
		shader.getUniforms().add(makeParameter("point2", "f0,f1"));
		shader.getUniforms().add(makeParameter("color1", "f0,f1,f1,f1"));
		shader.getUniforms().add(makeParameter("color2", "f1,f0,f1,f1"));

		rectangle.getComponents().add(shader);

		scene.getChildren().add(rectangle);
	}
}
