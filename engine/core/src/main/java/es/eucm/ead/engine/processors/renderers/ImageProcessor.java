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
package es.eucm.ead.engine.processors.renderers;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.assets.ScaledTexture;
import es.eucm.ead.engine.components.renderers.ImageComponent;
import es.eucm.ead.engine.components.renderers.RendererComponent;
import es.eucm.ead.schema.renderers.Image;

public class ImageProcessor extends RendererProcessor<Image> {

	public ImageProcessor(GameLoop engine, GameAssets gameAssets) {
		super(engine, gameAssets);
	}

	@Override
	public RendererComponent getComponent(Image image) {
		final ImageComponent imageComponent = createComponent();
		gameAssets.get(image.getUri() + ".tex", ScaledTexture.class,
				new AssetLoadedCallback<ScaledTexture>() {
					@Override
					public void loaded(String fileName, ScaledTexture asset) {
						imageComponent.setTexture(asset);
					}
				}, true);
		createCollider(image, imageComponent);
		return imageComponent;
	}

	protected void createCollider(Image image, ImageComponent component) {
		Array<es.eucm.ead.schema.data.shape.Polygon> schemaCollider = image
				.getCollider();
		if (schemaCollider != null && schemaCollider.size > 0) {
			Array<Polygon> collider = new Array<Polygon>(schemaCollider.size);
			for (es.eucm.ead.schema.data.shape.Polygon polygon : schemaCollider) {
				Array<Float> pointsArray = polygon.getPoints();
				float[] points = new float[pointsArray.size];
				for (int i = 0; i < pointsArray.size; i++) {
					points[i] = pointsArray.get(i);
				}
				Polygon contour = new Polygon(points);
				collider.add(contour);
			}
			component.setCollider(collider);
		}
	}

	protected ImageComponent createComponent() {
		return gameLoop.createComponent(ImageComponent.class);
	}
}
