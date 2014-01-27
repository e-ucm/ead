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
import es.eucm.ead.schema.components.Bounds;
import es.eucm.ead.schema.components.Dimension;

@Generated("org.jsonschema2pojo")
public class NinePatch extends Renderer {

	private Bounds bounds;
	/**
	 * Dimensions
	 * 
	 */
	private Dimension size;
	/**
	 * Uri to the file
	 * 
	 */
	private String uri;

	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	/**
	 * Dimensions
	 * 
	 */
	public Dimension getSize() {
		return size;
	}

	/**
	 * Dimensions
	 * 
	 */
	public void setSize(Dimension size) {
		this.size = size;
	}

	/**
	 * Uri to the file
	 * 
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Uri to the file
	 * 
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof NinePatch))
			return false;

		NinePatch ninePatch = (NinePatch) o;

		if (bounds != null ? !bounds.equals(ninePatch.bounds)
				: ninePatch.bounds != null)
			return false;
		if (size != null ? !size.equals(ninePatch.size)
				: ninePatch.size != null)
			return false;
		if (uri != null ? !uri.equals(ninePatch.uri) : ninePatch.uri != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = bounds != null ? bounds.hashCode() : 0;
		result = 31 * result + (size != null ? size.hashCode() : 0);
		result = 31 * result + (uri != null ? uri.hashCode() : 0);
		return result;
	}
}
