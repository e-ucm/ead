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

package es.eucm.ead.schema.effects.controlstructures;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import es.eucm.ead.schema.data.Script;

/**
 * Launches the script given with input arguments
 * 
 */
@Generated("org.jsonschema2pojo")
public class ScriptCall extends ControlStructure {

	/**
	 * A set of expressions that are evaluated and passed to the script as input
	 * arguments. Take into account that this array should contain as many
	 * expressions as input argument the script declares. However, no error is
	 * given if more input arguments are provided than needed
	 * 
	 */
	private List<String> inputArgumentValues = new ArrayList<String>();
	/**
	 * A set of effects that can be launched with argument initialization.
	 * 
	 */
	private Script script;

	/**
	 * A set of expressions that are evaluated and passed to the script as input
	 * arguments. Take into account that this array should contain as many
	 * expressions as input argument the script declares. However, no error is
	 * given if more input arguments are provided than needed
	 * 
	 */
	public List<String> getInputArgumentValues() {
		return inputArgumentValues;
	}

	/**
	 * A set of expressions that are evaluated and passed to the script as input
	 * arguments. Take into account that this array should contain as many
	 * expressions as input argument the script declares. However, no error is
	 * given if more input arguments are provided than needed
	 * 
	 */
	public void setInputArgumentValues(List<String> inputArgumentValues) {
		this.inputArgumentValues = inputArgumentValues;
	}

	/**
	 * A set of effects that can be launched with argument initialization.
	 * 
	 */
	public Script getScript() {
		return script;
	}

	/**
	 * A set of effects that can be launched with argument initialization.
	 * 
	 */
	public void setScript(Script script) {
		this.script = script;
	}

}
