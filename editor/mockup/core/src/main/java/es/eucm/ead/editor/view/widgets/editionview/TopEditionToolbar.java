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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import es.eucm.ead.editor.control.Actions;
import es.eucm.ead.editor.control.Clipboard.ClipboardListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.AddSceneElementFromResource;
import es.eucm.ead.editor.control.actions.editor.ChangeMockupView;
import es.eucm.ead.editor.control.actions.editor.MockupPaste;
import es.eucm.ead.editor.control.actions.editor.Redo;
import es.eucm.ead.editor.control.actions.editor.Undo;
import es.eucm.ead.editor.control.actions.editor.asynk.ExportMockupProject;
import es.eucm.ead.editor.control.actions.editor.asynk.TakePicture;
import es.eucm.ead.editor.control.actions.model.AddGatewayDefaultElement;
import es.eucm.ead.editor.control.actions.model.AddInteractiveZone;
import es.eucm.ead.editor.control.actions.model.AddLabelToScene;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.view.builders.LibrariesView;
import es.eucm.ead.editor.view.builders.gallery.BaseGallery;
import es.eucm.ead.editor.view.builders.gallery.PlayView;
import es.eucm.ead.editor.view.listeners.ActionListener;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.Toolbar;
import es.eucm.ead.editor.view.widgets.ToolbarIcon;
import es.eucm.ead.editor.view.widgets.editionview.draw.PaintToolbar;
import es.eucm.ead.editor.view.widgets.editionview.draw.PaintToolbar.DrawListener;
import es.eucm.ead.schema.entities.ModelEntity;

public class TopEditionToolbar extends Toolbar {

	private static final float BIG_PAD = 160;

	private float height;

	private OthersWidget others;

	private IconButton play;

	private IconButton undo;

	private IconButton redo;

	private IconButton paste;

	private IconButton camera;

	private IconButton repository;

	private IconButton android;

	private IconButton paint;

	private IconButton text;

	private IconButton zones;

	private IconButton gate;

	private IconButton share;

