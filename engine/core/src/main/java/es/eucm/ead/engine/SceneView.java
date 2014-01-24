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
package es.eucm.ead.engine;

import com.badlogic.gdx.scenes.scene2d.Group;

import es.eucm.ead.engine.actors.SceneActor;
import es.eucm.ead.schema.actions.Action;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;

/**
 * A view to show a scene
 */
public class SceneView extends Group {

	private Factory factory;

	private SceneActor currentScene;

	public SceneView(Factory factory) {
		this.factory = factory;
	}

	/**
	 * Sets a scene. All the assets required by the scene must be already
	 * loaded. This method is for internal usage only. Use
	 * {@link GameController#loadScene(String)} to load a scene
	 * 
	 * @param scene
	 *            the scene schema object
	 */
	public void setScene(Scene scene) {
		SceneActor sceneActor = factory.getEngineObject(scene);
		setScene(sceneActor);
	}

	public SceneActor getCurrentScene() {
		return currentScene;
	}

	protected void setScene(SceneActor scene) {
		this.clearChildren();
		this.addActor(scene);
		if (currentScene != null) {
			currentScene.dispose();
		}
		currentScene = scene;
	}

	/**
	 * Add an action to the root view
	 * 
	 * @param action
	 *            the action
	 */
	public void addAction(Action action) {
		addAction((com.badlogic.gdx.scenes.scene2d.Action) Engine.factory
				.getEngineObject(action));
	}

	/**
	 * Effectively adds the scene element to the scene, after all its resources
	 * has been loaded. This method is for internal usage only. Use
	 * {@link GameController#loadSceneElement(es.eucm.ead.schema.actors.SceneElement)}
	 * to add scene elements to the scene
	 * 
	 * @param sceneElement
	 *            the scene element to add
	 */
	public void addSceneElement(SceneElement sceneElement) {
		currentScene.addActor(sceneElement);
	}
}
