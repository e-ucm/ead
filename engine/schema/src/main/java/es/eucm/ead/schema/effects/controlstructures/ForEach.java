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

import javax.annotation.Generated;

/**
 * Equivalent to for (Object o: list){effects}
 * 
 */
@Generated("org.jsonschema2pojo")
public class ForEach extends ControlStructure {

	/**
	 * Identifies the name of the variable that contains the current element
	 * iterated in the list. Set to 'it' by default.
	 * 
	 */
	private String iteratorVar = "it";
	/**
	 * Expression whose result is the list iterated
	 * 
	 */
	private String listExpression;

	/**
	 * Identifies the name of the variable that contains the current element
	 * iterated in the list. Set to 'it' by default.
	 * 
	 */
	public String getIteratorVar() {
		return iteratorVar;
	}

	/**
	 * Identifies the name of the variable that contains the current element
	 * iterated in the list. Set to 'it' by default.
	 * 
	 */
	public void setIteratorVar(String iteratorVar) {
		this.iteratorVar = iteratorVar;
	}

	/**
	 * Expression whose result is the list iterated
	 * 
	 */
	public String getListExpression() {
		return listExpression;
	}

	/**
	 * Expression whose result is the list iterated
	 * 
	 */
	public void setListExpression(String listExpression) {
		this.listExpression = listExpression;
	}

}
