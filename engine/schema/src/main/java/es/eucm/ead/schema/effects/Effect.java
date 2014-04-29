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

package es.eucm.ead.schema.effects;

import javax.annotation.Generated;
import es.eucm.ead.schema.data.Condition;

/**
 * Effects define events that affects/changes the game state.
 * 
 */
@Generated("org.jsonschema2pojo")
public class Effect extends Condition {

	/**
	 * Defines which entities this effect has to be applied to. There are three
	 * options:<br/>
	 * <ol>
	 * <li><b>this</b>. Applies the effect only to the entity that contains the
	 * effect. This is the default option.</li>
	 * <li><b>all</b>. Applies the effect to all the entities found.</li>
	 * <li><b>each entity {expression}</b>. Applies the effect to each entity
	 * that matches the expression given among curly brackets. In this
	 * expression it is possible to refer to the entity's properties using
	 * special variable $entity.</li>
	 * </ol>
	 * 
	 */
	private String target;

	/**
	 * Defines which entities this effect has to be applied to. There are three
	 * options:<br/>
	 * <ol>
	 * <li><b>this</b>. Applies the effect only to the entity that contains the
	 * effect. This is the default option.</li>
	 * <li><b>all</b>. Applies the effect to all the entities found.</li>
	 * <li><b>each entity {expression}</b>. Applies the effect to each entity
	 * that matches the expression given among curly brackets. In this
	 * expression it is possible to refer to the entity's properties using
	 * special variable $entity.</li>
	 * </ol>
	 * 
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * Defines which entities this effect has to be applied to. There are three
	 * options:<br/>
	 * <ol>
	 * <li><b>this</b>. Applies the effect only to the entity that contains the
	 * effect. This is the default option.</li>
	 * <li><b>all</b>. Applies the effect to all the entities found.</li>
	 * <li><b>each entity {expression}</b>. Applies the effect to each entity
	 * that matches the expression given among curly brackets. In this
	 * expression it is possible to refer to the entity's properties using
	 * special variable $entity.</li>
	 * </ol>
	 * 
	 */
	public void setTarget(String target) {
		this.target = target;
	}

}