	public TopEditionToolbar(final Controller controller, String style,
			float height, float iconSize, float iconPad,
			final PaintToolbar paintToolbar, float smallPad, float normalPad) {
		super(controller.getApplicationAssets().getSkin(), style);
		Skin skin = controller.getApplicationAssets().getSkin();

		this.height = height;

		play = new ToolbarIcon("play80x80", iconPad, iconSize, skin, "inverted");
		share = new ToolbarIcon("share80x80", iconPad, iconSize, skin,
				"inverted");

		undo = new ToolbarIcon("undo80x80", iconPad, iconSize, skin);
		redo = new ToolbarIcon("redo80x80", iconPad, iconSize, skin);

		paste = new ToolbarIcon("paste80x80", iconPad, iconSize, skin);
		camera = new ToolbarIcon("camera80x80", iconPad, iconSize, skin);
		repository = new ToolbarIcon("repository80x80", iconPad, iconSize, skin);
		android = new ToolbarIcon("android_gallery80x80", iconPad, iconSize,
				skin);

		paint = new ToolbarIcon("paint80x80", iconPad, iconSize, skin);
		text = new ToolbarIcon("text80x80", iconPad, iconSize, skin);

		zones = new ToolbarIcon("interactive80x80", iconPad, iconSize, skin);
		gate = new ToolbarIcon("gateway80x80", iconPad, iconSize, skin);

		others = new OthersWidget(controller, iconPad, iconSize);

		paintToolbar.addListener(new DrawListener() {

			private Object selection;

			@Override
			public void drawStarted() {
				selection = controller.getModel().getSelection()
						.getSingle(Selection.SCENE_ELEMENT);
				if (selection != null) {
					controller.action(SetSelection.class, Selection.SCENE,
							Selection.SCENE_ELEMENT);
				}
				setDisabled(true, controller);
			}

			@Override
			public void drawEnded(boolean saved) {
				setDisabled(false, controller);
				if (!saved && selection != null) {
					controller.action(SetSelection.class, Selection.SCENE,
							Selection.SCENE_ELEMENT, selection);
				}
			}

		});

		ChangeListener buttonsListener = new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Actor listenerActor = event.getListenerActor();
				if (listenerActor == play) {
					controller.action(ChangeMockupView.class, PlayView.class);
				} else if (listenerActor == share) {
					controller.action(ExportMockupProject.class);
				} else if (listenerActor == undo) {
					controller.action(Undo.class);
				} else if (listenerActor == redo) {
					controller.action(Redo.class);
				} else if (listenerActor == camera) {
					controller.action(TakePicture.class);
				} else if (listenerActor == paste) {
					controller.action(MockupPaste.class);
				} else if (listenerActor == repository) {
					controller.action(ChangeMockupView.class,
							LibrariesView.class);
				} else if (listenerActor == android) {
					controller.action(AddSceneElementFromResource.class);
				} else if (listenerActor == paint) {
					if (paintToolbar.isShowing()) {
						paintToolbar.hide();
					} else {
						paintToolbar.show();
					}
				} else if (listenerActor == text) {
					controller.action(AddLabelToScene.class);
				} else if (listenerActor == zones) {
					controller.action(AddInteractiveZone.class);
				} else if (listenerActor == gate) {
					controller.action(AddGatewayDefaultElement.class,
							new ModelEntity());
				}
			}
		};

		play.addListener(buttonsListener);
		share.addListener(buttonsListener);
		undo.addListener(buttonsListener);
		redo.addListener(buttonsListener);
		camera.addListener(buttonsListener);
		paste.addListener(buttonsListener);
		repository.addListener(buttonsListener);
		android.addListener(buttonsListener);
		paint.addListener(buttonsListener);
		text.addListener(buttonsListener);
		zones.addListener(buttonsListener);
		gate.addListener(buttonsListener);

		ActionListener undoRedo = new ActionListener() {

			@Override
			public void enableChanged(Class actionClass, boolean enable) {
				if (actionClass == Undo.class) {
					undo.setDisabled(!enable);
				} else if (actionClass == Redo.class) {
					redo.setDisabled(!enable);
				}
			}
		};
		undo.setDisabled(true);
		redo.setDisabled(true);
		Actions actions = controller.getActions();
		actions.addActionListener(Undo.class, undoRedo);
		actions.addActionListener(Redo.class, undoRedo);

		paste.setDisabled(controller.getClipboard().getContents() == null);
		controller.getClipboard().addClipboardListener(new ClipboardListener() {

			@Override
			public void clipboardChanged(String clibpoardContent) {
				paste.setDisabled(false);

			}
		});

		defaults().expandY().fill();
		add(play).padLeft(BaseGallery.PLAY_PAD);
		add(share).padLeft(BaseGallery.SMALL_PAD);

		add(undo).padRight(smallPad).padLeft(BaseGallery.PLAY_PAD * 1.5F);
		add(redo).padRight(BIG_PAD);

		add().expandX();
		add(paste).padRight(smallPad).right();
		add(camera).padRight(smallPad);
		add(repository).padRight(smallPad);
		add(android).padRight(normalPad);

		add(paint).padRight(smallPad);
		add(text).padRight(normalPad);

		add(zones).padRight(smallPad);
		add(gate).padRight(normalPad);

		add(others).padRight(smallPad);
	}

	@Override
	public float getPrefHeight() {
		return height;
	}

	private void setDisabled(boolean disabled, Controller controller) {
		play.setDisabled(disabled);
		share.setDisabled(disabled);
		undo.setDisabled(disabled);
		redo.setDisabled(disabled);
		if (disabled) {
			paste.setDisabled(disabled);
		} else {
			paste.setDisabled(controller.getClipboard().getContents() == null);
		}
		camera.setDisabled(disabled);
		repository.setDisabled(disabled);
		android.setDisabled(disabled);
		text.setDisabled(disabled);
		zones.setDisabled(disabled);
		gate.setDisabled(disabled);
	}
}