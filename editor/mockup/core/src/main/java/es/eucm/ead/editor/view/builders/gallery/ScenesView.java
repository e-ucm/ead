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

import java.util.Collection;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.Save;
import es.eucm.ead.editor.control.actions.model.ChangeProjectName;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.editor.control.actions.model.scene.NewScene;
import es.eucm.ead.editor.model.Model.Resource;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.builders.EditionView;
import es.eucm.ead.editor.view.listeners.TextFieldListener;
import es.eucm.ead.editor.view.widgets.Notification;
import es.eucm.ead.editor.view.widgets.ToolbarIcon;
import es.eucm.ead.editor.view.widgets.gallery.GalleryItem;
import es.eucm.ead.editor.view.widgets.gallery.SceneItem;
import es.eucm.ead.editor.view.widgets.helpmessage.sequence.HelpSequence;
import es.eucm.ead.editor.view.widgets.helpmessage.sequence.ScenesViewHelp;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

public class ScenesView extends BaseGallery {

	private static final float UNIQUE_SCENE_NOTIF_TIMEOUT = 2F;

	private Notification uniqueSceneNotif;
	private TextField projectName;

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		uniqueSceneNotif = new Notification(skin).text(i18n
				.m("scene.delete.error-message"));
	}

	@Override
	protected Actor createPlayButton() {
		Button play = new ToolbarIcon("play80x80", ICON_PAD, iconSize, skin);
		play.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(ChangeView.class, PlayView.class);
			}
		});
		return play;
	}

	@Override
	protected Actor createBackButton() {
		Button back = new ToolbarIcon("play80x80", ICON_PAD, iconSize, skin);
		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(Save.class);
				controller.action(ChangeView.class, ProjectsView.class);
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

				return getStyle().font.getBounds(message).width * 1.5F;
			}

			@Override
			public float getPrefHeight() {
				return iconSize;
			}
		};
		projectName.setMessageText(i18n.m("gallery.enterAName"));
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
		controller.action(ChangeView.class, EditionView.class);
	}

	@Override
	protected void loadItems(Array<GalleryItem> items) {
		items.clear();
		Collection<Resource> values = controller.getModel()
				.getResources(ResourceCategory.SCENE).values();
		for (Resource value : values) {
			items.add(new SceneItem(controller,
					(ModelEntity) value.getObject(), this));
		}
	}

	@Override
	public Actor getView(Object... args) {
		controller.getCommands().clear();
		ModelEntity game = controller.getModel().getGame();
		projectName.setText(Q.getName(game, ""));
		return super.getView(args);
	}

	@Override
	public void itemClicked(GalleryItem item) {
		String sceneId = controller.getModel().getIdFor(
				((SceneItem) item).getScene());
		if (sceneId != null) {
			controller.action(EditScene.class, sceneId);
			controller.action(ChangeView.class, EditionView.class);
		}
	}

	@Override
	protected HelpSequence getHelpSequence(Controller controller) {
		return new ScenesViewHelp(controller, this, newButton);
	}
}
