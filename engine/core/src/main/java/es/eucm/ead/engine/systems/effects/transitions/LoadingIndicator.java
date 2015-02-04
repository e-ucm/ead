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
package es.eucm.ead.engine.systems.effects.transitions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import es.eucm.ead.engine.components.renderers.shape.ShapeToPixmap;
import es.eucm.ead.schema.data.shape.Circle;
import es.eucm.ead.schema.renderers.ShapeRenderer;

/**
 * Simple dot animation to display activity (i.e. loading in progress). Dots
 * increase/decrease size while they also fade in/out
 * 
 * Created by jtorrente on 3/02/15.
 */
public class LoadingIndicator extends Group {

	/**
	 * Creates default animation with 3 dots of 10 DPI radius
	 */
	public LoadingIndicator() {
		this(3, 10);
	}

	/**
	 * Creates an animation with the given number of dots (@param nDots). Radius
	 * of each dot, in DPI, is specified through @param radius.
	 */
	public LoadingIndicator(int nDots, int radius) {
		float dpiRadius = radius * Gdx.graphics.getDensity();
		// Create dot texture (simple circle)
		ShapeRenderer shapeRenderer = new ShapeRenderer();
		Circle circle = new Circle();
		circle.setRadius(Math.round(dpiRadius));
		shapeRenderer.setShape(circle);
		Pixmap dotPixmap = new ShapeToPixmap().createShape(shapeRenderer);
		Texture dotTexture = new Texture(dotPixmap);
		TextureRegion textureRegion = new TextureRegion(dotTexture);
		TextureRegionDrawable drawable = new TextureRegionDrawable(
				textureRegion);

		// Create dots
		float x = 0;
		float delay = 0.1F;
		float duration = 0.4F;
		for (int i = 0; i < nDots; i++) {
			Image dot = new Image(drawable);
			dot.setOriginX(dpiRadius / 2.0F);
			dot.setOriginY(dpiRadius / 2.0F);
			dot.setX(x);
			x += dpiRadius * 2.5F;
			addActor(dot);

			dot.addAction(Actions.sequence(Actions.delay(delay * i), Actions
					.forever(Actions.sequence(Actions.alpha(0.0F, 0.0F),
							Actions.scaleTo(0.0F, 0.0F), Actions.parallel(
									Actions.alpha(0.7F, duration),
									Actions.scaleTo(1.0F, 1.0F, duration)),
							Actions.parallel(Actions.alpha(0.0F, duration),
									Actions.scaleTo(0.0F, 0.0F, duration))))));
		}
	}
}
