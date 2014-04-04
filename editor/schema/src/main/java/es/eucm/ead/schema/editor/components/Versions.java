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

import javax.annotation.Generated;
import es.eucm.ead.schema.components.ModelComponent;

/**
 * A simple note for annotating stuff on editor schema.
 * 
 */
@Generated("org.jsonschema2pojo")
public class Versions extends ModelComponent {

	/**
	 * The version number of the editor used to create this game (e.g. 2.0.0).
	 * Gets updated every time the game is saved
	 * 
	 */
	private String appVersion;
	/**
	 * The version number of the model API of this game. See
	 * https://github.com/e-ucm/ead/wiki/Model-API-versions.
	 * 
	 */
	private String modelVersion;

	/**
	 * The version number of the editor used to create this game (e.g. 2.0.0).
	 * Gets updated every time the game is saved
	 * 
	 */
	public String getAppVersion() {
		return appVersion;
	}

	/**
	 * The version number of the editor used to create this game (e.g. 2.0.0).
	 * Gets updated every time the game is saved
	 * 
	 */
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	/**
	 * The version number of the model API of this game. See
	 * https://github.com/e-ucm/ead/wiki/Model-API-versions.
	 * 
	 */
	public String getModelVersion() {
		return modelVersion;
	}

	/**
	 * The version number of the model API of this game. See
	 * https://github.com/e-ucm/ead/wiki/Model-API-versions.
	 * 
	 */
	public void setModelVersion(String modelVersion) {
		this.modelVersion = modelVersion;
	}

}
