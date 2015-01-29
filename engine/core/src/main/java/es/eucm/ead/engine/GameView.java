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
package es.eucm.ead.engine;

import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schemax.Layer;

public interface GameView {

	/**
	 * Empties the given layer, getting all children entities removed from the
	 * engine as well. All children layers are preserved.
	 * 
	 * @param layer
	 *            The layer to empty
	 * @param clearChildrenLayers
	 *            If true, it works recursively, clearing also any layer in its
	 *            subtree
	 */
	void clearLayer(Layer layer, boolean clearChildrenLayers);

	/**
	 * Adds the given layer to the given entity. It just attaches the given
	 * {@code entity}'s group to the layer's group
	 * 
	 * @param layer
	 *            The layer
	 * @param entity
	 *            The entity to attach
	 */
	void addEntityToLayer(Layer layer, EngineEntity entity);

	/**
	 * @return The engine entity wrapping the content of the {@code layer}
	 *         specified
	 */
	public EngineEntity getLayer(Layer layer);

	/**
	 * Updates the game view world size
	 */
	void updateWorldSize(int width, int height);

	int getWorldWidth();

	int getWorldHeight();

	int getPixelsWidth();

	int getPixelsHeight();

	int getScreenX();

	int getScreenY();

	int getScreenWidth();

	int getScreenHeight();
}
