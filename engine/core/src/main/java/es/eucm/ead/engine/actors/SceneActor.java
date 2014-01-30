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
package es.eucm.ead.engine.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneActor extends AbstractActor<Scene> {

	private Map<String, Array<SceneElementActor>> sceneElementTags;

	public SceneActor() {
		sceneElementTags = new HashMap<String, Array<SceneElementActor>>();
	}

	@Override
	public void initialize(Scene schemaObject) {
		for (SceneElement se : schemaObject.getChildren()) {
			addActor(se);
		}
		sceneElementTags.clear();
		addChildrenTagsTo(this);
	}

	public void registerTags(SceneElementActor sceneElement, List<String> tags) {
		for (String tag : tags) {
			Array<SceneElementActor> sceneElements = sceneElementTags.get(tag);
			if (sceneElements == null) {
				sceneElements = new Array<SceneElementActor>();
				sceneElementTags.put(tag, sceneElements);
			}
			sceneElements.add(sceneElement);
		}
	}

	/**
	 * Returns all the scene elements tagged with the given tag.
	 * 
	 * @param tag
	 *            the tag
	 * @return the list of scene elements. It is null when no scene element has
	 *         the given tag
	 */
	public Array<SceneElementActor> findByTag(String tag) {
		return sceneElementTags.get(tag);
	}

	@Override
	public void dispose() {
		super.dispose();
		for (Actor a : getChildren()) {
			if (a instanceof AbstractActor) {
				((AbstractActor) a).dispose();
			}
		}
		clearChildren();
	}
}
