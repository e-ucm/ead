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
import com.badlogic.gdx.utils.Pool.Poolable;

public abstract class ChipComponent implements Poolable {

	protected ObjectMap<String, Object> inputs = new ObjectMap<String, Object>();

	private ObjectMap<String, Object> outputs = new ObjectMap<String, Object>();

	private ObjectMap<String, Array<Wire>> wiresMap = new ObjectMap<String, Array<Wire>>();

	/**
	 * Adds a connection between an output of this chip and the input of the
	 * given chip
	 */
	public void addWire(String output, ChipComponent chip, String input) {
		Array<Wire> wires = wiresMap.get(output);
		if (wires == null) {
			wires = new Array<Wire>();
			wiresMap.put(output, wires);
		}
		wires.add(new Wire(chip, input));
	}

	/**
	 * Sets the value for an input
	 */
	public void setInput(String inputName, Object value) {
		Object currentValue = inputs.get(inputName);
		if ((currentValue != null && !currentValue.equals(value))
				|| currentValue != value) {
			inputs.put(inputName, value);
			calculateOutputs();
		}
	}

	/**
	 * @return the current value in the given output
	 */
	public <T> T getOutput(String outputName) {
		return (T) outputs.get(outputName);
	}

	protected Object getInput(String inputName) {
		return getInput(inputName, null);
	}

	protected <T> T getInput(String inputName, T defaultValue) {
		T value = (T) inputs.get(inputName);
		return value == null ? defaultValue : value;
	}

	protected void setOutput(String output, Object value) {
		Object currentValue = outputs.get(output);
		if ((currentValue != null && !currentValue.equals(value))
				|| currentValue != value) {
			outputs.put(output, value);
			Array<Wire> wires = wiresMap.get(output);
			if (wires != null) {
				for (Wire wire : wires) {
					wire.chip.setInput(wire.input, value);
				}
			}
		}
	}

	/**
	 * Sets the outputs values according to the current input values
	 */
	protected abstract void calculateOutputs();

	private static class Wire {

		public ChipComponent chip;

		public String input;

		public Wire(ChipComponent chip, String input) {
			this.chip = chip;
			this.input = input;
		}
	}

}
