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
package es.eucm.ead.engine;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import es.eucm.ead.engine.actors.SceneElementEngineObject;
import es.eucm.ead.engine.actors.SceneEngineObject;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.effects.Effect;

/**
 * A view to show a scene
 */
public class GameView extends WidgetGroup {

	private EngineAssets engineAssets;

	private Group sceneNode;

	private SceneEngineObject currentScene;

	public GameView(EngineAssets engineAssets) {
		this.engineAssets = engineAssets;
		sceneNode = new Group();
		addActor(sceneNode);
	}

	/**
	 * Sets a scene. All the assets required by the scene must be already
	 * loaded. This method is for internal usage only. Use
	 * {@link GameLoop#loadScene(String)} to load a scene
	 * 
	 * @param scene
	 *            the scene schema object
	 */
	public void setScene(Scene scene) {
		SceneEngineObject sceneActor = engineAssets.getEngineObject(scene);
		setScene(sceneActor);
	}

	/**
	 * 
	 * @return Returns the current scene node
	 */
	public SceneEngineObject getCurrentScene() {
		return currentScene;
	}

	protected void setScene(SceneEngineObject scene) {
		sceneNode.clearChildren();
		sceneNode.addActor(scene);
		if (currentScene != null) {
			currentScene.dispose();
		}
		currentScene = scene;
	}

	/**
	 * Adds an effect to the root view
	 * 
	 * @param effect
	 *            the effect
	 */
	public void addEffect(Effect effect) {
		addAction((Action) engineAssets.getEngineObject(effect));
	}

	public void addActor(SceneElement sceneElement) {
		SceneElementEngineObject actor = engineAssets
				.getEngineObject(sceneElement);
		addActor(actor);
	}
}
