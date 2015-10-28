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
package es.eucm.ead.editor.control.actions.model;

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.ListCommand.AddToListCommand;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.engine.variables.ReservedVariableNames;
import es.eucm.ead.schema.components.Background;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.Shader;
import es.eucm.ead.schema.data.Parameter;
import es.eucm.ead.schema.data.shape.Rectangle;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.ShapeRenderer;

/**
 * <p>
 * Changes the scene's background (shader) to a new one, if there is a previous
 * background (if any) it replaces it with the new one.
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>String</em> path to the shader that will be
 * used as the scene background, if null no background will be added but the
 * previous background (is any) will be removed.</dd>
 * </dl>
 */
public class ChangeSceneBackgroundShader extends ModelAction {

	public ChangeSceneBackgroundShader() {
		super(true, true, String.class);
	}

	@Override
	public CompositeCommand perform(Object... args) {
		ModelEntity scene = (ModelEntity) controller.getModel().getSelection()
				.getSingle(Selection.SCENE);
		Array<ModelComponent> sceneComponents = scene.getComponents();
		CompositeCommand compositeCommand = new CompositeCommand();

		for (int i = 0; i < sceneComponents.size; i++) {
			ModelComponent sceneComponent = sceneComponents.get(i);
			if (sceneComponent instanceof Background) {
				sceneComponents.removeIndex(i);
			}
		}

		String shaderUri = (String) args[0];
		if (shaderUri != null) {
			ModelEntity entityBackground = new ModelEntity();

			GameData gameData = Q.getComponent(controller.getModel().getGame(),
					GameData.class);
			ShapeRenderer shapeRenderer = new ShapeRenderer();
			Rectangle rectangle = new Rectangle();
			rectangle.setWidth(gameData.getWidth());
			rectangle.setHeight(gameData.getHeight());
			shapeRenderer.setShape(rectangle);

			entityBackground.getComponents().add(shapeRenderer);

			Shader shader = new Shader();
			shader.setUri(shaderUri);
			shader.getUniforms().add(
					param("time", "$" + ReservedVariableNames.TIME));
			shader.getUniforms().add(
					param("resolution", "$" + ReservedVariableNames.FRAME_WIDTH
							+ ",$" + ReservedVariableNames.FRAME_HEIGHT));

			entityBackground.getComponents().add(shader);

			Background backgroundComponent = new Background();
			backgroundComponent.setEntity(entityBackground);
			compositeCommand.addCommand(new AddToListCommand(scene,
					sceneComponents, backgroundComponent, 0));
		}
		return compositeCommand;
	}

	protected Parameter param(String name, String value) {
		Parameter parameter = new Parameter();
		parameter.setName(name);
		parameter.setValue(value);
		return parameter;
	}

}
