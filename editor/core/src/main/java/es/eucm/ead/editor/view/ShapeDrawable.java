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
package es.eucm.ead.editor.view;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Declares that this actor wants to draw itself on a ShapeRenderer.
 * 
 * This is intended to be transparent to actors by closely copying the
 * 
 * @link{com.badlogic.gdx.scenes/scene2d.Actor#draw(com.badlogic.gdx.graphics.g2d.Batch,float) 
 *                                                                                             method
 *                                                                                             .
 */
public interface ShapeDrawable {
	/**
	 * Performs vector rendering for the current actor.
	 * 
	 * Called in a second pass (after all
	 * 
	 * @link{com.badlogic.gdx.scenes/scene2d.Actor 
	 *                                             #draw(com.badlogic.gdx.graphics
	 *                                             .g2d.Batch,float)} calls have
	 *                                             finished) with a
	 *                                             fully-transformded
	 *                                             ShapeRenderer.
	 * 
	 * @param shapeRenderer
	 *            a closed (you must call begin() and end()) ShapeRenderer,
	 *            already configured with all relevant transforms. When
	 *            rendering, you will be using your own inherited coordinate
	 *            space.
	 */
	public void drawShapes(ShapeRenderer shapeRenderer);
}
