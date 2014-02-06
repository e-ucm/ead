/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class GoSubgame extends Effect {

	/**
	 * Name of the subgame. The engine will attempt to load a game stored in
	 * subgames/name
	 * 
	 */
	private String name;
	/**
	 * Effects to be executed after the game is ended through an 'end game'
	 * effect
	 * 
	 */
	private List<Effect> postEffects = new ArrayList<Effect>();

	/**
	 * Name of the subgame. The engine will attempt to load a game stored in
	 * subgames/name
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Name of the subgame. The engine will attempt to load a game stored in
	 * subgames/name
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Effects to be executed after the game is ended through an 'end game'
	 * effect
	 * 
	 */
	public List<Effect> getPostEffects() {
		return postEffects;
	}

	/**
	 * Effects to be executed after the game is ended through an 'end game'
	 * effect
	 * 
	 */
	public void setPostEffects(List<Effect> postEffects) {
		this.postEffects = postEffects;
	}

}
