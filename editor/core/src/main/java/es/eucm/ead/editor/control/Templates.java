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
package es.eucm.ead.editor.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.editor.components.Date;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * This class gives several methods to create and initialized common schema
 * objects. It should be used whenever a new schema object is needed
 */
public class Templates {

	private Controller controller;

	public Templates(Controller controller) {
		this.controller = controller;
	}

	/**
	 * Creates a game with the given title and description. It doesn't
	 * initialize any other attribute
	 * 
	 * @param title
	 *            the title
	 * @param description
	 *            the description
	 * @param width
	 *            game's width
	 * @param height
	 *            game's height
	 * @return the game created
	 */
	public ModelEntity createGame(String title, String description, int width,
			int height) {
		ModelEntity game = new ModelEntity();
		Documentation note = Q.getComponent(game, Documentation.class);
		note.setName(title);
		note.setDescription(description);
		GameData gameData = Q.getComponent(game, GameData.class);
		gameData.setWidth(width);
		gameData.setHeight(height);
		return game;
	}

	/**
	 * Creates a game with the given title and description. It doesn't
	 * initialize any other attribute
	 * 
	 * @param title
	 *            the title
	 * @param description
	 *            the description
	 * @return the game created
	 */
	public ModelEntity createGame(String title, String description) {
		return createGame(title, description, 0, 0);
	}

	/**
	 * Creates an empty scene with the given name
	 * 
	 * @param name
	 *            the name for the scene
	 * @return the scene created
	 */
	public ModelEntity createScene(String name) {
		ModelEntity scene = new ModelEntity();
		Q.getComponent(scene, Documentation.class).setName(name);
		Q.getComponent(scene, Date.class).setDate(
				System.currentTimeMillis() + "");
		return scene;
	}

	/**
	 * Creates an element in the center of the screen.
	 * 
	 * @param imagePath
	 * @return
	 */
	public ModelEntity createSceneElement(String imagePath, boolean filled) {
		GameData data = Q.getComponent(controller.getModel().getGame(),
				GameData.class);
		return createSceneElement(imagePath, data.getWidth() * .5f,
				data.getHeight() * .5f, filled);
	}

	/**
	 * Creates an element.
	 * 
	 * @param imagePath
	 * @param x
	 *            the center x coordinate of the scene element
	 * @param y
	 *            the center y coordinate of the scene element
	 */
	public ModelEntity createSceneElement(String imagePath, float x, float y) {
		return createSceneElement(imagePath, x, y, false);
	}

	/**
	 * Creates a scene element with a image as renderer. Calculates the origin
	 * at the center.
	 * 
	 * @param imagePath
	 *            the path to the image. If the image is not contained by the
	 *            game assets, is copied to them and then loaded.
	 * @param x
	 *            the center x coordinate of the scene element
	 * @param y
	 *            the center y coordinate of the scene element
	 * @param filled
	 *            if the element should be filled
	 * @return the scene element created
	 */
	public ModelEntity createSceneElement(String imagePath, float x, float y,
			final boolean filled) {

		EditorGameAssets assets = controller.getEditorGameAssets();

		String newPath = assets.copyToProjectIfNeeded(imagePath, Texture.class);

		final ModelEntity sceneElement = new ModelEntity();
		assets.get(newPath, Texture.class, new AssetLoadedCallback<Texture>() {
			@Override
			public void loaded(String fileName, Texture texture) {

				// Center the origin
				sceneElement.setOriginX(texture.getWidth() * .5f);
				sceneElement.setOriginY(texture.getHeight() * .5f);

				if (filled) {
					GameData data = Q.getComponent(controller.getModel()
							.getGame(), GameData.class);
					Vector2 vector = Scaling.fill.apply(texture.getWidth(),
							texture.getHeight(), data.getWidth(),
							data.getHeight());
					sceneElement.setScaleX(vector.x / texture.getWidth());
					sceneElement.setScaleY(vector.y / texture.getHeight());
				}
			}
		}, true);
		sceneElement.setX(x - sceneElement.getOriginX());
		sceneElement.setY(y - sceneElement.getOriginY());

		Image renderer = new Image();
		renderer.setUri(newPath);

		sceneElement.getComponents().add(renderer);
		return sceneElement;
	}

	/**
	 * Creates and effect of the given class with valid default values
	 */
	public <T> T createEffect(Class<T> effectClass) {
		try {
			T effect = ClassReflection.newInstance(effectClass);

			if (effect instanceof GoScene) {
				defaultValues((GoScene) effect);
			}

			return effect;
		} catch (ReflectionException e) {
			Gdx.app.error("Templates", "Impossible to create effect", e);
		}
		return null;
	}

	private void defaultValues(GoScene goScene) {
		goScene.setSceneId(controller.getModel()
				.getResources(ResourceCategory.SCENE).keySet().iterator()
				.next());
	}
}
