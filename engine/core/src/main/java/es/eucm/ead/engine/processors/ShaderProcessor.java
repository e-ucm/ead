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
package es.eucm.ead.engine.processors;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ObjectMap;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.ShaderComponent;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.components.Shader;
import es.eucm.ead.schema.data.Parameter;

public class ShaderProcessor extends ComponentProcessor<Shader> {

	private static final String VERTEX_SHADER = "attribute vec4 "
			+ ShaderProgram.POSITION_ATTRIBUTE
			+ ";\n" //
			+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE
			+ ";\n" //
			+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE
			+ "0;\n" //
			+ "uniform mat4 u_projTrans;\n" //
			+ "varying vec4 v_color;\n" //
			+ "varying vec2 v_texCoords;\n" //
			+ "\n" //
			+ "void main()\n" //
			+ "{\n" //
			+ "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE
			+ ";\n" //
			+ "   v_color.a = v_color.a * (255.0/254.0);\n" //
			+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE
			+ "0;\n" //
			+ "   gl_Position =  u_projTrans * "
			+ ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
			+ "}\n";

	private GameAssets gameAssets;

	private VariablesManager variablesManager;

	private ObjectMap<String, ShaderProgram> shaders = new ObjectMap<String, ShaderProgram>();

	public ShaderProcessor(GameLoop gameLoop, GameAssets gameAssets,
			VariablesManager variablesManager) {
		super(gameLoop);
		this.gameAssets = gameAssets;
		this.variablesManager = variablesManager;
	}

	@Override
	public Component getComponent(Shader shader) {
		ShaderComponent shaderComponent = gameLoop
				.createComponent(ShaderComponent.class);
		shaderComponent.setVariablesManager(variablesManager);

		ShaderProgram shaderProgram = shaders.get(shader.getUri());
		if (shaderProgram == null) {
			shaderProgram = new ShaderProgram(VERTEX_SHADER, gameAssets
					.resolve(shader.getUri()).readString());
			if (!shaderProgram.isCompiled()) {
				Gdx.app.error("ShaderProcessor", "Error parsing shader: "
						+ shaderProgram.getLog());
			}
			shaders.put(shader.getUri(), shaderProgram);
		}
		shaderComponent.setShaderProgram(shaderProgram);

		for (Parameter parameter : shader.getUniforms()) {
			String[] values = ((String) parameter.getValue()).split(",");
			shaderComponent.setUniform(parameter.getName(), values);
		}

		return shaderComponent;
	}
}
