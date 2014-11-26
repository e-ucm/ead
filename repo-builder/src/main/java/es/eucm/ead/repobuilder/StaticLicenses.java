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
package es.eucm.ead.repobuilder;

import es.eucm.ead.schema.editor.components.repo.licenses.DefaultLicenses;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jtorrente on 19/11/14.
 */
public class StaticLicenses {

	private static final Map<String, DefaultLicenses> defaultLicenses = new HashMap<String, DefaultLicenses>();

	public static DefaultLicenses get(String strLicense) {
		try {
			return get(DefaultLicenses.License.fromValue(strLicense));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static DefaultLicenses get(DefaultLicenses.License l) {
		if (!defaultLicenses.containsKey(l.toString())) {
			DefaultLicenses license = new DefaultLicenses();
			license.setLicense(l);
			defaultLicenses.put(l.toString(), license);
		}

		return defaultLicenses.get(l.toString());
	}

}
