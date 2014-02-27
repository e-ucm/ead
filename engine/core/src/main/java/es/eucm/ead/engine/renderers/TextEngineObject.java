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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import es.eucm.ead.engine.assets.serializers.TextSerializer;
import es.eucm.ead.schema.renderers.Text;
import es.eucm.ead.schema.renderers.TextStyle;

import java.util.logging.FileHandler;

public class TextEngineObject extends RendererEngineObject<Text> {

	/**
	 * Path of the textstyle file that defines the default style to be applied
	 * to those texts that do not declare "style" and "style-ref"
	 */
	private final String DEFAULT_TEXT_STYLE_PATH = TextSerializer.DEFAULT_TEXT_STYLE_PATH;

	private String text;

	private TextBounds bounds;

	private BitmapFont bitmapFont;

	private float scale;

	private Color color;

	private TextStyle style;

	@Override
	public void initialize(Text schemaObject) {
		text = gameLoop.getAssets().getI18N().m(schemaObject.getText());
		style = null;

		String styleRefPath = schemaObject.getStyleref();
		TextStyle embeddedTextStyle = schemaObject.getStyle();

		// If the embedded text style is not null, use this one
		if (embeddedTextStyle != null) {
			style = embeddedTextStyle;
		}

		// If embedded style is null but a style-ref file is declared, try to
		// use this one
		else if (styleRefPath != null) {
			FileHandle fh = gameLoop.getAssets().resolve(styleRefPath);
			if (fh != null && fh.exists()) {
				TextStyle styleRef = gameLoop.getAssets().get(styleRefPath,
						TextStyle.class);
				if (styleRef != null) {
					style = styleRef;
				} else {
					Gdx.app.error(
							"Text",
							"A style-ref file ("
									+ styleRefPath
									+ ") was declared for this piece of text. However, this file could not be loaded. Text="
									+ schemaObject.getText());
				}
			}
		}

		// If both style-ref and style are null, or the style-ref file could not
		// be loaded, then try to load default text style
		if (style == null) {
			FileHandle fh = gameLoop.getAssets().resolve(
					DEFAULT_TEXT_STYLE_PATH);
			if (fh != null && fh.exists()) {
				TextStyle defaultTextStyle = gameLoop.getAssets().get(
						DEFAULT_TEXT_STYLE_PATH, TextStyle.class);
				if (defaultTextStyle != null) {
					style = defaultTextStyle;
				} else {
					Gdx.app.error(
							"Text",
							"This piece of text does not have style associated and the default text style ("
									+ DEFAULT_TEXT_STYLE_PATH
									+ ") is missing. Text="
									+ schemaObject.getText());
				}
			}
		}

		// IF style is still null, create a basic one
		if (style == null) {
			style = new TextStyle();
			scale = 1.0F;
			color = Color.WHITE;
			bitmapFont = gameLoop.getAssets().getDefaultFont();
		} else {
			scale = style.getScale();

			es.eucm.ead.schema.components.Color c = style.getColor();
			color = c == null ? Color.WHITE : new Color(c.getR(), c.getG(),
					c.getB(), c.getA());

			String fontFile = style.getFont();
			if (fontFile == null
					|| !gameLoop.getAssets().resolve(fontFile).exists()) {
				bitmapFont = gameLoop.getAssets().getDefaultFont();
			} else {
				bitmapFont = gameLoop.getAssets().get(style.getFont(),
						BitmapFont.class);
			}
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
