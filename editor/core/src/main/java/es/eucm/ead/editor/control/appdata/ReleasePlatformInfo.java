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

package es.eucm.ead.editor.control.appdata;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

/**
 * Simple object for pairing the installer url with an os version
 * 
 */
@Generated("org.jsonschema2pojo")
public class ReleasePlatformInfo {

	/**
	 * The installer version: win32 | win64 | mac, etc.
	 * 
	 */
	private ReleasePlatformInfo.Os os = ReleasePlatformInfo.Os
			.fromValue("multiplatform");
	/**
	 * The appropriate url for downloading the installer for this particular os
	 * 
	 */
	private String url;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * The installer version: win32 | win64 | mac, etc.
	 * 
	 */
	public ReleasePlatformInfo.Os getOs() {
		return os;
	}

	/**
	 * The installer version: win32 | win64 | mac, etc.
	 * 
	 */
	public void setOs(ReleasePlatformInfo.Os os) {
		this.os = os;
	}

	/**
	 * The appropriate url for downloading the installer for this particular os
	 * 
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * The appropriate url for downloading the installer for this particular os
	 * 
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Generated("org.jsonschema2pojo")
	public static enum Os {

		MULTIPLATFORM("multiplatform"), WIN_32("win32"), WIN_64("win64"), MACOSX(
				"macosx"), LINUX_I_386("linux-i386"), LINUX_AMD_64(
				"linux-amd64");
		private final String value;
		private static Map<String, ReleasePlatformInfo.Os> constants = new HashMap<String, ReleasePlatformInfo.Os>();

		static {
			for (ReleasePlatformInfo.Os c : ReleasePlatformInfo.Os.values()) {
				constants.put(c.value, c);
			}
		}

		private Os(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public static ReleasePlatformInfo.Os fromValue(String value) {
			ReleasePlatformInfo.Os constant = constants.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}

	}

}
