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

package es.eucm.ead.schema.editor.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import es.eucm.ead.schema.components.ModelComponent;

/**
 * A simple editor component for elements that are meant to be shared and reused
 * through the repository.
 * 
 */
@Generated("org.jsonschema2pojo")
public class RepoElement extends ModelComponent {

	/**
	 * Relative url where the thumbnail for this element is placed
	 * 
	 */
	private String thumbnail;
	private Author author;
	/**
	 * Information associated to the license of the resource, as provided by the
	 * author. Only creative commons and public domain licenses supported.
	 * 
	 */
	private RepoElement.License license;
	/**
	 * A brief description of the resource, including what kind of contents it
	 * has, and how many (e.g. number of animations, frames, etc.)
	 * 
	 */
	private String description;
	/**
	 * A list of tags - useful for searching and grouping resources
	 * 
	 */
	private List<String> tags = new ArrayList<String>();

	/**
	 * Relative url where the thumbnail for this element is placed
	 * 
	 */
	public String getThumbnail() {
		return thumbnail;
	}

	/**
	 * Relative url where the thumbnail for this element is placed
	 * 
	 */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	/**
	 * Information associated to the license of the resource, as provided by the
	 * author. Only creative commons and public domain licenses supported.
	 * 
	 */
	public RepoElement.License getLicense() {
		return license;
	}

	/**
	 * Information associated to the license of the resource, as provided by the
	 * author. Only creative commons and public domain licenses supported.
	 * 
	 */
	public void setLicense(RepoElement.License license) {
		this.license = license;
	}

	/**
	 * A brief description of the resource, including what kind of contents it
	 * has, and how many (e.g. number of animations, frames, etc.)
	 * 
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * A brief description of the resource, including what kind of contents it
	 * has, and how many (e.g. number of animations, frames, etc.)
	 * 
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * A list of tags - useful for searching and grouping resources
	 * 
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * A list of tags - useful for searching and grouping resources
	 * 
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	@Generated("org.jsonschema2pojo")
	public static enum License {

		PUBLIC_DOMAIN("public-domain"), CC_BY("cc-by"), CC_BY_ND("cc-by-nd"), CC_BY_SA(
				"cc-by-sa"), CC_BY_NC("cc-by-nc"), CC_BY_ND_NC("cc-by-nd-nc"), CC_BY_SA_NC(
				"cc-by-sa-nc");
		private final String value;
		private static Map<String, RepoElement.License> constants = new HashMap<String, RepoElement.License>();

		static {
			for (RepoElement.License c : RepoElement.License.values()) {
				constants.put(c.value, c);
			}
		}

		private License(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public static RepoElement.License fromValue(String value) {
			RepoElement.License constant = constants.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}

	}

}
