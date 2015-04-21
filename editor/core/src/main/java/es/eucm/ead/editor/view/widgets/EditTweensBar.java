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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.IconTextButton.Position;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

/**
 * UI for add tweens
 * 
 */
public class EditTweensBar extends Table {

	private static final float LATERAL_MARGIN = 10, BASE_MARGIN = 2,
			WIDTH_TYPE_TWEEN = 170, PAD_TYPE_TWEEN = 10;

	private static final String INSTANT = "education32x32",
			GRADUAL = "education32x32"; // TODO icons

	private Button instantButton;

	private Button gradualButton;

	protected TypeTweensBar instantTweens;

	protected TypeTweensBar gradualsTweens;

	private Cell<TypeTweensBar> right;

	protected DragAndDrop dragNDrop;

	private Skin skin;

	private Drawable backgroundTweens;

	/**
	 * Create a {@link EditTweensBar} without background.
	 * 
	 */
	public EditTweensBar(Controller controller) {
		this(null, null, controller, controller.getApplicationAssets()
				.getSkin());
	}

	/**
	 * Create a {@link EditTweensBar} without background and with
	 * {@link DragAndDrop}.
	 * 
	 */
	public EditTweensBar(DragAndDrop dragAndDrop, Controller controller) {
		this(null, null, controller, controller.getApplicationAssets()
				.getSkin());
		dragNDrop = dragAndDrop;
	}

	/**
	 * Create a {@link EditTweensBar} with background and {@link DragAndDrop}.
	 * 
	 */
	public EditTweensBar(Drawable background, DragAndDrop dragAndDrop,
			Controller controller) {
		this(background, null, controller, controller.getApplicationAssets()
				.getSkin());
		dragNDrop = dragAndDrop;
	}

	/**
	 * Create a {@link EditTweensBar} with background and with
	 * {@link DragAndDrop}. The {@link StretchableButton}s created by drag and
	 * drop have own background <b>backgroundTweens</b>.
	 */
	public EditTweensBar(Drawable background, Drawable backgroundTweens,
			DragAndDrop dragAndDrop, Controller controller) {
		this(background, backgroundTweens, controller, controller
				.getApplicationAssets().getSkin());
		dragNDrop = dragAndDrop;
	}

