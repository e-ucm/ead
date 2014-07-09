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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

/**
 * Creates a dialog window. This class is inspired by
 * {@link com.badlogic.gdx.scenes.scene2d.ui.Dialog} and
 * {@link com.badlogic.gdx.scenes.scene2d.ui.Window}.
 * 
 * By default, the dialog is modal and has maximize button, but they can be
 * changed using the appropriate setters.
 * 
 * There are not setter method for {@link Dialog#maximizable} does not appear in
 * order to avoid methods with non-typical behaviours: the maximizer button is
 * added at the dialog creation and the {@link Dialog#maximizable} only inform
 * about if that button was included or not
 * 
 * 
 */
public class Dialog extends LinearLayout {

	private static final float DRAG_MARGIN = 20.0f;

	private Skin skin;

	private DialogStyle style;

	private Label titleLabel;

	private PlaceHolder content;

	private LinearLayout buttons;

	private boolean maximized = false;

	private float oldX;

	private float oldY;

	private float oldWidth;

	private float oldHeight;

	private Actor previousKeyboardFocus;

	private Actor previousScrollFocus;

	private boolean isModal = true;

	private boolean maximizable;

	/**
	 * Creates a default dialog, modal with close and maximize buttons
	 */
	public Dialog(Skin skin) {
		this(skin, true);
	}

	/**
	 * Creates a default dialog (modal and which include close and maximize
	 * button)
	 * 
	 * @param maximizable
	 *            include or not the maximizer button
	 */
	public Dialog(Skin skin, boolean maximizable) {
		super(false);
		this.skin = skin;
		this.maximizable = maximizable;
		style = skin.get(DialogStyle.class);
		background(style.background);

		addTitle();
		add(content = new PlaceHolder()).margin(style.pad);
		addButtons();

		addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				return isModal;
			}

			@Override
			public boolean mouseMoved(InputEvent event, float x, float y) {
				return isModal;
			}

			@Override
			public boolean scrolled(InputEvent event, float x, float y,
					int amount) {
				return isModal;
			}

			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				return isModal;
			}

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				switch (keycode) {
				case Keys.ESCAPE:
					hide();
					break;
				}
				event.cancel();
				return isModal;
			}

			@Override
			public boolean keyTyped(InputEvent event, char character) {
				event.cancel();
				return isModal;
			}
		});
	}

	private void addTitle() {
		LinearLayout titleBar = new LinearLayout(true, style.titleBackground);
		titleBar.defaultWidgetsMargin(style.titleMargin);
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = style.titleFont;
		labelStyle.fontColor = style.titleFontColor;
		titleLabel = new Label("", labelStyle);
		titleBar.add(titleLabel);
		titleBar.addSpace();
		add(titleBar).margin(0, 0, 0, style.titleMargin).expandX();
		Image close = new Image(style.close);
		close.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				super.touchDown(event, x, y, pointer, button);
				event.stop();
				return true;
			}

			@Override
			public void clicked(InputEvent event, float x, float y) {
				hide();
			}
		});

		Image maximizeButton = new Image(style.maximize);
		maximizeButton.addListener(new ClickListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				super.touchDown(event, x, y, pointer, button);
				event.stop();
				return true;
			}

			@Override
			public void clicked(InputEvent event, float x, float y) {
				maximize();
			}
		});

		titleBar.add(maximizeButton);
		maximizeButton.setVisible(maximizable);

		titleBar.add(close);
		titleBar.addListener(new InputListener() {

			float startX;

			float startY;

			float dialogX;

			float dialogY;

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (maximized) {
					return false;
				}
				dialogX = getX();
				dialogY = getY();
				startX = event.getStageX();
				startY = event.getStageY();
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				float newX = Math.min(
						event.getStage().getWidth() - DRAG_MARGIN
								- Dialog.this.getWidth(),
						Math.max(DRAG_MARGIN, dialogX + event.getStageX()
								- startX));
				float newY = Math.min(
						event.getStage().getHeight() - DRAG_MARGIN
								- Dialog.this.getHeight(),
						Math.max(DRAG_MARGIN, dialogY + event.getStageY()
								- startY));
				setPosition(newX, newY);
			}
		});

		titleBar.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (maximizable && getTapCount() > 1) {
					maximize();
				}
			}
		});
	}

	private void addButtons() {
		buttons = new LinearLayout(true);
		buttons.addSpace();
		buttons.defaultWidgetsMargin(style.buttonsMargin);
		add(buttons).expandX();
	}

	private void maximize() {
		if (maximized) {
			setBounds(oldX, oldY, oldWidth, oldHeight);
		} else {
			oldWidth = getWidth();
			oldHeight = getHeight();
			oldX = getX();
			oldY = getY();
			setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			center();
		}
		maximized = !maximized;
	}

	public boolean isModal() {
		return isModal;
	}

	public void setModal(boolean isModal) {
		this.isModal = isModal;
	}

	/**
	 * Sets dialog content
	 */
	public Dialog root(Actor root) {
		content.setContent(root);
		return this;
	}

	public TextButton button(String text) {
		TextButton button = new TextButton(text, skin);
		buttons.add(button);
		return button;
	}

	public TextButton button(String text, String style) {
		TextButton button = new TextButton(text, skin, style);
		buttons.add(button);
		return button;
	}

	public Dialog title(String title) {
		titleLabel.setText(title);
		return this;
	}

	public void clearButtons() {
		buttons.clearChildren();
		buttons.addSpace();
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		Actor actor = super.hit(x, y, touchable);
		return (actor == null && isModal) ? this : actor;
	}

	/**
	 * Centers the dialog in the screen
	 */
	public void center() {
		float width = this.getWidth();
		float height = this.getHeight();
		float x = (getStage().getWidth() - width) / 2.0f;
		float y = (getStage().getHeight() - height) / 2.0f;
		setBounds(x, y, width, height);
	}

	/**
	 * Shows the dialog in the stage
	 */
	public void show(Stage stage) {
		previousKeyboardFocus = null;
		Actor actor = stage.getKeyboardFocus();
		if (actor != null && !actor.isDescendantOf(this))
			previousKeyboardFocus = actor;

		previousScrollFocus = null;
		actor = stage.getScrollFocus();
		if (actor != null && !actor.isDescendantOf(this))
			previousScrollFocus = actor;

		stage.addActor(this);
		stage.setKeyboardFocus(this);
		stage.setScrollFocus(this);

	}

	/**
	 * Hides the dialog
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
		remove();
	}

	/**
	 * Style for dialogs
	 */
	public static class DialogStyle {

		public BitmapFont titleFont;

		public Color titleFontColor;

		public Drawable background, titleBackground;

		public Drawable close, maximize;

		public float pad = 10.0f;

		public float titleMargin = 5.0f;

		public float buttonsMargin = 5.0f;
	}
}
