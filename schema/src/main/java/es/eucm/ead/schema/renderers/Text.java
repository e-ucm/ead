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
package es.eucm.ead.schema.renderers;

import javax.annotation.Generated;
import es.eucm.ead.schema.components.Color;

@Generated("org.jsonschema2pojo")
public class Text extends Renderer {

	private Color color;
	/**
	 * Uri to the file
	 * 
	 */
	private String font;
	private float scale = 1.0F;
	private String text;

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Uri to the file
	 * 
	 */
	public String getFont() {
		return font;
	}

	/**
	 * Uri to the file
	 * 
	 */
	public void setFont(String font) {
		this.font = font;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Text))
			return false;

		Text text1 = (Text) o;

		if (Float.compare(text1.scale, scale) != 0)
			return false;
		if (color != null ? !color.equals(text1.color) : text1.color != null)
			return false;
		if (font != null ? !font.equals(text1.font) : text1.font != null)
			return false;
		if (text != null ? !text.equals(text1.text) : text1.text != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = color != null ? color.hashCode() : 0;
		result = 31 * result + (font != null ? font.hashCode() : 0);
		result = 31 * result
				+ (scale != +0.0f ? Float.floatToIntBits(scale) : 0);
		result = 31 * result + (text != null ? text.hashCode() : 0);
		return result;
	}
}
