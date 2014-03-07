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
package es.eucm.ead.editor.view.builders.classic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.FieldNames;
import es.eucm.ead.editor.control.actions.AddScene;
import es.eucm.ead.editor.control.actions.DeleteScene;
import es.eucm.ead.editor.control.actions.EditScene;
import es.eucm.ead.editor.control.actions.InitialScene;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.view.builders.ContextMenuBuilder;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.ToggleImageButton;
import es.eucm.ead.editor.view.widgets.layouts.TopBottomLayout;

public class ScenesList extends AbstractWidget {

	private Controller controller;

	private Skin skin;

	private float prefSize;

	private TopBottomLayout container;

	private ScrollPane scrollPane;

	public ScenesList(Controller controller, Skin skin) {
		this.controller = controller;
		this.skin = skin;
		container = new TopBottomLayout();
		scrollPane = new ScrollPane(container);
		addActor(scrollPane);

		// Add the general scene context menu (when you hit the background)
		ContextMenuBuilder.Builder backgroundContextMenu = new ContextMenuBuilder(
				controller).build();
		backgroundContextMenu.item(
				controller.getEditorAssets().getI18N().m("scene.add"),
				AddScene.NAME);
		controller.getViews().registerContextMenu(getBackground(),
				backgroundContextMenu.done());

	}

	public ScenesList addScene(String scene) {
		SceneWidget widget = new SceneWidget(scene);
		container.addTop(widget);
		return this;
	}

	public void removeScene(String scene) {
		container.removeTop(this.findActor(scene + "Widget"));
		container.layout();
	}

	public Actor getBackground() {
		return container;
	}

	public void clearScenes() {
		container.clearChildren();
	}

	public ScenesList prefSize(float prefSize) {
		this.prefSize = prefSize;
		return this;
	}

	@Override
	public float getPrefWidth() {
		return container.getPrefWidth();
	}

	@Override
	public float getPrefHeight() {
		return container.getPrefHeight();
	}

	@Override
	public void layout() {
		setBounds(scrollPane, 0, 0, getWidth(), getHeight());
	}

	public class SceneWidget extends AbstractWidget {

		private ToggleImageButton button;

		private Label label;

		private String sceneName;

		// A simple icon that is displayed on the scene that is the initial one
		private Image initialSceneIcon;
		private boolean isInitialScene;

		public SceneWidget(String scene) {
			this.setName(scene + "Widget");
			sceneName = scene;
			button = new ToggleImageButton(skin.getDrawable("blank"), skin);
			button.addListener(new ActionOnClickListener(controller,
					EditScene.NAME, scene));
			label = new Label(scene, skin);
			label.setColor(Color.BLACK);
			label.setAlignment(Align.center);
			label.setWrap(true);
			label.setTouchable(Touchable.disabled);

			addActor(button);
			addActor(label);

			// Create the icon for marking the initial scene
			initialSceneIcon = new Image(skin.getDrawable("initialscene"));
			// If this is the initial scene, add the icon as an actor
			if (controller.getModel().getGame().getInitialScene()
					.equals(sceneName)) {
				addActor(initialSceneIcon);
				isInitialScene = true;
			}

			// Adding the listener that is notified whenever the initial scene
			// changes.
			controller.getModel().addFieldListener(
					controller.getModel().getGame(), new Model.FieldListener() {
						@Override
						public boolean listenToField(
								FieldNames fieldName) {
							return FieldNames.INITIAL_SCENE == fieldName;
						}

						@Override
						public void modelChanged(FieldEvent event) {
							if (InitialScene.NAME.equals(event.getField())) {
								if (controller.getModel().getGame()
										.getInitialScene().equals(sceneName)
										&& !isInitialScene) {
									addActor(initialSceneIcon);
									isInitialScene = true;
								} else if (!controller.getModel().getGame()
										.getInitialScene().equals(sceneName)
										&& isInitialScene) {
									removeActor(initialSceneIcon);
									isInitialScene = false;
								}
							}
						}
					});

			// Add the context menu to this widget's children
			buildSceneContextMenu(scene, button, label, initialSceneIcon);

		}

		@Override
		public float getPrefWidth() {
			return prefSize;
		}

		@Override
		public float getPrefHeight() {
			return prefSize;
		}

		@Override
		public void layout() {
			setBounds(button, 0, 0, getWidth(), getHeight());
			setPosition(label, getWidth() / 2.0f, getHeight() / 2.0f);
		}

		/**
		 * Creates a contextual menu with all the actions related to a
		 * particular scene:
		 * 
		 * - Setting the scene selected as the initial one - Deleting the scene
		 * selected
		 * 
		 * And also, allows:
		 * 
		 * - Adding a new scene
		 * 
		 * @param sceneName
		 *            The name of the current scene (e.g. "scene1")
		 * @param actors
		 *            A list of actors that should display this context menu.
		 *            The list is iterated and the context menu is registered
		 *            for all of them
		 * @return The context menu created
		 */
		private ContextMenuBuilder.Builder buildSceneContextMenu(
				String sceneName, Actor... actors) {
			ContextMenuBuilder.Builder sceneContextMenu = new ContextMenuBuilder(
					controller).build();
			sceneContextMenu.item(
					controller.getEditorAssets().getI18N().m("scene.add"),
					AddScene.NAME);
			sceneContextMenu.item(
					controller.getEditorAssets().getI18N().m("scene.delete"),
					DeleteScene.NAME, sceneName);

			sceneContextMenu.item(
					controller.getEditorAssets().getI18N().m("scene.initial"),
					InitialScene.NAME, sceneName);

			for (Actor actor : actors) {
				controller.getViews().registerContextMenu(actor,
						sceneContextMenu.done());
			}
			return sceneContextMenu;
		}

	}

}
