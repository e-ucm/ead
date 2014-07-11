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
package es.eucm.ead.editor.indexes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.schema.effects.Effect;

/**
 * An index relating the short string representation of an effect (translated to
 * the current language) and its class
 */
public class EffectsIndex extends ControllerIndex {

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(Controller controller) {
		I18N i18N = controller.getApplicationAssets().getI18N();
		Array<String> bindings = controller.getApplicationAssets()
				.fromJsonPath(Array.class, GameAssets.ENGINE_BINDINGS);

		String classPackage = null;
		for (String line : bindings) {
			if (line.contains(".")) {
				classPackage = line;
			} else {
				try {
					Class effectClass = ClassReflection.forName(classPackage
							+ "." + line);

					if (ClassReflection.isAssignableFrom(Effect.class,
							effectClass)) {
						addTerm(i18N.m(effectClass.getSimpleName()),
								effectClass);
					}
				} catch (ReflectionException e) {
					Gdx.app.error("EffectsIndex", "No class for "
							+ classPackage + "." + line, e);
				}
			}
		}
	}
}
