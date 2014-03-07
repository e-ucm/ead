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
package es.eucm.ead.editor.view.widgets.mockup.buttons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;

/**
 * An Image used in the NavigationPanel.
 */
public class Icon extends Image {

	private final Vector2 viewport;
	private final float prefWidth;

	/**
	 * Creates a squared icon with a size of 0.075 * screen's width.
	 */
	public Icon(Vector2 viewport, Drawable drawable) {
		super(drawable);
		setScaling(Scaling.fit);
		this.prefWidth = 0.075f;
		this.viewport = viewport;
	}

	public Icon(Vector2 viewport, Drawable drawable, float prefWidth) {
		super(drawable);
		setScaling(Scaling.fit);
		this.prefWidth = prefWidth;
		this.viewport = viewport;
	}

	@Override
	public float getPrefWidth() {
		return this.viewport == null ? 0 : this.viewport.x * this.prefWidth;
	}

	@Override
	public float getPrefHeight() {
		// We make sure it's a square
		return getPrefWidth();
	}
}
