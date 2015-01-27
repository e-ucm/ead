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
package es.eucm.ead.engine.processors.templates.circuits;

import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Pools;

import es.eucm.ead.engine.ComponentLoader;
import es.eucm.ead.engine.components.ShaderComponent;
import es.eucm.ead.schema.components.circuits.chips.ShaderChip;

public class ShaderChipFactory implements ChipFactory<ShaderChip> {

	private ComponentLoader componentLoader;

	public ShaderChipFactory(ComponentLoader componentLoader) {
		this.componentLoader = componentLoader;
	}

	@Override
	public ChipComponent build(ShaderChip chip) {
		ShaderComponent shaderComponent = componentLoader
				.toEngineComponent(chip.getShader());
		return new ShaderChipComponent(shaderComponent);
	}

	public static class ShaderChipComponent extends ChipComponent {

		private ShaderComponent shader;

		public ShaderChipComponent(ShaderComponent shader) {
			this.shader = shader;
		}

		@Override
		protected void calculateOutputs() {
			for (Entry<String, Object> entry : inputs) {
				if (entry.value.getClass().isArray()) {
					shader.setUniform(entry.key, (float[]) entry.value);
				} else {
					shader.setUniform(entry.key, (Float) entry.value);
				}
			}
			setOutput("shader", shader);
		}

		@Override
		public void reset() {
			Pools.free(shader);
		}
	}
}
