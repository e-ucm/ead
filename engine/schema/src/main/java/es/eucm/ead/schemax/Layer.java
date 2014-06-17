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

package es.eucm.ead.schemax;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple enum that identifies the layers in the game See <a
 * href="https://github.com/e-ucm/ead/wiki/Game-view">this wiki page</a> for
 * more info.
 * 
 * <pre>
 * -hud - scene + --camera + --scene_content + --scene_hud
 * </pre>
 */
public enum Layer {

	/*
	 * NOTE: THE ORDER OF THIS LIST MATTERS! Layers should appear here in the
	 * order they have to be added to the view. That is, the opposite to how
	 * they are shown on screen.
	 */
	SCENE("scene"), CAMERA("camera", SCENE), SCENE_CONTENT("scene_content",
			CAMERA), SCENE_HUD("scene_hud", SCENE), HUD("hud");
	private final String value;
	private final Layer parent;
	private static Map<String, Layer> constants = new HashMap<String, Layer>();

	static {
		for (Layer c : Layer.values()) {
			constants.put(c.value, c);
		}
	}

	private Layer(String value) {
		this(value, null);
	}

	private Layer(String value, Layer parent) {
		this.value = value;
		this.parent = parent;
	}

	@Override
	public String toString() {
		return this.value;
	}

	/**
	 * @return The parent of this layer in the hierarchy, or {@code null} if it
	 *         is a root layer (e.g. {@link #SCENE}, {@link #HUD}).
	 */
	public Layer getParentLayer() {
		return parent;
	}

	public static Layer fromValue(String value) {
		Layer constant = constants.get(value);
		if (constant == null) {
			throw new IllegalArgumentException(value);
		} else {
			return constant;
		}
	}

}
