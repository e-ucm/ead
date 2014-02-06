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
package es.eucm.ead.engine.effects;

import com.badlogic.gdx.Gdx;
import es.eucm.ead.engine.actors.SceneElementEngineObject;
import es.eucm.ead.schema.effects.ChangeRenderer;

public class ChangeRendererEngineObject extends
		EffectEngineObject<ChangeRenderer> {

	@Override
	protected boolean delegate(float delta) {
		return true;
	}

	@Override
	public void initialize(ChangeRenderer schemaObject) {
		boolean rendererChanged = false;
		if (actor instanceof SceneElementEngineObject) {
			if (schemaObject.isSetInitialRenderer()) {
				rendererChanged = ((SceneElementEngineObject) actor)
						.restoreInitialRenderer();
				if (rendererChanged) {
					Gdx.app.log("ChangeRenderer",
							"Attempted to restore the initial renderer. Successful!");
				} else {
					Gdx.app.log(
							"ChangeRenderer",
							"Attempted to restore the initial renderer. Unsuccessful since the initial renderer was already being used. ");
				}
			} else {
				rendererChanged = ((SceneElementEngineObject) actor)
						.setRenderer(schemaObject.getNewRenderer());
				if (rendererChanged) {
					Gdx.app.log(
							"ChangeRenderer",
							"Attempted to set a new renderer. Successful! NewRenderer="
									+ (schemaObject.getNewRenderer() != null ? schemaObject
											.getNewRenderer().toString()
											: "null"));
				} else {
					Gdx.app.log(
							"ChangeRenderer",
							"Attempted to set a new renderer. Unsuccessful since the old and new renderers were the same. ");
				}
			}
		} else {
			Gdx.app.error("ChangeRenderer",
					"Attempted to change the renderer of an actor that is not an sceneElement.");
		}
	}

}
