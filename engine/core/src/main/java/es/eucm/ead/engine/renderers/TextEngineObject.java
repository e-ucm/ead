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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import es.eucm.ead.schema.renderers.Text;

public class TextEngineObject extends RendererEngineObject<Text> {

	private BitmapFont bitmapFont;

	private String text;

	private Color color;

	private TextBounds bounds;

	private float scale;

	@Override
	public void initialize(Text schemaObject) {
		text = gameLoop.getAssets().getI18N().m(schemaObject.getText());
		scale = schemaObject.getScale();

		es.eucm.ead.schema.components.Color c = schemaObject.getColor();
		color = c == null ? Color.WHITE : new Color(c.getR(), c.getG(),
				c.getB(), c.getA());

		String fontFile = schemaObject.getFont();
		if (fontFile == null
				|| !gameLoop.getAssets().resolve(fontFile).exists()) {
			bitmapFont = gameLoop.getAssets().getDefaultFont();
		} else {
			bitmapFont = gameLoop.getAssets().get(schemaObject.getFont(),
					BitmapFont.class);
		}

		bounds = bitmapFont.getBounds(text);
	}

	@Override
	public void draw(Batch batch) {
		bitmapFont.setColor(color);
		bitmapFont.setScale(scale);
		bitmapFont.draw(batch, text, 0, getHeight());
	}

	@Override
	public float getHeight() {
		return bounds.height;
	}

	@Override
	public float getWidth() {
		return bounds.width;
	}

}
