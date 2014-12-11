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
package es.eucm.ead.engine.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

public class ScaledTexture extends BaseDrawable {

	private Texture texture;

	private float scale;

	private Matrix4 textureMatrix = new Matrix4();

	public ScaledTexture(Texture texture, float scale) {
		this.texture = texture;
		this.scale = scale;
	}

	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		Matrix4 oldMatrix = batch.getTransformMatrix();
		textureMatrix.set(oldMatrix);
		textureMatrix.scl(scale, scale, 1.0f);
		batch.setTransformMatrix(textureMatrix);
		batch.draw(texture, x, y);
		batch.setTransformMatrix(oldMatrix);
	}

	public float getWidth() {
		return texture.getWidth() * scale;
	}

	public float getHeight() {
		return texture.getHeight() * scale;
	}
}
