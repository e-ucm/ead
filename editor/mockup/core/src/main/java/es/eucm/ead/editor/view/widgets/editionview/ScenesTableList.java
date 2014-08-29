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
package es.eucm.ead.editor.view.widgets.editionview;

import java.util.Collection;
import java.util.Map.Entry;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.tablelayout.Cell;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Model.Resource;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.model.events.ResourceEvent;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

public class ScenesTableList extends Table implements
		ModelListener<ResourceEvent> {

	private static final float PAD = 10;

	private Skin skin;

	private InputListener listener;

	private Controller controller;

	private String styleButton;

	private ModelListener<LoadEvent> loadListener;

	public ScenesTableList(Controller controller, InputListener listener) {
		this(controller, listener, "default");
	}

	public ScenesTableList(Controller controller, InputListener listener,
			String styleButton) {
		super();
		this.skin = controller.getApplicationAssets().getSkin();
		this.listener = listener;
		this.controller = controller;
		this.styleButton = styleButton;

		loadListener = new ModelListener<LoadEvent>() {
			@Override
			public void modelChanged(LoadEvent event) {
				if (event.getType() == LoadEvent.Type.LOADED) {
					initialize();
				} else {
					clear();
				}
			}
		};

		controller.getModel().addResourceListener(this);
		controller.getModel().addLoadListener(loadListener);
		initialize();
	}

	public void initialize() {
		this.initialize("");
	}

	public void initialize(String sceneId) {
		String initialName = Q.getComponent(controller.getModel().getGame(),
				GameData.class).getInitialScene();

		clear();

		ButtonGroup group = new ButtonGroup();
		group.setMaxCheckCount(1);
		group.setMinCheckCount(0);

		Cell initial = add().expandX().fill();

		Collection<Entry<String, Resource>> values = controller.getModel()
				.getResources(ResourceCategory.SCENE).entrySet();

		for (Entry<String, Resource> value : values) {
			ModelEntity scene = (ModelEntity) value.getValue().getObject();
			SceneButton sceneButton = new SceneButton(scene, controller, PAD,
					skin, styleButton);
			sceneButton.addListener(listener);
			if (value.getKey().equals(initialName)) {
				initial.setWidget(sceneButton);
			} else {
				row();
				add(sceneButton).expandX().fill();
			}

			group.add(sceneButton);

			if (sceneId.equals(controller.getModel().getIdFor(
					sceneButton.getScene()))) {
				sceneButton.setChecked(true);
			}
		}
	}

	public void selectScene(String id) {
		for (Actor actor : getChildren()) {
			SceneButton button = (SceneButton) actor;
			if (controller.getModel().getIdFor(button.getScene()).equals(id)) {
				button.setChecked(true);
				break;
			}
		}

	}

	@Override
	public void modelChanged(ResourceEvent event) {
		if (event.getCategory() == ResourceCategory.SCENE) {
			initialize();
		}

	}
}
