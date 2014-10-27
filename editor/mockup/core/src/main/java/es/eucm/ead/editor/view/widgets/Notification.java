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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class Notification extends HiddenPanel {

	private static final float DEFAULT_SPACING = 10F;
	private static final float FADE_IN = .3F;
	private static final float FADE_OUT = .2F;

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
		setTouchable(Touchable.disabled);
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

		Action action = null;
		getColor().a = 0f;
		if (timeout > 0) {
			removeProgressBar();
			action = Actions.sequence(
					Actions.fadeIn(FADE_IN, Interpolation.fade),
					Actions.delay(timeout - FADE_IN,
							Actions.run(hideRunnable())));
		} else {
			setUpProgressBar();
			action = Actions.fadeIn(FADE_IN, Interpolation.fade);
		}

		float prefWidth = Math.min(getPrefWidth(), stage.getWidth());
		float prefHeight = getPrefHeight();
		setBounds(Math.round((stage.getWidth() - prefWidth) / 2),
				Math.round(prefHeight * .1f), Math.round(prefWidth),
				Math.round(prefHeight));

		super.show(stage, action);
		return this;
	}

	private void removeProgressBar() {
		if (progressBarCell != null) {
			progressBarCell.setActor(null).padLeft(0f);
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
		progressBarCell.setActor(progressBar).padLeft(DEFAULT_SPACING);
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
		return hasParent();
	}
}
