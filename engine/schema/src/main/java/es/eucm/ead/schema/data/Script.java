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

package es.eucm.ead.schema.data;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import es.eucm.ead.schema.effects.Effect;

/**
 * A set of effects that can be launched with argument initialization.
 * 
 */
@Generated("org.jsonschema2pojo")
public class Script {

	/**
	 * A set of variables that serve as input arguments. These variables can be
	 * initialized and made available to the effects contained in the script.
	 * 
	 */
	private List<VariableDef> inputArguments = new ArrayList<VariableDef>();
	/**
	 * A list of effects that are queued for execution as soon as the script is
	 * launched.
	 * 
	 */
	private List<Effect> effects = new ArrayList<Effect>();

	/**
	 * A set of variables that serve as input arguments. These variables can be
	 * initialized and made available to the effects contained in the script.
	 * 
	 */
	public List<VariableDef> getInputArguments() {
		return inputArguments;
	}

	/**
	 * A set of variables that serve as input arguments. These variables can be
	 * initialized and made available to the effects contained in the script.
	 * 
	 */
	public void setInputArguments(List<VariableDef> inputArguments) {
		this.inputArguments = inputArguments;
	}

	/**
	 * A list of effects that are queued for execution as soon as the script is
	 * launched.
	 * 
	 */
	public List<Effect> getEffects() {
		return effects;
	}

	/**
	 * A list of effects that are queued for execution as soon as the script is
	 * launched.
	 * 
	 */
	public void setEffects(List<Effect> effects) {
		this.effects = effects;
	}

}
