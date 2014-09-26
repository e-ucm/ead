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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

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
import es.eucm.ead.editor.view.builders.gallery.PlayView;
import es.eucm.ead.editor.view.builders.gallery.repository.LibrariesView;
import es.eucm.ead.editor.view.listeners.ActionListener;
import es.eucm.ead.editor.view.widgets.DropDown;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.ScrollPaneDif;
import es.eucm.ead.editor.view.widgets.Toolbar;
import es.eucm.ead.editor.view.widgets.editionview.draw.PaintToolbar;
import es.eucm.ead.editor.view.widgets.editionview.draw.PaintToolbar.DrawListener;
import es.eucm.ead.editor.view.widgets.gallery.AboutWidget;
import es.eucm.ead.editor.view.widgets.iconwithpanel.IconWithScalePanel;
import es.eucm.ead.engine.assets.GameAssets;

public class TopEditionToolbar extends Toolbar {

	private OthersWidget others;

	private IconButton normalPlay;

	private IconButton debugPlay;

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

	private IconWithScalePanel about;

	public TopEditionToolbar(final Controller controller, String style,
			final PaintToolbar paintToolbar, float smallPad, float normalPad,
			Actor reference) {
		super(controller.getApplicationAssets().getSkin(), style);
		Skin skin = controller.getApplicationAssets().getSkin();

		about = new AboutWidget(controller, reference);
		normalPlay = new IconButton("play", "normalPlay", 0f, skin, "inverted");
		debugPlay = new IconButton("play", "debugPlay", 0f, skin, "inverted");
		share = new IconButton("share", "share80x80", 0f, skin, "inverted");

		undo = new IconButton("undo", "undo80x80", 0f, skin);
		redo = new IconButton("redo", "redo80x80", 0f, skin);

		paste = new IconButton("paste", "paste80x80", 0f, skin);
		camera = new IconButton("camera", "camera80x80", 0f, skin);
		repository = new IconButton("repository", "repository80x80", 0f, skin);
		android = new IconButton("gallery", "android_gallery80x80", 0f, skin);

		paint = new IconButton("paint", "paint80x80", 0f, skin);
		text = new IconButton("text", "text80x80", 0f, skin);

		zones = new IconButton("zone", "interactive80x80", 0f, skin);
		gate = new IconButton("exit", "gateway80x80", 0f, skin);

		others = new OthersWidget(controller);

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
				if (listenerActor == normalPlay) {
					controller.action(ChangeMockupView.class, PlayView.class,
							GameAssets.GAME_FILE);
				} else if (listenerActor == debugPlay) {
					controller.action(ChangeMockupView.class, PlayView.class,
							GameAssets.GAME_DEBUG);
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
					controller.action(AddGatewayDefaultElement.class);
				}
			}
		};

		normalPlay.addListener(buttonsListener);
		debugPlay.addListener(buttonsListener);
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
		add(about);

		DropDown playDown = new DropDown(skin, false);
		Array<Actor> items = new Array<Actor>();
		items.add(new IconButton("play", "play80x80", 0f, skin, "inverted"));
		items.add(normalPlay);
		items.add(debugPlay);
		playDown.setItems(items);
		add(playDown);
		add(share);

		add().expandX();
		add(undo).fill();
		add(redo).fill();
		add().expandX();

		Table table = new Table();
		table.defaults().expandY().fill();
		ScrollPaneDif scrollPane = new ScrollPaneDif(table, skin, "fadeX");
		scrollPane.setScrollingDisabled(false, true);
		table.add(paste).padRight(smallPad).right();
		table.add(camera).padRight(smallPad);
		table.add(repository).padRight(smallPad);
		table.add(android).padRight(normalPad);

		table.add(paint).padRight(smallPad);
		table.add(text).padRight(normalPad);

		table.add(zones).padRight(smallPad);
		table.add(gate).padRight(normalPad);

		add(scrollPane);
		add(others).padRight(smallPad);
	}

	private void setDisabled(boolean disabled, Controller controller) {
		normalPlay.setDisabled(disabled);
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