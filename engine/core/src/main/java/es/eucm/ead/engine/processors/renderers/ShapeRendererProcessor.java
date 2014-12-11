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

import ashley.core.Component;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.assets.ScaledTexture;
import es.eucm.ead.engine.components.renderers.shape.ShapeRendererComponent;
import es.eucm.ead.engine.components.renderers.shape.ShapeToPixmap;
import es.eucm.ead.engine.utils.ShapeToCollider;
import es.eucm.ead.schema.renderers.ShapeRenderer;

/**
 * Created by Javier Torrente on 8/06/14.
 */
public class ShapeRendererProcessor extends RendererProcessor<ShapeRenderer> {

	private static final int N_SIDES_FOR_CIRCLE = 50;

	private ShapeToPixmap shapeToPixmap;

	public ShapeRendererProcessor(GameLoop engine) {
		super(engine, null);
		shapeToPixmap = new ShapeToPixmap();
	}

	@Override
	public Component getComponent(ShapeRenderer component) {
		ShapeRendererComponent shapeRendererComponent = gameLoop
				.createComponent(ShapeRendererComponent.class);
		// Set collider
		Array<Polygon> collider = new Array<Polygon>();
		collider.add(ShapeToCollider.buildShapeCollider(component.getShape(),
				N_SIDES_FOR_CIRCLE));
		shapeRendererComponent.setCollider(collider);
		// Set pixmap
		Pixmap pixmap = shapeToPixmap.createShape(component);
		shapeRendererComponent.setTexture(new ScaledTexture(
				new Texture(pixmap), 1.0f));
		pixmap.dispose();
		return shapeRendererComponent;
	}
}
