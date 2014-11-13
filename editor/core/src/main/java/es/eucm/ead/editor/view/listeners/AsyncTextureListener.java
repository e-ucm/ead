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
package es.eucm.ead.editor.view.listeners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import es.eucm.ead.editor.view.drawables.TextureDrawable;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.assets.Assets.AssetLoadingListener;

public class AsyncTextureListener implements AssetLoadingListener<Texture> {

	private String fileName;

	private TextureDrawable textureDrawable;

	public AsyncTextureListener(String fileName, TextureDrawable textureDrawable) {
		this.fileName = fileName;
		this.textureDrawable = textureDrawable;
	}

	@Override
	public void loaded(String fileName, Texture texture, Assets assets) {
		if (fileName.equals(this.fileName)) {
			textureDrawable.setTexture(texture);
			Gdx.graphics.requestRendering();
		}
	}

	@Override
	public void unloaded(String fileName, Assets assets) {
		if (fileName.equals(this.fileName)) {
			textureDrawable.setTexture(null);
			Gdx.graphics.requestRendering();
		}
	}
}
