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
package es.eucm.ead.engine.processors;

import ashley.core.Component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import es.eucm.ead.engine.ComponentLoader;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.schema.components.ModelComponent;

public class RefProcessor<T extends ModelComponent> extends
		ComponentProcessor<T> implements AssetLoadedCallback<Object> {

	private ComponentLoader componentLoader;

	private GameAssets gameAssets;

	private T loadedComponent;

	public RefProcessor(GameLoop gameLoop, GameAssets gameAssets,
			ComponentLoader componentLoader) {
		super(gameLoop);
		this.componentLoader = componentLoader;
		this.gameAssets = gameAssets;
	}

	@Override
	public Component getComponent(T component) {
		try {
			Field field = ClassReflection.getDeclaredField(
					component.getClass(), "uri");
			field.setAccessible(true);
			loadedComponent = null;
			gameAssets.get(field.get(component) + "", Object.class, this);
			gameAssets.finishLoading();
			return componentLoader.toEngineComponent(loadedComponent);
		} catch (ReflectionException e) {
			Gdx.app.error("RefProcessor", "No uri field in " + component);
			return null;
		}
	}

	@Override
	public void loaded(String fileName, Object asset) {
		this.loadedComponent = (T) asset;
	}
}
