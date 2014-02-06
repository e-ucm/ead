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

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import es.eucm.ead.schema.renderers.Shape;

/**
 * Renderer for shapes (rectangles, circles and polygons). Relies on
 * {@link ShapesFactory} to create images that represent the shapes. These
 * generated images are the ones used in the draw method.
 */
public class ShapeEngineObject extends RendererEngineObject<Shape> {

	public static final ShapesFactory shapesFactory = new ShapesFactory();

	private float originX;

	private float originY;

	private int width;

	private int height;

	private Texture texture;

	@Override
	public void initialize(Shape schemaObject) {
		Pixmap pixmap = shapesFactory.createShape(schemaObject);
		width = pixmap.getWidth();
		height = pixmap.getHeight();
		originX = shapesFactory.getOriginX();
		originY = shapesFactory.getOriginY();
		texture = new Texture(pixmap);
		pixmap.dispose();
	}

	@Override
	public void draw(Batch batch) {
		batch.draw(texture, originX, originY);
	}

	@Override
	public float getWidth() {
		return width + originX;
	}

	@Override
	public float getHeight() {
		return height + originY;
	}

	@Override
	public void dispose() {
		super.dispose();
		// dispose texture, since it was created from a pixmap and it is not
		// managed by Engine.assets
		texture.dispose();
	}

}
