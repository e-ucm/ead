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
package es.eucm.ead.engine.components;

import com.badlogic.ashley.core.Component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Pool.Poolable;
import es.eucm.ead.engine.variables.VariablesManager;

public class ShaderComponent extends Component implements Poolable {

	private VariablesManager variablesManager;

	private ShaderProgram shaderProgram;

	private float[] values = new float[4];

	private ObjectMap<String, String[]> uniforms = new ObjectMap<String, String[]>();

	public void setVariablesManager(VariablesManager variablesManager) {
		this.variablesManager = variablesManager;
	}

	public void setUniform(String name, String[] values) {
		uniforms.put(name, values);
	}

	public ShaderProgram getShaderProgram() {
		return shaderProgram;
	}

	public void setShaderProgram(ShaderProgram shaderProgram) {
		this.shaderProgram = shaderProgram;
	}

	@Override
	public void reset() {
		shaderProgram = null;
		uniforms.clear();
	}

	public void prepare() {
		for (Entry<String, String[]> entry : uniforms.entries()) {
			switch (entry.value.length) {
			case 1:
				shaderProgram.setUniformf(entry.key, values(entry.value)[0]);
				break;
			case 2:
				shaderProgram.setUniform2fv(entry.key, values(entry.value), 0,
						2);
				break;
			case 3:
				shaderProgram.setUniform3fv(entry.key, values(entry.value), 0,
						3);
				break;
			case 4:
				shaderProgram.setUniform4fv(entry.key, values(entry.value), 0,
						4);
				break;
			}
		}
	}

	private float[] values(String[] expressions) {
		for (int i = 0; i < expressions.length; i++) {
			Object o = variablesManager.evaluateExpression(expressions[i]);
			if (o instanceof Number) {
				values[i] = ((Number) o).floatValue();
			} else {
				Gdx.app.error("ShaderComponent",
						"Invalid expression for shader uniform.");
			}
		}
		return values;
	}
}
