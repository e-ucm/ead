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
package es.eucm.ead.editor.utils;

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.schema.components.Shader;
import es.eucm.ead.schema.components.circuits.Chip;
import es.eucm.ead.schema.components.circuits.Circuit;
import es.eucm.ead.schema.components.circuits.Generator;
import es.eucm.ead.schema.components.circuits.Wire;
import es.eucm.ead.schema.components.circuits.chips.EntityChip;
import es.eucm.ead.schema.components.circuits.chips.RectangleChip;
import es.eucm.ead.schema.components.circuits.chips.ShaderChip;

public class CircuitBuilder {

	private Circuit circuit;

	public CircuitBuilder newCircuit() {
		circuit = new Circuit();
		return this;
	}

	public CircuitBuilder inputs(String... inputs) {
		circuit.setInputs(new Array<String>(inputs));
		return this;
	}

	public CircuitBuilder outputs(String... outputs) {
		circuit.setOutputs(new Array<String>(outputs));
		return this;
	}

	public CircuitBuilder rectangle(String name) {
		return chip(name, new RectangleChip());
	}

	public CircuitBuilder entity(String name) {
		return chip(name, new EntityChip());
	}

	public CircuitBuilder shader(String name, String shaderUri) {
		Shader shader = new Shader();
		shader.setUri(shaderUri);
		ShaderChip chip = new ShaderChip();
		chip.setShader(shader);
		return chip(name, chip);
	}

	public CircuitBuilder chip(String name, Chip chip) {
		chip.setName(name);
		circuit.getChips().add(chip);
		return this;
	}

	public CircuitBuilder wire(String output, String... inputs) {
		Wire wire = new Wire();
		wire.setOutput(output);
		wire.setInputs(new Array<String>(inputs));
		circuit.getWires().add(wire);
		return this;
	}

	public CircuitBuilder generator(String name, Object value) {
		Generator generator = new Generator();
		generator.setName(name);
		generator.setValue(value);
		circuit.getGenerators().add(generator);
		return this;
	}

	public Circuit build() {
		return circuit;
	}
}
