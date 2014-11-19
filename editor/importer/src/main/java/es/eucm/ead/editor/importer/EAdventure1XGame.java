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
package es.eucm.ead.editor.importer;

import es.eucm.eadventure.common.data.adventure.AdventureData;
import es.eucm.eadventure.common.data.animation.Animation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Javier Torrente on 28/09/14.
 */
public class EAdventure1XGame {

	private AdventureData adventureData;

	private Map<String, Animation> animations = new HashMap<String, Animation>();

	public AdventureData getAdventureData() {
		return adventureData;
	}

	public void setAdventureData(AdventureData adventureData) {
		this.adventureData = adventureData;
	}

	public Map<String, Animation> getAnimations() {
		return animations;
	}

	public void setAnimations(Map<String, Animation> animations) {
		this.animations = animations;
	}
}