	/**
	 * Create a {@link EditTweensBar} with background and with
	 * {@link DragAndDrop}. The {@link StretchableButton}s created by drag and
	 * drop have own background <b>backgroundTweens</b>.
	 */
	public EditTweensBar(Drawable background, Drawable backgroundTweens,
			Controller controller, Skin skin) {
		super(skin);
		this.skin = skin;
		this.backgroundTweens = backgroundTweens;

		I18N i18n = controller.getApplicationAssets().getI18N();

		setBackground(background);

		align(Align.left);

		instantTweens = new TypeTweensBar(skin);
		gradualsTweens = new TypeTweensBar(skin);

		instantButton = new IconTextButton(i18n.m("Instant"), skin,
				skin.getDrawable(INSTANT), Position.RIGHT, PAD_TYPE_TWEEN, 0,
				WIDTH_TYPE_TWEEN);
		instantButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				changeTweensBar(instantTweens);
			}
		});

		gradualButton = new IconTextButton(i18n.m("Over time"), skin,
				skin.getDrawable(GRADUAL), Position.RIGHT, PAD_TYPE_TWEEN, 0,
				WIDTH_TYPE_TWEEN);
		gradualButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				changeTweensBar(gradualsTweens);
			}
		});

		LinearLayout left = new LinearLayout(false);

		left.add(instantButton)
				.margin(LATERAL_MARGIN, BASE_MARGIN, LATERAL_MARGIN,
						BASE_MARGIN).expand(true, true);
		left.add(new Separator(true, skin));
		left.add(gradualButton)
				.margin(LATERAL_MARGIN, BASE_MARGIN, LATERAL_MARGIN,
						BASE_MARGIN).expand(true, true);

		this.add(left);
		this.add(new Separator(false, skin));
		right = this.add(instantTweens).expand(true, true).align(Align.left);
	}

	public void addInstant(final IconTextButton actor) {
		instantTweens.addButton(actor);
		if (dragNDrop != null) {
			dragNDrop.addSource(createDefaultInstantSource(actor));
		}
	}

	protected Source createDefaultInstantSource(final IconTextButton actor) {
		return new Source(actor) {

			@Override
			public Payload dragStart(InputEvent event, float x, float y,
					int pointer) {

				instantTweens.cancelScrollFocus(false);

				Payload payload = new Payload();

				FixedButton instant = new FixedButton(actor.getDrawableImage(),
						actor.getDrawableImage(), skin);

				payload.setDragActor(instant);
				return payload;
			}

			@Override
			public void dragStop(InputEvent event, float x, float y,
					int pointer, Payload payload, Target target) {

				instantTweens.cancelScrollFocus(true);
			}

		};
	}

	public void addGradual(final IconTextButton actor) {
		gradualsTweens.addButton(actor);
		if (dragNDrop != null) {
			dragNDrop.addSource(createDefaultGradualSource(actor));
		}
	}

	protected Source createDefaultGradualSource(final IconTextButton actor) {
		return new Source(actor) {

			@Override
			public Payload dragStart(InputEvent event, float x, float y,
					int pointer) {

				gradualsTweens.cancelScrollFocus(false);

				Payload payload = new Payload();

				IconButton icon = new IconButton(actor.getDrawableImage(), skin);

				StretchableButton gradual = new StretchableButton(icon, actor
						.getDrawableImage().getMinWidth(), backgroundTweens,
						skin);

				payload.setDragActor(gradual);
				return payload;
			}

			@Override
			public void dragStop(InputEvent event, float x, float y,
					int pointer, Payload payload, Target target) {

				instantTweens.cancelScrollFocus(true);
			}

		};
	}

	private void changeTweensBar(TypeTweensBar newBar) {
		right.setActor(null);
		right.setActor(newBar);
	}

	/**
	 * List of buttons with a scroll pane
	 * 
	 */
	protected class TypeTweensBar extends Table {

		private static final float SCROLL_MOVING = 115, DEFAULT_PAD = 5;
		private static final String FORWARD = "forward24x24",
				BACK = "back24x24";

		private Button leftArrow;

		private Button rightArrow;

		private ScrollPane scroll;

		private Table inner;

		private Skin skin;

		public TypeTweensBar(Skin skin) {
			super();

			this.skin = skin;

			inner = new Table();
			scroll = new ScrollPane(inner);

			leftArrow = new IconButton(BACK, DEFAULT_PAD, skin) {
				@Override
				public float getPrefHeight() {
					if (this.getParent() != null) {
						return this.getParent().getHeight();
					}
					return super.getPrefHeight();
				}
			};
			leftArrow.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					scroll.setScrollX(scroll.getScrollX() - SCROLL_MOVING);
					return super.touchDown(event, x, y, pointer, button);
				}
			});

			rightArrow = new IconButton(FORWARD, DEFAULT_PAD, skin) {
				@Override
				public float getPrefHeight() {
					if (this.getParent() != null) {
						return this.getParent().getHeight();
					}
					return super.getPrefHeight();
				}
			};
			rightArrow.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					scroll.setScrollX(scroll.getScrollX() + SCROLL_MOVING);
					return super.touchDown(event, x, y, pointer, button);
				}
			});

			this.add(leftArrow);
			this.add(scroll);
			inner.add(new Separator(false, skin));
			this.add(rightArrow);
		}

		public void addButton(Actor actor) {
			inner.add(actor);
			inner.add(new Separator(false, skin));
		}

		public void cancelScrollFocus(boolean focus) {
			scroll.setCancelTouchFocus(focus);
			if (!focus) {
				scroll.cancel();
			}
		}

	}

}
