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
package es.eucm.ead.editor.view.widgets.mockup;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.esotericsoftware.tablelayout.Cell;

import es.eucm.ead.editor.view.widgets.mockup.panels.HiddenPanel;

public class Notification extends HiddenPanel {

	private static final float DEFAULT_SPACING = 10F;

	private Runnable hide;
	private Actor previousKeyboardFocus, previousScrollFocus;
	private Skin skin;

	public Notification(Skin skin) {
		super(skin);
	}

	@Override
	protected void initialize(Skin skin) {
		modal(false);
		this.skin = skin;
		setVisible(false);
		pad(DEFAULT_SPACING);
		defaults().space(DEFAULT_SPACING);
	}

	public Notification modal(boolean isModal) {
		super.setModal(isModal);
		return this;
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
	public Cell<Label> text(Label label) {
		return add(label);
	}

	/**
	 * Creates an undefined progress bar and adds it to this notification.
	 */
	public Notification createUndefinedProgressBar() {
		add(new Image(skin.getDrawable("ic_undefined_progress_bar")) {
			@Override
			public void layout() {
				super.layout();
				setOrigin(getWidth() * .5f, getHeight() * .5f);
			}

			@Override
			public void act(float delta) {
				setRotation(getRotation() + 360f * delta);
			}
		});
		return this;
	}

	/**
	 * {@link #pack() Packs} the notification and adds it to the stage,
	 * bottom-center position. Duration equal to timeout seconds.
	 */
	public Notification show(Stage stage, float timeout) {
		if (isVisible())
			return this;
		clearActions();

		previousKeyboardFocus = null;
		Actor actor = stage.getKeyboardFocus();
		if (actor != null && !actor.isDescendantOf(this))
			previousKeyboardFocus = actor;

		previousScrollFocus = null;
		actor = stage.getScrollFocus();
		if (actor != null && !actor.isDescendantOf(this))
			previousScrollFocus = actor;

		pack();
		setPosition(Math.round((stage.getWidth() - getWidth()) / 2),
				getHeight() * .1f);
		stage.addActor(this);
		stage.setKeyboardFocus(this);
		stage.setScrollFocus(this);
		super.show();
		if (timeout != -1 && timeout > 0)
			stage.addAction(Actions.sequence(Actions.delay(timeout),
					Actions.run(hideRunnable())));
		return this;
	}

	/**
	 * {@link #pack() Packs} the notification and adds it to the stage,
	 * bottom-center position. Duration undefined.
	 */
	public Notification show(Stage stage) {
		return show(stage, -1);
	}

	/**
	 * Hides the notification if a touch event is detected outside this actor
	 * and this notification {@link #isModal()} and
	 * {@link #setHideOnExternalTouch(boolean)} is set to true. The default
	 * implementation fades out the dialog over {@link HiddenPanel#fadeDuration}
	 * seconds and then removes it from the stage.
	 */
	public void hide() {
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

		super.hide();
	}

	@Override
	protected void onFadedOut() {
		remove();
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
}
