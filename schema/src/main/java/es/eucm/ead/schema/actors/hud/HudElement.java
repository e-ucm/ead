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
package es.eucm.ead.schema.actors.hud;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import es.eucm.ead.schema.actors.SceneElement;

/**
 * A scene is a container of scene elements. It's the basic unit for the game
 * engine. The engine always shows a scene
 * 
 */
@Generated("org.jsonschema2pojo")
public class HudElement {

	/**
	 * Scene elements are the basic units for scenes. A scene element represents
	 * an object in the scene, with an appearance (renderer), a transfomration
	 * (position, scale, rotation...), and behaviors (reactions to input
	 * events), among other attributes.
	 * 
	 */
	private SceneElement sceneElement;
	/**
	 * Vertical align for the element.
	 * 
	 */
	private HudElement.VerticalAlign verticalAlign;
	/**
	 * Horizontal align for the element.
	 * 
	 */
	private HudElement.HorizontalAlign horizontalAlign;

	/**
	 * Scene elements are the basic units for scenes. A scene element represents
	 * an object in the scene, with an appearance (renderer), a transfomration
	 * (position, scale, rotation...), and behaviors (reactions to input
	 * events), among other attributes.
	 * 
	 */
	public SceneElement getSceneElement() {
		return sceneElement;
	}

	/**
	 * Scene elements are the basic units for scenes. A scene element represents
	 * an object in the scene, with an appearance (renderer), a transfomration
	 * (position, scale, rotation...), and behaviors (reactions to input
	 * events), among other attributes.
	 * 
	 */
	public void setSceneElement(SceneElement sceneElement) {
		this.sceneElement = sceneElement;
	}

	/**
	 * Vertical align for the element.
	 * 
	 */
	public HudElement.VerticalAlign getVerticalAlign() {
		return verticalAlign;
	}

	/**
	 * Vertical align for the element.
	 * 
	 */
	public void setVerticalAlign(HudElement.VerticalAlign verticalAlign) {
		this.verticalAlign = verticalAlign;
	}

	/**
	 * Horizontal align for the element.
	 * 
	 */
	public HudElement.HorizontalAlign getHorizontalAlign() {
		return horizontalAlign;
	}

	/**
	 * Horizontal align for the element.
	 * 
	 */
	public void setHorizontalAlign(HudElement.HorizontalAlign horizontalAlign) {
		this.horizontalAlign = horizontalAlign;
	}

	@Generated("org.jsonschema2pojo")
	public static enum HorizontalAlign {

		LEFT("left"), CENTER("center"), RIGHT("right");
		private final String value;
		private static Map<String, HudElement.HorizontalAlign> constants = new HashMap<String, HudElement.HorizontalAlign>();

		static {
			for (HudElement.HorizontalAlign c : HudElement.HorizontalAlign
					.values()) {
				constants.put(c.value, c);
			}
		}

		private HorizontalAlign(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public static HudElement.HorizontalAlign fromValue(String value) {
			HudElement.HorizontalAlign constant = constants.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}

	}

	@Generated("org.jsonschema2pojo")
	public static enum VerticalAlign {

		TOP("top"), MIDDLE("middle"), BOTTOM("bottom");
		private final String value;
		private static Map<String, HudElement.VerticalAlign> constants = new HashMap<String, HudElement.VerticalAlign>();

		static {
			for (HudElement.VerticalAlign c : HudElement.VerticalAlign.values()) {
				constants.put(c.value, c);
			}
		}

		private VerticalAlign(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public static HudElement.VerticalAlign fromValue(String value) {
			HudElement.VerticalAlign constant = constants.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}

	}

}
