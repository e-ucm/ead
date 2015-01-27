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

import ashley.core.Component;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ShaderComponent extends Component implements Poolable {

	private ShaderProgram shaderProgram;

	private ObjectMap<String, Object> uniforms = new ObjectMap<String, Object>();

	public void setUniform(String name, float[] values) {
		uniforms.put(name, values);
	}

	public void setUniform(String name, float value) {
		uniforms.put(name, value);
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
		for (Entry<String, Object> entry : uniforms.entries()) {
			if (entry.value.getClass().isArray()) {
				float[] value = (float[]) entry.value;
				switch (value.length) {
				case 1:
					shaderProgram.setUniformf(entry.key, value[0]);
					break;
				case 2:
					shaderProgram.setUniform2fv(entry.key, value, 0, 2);
					break;
				case 3:
					shaderProgram.setUniform3fv(entry.key, value, 0, 3);
					break;
				case 4:
					shaderProgram.setUniform4fv(entry.key, value, 0, 4);
					break;
				}
			} else {
				shaderProgram.setUniformf(entry.key, (Float) entry.value);
			}
		}
	}
}
