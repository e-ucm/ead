/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.engine.renderers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import es.eucm.ead.schema.components.Bounds;
import es.eucm.ead.schema.components.Dimension;
import es.eucm.ead.schema.renderers.NinePatch;

public class NinePatchEngineObject extends RendererEngineObject<NinePatch> {

	private NinePatchDrawable drawable;

	private Dimension size;

	@Override
	public void initialize(NinePatch schemaObject) {
		size = schemaObject.getSize();
		Bounds bounds = schemaObject.getBounds();
		com.badlogic.gdx.graphics.g2d.NinePatch ninePatch = new com.badlogic.gdx.graphics.g2d.NinePatch(
				gameLoop.getAssets().get(schemaObject.getUri(), Texture.class),
				bounds.getLeft(), bounds.getRight(), bounds.getTop(),
				bounds.getBottom());
		drawable = new NinePatchDrawable(ninePatch);
	}

	@Override
	public void draw(Batch batch) {
		drawable.draw(batch, 0, 0, size.getWidth(), size.getHeight());
	}

	@Override
	public float getHeight() {
		return size.getHeight();
	}

	@Override
	public float getWidth() {
		return size.getWidth();
	}

}
