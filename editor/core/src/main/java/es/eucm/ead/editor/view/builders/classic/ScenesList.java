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
package es.eucm.ead.editor.view.builders.classic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Clipboard.CopyListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.*;
import es.eucm.ead.editor.control.actions.DeleteScene;
import es.eucm.ead.editor.control.actions.EditScene;
import es.eucm.ead.editor.model.FieldNames;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.view.builders.ContextMenuBuilder;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.TextField;
import es.eucm.ead.editor.view.widgets.ToggleImageButton;
import es.eucm.ead.editor.view.widgets.layouts.TopBottomLayout;
import es.eucm.ead.schema.actors.Scene;

public class ScenesList extends AbstractWidget implements CopyListener {

	private Controller controller;

	private Skin skin;

	private float prefSize;

	private TopBottomLayout container;

	private ScrollPane scrollPane;

	public ScenesList(Controller controller, Skin skin) {
		this.controller = controller;
		this.setRequestKeyboardFocus(true);
		this.skin = skin;
		container = new TopBottomLayout();
		scrollPane = new ScrollPane(container);
		addActor(scrollPane);

		// Add the general scene context menu (when you hit the background)
		// FIXME We should think of how context menus are created.
		ContextMenuBuilder.Builder backgroundContextMenu = new ContextMenuBuilder(
				controller).build();
		backgroundContextMenu.item(
				controller.getEditorAssets().getI18N().m("scene.add"),
				AddScene.class);
		controller.getViews().registerContextMenu(getBackground(),
				backgroundContextMenu.done());

	}

	public ScenesList addScene(String sceneId, String sceneName) {
		SceneWidget widget = new SceneWidget(sceneId, sceneName);
		container.addTop(widget);
		return this;
	}

	public ScenesList addScene(String sceneId, String sceneName, int index) {
		SceneWidget widget = new SceneWidget(sceneId, sceneName);
		container.addSecond(widget, index);
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

	@Override
	public Object copy(boolean cut) {
		Scene scene = controller.getModel().getEditScene();
		if (cut) {
			controller.action(DeleteScene.class, controller.getModel()
					.getGameMetadata().getEditScene());
		}
		return scene;
	}

	public class SceneWidget extends AbstractWidget {

		private ToggleImageButton button;

		private TextField sceneNameField;

		private String sceneId;

		// FIXME Remove this once we have sources in model events
		private boolean iChangedIt = false;

		// A simple icon that is displayed on the scene that is the initial one
		private Image initialSceneIcon;
		private boolean isInitialScene;

		public SceneWidget(String sceneId, String sceneName) {
			this.setName(sceneId + "Widget");
			this.sceneId = sceneId;
			button = new ToggleImageButton(skin.getDrawable("blank"), skin);
			button.addListener(new ActionOnClickListener(controller,
					EditScene.class, sceneId));
			sceneNameField = new TextField(sceneName, skin);
			// sceneNameField = new Label(sceneId, skin);
			sceneNameField.setColor(Color.BLACK);
			// sceneNameField.setAlignment(Align.center);
			// sceneNameField.setWrap(true);
			// sceneNameField.setTouchable(Touchable.disabled);

			// Adding the listener (Model -> View) that is notified whenever the
			// scene name changes
			controller.getModel().addFieldListener(
					controller.getModel().getScenes().get(sceneId),
					new Model.FieldListener() {
						@Override
						public boolean listenToField(FieldNames fieldName) {
							return FieldNames.NAME == fieldName;
						}

						@Override
						public void modelChanged(FieldEvent event) {
							if (!iChangedIt
									&& FieldNames.NAME == event.getField()
									&& event.getTarget() == controller
											.getModel().getScenes()
											.get(SceneWidget.this.sceneId)) {
								sceneNameField.setText(event.getValue()
										.toString());
							}
						}
					});

			// Adding the listener (View -> Model) that updates the scene name
			// whenever the scene name changes
			sceneNameField
					.setTextFieldListener(new TextField.TextFieldListener() {

						@Override
						public void keyTyped(TextField textField, char c) {
							iChangedIt = true;
							controller.action(RenameScene.class,
									SceneWidget.this.sceneId,
									textField.getText());
							iChangedIt = false;
						}
					});

			addActor(button);
			addActor(sceneNameField);

			// Create the icon for marking the initial scene
			initialSceneIcon = new Image(skin.getDrawable("initialscene"));
			// If this is the initial scene, add the icon as an actor
			if (controller.getModel().getGame().getInitialScene()
					.equals(this.sceneId)) {
				addActor(initialSceneIcon);
				isInitialScene = true;
			}

			// Adding the listener that is notified whenever the initial scene
			// changes.
			controller.getModel().addFieldListener(
					controller.getModel().getGame(), new Model.FieldListener() {
						@Override
						public boolean listenToField(FieldNames fieldName) {
							return FieldNames.INITIAL_SCENE == fieldName;
						}

						@Override
						public void modelChanged(FieldEvent event) {
							if (FieldNames.INITIAL_SCENE == event.getField()) {
								if (controller.getModel().getGame()
										.getInitialScene()
										.equals(SceneWidget.this.sceneId)
										&& !isInitialScene) {
									addActor(initialSceneIcon);
									isInitialScene = true;
								} else if (!controller.getModel().getGame()
										.getInitialScene()
										.equals(SceneWidget.this.sceneId)
										&& isInitialScene) {
									removeActor(initialSceneIcon);
									isInitialScene = false;
								}
							}
						}
					});

			// Add the context menu to this widget's children
			buildSceneContextMenu(sceneId, button, sceneNameField,
					initialSceneIcon);

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
			setPosition(sceneNameField, getWidth() / 2.0f, getHeight() / 2.0f);
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
		 * @param sceneId
		 *            The id of the current scene (e.g. "scene1")
		 * @param actors
		 *            A list of actors that should display this context menu.
		 *            The list is iterated and the context menu is registered
		 *            for all of them
		 * @return The context menu created
		 */
		private ContextMenuBuilder.Builder buildSceneContextMenu(
				String sceneId, Actor... actors) {
			ContextMenuBuilder.Builder sceneContextMenu = new ContextMenuBuilder(
					controller).build();
			sceneContextMenu.item(
					controller.getEditorAssets().getI18N().m("scene.add"),
					AddScene.class);
			sceneContextMenu.item(
					controller.getEditorAssets().getI18N().m("scene.delete"),
					DeleteScene.class, sceneId);

			sceneContextMenu.item(
					controller.getEditorAssets().getI18N().m("scene.initial"),
					ChangeInitialScene.class, sceneId);

			sceneContextMenu.item(
					controller.getEditorAssets().getI18N().m("scene.move.up"),
					ReorderScenes.class, sceneId, -1, true);

			sceneContextMenu
					.item(controller.getEditorAssets().getI18N()
							.m("scene.move.down"), ReorderScenes.class,
							sceneId, +1, true);

			for (Actor actor : actors) {
				controller.getViews().registerContextMenu(actor,
						sceneContextMenu.done());
			}
			return sceneContextMenu;
		}

	}

}
