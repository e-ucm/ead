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
package es.eucm.ead.engine.systems.effects;

import ashley.core.Component;
import ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.schema.effects.RemoveComponent;

/**
 * Executes {@link RemoveComponent}.
 * 
 * Created by Javier Torrente on 24/04/14.
 */
public class RemoveComponentExecutor extends EffectExecutor<RemoveComponent> {

	// Needed to convert class alias into class names (e.g. textbutton =>
	// TextButton).
	private GameAssets gameAssets;

	public RemoveComponentExecutor(GameAssets gameAssets) {
		this.gameAssets = gameAssets;
	}

	@Override
	public void execute(Entity owner, RemoveComponent effect) {
		// Build component to be removed
		String classAlias = effect.getComponent();
		boolean correct = true;
		try {
			Class componentClass = gameAssets.getClass(classAlias);
			// Check class returned is not null and also that it is a component
			// subclass
			if (componentClass == null
					|| !ClassReflection.isAssignableFrom(Component.class,
							componentClass)) {
				correct = false;
			} else {
				// Remove the component
				owner.remove(componentClass);
			}
		} catch (SerializationException exception) {
			correct = false;
		}

		if (!correct) {
			Gdx.app.error(
					"RemoveComponentExecutor",
					"The effect could not be executed because the class alias provided was not found to match an existing component class.");
		}
	}
}
