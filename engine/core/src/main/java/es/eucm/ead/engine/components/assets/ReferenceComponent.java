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
package es.eucm.ead.engine.components.assets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.renderers.RendererComponent;

/**
 * References for entities
 */
public class ReferenceComponent extends RendererComponent implements Poolable {

	private Group group;
	private GameLoop gameLoop;

	public void set(Group group, GameLoop gameLoop) {
		this.group = group;
		this.gameLoop = gameLoop;
		group.setTransform(false);
	}

	@Override
	public void draw(Batch batch) {
		if (group != null) {
			group.draw(batch, 1.0f);
		}
	}

	@Override
	public float getWidth() {
		return group == null ? 0 : group.getWidth();
	}

	@Override
	public float getHeight() {
		return group == null ? 0 : group.getHeight();
	}

	@Override
	public Array<Polygon> getCollider() {
		return null;
	}

	@Override
	public void reset() {
		group = null;
	}

	@Override
	public void act(float delta) {
		if (group != null && gameLoop.isPlaying()) {
			group.act(delta);
		}
	}
}
