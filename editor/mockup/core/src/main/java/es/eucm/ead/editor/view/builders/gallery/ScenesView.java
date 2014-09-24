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
package es.eucm.ead.editor.view.builders.gallery;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeMockupView;
import es.eucm.ead.editor.control.actions.editor.asynk.CloseMockupGame;
import es.eucm.ead.editor.control.actions.editor.asynk.ExportMockupProject;
import es.eucm.ead.editor.control.actions.model.ChangeProjectName;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.editor.control.actions.model.scene.NewScene;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.Resource;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.view.builders.EditionView;
import es.eucm.ead.editor.view.listeners.TextFieldListener;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.Notification;
import es.eucm.ead.editor.view.widgets.gallery.GalleryItem;
import es.eucm.ead.editor.view.widgets.gallery.SceneItem;
import es.eucm.ead.editor.view.widgets.gallery.SortWidget;
import es.eucm.ead.editor.view.widgets.helpmessage.sequence.HelpSequence;
import es.eucm.ead.editor.view.widgets.helpmessage.sequence.ScenesViewHelp;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.schemax.entities.ResourceCategory;

import java.util.Collection;

public class ScenesView extends BaseGallery {

	private static final int ROWS = 4;
	private static final float UNIQUE_SCENE_NOTIF_TIMEOUT = 4F;

	private Notification uniqueSceneNotif;
	private TextField projectName;

	private FieldListener initalSceneListener = new FieldListener() {

		@Override
		public void modelChanged(FieldEvent event) {
			Object value = event.getValue();
			if (value != null) {
				for (Actor actor : galleryGrid.getChildren()) {
					if (actor instanceof SceneItem) {
						((SceneItem) actor).checkInitial();
					}
				}
			}
		}

		@Override
		public boolean listenToField(String fieldName) {
			return fieldName.equals(FieldName.INITIAL_SCENE);
		}
	};

	protected int getColumns() {
		return ROWS;
	};

	@Override
	protected Actor createShareButton() {
		Button share = new IconButton("share80x80", 0f, skin, "inverted");
		share.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				controller.action(ExportMockupProject.class);
			}
		});
		return share;
	}

	@Override
	protected SortWidget addReorderWidget() {
		return new SortWidget(skin, items, this, true);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		uniqueSceneNotif = new Notification(skin).text(i18n
				.m("scene.delete.error-message"));
	}

	@Override
	protected Actor createPlayButton() {
		Button play = new IconButton("play80x80", 0f, skin, "inverted");
		play.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				controller.action(ChangeMockupView.class, PlayView.class);
			}
		});
		return play;
	}

	@Override
	protected Actor createBackButton() {
		Button back = new IconButton("back80x80", skin);
		back.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				controller.action(CloseMockupGame.class);
			}
		});
		return back;
	}

	@Override
	protected Actor createToolbarText() {
		projectName = new TextField("", skin) {
			@Override
			public float getPrefWidth() {
				String message = getMessageText();
				if (message == null) {
					return super.getPrefWidth();
				}

				return getStyle().font.getBounds(message).width * 2.5F;
			}
		};
		projectName.setMessageText(i18n.m("name"));
		projectName.addListener(new TextFieldListener(projectName) {

			@Override
			protected void keyTyped(String text) {
				controller.action(ChangeProjectName.class, text);
			}

		});
		projectName.setFocusTraversal(false);
		return projectName;
	}

	@Override
	public void deleteItem(GalleryItem item) {
		int scenes = controller.getModel().getResources(ResourceCategory.SCENE)
				.size();
		if (scenes == 1 || scenes - undosPending.size == 1) {
			uniqueSceneNotif.show(projectName.getStage(),
					UNIQUE_SCENE_NOTIF_TIMEOUT);
			if (undoNotification.isShowing()) {
				uniqueSceneNotif.setY(undoNotification.getY()
						+ undoNotification.getHeight());
			}
		} else {
			super.deleteItem(item);
		}
	}

	@Override
	protected String getNewButtonIcon() {
		return "new_scene80x80";
	}

	@Override
	protected void newItem() {
		controller.action(NewScene.class, "");
		controller.action(ChangeMockupView.class, EditionView.class);
	}

	@Override
	protected void loadItems(Array<GalleryItem> items) {
		items.clear();
		Collection<Resource> resources = controller.getModel()
				.getResources(ResourceCategory.SCENE).values();
		for (Resource resource : resources) {
			ModelEntity scene = (ModelEntity) resource.getObject();
			SceneItem sceneItem = new SceneItem(controller, scene, this);
			sceneItem.setUserObject(scene);
			items.add(sceneItem);
		}
	}

	@Override
	public Actor getView(Object... args) {
		controller.getCommands().pushStack();
		ModelEntity game = controller.getModel().getGame();
		projectName.setText(Q.getName(game, ""));
		controller.getModel().addFieldListener(
				Q.getComponent(game, GameData.class), initalSceneListener);
		return super.getView(args);
	}

	@Override
	public void release(Controller controller) {
		super.release(controller);
		controller.getModel().removeListenerFromAllTargets(initalSceneListener);
		controller.getCommands().popStack(false);
	}

	@Override
	public void itemClicked(GalleryItem item) {
		String sceneId = controller.getModel().getIdFor(
				((SceneItem) item).getScene());
		if (sceneId != null) {
			controller.action(EditScene.class, sceneId);
			controller.action(ChangeMockupView.class, EditionView.class);
		}
	}

	@Override
	protected HelpSequence getHelpSequence(Controller controller) {
		return new ScenesViewHelp(controller, this, newButton);
	}
}
