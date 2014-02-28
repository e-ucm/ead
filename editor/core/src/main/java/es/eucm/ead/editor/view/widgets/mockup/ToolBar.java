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
package es.eucm.ead.editor.view.widgets.mockup;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.esotericsoftware.tablelayout.Cell;

/**
 * A simple Table with one row and background
 */
public class ToolBar extends Table {

	private Drawable stageBackground;

	private final Vector2 viewport;
	private final float prefHeight;

	/**
	 * Create a {@link ToolBar toolbar} with default style.
	 * 
	 * @param skin
	 *            the skin to use
	 */
	public ToolBar(Vector2 viewport, Skin skin) {
		super(skin);
		setBackground("blueBlackMedium");
		this.viewport = viewport;
		this.prefHeight = .075f;
	}

	/**
	 * Create a {@link ToolBar} with default style and custom
	 * height.
	 * 
	 * @param skin
	 *            the skin to use
	 */
	public ToolBar(Vector2 viewport, Skin skin, float prefHeight) {
		super(skin);
		setBackground("blueBlackMedium");
		this.viewport = viewport;
		this.prefHeight = prefHeight;
	}

	/**
	 * Create a {@link ToolBar} with the specified background.
	 */
	public ToolBar(Vector2 viewport, Skin skin, String drawableBackground) {
		super(skin);
		setBackground(drawableBackground);
		this.viewport = viewport;
		this.prefHeight = .075f;
	}

	@Override
	public Cell<?> row() {
		throw new IllegalStateException("There are no rows in a ToolBar");
	}

	@Override
	protected void drawBackground(Batch batch, float parentAlpha, float x,
			float y) {
		if (this.stageBackground != null) {
			Color color = getColor();
			batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
			Stage stage = getStage();
			this.stageBackground.draw(batch, 0, 0, stage.getWidth(),
					stage.getHeight());

		}
		super.drawBackground(batch, parentAlpha, x, y);
	}

	@Override
	public float getPrefHeight() {
		return this.viewport.x * this.prefHeight;
	}
}
