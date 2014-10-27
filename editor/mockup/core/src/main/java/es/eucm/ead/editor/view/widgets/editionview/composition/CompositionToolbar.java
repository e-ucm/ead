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
package es.eucm.ead.editor.view.widgets.editionview.composition;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Clipboard.ClipboardListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.AddLabelToScene;
import es.eucm.ead.editor.control.actions.editor.AddSceneElementFromResource;
import es.eucm.ead.editor.control.actions.editor.ChangeMockupView;
import es.eucm.ead.editor.control.actions.editor.MockupPaste;
import es.eucm.ead.editor.control.actions.editor.asynk.TakePicture;
import es.eucm.ead.editor.control.actions.editor.elementeditstate.SetAllUnlocked;
import es.eucm.ead.editor.control.actions.editor.elementeditstate.SetAllVisible;
import es.eucm.ead.editor.control.actions.model.AddInteractiveZone;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.model.events.SelectionEvent.Type;
import es.eucm.ead.editor.view.builders.gallery.repository.LibrariesView;
import es.eucm.ead.editor.view.widgets.HorizontalToolbar;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.MultiHorizontalToolbar;
import es.eucm.ead.editor.view.widgets.editionview.TransformationsWidget;
import es.eucm.ead.editor.view.widgets.editionview.composition.draw.PaintToolbar;
import es.eucm.ead.editor.view.widgets.editionview.composition.draw.PaintToolbar.DrawListener;

public class CompositionToolbar extends MultiHorizontalToolbar implements
		SelectionListener {

	private Skin skin;

	private Controller controller;

	private PaintToolbar paintToolbar;
	private HorizontalToolbar insertToolbar;
	private HorizontalToolbar transformToolbar;

	public CompositionToolbar(final Controller controller,
			PaintToolbar paintToolbar) {
		this.controller = controller;
		this.skin = controller.getApplicationAssets().getSkin();

		this.paintToolbar = paintToolbar;
		createInsertToolbar();
		createTransformationToolbar();

		addHorizontalToolbar(insertToolbar, transformToolbar, paintToolbar);
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
			}

			@Override
			public void drawEnded(boolean saved) {
				if (!saved && selection != null) {
					controller.action(SetSelection.class, Selection.SCENE,
							Selection.SCENE_ELEMENT, selection);
				}
			}
		});
		controller.getModel().addSelectionListener(this);
	}

	private void createInsertToolbar() {
		this.insertToolbar = new HorizontalToolbar(skin, "white_bottom");
		insertToolbar.backgroundColor(Color.CYAN);

		final IconButton paste = new IconButton("paste", "paste80x80", 0f, skin);
		final IconButton camera = new IconButton("camera", "camera80x80", 0f,
				skin);
		final IconButton repository = new IconButton("repository",
				"repository80x80", 0f, skin);
		final IconButton android = new IconButton("gallery",
				"android_gallery80x80", 0f, skin);

		final IconButton paint = new IconButton("paint", "paint80x80", 0f, skin);
		final IconButton text = new IconButton("text", "text80x80", 0f, skin);

		final IconButton zones = new IconButton("zone", "interactive80x80", 0f,
				skin);

		ChangeListener buttonsListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Actor listenerActor = event.getListenerActor();
				if (listenerActor == camera) {
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
						show(paintToolbar);
						paintToolbar.show();
					}
				} else if (listenerActor == text) {
					controller.action(AddLabelToScene.class);
				} else if (listenerActor == zones) {
					controller.action(AddInteractiveZone.class);
				}
			}
		};

		camera.addListener(buttonsListener);
		paste.addListener(buttonsListener);
		repository.addListener(buttonsListener);
		android.addListener(buttonsListener);
		paint.addListener(buttonsListener);
		text.addListener(buttonsListener);
		zones.addListener(buttonsListener);

		final IconButton invisible = new IconButton("visibility80x80", 0f, skin);
		final IconButton lock = new IconButton("lock80x80", 0f, skin);
		ClickListener listener = new ClickListener() {
			public void clicked(
					com.badlogic.gdx.scenes.scene2d.InputEvent event, float x,
					float y) {
				Actor listener = event.getListenerActor();
				if (listener == invisible) {
					controller.action(SetAllVisible.class);
				} else if (listener == lock) {
					controller.action(SetAllUnlocked.class);
				}
			};
		};

		lock.addListener(listener);
		invisible.addListener(listener);

		insertToolbar.leftAdd(paste);
		insertToolbar.leftAdd(invisible);
		insertToolbar.leftAdd(lock);

		insertToolbar.rightAdd(text);
		insertToolbar.rightAdd(repository);
		insertToolbar.rightAdd(paint);
		insertToolbar.rightAdd(camera);
		insertToolbar.rightAdd(android);
		insertToolbar.rightAdd(zones);

		paste.setDisabled(controller.getClipboard().getContents() == null);

		controller.getClipboard().addClipboardListener(new ClipboardListener() {
			@Override
			public void clipboardChanged(String clibpoardContent) {
				paste.setDisabled(false);
			}
		});
	}

	private void createTransformationToolbar() {
		this.transformToolbar = new HorizontalToolbar(skin, "white_bottom");
		transformToolbar.backgroundColor(Color.ORANGE);
		transformToolbar.rightAdd(new TransformationsWidget(controller, 5f));
		final IconButton paint = new IconButton("paint", "paint80x80", 0f, skin);
		transformToolbar.rightAdd(paint);

		ChangeListener buttonsListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Actor listenerActor = event.getListenerActor();
				if (listenerActor == paint) {
					if (paintToolbar.isShowing()) {
						paintToolbar.hide();
					} else {
						show(paintToolbar);
						paintToolbar.show();
					}
				}
			}
		};

		paint.addListener(buttonsListener);
		// TODO
	}

	public HorizontalToolbar getInsertToolbar() {
		return insertToolbar;
	}

	public HorizontalToolbar getTransformToolbar() {
		return transformToolbar;
	}

	public PaintToolbar getPaintToolbar() {
		return paintToolbar;
	}

	@Override
	public void modelChanged(SelectionEvent event) {
		int selected = controller.getModel().getSelection()
				.get(Selection.SCENE_ELEMENT).length;
		if (event.getType() == Type.FOCUSED) {
			if (selected > 0 && getCurrentToolbar() != transformToolbar) {
				show(transformToolbar);
			} else if (selected == 0 && getCurrentToolbar() != insertToolbar
					&& toShow != paintToolbar) {
				show(insertToolbar);
			}
		}
	}

	@Override
	public boolean listenToContext(String contextId) {
		return true;
	}
}
