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
package es.eucm.ead.editor.view.widgets.editionview.draw;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Pools;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.control.Toasts;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.background.BackgroundExecutor.BackgroundTaskListener;
import es.eucm.ead.editor.control.background.BackgroundTask;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.Toolbar;
import es.eucm.ead.editor.view.widgets.editionview.MockupSceneEditor;
import es.eucm.ead.editor.view.widgets.editionview.draw.BrushStrokes.Mode;
import es.eucm.ead.engine.I18N;

public class PaintToolbar extends Toolbar {

	private static final String LOGTAG = "PaintToolbar";

	private static final Vector2 TEMP = new Vector2();

	private static final float NORMAL_PAD = .02F;
	private static final float SLIDER_WIDTH = .1F;
	private static final float IN_DURATION = .3F;
	private static final float OUT_DURATION = .2F;

	private MockupSceneEditor parent;

	private BrushStrokes brushStrokes;

	public PaintToolbar(MockupSceneEditor parent, final Controller controller) {
		super(controller.getApplicationAssets().getSkin(), "white_bottom");
		this.parent = parent;
		brushStrokes = new BrushStrokes(parent, controller);
		ApplicationAssets assets = controller.getApplicationAssets();
		Skin skin = assets.getSkin();
		final I18N i18n = assets.getI18N();
		final Toasts toasts = ((MockupViews) controller.getViews()).getToasts();
		String styleName = "checkable";
		final IconButton erase = new IconButton("rubber80x80", 0f, skin,
				styleName);
		erase.setColor(Color.PINK);

		// Colors
		IconButton color1 = new IconButton("rectangle", 0f, skin, styleName);
		color1.getIcon().setColor(Color.WHITE);

		IconButton color3 = new IconButton("rectangle", 0f, skin, styleName);
		color3.getIcon().setColor(Color.RED);

		IconButton color4 = new IconButton("rectangle", 0f, skin, styleName);
		color4.getIcon().setColor(Color.GREEN);

		IconButton color5 = new IconButton("rectangle", 0f, skin, styleName);
		color5.getIcon().setColor(Color.BLUE);

		IconButton color6 = new IconButton("rectangle", 0f, skin, styleName);
		color6.getIcon().setColor(Color.BLACK);

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
					picker.showPanel();
					picker.colorChanged();
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
		Table colors = new Table();
		colors.add(color1);
		colors.add(color3);
		colors.add(color4);
		colors.add(color5);
		colors.add(color6);
		ScrollPane pane = new ScrollPane(colors);
		pane.setScrollingDisabled(false, true);

		float topBottomPad = 0f;
		float normalPad = Gdx.graphics.getWidth() * NORMAL_PAD;
		defaults().expand().fill();
		add(erase).padLeft(normalPad).padRight(normalPad);
		add(pane);
		add(picker).padLeft(normalPad);
		add(slider).padLeft(normalPad).width(
				Gdx.graphics.getWidth() * SLIDER_WIDTH);
		add(save).pad(topBottomPad, normalPad, topBottomPad, normalPad);
		add(cancel).pad(topBottomPad, 0f, topBottomPad, normalPad);

		ChangeListener listener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Actor listener = event.getListenerActor();

				if (listener == cancel) {
					hide();
				} else if (listener == save) {

					toasts.showNotification(i18n.m("edition.creatingImage"));
					controller.getBackgroundExecutor().submit(saveTask,
							saveListener);
					hide(false, true);
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
						brushStrokes.createSceneElement();
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
		color1.addListener(listener);
		color3.addListener(listener);
		color4.addListener(listener);
		color5.addListener(listener);
		color6.addListener(listener);
		slider.addListener(listener);
		save.addListener(listener);
		cancel.addListener(listener);

		new ButtonGroup(erase, color1, color3, color4, color5, color6, picker);
		color6.setChecked(true);
		brushStrokes.setColor(color6.getIcon().getColor());
	}

	public void show() {
		Stage stage = parent.getStage();
		if (stage == null) {
			return;
		}
		clearActions();
		setTouchable(Touchable.enabled);
		stage.addActor(this);
		parent.localToStageCoordinates(TEMP.set(0f, 0f));
		brushStrokes.show();

		float prefW = MathUtils.round(Math.min(getPrefWidth(),
				parent.getWidth()));
		float prefH = MathUtils.round(getPrefHeight());
		float x = MathUtils.round(TEMP.x + (parent.getWidth() - prefW) * .5f);
		float y = -prefH;

		setBounds(x, y, prefW, prefH);
		addAction(Actions.moveTo(x, 0f, IN_DURATION, Interpolation.sineOut));
		fireDraw(true, false);
	}

	public void hide() {
		hide(true, false);
	}

	private void hide(boolean release, boolean saved) {
		if (!isShowing())
			return;
		clearActions();
		setTouchable(Touchable.disabled);
		brushStrokes.hide(release);

		addAction(sequence(Actions.moveTo(getX(), -getHeight(), OUT_DURATION,
				Interpolation.fade), Actions.removeActor()));
		fireDraw(false, saved);
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
