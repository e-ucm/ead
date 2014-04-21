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

@Generated("org.jsonschema2pojo")
public enum OS {

	MULTIPLATFORM("multiplatform"), MULTIPLATFORM_JRE_WIN_32(
			"multiplatform-jre-win32"), MULTIPLATFORM_JRE_WIN_64(
			"multiplatform-jre-win64"), MULTIPLATFORM_JRE_MACOSX(
			"multiplatform-jre-macosx"), MULTIPLATFORM_JRE_LINUX_I_386(
			"multiplatform-jre-linux-i386"), MULTIPLATFORM_JRE_LINUX_AMD_64(
			"multiplatform-jre-linux-amd64"), WIN_64("win64"), MACOSX("macosx"), LINUX_I_386(
			"linux-i386"), LINUX_AMD_64("linux-amd64");
	private final String value;
	private static Map<String, OS> constants = new HashMap<String, OS>();

	static {
		for (OS c : OS.values()) {
			constants.put(c.value, c);
		}
	}

	private OS(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}

	public static OS fromValue(String value) {
		OS constant = constants.get(value);
		if (constant == null) {
			throw new IllegalArgumentException(value);
		} else {
			return constant;
		}
	}

}
