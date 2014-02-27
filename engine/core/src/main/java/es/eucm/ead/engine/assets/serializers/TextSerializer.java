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
package es.eucm.ead.engine.assets.serializers;

import es.eucm.ead.engine.Assets;
import es.eucm.ead.schema.renderers.Text;
import es.eucm.ead.schema.renderers.TextStyle;

/**
 * Created by Javier Torrente on 27/02/14.
 */
public class TextSerializer extends SimpleSerializer<Text> {

	/**
	 * Path of the textstyle file that defines the default style to be applied
	 * to those texts that do not declare "style" and "style-ref"
	 */
	public static final String DEFAULT_TEXT_STYLE_PATH = "textstyles/defaulttextstyle.json";

	public TextSerializer(Assets assets) {
		super(assets, "styleref", TextStyle.class);

	}

	protected void doExtraDependenciesProcessing(Text o) {
		super.doExtraDependenciesProcessing(o);
		// If neither embedded style nor external style are used, then schedule
		// default text style for loading, because it will be needed
		if (o.getStyle() == null && o.getStyleref() == null) {
			assets.addDependency(DEFAULT_TEXT_STYLE_PATH, TextStyle.class);
		}
	}

}
