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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import es.eucm.ead.editor.view.drawables.TextureDrawable;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.assets.Assets.AssetLoadingListener;

public class AsyncImage extends Image implements AssetLoadingListener<Texture> {

	private Assets assets;

	private String fileName;

	private TextureDrawable textureDrawable;

	private boolean reload;

	public AsyncImage(String fileName, Assets assets) {
		this.fileName = fileName;
		this.assets = assets;
		setDrawable(this.textureDrawable = new TextureDrawable());
		reload = true;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (reload) {
			if (assets.isLoaded(fileName, Texture.class)) {
				loaded(null, assets.get(fileName, Texture.class), null);
			} else {
				assets.load(fileName, Texture.class);
			}
			reload = false;
		}
		super.draw(batch, parentAlpha);
	}

	@Override
	protected void setParent(Group parent) {
		super.setParent(parent);
		if (parent != null) {
			assets.addAssetListener(this);
		}
	}

	@Override
	public boolean remove() {
		assets.removeAssetListener(this);
		return super.remove();
	}

	@Override
	public boolean listenTo(String fileName) {
		return this.fileName.equals(fileName);
	}

	@Override
	public void loaded(String fileName, Texture texture, Assets assets) {
		textureDrawable.setTexture(texture);
		invalidateHierarchy();
	}

	@Override
	public void unloaded(String fileName, Assets assets) {
		textureDrawable.setTexture(null);
		reload = true;
	}
}
