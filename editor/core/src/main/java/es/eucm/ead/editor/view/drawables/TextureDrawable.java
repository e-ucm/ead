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
package es.eucm.ead.editor.view.drawables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.engine.systems.effects.transitions.Region;

public class TextureDrawable extends BaseDrawable {

	private Texture texture;
	private Region region;

	public TextureDrawable() {
		this(null);
	}

	public TextureDrawable(Texture texture) {
		setTexture(texture);
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
		if (texture != null) {
			setMinWidth(texture.getWidth());
			setMinHeight(texture.getHeight());
		}
	}

	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		if (texture != null) {
			if (region == null) {
				Vector2 fill = Scaling.fit.apply(width, height,
						texture.getWidth(), texture.getHeight());
				region = new Region((texture.getWidth() - fill.x) * .5f,
						(texture.getHeight() - fill.y) * .5f, fill.x, fill.y);
			}

			batch.draw(texture, x, y, width, height, region.x, region.y,
					region.w, region.h, false, false);
		}
	}
}
