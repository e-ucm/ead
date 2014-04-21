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

package es.eucm.ead.schema.renderers;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Text extends Renderer {

	/**
	 * Comma-separated list of indexed properties (available for full-text
	 * search in editor)
	 * 
	 */
	private String indexed = "text";
	private String text;
	/**
	 * this (optional) uri should point to a textstyle.json object. This allows
	 * defining the style in a separate file, that can be user-defined or
	 * system-defined, which allows the creator of the game specify consistent
	 * text styles for his/her games
	 * 
	 */
	private String styleref;
	private TextStyle style;

	/**
	 * Comma-separated list of indexed properties (available for full-text
	 * search in editor)
	 * 
	 */
	public String getIndexed() {
		return indexed;
	}

	/**
	 * Comma-separated list of indexed properties (available for full-text
	 * search in editor)
	 * 
	 */
	public void setIndexed(String indexed) {
		this.indexed = indexed;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * this (optional) uri should point to a textstyle.json object. This allows
	 * defining the style in a separate file, that can be user-defined or
	 * system-defined, which allows the creator of the game specify consistent
	 * text styles for his/her games
	 * 
	 */
	public String getStyleref() {
		return styleref;
	}

	/**
	 * this (optional) uri should point to a textstyle.json object. This allows
	 * defining the style in a separate file, that can be user-defined or
	 * system-defined, which allows the creator of the game specify consistent
	 * text styles for his/her games
	 * 
	 */
	public void setStyleref(String styleref) {
		this.styleref = styleref;
	}

	public TextStyle getStyle() {
		return style;
	}

	public void setStyle(TextStyle style) {
		this.style = style;
	}

}
