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

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class CircuitComponent extends ChipComponent {

	private ObjectMap<String, ChipComponent> chips = new ObjectMap<String, ChipComponent>();

	private ObjectMap<String, Object> generators = new ObjectMap<String, Object>();

	public void putChip(String name, ChipComponent chipComponent) {
		chips.put(name, chipComponent);
	}

	public void putGenerator(String name, Object value) {
		generators.put(name, value);
	}

	public void addWire(String output, Array<String> inputs) {
		if (output.indexOf('.') == -1) {
			if (generators.containsKey(output)) {
				for (String input : inputs) {
					setValue(generators.get(output), input);
				}
			} else {
				for (String input : inputs) {
					addWire(output, input);
				}
			}
		} else {
			String[] outputParts = output.split("\\.");
			ChipComponent chip = chips.get(outputParts[0]);
			for (String input : inputs) {
				if (input.indexOf('.') == -1) {
					chip.addWire(outputParts[1], this, input);
				} else {
					String[] inputParts = input.split("\\.");
					chip.addWire(outputParts[1], chips.get(inputParts[0]),
							inputParts[1]);
				}
			}
		}
	}

	private void setValue(Object value, String input) {
		if (input.indexOf('.') == -1) {
			this.setInput(input, value);
		} else {
			String[] inputParts = input.split("\\.");
			chips.get(inputParts[0]).setInput(inputParts[1], value);
		}
	}

	public void addWire(String output, String input) {
		ChipComponent chip;
		if (input.indexOf('.') != -1) {
			String[] inputParts = input.split("\\.");
			chip = chips.get(inputParts[0]);
			input = inputParts[1];
		} else {
			chip = this;
		}
		this.addWire(output, chip, input);
	}

	@Override
	public void setInput(String inputName, Object value) {
		setOutput(inputName, value);
		super.setInput(inputName, value);
	}

	@Override
	protected void calculateOutputs() {
	}

	@Override
	public void reset() {
		chips.clear();
		generators.clear();
	}
}
