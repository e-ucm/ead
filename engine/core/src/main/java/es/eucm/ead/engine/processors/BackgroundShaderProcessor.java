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
import es.eucm.ead.engine.systems.ShaderSystem;
import es.eucm.ead.schema.components.BackgroundShader;
import es.eucm.ead.schema.components.Shader;
import es.eucm.ead.schema.data.Parameter;

public class BackgroundShaderProcessor extends
		ComponentProcessor<BackgroundShader> {

	private ShaderProcessor shaderProcessor;

	public BackgroundShaderProcessor(ShaderProcessor shaderProcessor) {
		super(null);
		this.shaderProcessor = shaderProcessor;
	}

	@Override
	public Component getComponent(BackgroundShader backgroundShader) {
		Shader shader = new Shader();
		shader.setUri(backgroundShader.getUri());
		shader.getUniforms().add(param("time", "$" + ShaderSystem.TIME));
		shader.getUniforms().add(
				param("resolution", "$" + ShaderSystem.WIDTH + ",$"
						+ ShaderSystem.HEIGHT));
		return shaderProcessor.getComponent(shader);
	}

	private Parameter param(String name, String value) {
		Parameter parameter = new Parameter();
		parameter.setName(name);
		parameter.setValue(value);
		return parameter;
	}
}
