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
package es.eucm.ead.editor.view.widgets.editionview.composition.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Pools;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.Toasts;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.actions.model.ReplaceEntity;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.background.BackgroundExecutor.BackgroundTaskListener;
import es.eucm.ead.editor.control.background.BackgroundTask;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.widgets.HorizontalToolbar;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.editionview.MockupSceneEditor;
import es.eucm.ead.editor.view.widgets.editionview.composition.CompositionToolbar;
import es.eucm.ead.editor.view.widgets.editionview.composition.draw.BrushStrokes.Mode;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Image;

public class PaintToolbar extends HorizontalToolbar {

	private static final String LOGTAG = "PaintToolbar";
	private static final float ALPHA_FACTOR = .5F;

	private BrushStrokes brushStrokes;

	private Controller controller;

	private ModelEntity selection;

	private MockupSceneEditor sceneEditor;

	private float selectionAlpha;

	public PaintToolbar(MockupSceneEditor sceneEditor, Controller control) {
		super(control.getApplicationAssets().getSkin(), "white_bottom");
		this.controller = control;
		this.sceneEditor = sceneEditor;
		brushStrokes = new BrushStrokes(sceneEditor, controller);
		ApplicationAssets assets = controller.getApplicationAssets();
		Skin skin = assets.getSkin();
		final I18N i18n = assets.getI18N();
		final Toasts toasts = ((MockupViews) controller.getViews()).getToasts();
		String styleName = "checkable";
		final IconButton erase = new IconButton("rubber80x80", 0f, skin,
				styleName);
		erase.setColor(Color.PINK);

		final ColorPicker picker = new ColorPicker(false, skin) {
			@Override
			protected void colorChanged(Color newColor) {
				brushStrokes.setColor(newColor);
				brushStrokes.setMode(Mode.DRAW);
			}
		};
		picker.clearListeners();
		picker.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!picker.getPanel().hasParent()) {
					picker.setChecked(true);
					picker.colorChanged();
					picker.showPanel();
				} else {
					picker.setChecked(false);
					picker.hidePanel();
				}
			}
		});

		final Slider slider = new Slider(5, 40, 1, false, skin,
				"white-horizontal");
		slider.setValue(20);
		brushStrokes.setMaxDrawRadius(40f);
		brushStrokes.setRadius(slider.getValue());

		final TextButton save = new TextButton(i18n.m("save"), skin, "to_color");
		save.setColor(Color.GREEN);

		final TextButton cancel = new TextButton(i18n.m("cancel"), skin,
				"white");

		leftAdd(save);
		leftAdd(cancel);

		rightAdd(erase);
		rightAdd(picker);
		rightAdd(slider);

		ChangeListener listener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Actor listener = event.getListenerActor();

				CompositionToolbar parent = null;
				if (getParent() instanceof CompositionToolbar) {
					parent = (CompositionToolbar) getParent();
				}
				if (listener == cancel) {
					hide();
					if (parent != null) {
						parent.show(parent.getInsertToolbar());
					}
				} else if (listener == save) {
					toasts.showNotification(i18n.m("edition.creatingImage"));
					controller.getBackgroundExecutor().submit(saveTask,
							saveListener);
					hide(false, true);
					if (parent != null) {
						parent.show(parent.getTransformToolbar());
					}
				} else if (listener == slider) {
					brushStrokes.setRadius(slider.getValue());
				} else {
					Mode mode;
					if (listener == erase) {
						mode = Mode.ERASE;
					} else {
						mode = Mode.DRAW;
						brushStrokes.setColor(((IconButton) event
								.getListenerActor()).getIcon().getColor());
					}
					brushStrokes.setMode(mode);
				}
			}

			private final BackgroundTaskListener<Boolean> saveListener = new BackgroundTaskListener<Boolean>() {

				@Override
				public void completionPercentage(float percentage) {
				}

				@Override
				public void done(BackgroundExecutor backgroundExecutor,
						Boolean result) {
					Gdx.app.log(LOGTAG, "done saving, result is: " + result);
					toasts.hideNotification();
					if (result) {
						ModelEntity sceneElement = brushStrokes
								.createSceneElement();
						if (selection == null) {
							controller.action(AddSceneElement.class,
									sceneElement);
						} else {
							controller.action(ReplaceEntity.class, selection,
									sceneElement);
							controller.action(SetSelection.class,
									Selection.EDITED_GROUP,
									Selection.SCENE_ELEMENT, sceneElement);
						}
						selection = null;
					}
				}

				@Override
				public void error(Throwable e) {
					Gdx.app.error(LOGTAG, "error saving", e);
					toasts.hideNotification();
				}
			};

			private final BackgroundTask<Boolean> saveTask = new BackgroundTask<Boolean>() {
				@Override
				public Boolean call() throws Exception {

					boolean saved = brushStrokes.save();
					brushStrokes.release();
					return saved;
				}
			};
		};
		erase.addListener(listener);
		slider.addListener(listener);
		save.addListener(listener);
		cancel.addListener(listener);
	}

	public void show() {

		Object[] selArray = controller.getModel().getSelection()
				.get(Selection.SCENE_ELEMENT);
		if (selArray.length == 1) {
			Object sel = selArray[0];
			if (sel instanceof ModelEntity) {
				selection = (ModelEntity) sel;
				if (selection.getChildren().size != 0
						|| !Q.hasComponent(selection, Image.class)) {
					selection = null;
				}
			}
		}

		transparency(false, false);
		brushStrokes.show(selection);

		fireDraw(true, false);
	}

	public void hide() {
		hide(true, false);
	}

	private void hide(boolean release, boolean saved) {
		brushStrokes.hide(release);

		transparency(true, saved);
		fireDraw(false, saved);
	}

	private void transparency(boolean restore, boolean saved) {
		for (Actor actor : sceneEditor.getRootGroup().getChildren()) {

			Color color = actor.getColor();
			if (selection != null && Q.getModelEntity(actor) == selection) {
				if (restore) {
					if (!saved) {
						color.a = selectionAlpha;
					}
				} else {
					selectionAlpha = color.a;
					color.a = 0f;
				}
			} else {
				color.a = restore ? (color.a / ALPHA_FACTOR)
						: (color.a * ALPHA_FACTOR);
			}
		}
	}

	public boolean isShowing() {
		return hasParent() && isTouchable();
	}

	public void addDrawListener(DrawListener listener) {
		brushStrokes.addListener(listener);
	}

	/**
	 * Fires that this actor is going to draw or not
	 */
	private void fireDraw(boolean show, boolean saved) {
		DrawEvent drawEvent = Pools.obtain(DrawEvent.class);
		drawEvent.show = show;
		drawEvent.saved = saved;
		fire(drawEvent);
		Pools.free(drawEvent);
	}

	/**
	 * Base class to listen to {@link DrawEvent}s produced by
	 * {@link PaintToolbar}.
	 */
	public static abstract class DrawListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			if (event instanceof DrawEvent) {
				DrawEvent draw = ((DrawEvent) event);
				if (draw.show) {
					drawStarted();
				} else {
					drawEnded(draw.saved);
				}
			}
			return true;
		}

		/**
		 * The draw process has started
		 * 
		 * @param event
		 */
		public abstract void drawStarted();

		/**
		 * The draw process has ended
		 * 
		 * @param event
		 */
		public abstract void drawEnded(boolean saved);
	}

	public static class DrawEvent extends Event {

		private boolean show, saved;
	}
}
