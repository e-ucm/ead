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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.HashMap;
import java.util.Map;

public class GameLayers extends WidgetGroup {

	public static final String HUD = "hud";

	public static final String SCENE = "scene";

	public static final String SCENE_CONTENT = "scene_content";

	public static final String SCENE_HUD = "scene_hud";

	private Map<String, Group> layers;

	public GameLayers() {
		layers = new HashMap<String, Group>();
		addLayers();
	}

	private void addLayers() {
		Group hud = new Group();
		Group scene = new Group();
		Group sceneHud = new Group();
		Group sceneContent = new Group();

		layers.put(HUD, hud);
		layers.put(SCENE, scene);
		layers.put(SCENE_CONTENT, sceneContent);
		layers.put(SCENE_HUD, sceneHud);

		// Create hierarchy
		// - hud
		// - scene
		// +-- scene_hud
		// +-- scene_content
		addActor(scene);
		addActor(hud);

		scene.addActor(sceneContent);
		scene.addActor(sceneHud);
	}

	public void setLayer(String layerName, Group group) {
		Group layer = layers.get(layerName);
		if (layer != null) {
			layer.clearChildren();
			// FIXME remove all entities in the layer
			layer.addActor(group);
		}
	}

	public void updateWorldSize(int width, int height) {
		getStage().setViewport(new FitViewport(width, height));
		getStage().getViewport().update(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight(), true);
	}
}
