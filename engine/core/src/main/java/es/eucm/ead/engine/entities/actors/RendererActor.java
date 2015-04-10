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
package es.eucm.ead.engine.entities.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool.Poolable;

import es.eucm.ead.engine.components.renderers.RendererComponent;

public class RendererActor extends EntityGroup implements Poolable {

	protected RendererComponent renderer;

	public void setRenderer(RendererComponent renderer) {
		this.renderer = renderer;
		this.setWidth(renderer.getWidth());
		this.setHeight(renderer.getHeight());
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		renderer.act(delta);
	}

	@Override
	public void drawChildren(Batch batch, float parentAlpha) {
		if (renderer != null) {
			// Set alpha and color
			Color color = getColor();

			Color batchColor = batch.getColor();
			float packedColor = batch.getPackedColor();
			batchColor.mul(color.r, color.g, color.b, color.a * parentAlpha);
			batch.setColor(batchColor);

			renderer.draw(batch);

			// Restore the color
			batch.setColor(packedColor);
		}
		super.drawChildren(batch, parentAlpha);
	}

	@Override
	public float getWidth() {
		return renderer == null ? 0 : renderer.getWidth();
	}

	@Override
	public float getHeight() {
		return renderer == null ? 0 : renderer.getHeight();
	}

	@Override
	public void reset() {
		this.renderer = null;
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		Actor actor = super.hit(x, y, touchable);
		if (actor == null && isTouchable()) {
			return renderer != null && renderer.hit(x, y) ? this : null;
		} else {
			return actor;
		}
	}
}
