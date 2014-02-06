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
package es.eucm.ead.schema.actors;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import es.eucm.ead.schema.behaviors.Behavior;
import es.eucm.ead.schema.components.Transformation;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.renderers.Renderer;

/**
 * Scene elements are the basic units for scenes. A scene element represents an
 * object in the scene, with an appearance (renderer), a transfomration
 * (position, scale, rotation...), and behaviors (reactions to input events),
 * among other attributes.
 * 
 */
@Generated("org.jsonschema2pojo")
public class SceneElement {

	/**
	 * An uri pointing to a file representing the base scene element for this
	 * element. This scene element will be loaded first, and its properties will
	 * be overriden by the properties defined for this scene element
	 * 
	 */
	private String ref;
	/**
	 * Tags of the scene element. These tags are used to group and identify
	 * scene elements.
	 * 
	 */
	private List<String> tags = new ArrayList<String>();
	/**
	 * Initial effects for the scene element. These effects are automatically
	 * executed when the parent scene is loaded
	 * 
	 */
	private List<Effect> effects = new ArrayList<Effect>();
	/**
	 * Scene element children. A scene element con contain other scene elements,
	 * to create compund scene elements
	 * 
	 */
	private List<SceneElement> children = new ArrayList<SceneElement>();
	/**
	 * Sets if the scene element reacts to touch events (can be changed during
	 * game play)
	 * 
	 */
	private boolean enable = true;
	private Renderer renderer;
	/**
	 * Contains a 2D transformation (position, scale, rotation and color)
	 * 
	 */
	private Transformation transformation;
	/**
	 * Whether the scene element is visible or not (can be change during game
	 * play)
	 * 
	 */
	private boolean visible = true;
	/**
	 * List of behaviors of the scene element. These behaviors define how the
	 * scene element reacts to input events
	 * 
	 */
	private List<Behavior> behaviors = new ArrayList<Behavior>();

	/**
	 * An uri pointing to a file representing the base scene element for this
	 * element. This scene element will be loaded first, and its properties will
	 * be overriden by the properties defined for this scene element
	 * 
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * An uri pointing to a file representing the base scene element for this
	 * element. This scene element will be loaded first, and its properties will
	 * be overriden by the properties defined for this scene element
	 * 
	 */
	public void setRef(String ref) {
		this.ref = ref;
	}

	/**
	 * Tags of the scene element. These tags are used to group and identify
	 * scene elements.
	 * 
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * Tags of the scene element. These tags are used to group and identify
	 * scene elements.
	 * 
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	/**
	 * Initial effects for the scene element. These effects are automatically
	 * executed when the parent scene is loaded
	 * 
	 */
	public List<Effect> getEffects() {
		return effects;
	}

	/**
	 * Initial effects for the scene element. These effects are automatically
	 * executed when the parent scene is loaded
	 * 
	 */
	public void setEffects(List<Effect> effects) {
		this.effects = effects;
	}

	/**
	 * Scene element children. A scene element con contain other scene elements,
	 * to create compund scene elements
	 * 
	 */
	public List<SceneElement> getChildren() {
		return children;
	}

	/**
	 * Scene element children. A scene element con contain other scene elements,
	 * to create compund scene elements
	 * 
	 */
	public void setChildren(List<SceneElement> children) {
		this.children = children;
	}

	/**
	 * Sets if the scene element reacts to touch events (can be changed during
	 * game play)
	 * 
	 */
	public boolean isEnable() {
		return enable;
	}

	/**
	 * Sets if the scene element reacts to touch events (can be changed during
	 * game play)
	 * 
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * Contains a 2D transformation (position, scale, rotation and color)
	 * 
	 */
	public Transformation getTransformation() {
		return transformation;
	}

	/**
	 * Contains a 2D transformation (position, scale, rotation and color)
	 * 
	 */
	public void setTransformation(Transformation transformation) {
		this.transformation = transformation;
	}

	/**
	 * Whether the scene element is visible or not (can be change during game
	 * play)
	 * 
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Whether the scene element is visible or not (can be change during game
	 * play)
	 * 
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * List of behaviors of the scene element. These behaviors define how the
	 * scene element reacts to input events
	 * 
	 */
	public List<Behavior> getBehaviors() {
		return behaviors;
	}

	/**
	 * List of behaviors of the scene element. These behaviors define how the
	 * scene element reacts to input events
	 * 
	 */
	public void setBehaviors(List<Behavior> behaviors) {
		this.behaviors = behaviors;
	}

}
