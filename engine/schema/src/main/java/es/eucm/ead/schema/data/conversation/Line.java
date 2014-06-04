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

package es.eucm.ead.schema.data.conversation;

import javax.annotation.Generated;

/**
 * Something said by one of the speakers in a conversation.
 * 
 */
@Generated("org.jsonschema2pojo")
public class Line {

	/**
	 * How this line is to be rendered. Default is 'text', but we want to
	 * support 'audio', among others
	 * 
	 */
	private String media = "text";
	/**
	 * Media-dependent contents; may be an i18n key, or an asset name, or ...
	 * 
	 */
	private String value;

	/**
	 * How this line is to be rendered. Default is 'text', but we want to
	 * support 'audio', among others
	 * 
	 */
	public String getMedia() {
		return media;
	}

	/**
	 * How this line is to be rendered. Default is 'text', but we want to
	 * support 'audio', among others
	 * 
	 */
	public void setMedia(String media) {
		this.media = media;
	}

	/**
	 * Media-dependent contents; may be an i18n key, or an asset name, or ...
	 * 
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Media-dependent contents; may be an i18n key, or an asset name, or ...
	 * 
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
