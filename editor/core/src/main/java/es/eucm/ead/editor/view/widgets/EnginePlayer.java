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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import es.eucm.ead.engine.DefaultGameView;
import es.eucm.ead.engine.GameLoop;

/**
 * A widget to contain a working instance of the engine
 */
public class EnginePlayer extends DefaultGameView {

	public EnginePlayer(GameLoop gameLoop) {
		super(gameLoop);
	}

	@Override
	public void drawChildren(Batch batch, float parentAlpha) {
		if (clipBegin(0, 0, worldWidth, worldHeight)) {
			super.drawChildren(batch, parentAlpha);
			clipEnd();
		}
	}

	@Override
	public float getPrefWidth() {
		return worldWidth;
	}

	@Override
	public float getPrefHeight() {
		return worldHeight;
	}

	@Override
	public void updateWorldSize(int width, int height) {
		this.worldWidth = width;
		this.worldHeight = height;
		invalidateHierarchy();
	}

	@Override
	public void layout() {
		float scaleX = getWidth() / worldWidth;
		float scaleY = getHeight() / worldHeight;
		float scale = Math.min(scaleX, scaleY);
		float offsetX = (getWidth() - worldWidth * scale) / 2.0f;
		float offsetY = (getHeight() - worldHeight * scale) / 2.0f;
		setPosition(offsetX, offsetY);
		setScale(scale);
	}
}
