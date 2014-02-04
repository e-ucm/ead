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
package es.eucm.ead.engine.assets.serializers;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import es.eucm.ead.engine.Assets;
import es.eucm.ead.schema.actors.SceneElement;

/**
 * Loads an scene element, taking care of processing the "ref" attribute
 */
public class SceneElementSerializer extends DefaultSerializer<SceneElement> {

	public SceneElementSerializer(Assets assets) {
		super(assets);
	}

	@Override
	public SceneElement read(Json json, JsonValue jsonData, Class type) {
		SceneElement sceneElement;
		// Create the basis scene element
		JsonValue ref = jsonData.get("ref");
		if (ref != null) {
			// Based on another scene element
			sceneElement = assets.fromJsonPath(SceneElement.class,
					ref.asString());
		} else {
			// Based on an empty scene element
			sceneElement = new SceneElement();
		}

		json.readFields(sceneElement, jsonData);
		return sceneElement;
	}
}
