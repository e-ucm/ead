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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

public class TextureDrawable extends BaseDrawable {

	private Texture texture;

	private TextureRegion textureRegion = new TextureRegion();

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
			textureRegion.setTexture(texture);
		}

	}

	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		if (texture != null) {
			updateRegion(width, height);
			batch.draw(textureRegion, 0, 0, width, height);
		}
	}

	private void updateRegion(float width, float height) {
		textureRegion.setRegion(0, 0, (int) width, (int) height);
	}

}
