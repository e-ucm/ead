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

import com.badlogic.gdx.graphics.Texture;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.components.Transformation;
import es.eucm.ead.schema.editor.actors.EditorScene;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.editor.game.EditorGame;
import es.eucm.ead.schema.renderers.Image;

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
	public EditorGame createGame(String title, String description, int width,
			int height) {
		EditorGame game = new EditorGame();
		game.setNotes(new Note());
		game.getNotes().setTitle(title);
		game.getNotes().setDescription(description);
		game.setWidth(width);
		game.setHeight(height);
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
	public EditorGame createGame(String title, String description) {
		return createGame(title, description, 0, 0);
	}

	/**
	 * Creates an empty scene with the given name
	 * 
	 * @param name
	 *            the name for the scene
	 * @return the scene created
	 */
	public EditorScene createScene(String name) {
		EditorScene scene = new EditorScene();
		scene.setName(name);
		scene.setNotes(new Note());
		return scene;
	}

	/**
	 * Creates a scene element with a image as renderer, and default
	 * transformation (position 0,0, scale: 1, rotation 0)
	 * 
	 * @param imagePath
	 *            the path to the image. If the image is not contained by the
	 *            game assets, is copied to them and then loaded
	 * @return the scene element created
	 */
	public SceneElement createSceneElement(String imagePath) {
		EditorGameAssets assets = controller.getEditorGameAssets();

		String newPath = imagePath;
		// If image path is not loaded
		if (!assets.isLoaded(imagePath, Texture.class)) {
			// Check if the image is inside the project
			if (assets.resolve(imagePath).exists()) {
				assets.load(imagePath, Texture.class);
			} else {
				// If not, try to bring it to the project
				newPath = controller.getEditorGameAssets().copyAndLoad(
						imagePath, Texture.class);
			}
			controller.getEditorGameAssets().finishLoading();
		}

		Texture texture = controller.getEditorGameAssets().get(newPath,
				Texture.class);
		SceneElement sceneElement = new SceneElement();
		Image renderer = new Image();
		renderer.setUri(newPath);
		sceneElement.setRenderer(renderer);
		// Center the origin
		Transformation transformation = new Transformation();
		transformation.setOriginX(texture.getWidth() / 2.0f);
		transformation.setOriginY(texture.getHeight() / 2.0f);
		sceneElement.setTransformation(transformation);
		return sceneElement;
	}
}
