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
package es.eucm.ead.editor.processors;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.vividsolutions.jts.geom.Geometry;

import es.eucm.ead.editor.components.EditorImageComponent;
import es.eucm.ead.engine.utils.GeometryUtils;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.renderers.ImageComponent;
import es.eucm.ead.engine.components.renderers.RendererComponent;
import es.eucm.ead.engine.processors.renderers.ImageProcessor;
import es.eucm.ead.schema.renderers.Image;

public class EditorImageProcessor extends ImageProcessor {

	private double threshold, distanceTolerance;
	protected ShapeRenderer shapeRenderer;

	public EditorImageProcessor(GameLoop engine, GameAssets gameAssets,
			ShapeRenderer shapeRenderer) {
		this(engine, gameAssets, shapeRenderer, .1D, 2D);
	}

	/**
	 * @param threshold
	 *            a number between 0 (transparent) and 1 (opaque) used to
	 *            determine the sensitivity of the borders. Recommended value is
	 *            0.3 (lower = only very transparent things are 'out')
	 * @param distanceTolerance
	 *            used during polygon-simplification. Points in the polygon will
	 *            be separated by at least distanceTolerance pixels.
	 */
	public EditorImageProcessor(GameLoop engine, GameAssets gameAssets,
			ShapeRenderer shapeRenderer, double threshold,
			double distanceTolerance) {
		super(engine, gameAssets);
		this.shapeRenderer = shapeRenderer;
		this.threshold = threshold;
		this.distanceTolerance = distanceTolerance;
	}

	@Override
	public RendererComponent getComponent(Image image) {
		if (image.getCollider().size == 0) {
			try {
				Pixmap pixmap = new Pixmap(gameAssets.resolve(image.getUri()));
				Array<Geometry> geometryArray = GeometryUtils.findBorders(
						pixmap, threshold, distanceTolerance);
				for (Geometry geometry : geometryArray) {
					image.getCollider().add(
							GeometryUtils.jtsToSchemaPolygon(geometry));
				}
				pixmap.dispose();
			} catch (Exception e) {

			}
		}
		return super.getComponent(image);
	}

	@Override
	protected ImageComponent createComponent() {
		EditorImageComponent component = gameLoop
				.createComponent(EditorImageComponent.class);
		component.setShapeRenderer(shapeRenderer);
		return component;
	}
}
