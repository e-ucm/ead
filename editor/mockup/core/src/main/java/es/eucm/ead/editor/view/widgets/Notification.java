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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.esotericsoftware.tablelayout.Cell;

public class Notification extends HiddenPanel {

	private static final float DEFAULT_SPACING = 30F;
	private static final float FADE_IN = .3F;
	private static final float FADE_OUT = .2F;

	private Actor previousKeyboardFocus, previousScrollFocus;

	private Runnable hide;

	private Drawable progressBarDrawable;

	private Image progressBar;

	private Cell progressBarCell;

	public Notification(Skin skin) {
		this(skin, skin.get(NotificationStyle.class));
	}

	public Notification(Skin skin, NotificationStyle style) {
		super(skin, style.stageBackground);
		progressBarDrawable = style.undefinedProgressBar;
		setBackground(style.background);
		padBottom(getPadBottom() + DEFAULT_SPACING);
		padRight(getPadRight() + DEFAULT_SPACING);
		padTop(getPadTop() + DEFAULT_SPACING);
		padLeft(getPadLeft() + DEFAULT_SPACING);
		defaults().space(DEFAULT_SPACING);
	}

	/**
	 * Adds a label to the content table. The dialog must have been constructed
	 * with a skin to use this method.
	 */
	public Notification text(String text) {
		add(text);
		return this;
	}

	/**
	 * Adds the given Label to this notification.
	 */
	public Cell<?> text(Label label) {
		return add(label);
	}

	/**
	 * {@link #pack() Packs} the notification and adds it to the stage,
	 * bottom-center position. Duration equal to timeout seconds.
	 */
	public Notification show(Stage stage, float timeout) {
		if (hasParent()) {

			pack();
			setPosition(Math.round((stage.getWidth() - getPrefWidth()) / 2),
					getHeight() * .1f);
			clearActions();
			addAction(Actions.delay(timeout - FADE_IN,
					Actions.run(hideRunnable())));
			return this;
		}

		Action action = null;
		getColor().a = 0f;
		if (timeout != -1 && timeout > 0) {
			removeProgressBar();
			action = Actions.sequence(
					Actions.fadeIn(FADE_IN, Interpolation.fade),
					Actions.delay(timeout - FADE_IN,
							Actions.run(hideRunnable())));
		} else {
			setUpProgressBar();
			action = Actions.fadeIn(FADE_IN, Interpolation.fade);
		}

		previousKeyboardFocus = null;
		Actor actor = stage.getKeyboardFocus();
		if (actor != null && !actor.isDescendantOf(this))
			previousKeyboardFocus = actor;

		previousScrollFocus = null;
		actor = stage.getScrollFocus();
		if (actor != null && !actor.isDescendantOf(this))
			previousScrollFocus = actor;

		pack();
		setPosition(Math.round((stage.getWidth() - getPrefWidth()) / 2),
				getHeight() * .1f);
		stage.setKeyboardFocus(this);
		stage.setScrollFocus(this);

		super.show(stage, action);
		return this;
	}

	private void removeProgressBar() {
		if (progressBarCell != null) {
			progressBarCell.setWidget(null);
		}
	}

	private void setUpProgressBar() {
		if (progressBar == null) {
			progressBar = new Image(progressBarDrawable);
			progressBar.setOrigin(progressBar.getPrefWidth() * .5f,
					progressBar.getPrefHeight() * .5f);
			progressBar
					.addAction(Actions.forever(Actions.rotateBy(-360f, 1.5f)));
			progressBarCell = add();
		}
		progressBarCell.setWidget(progressBar);
	}

	@Override
	public void show(Stage stage) {
		this.show(stage, -1F);
	}

	/**
	 * Hides the notification if a touch event is detected outside this actor
	 * and this notification {@link #isModal()} and
	 * {@link #setHideOnExternalTouch(boolean)} is set to true. The default
	 * implementation fades out the dialog over {@link HiddenPanel#fadeDuration}
	 * seconds and then removes it from the stage.
	 */
	public void hide() {
		if (!isShowing()) {
			return;
		}
		Stage stage = getStage();
		if (stage != null) {
			if (previousKeyboardFocus != null
					&& previousKeyboardFocus.getStage() == null)
				previousKeyboardFocus = null;
			Actor actor = stage.getKeyboardFocus();
			if (actor == null || actor.isDescendantOf(this))
				stage.setKeyboardFocus(previousKeyboardFocus);

			if (previousScrollFocus != null
					&& previousScrollFocus.getStage() == null)
				previousScrollFocus = null;
			actor = stage.getScrollFocus();
			if (actor == null || actor.isDescendantOf(this))
				stage.setScrollFocus(previousScrollFocus);
		}
		super.hide(Actions.fadeOut(FADE_OUT, Interpolation.fade));

	}

	public static class NotificationStyle {

		/** Optional **/
		private Drawable background, stageBackground, undefinedProgressBar;

		public NotificationStyle() {
		}
	}

	private Runnable hideRunnable() {
		if (hide == null) {
			hide = new Runnable() {
				@Override
				public void run() {
					hide();
				}
			};
		}
		return hide;
	}

	public boolean isShowing() {
		return hasParent() || isTouchable();
	}
}
