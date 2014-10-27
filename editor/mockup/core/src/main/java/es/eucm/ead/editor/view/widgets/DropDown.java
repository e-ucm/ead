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

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

import es.eucm.ead.editor.view.widgets.iconwithpanel.IconWithScalePanel;

/**
 * A drop down allows a user to choose one of a number of values from a list.
 * When inactive, the selected value is displayed. When activated, it shows the
 * list of values that may be selected.
 * <p>
 * {@link ChangeEvent} is fired when the drop down selection changes.
 * <p>
 * The preferred size of the select box is determined by the maximum bounds of
 * the items and the size of the {@link DropDownStyle#background}.
 */
public class DropDown extends Container<Actor> implements Disableable {
	private static final float FADE = .25f;

	private static final Vector2 tmpCoords = new Vector2();

	private ClickListener clickListener;
	private DropDownStyle style;
	private ListScroll scroll;
	private boolean disabled;
	private Actor selection;
	private boolean changeIcon;

	public DropDown(Skin skin) {
		this(skin, skin.get(DropDownStyle.class), true);
	}

	public DropDown(Skin skin, boolean changeIcon) {
		this(skin, skin.get(DropDownStyle.class), changeIcon);
	}

	public DropDown(Skin skin, String styleName, boolean changeIcon) {
		this(skin, skin.get(styleName, DropDownStyle.class), changeIcon);
	}

	public DropDown(Skin skin, String styleName) {
		this(skin, skin.get(styleName, DropDownStyle.class), true);
	}

	private DropDown(Skin skin, DropDownStyle style, boolean changeIcon) {
		setStyle(style);
		setBackground(style.background);
		this.changeIcon = changeIcon;
		scroll = new ListScroll(skin);
		scroll.setBackground(style.listBackgroundDown);

		addListener(clickListener = new ClickListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (pointer == 0 && button != 0)
					return false;
				if (disabled)
					return false;
				showList();
				return true;
			}
		});
	}

	@Override
	public float getPrefWidth() {
		Actor sel = scroll.first.getActor();
		if (sel == null) {
			sel = selection;
		}
		if (sel != null) {
			return sel.getWidth();
		}
		return super.getPrefWidth();
	}

	@Override
	public float getPrefHeight() {
		Actor sel = scroll.first.getActor();
		if (sel == null) {
			sel = selection;
		}
		if (sel != null) {
			return sel.getHeight();
		}
		return super.getPrefHeight();
	}

	public void setStyle(DropDownStyle style) {
		if (style == null)
			throw new IllegalArgumentException("style cannot be null.");
		this.style = style;
		invalidateHierarchy();
	}

	/**
	 * Returns the drop down's style. Modifying the returned style may not have
	 * an effect until {@link #setStyle(DropDowntyle)} is called.
	 */
	public DropDownStyle getStyle() {
		return style;
	}

	public void setItems(Actor... newItems) {
		if (newItems == null)
			throw new IllegalArgumentException("newItems cannot be null.");

		resetScroll();
		for (int i = 1; i < newItems.length; ++i) {
			addToList(newItems[i]);
		}

		if (newItems.length > 0) {
			setSelected(newItems[0]);
		}

	}

	public void setItems(Array<Actor> newItems) {
		if (newItems == null)
			throw new IllegalArgumentException("newItems cannot be null.");

		resetScroll();
		for (int i = 1; i < newItems.size; ++i) {
			addToList(newItems.get(i));
		}

		if (newItems.size > 0) {
			setSelected(newItems.first());
		}

	}

	private void resetScroll() {
		scroll.clearChildren();
		scroll.first = scroll.add((Actor) null);
		scroll.row();
	}

	public Array<Actor> getItems() {
		return scroll.getChildren();
	}

	@Override
	protected void drawBackground(Batch batch, float parentAlpha, float x,
			float y) {
		Drawable background;
		if (disabled && style.backgroundDisabled != null) {
			background = style.backgroundDisabled;
		} else if (scroll.hasParent()) {
			if (scroll.getBackground() == style.listBackgroundUp) {
				background = style.backgroundOpenUp;
			} else {
				background = style.backgroundOpenDown;
			}
		} else if (clickListener.isOver() && style.backgroundOver != null) {
			background = style.backgroundOver;
		} else {
			background = style.background;
		}
		if (background != null) {
			setBackground(background);
		}
		super.drawBackground(batch, parentAlpha, x, y);
	}

	/** Returns the selected actor, or null. */
	public Actor getSelected() {
		return selection;
	}

	/**
	 * Sets the selection
	 */
	public void setSelected(Actor item) {
		if (getActor() == item) {
			return;
		}
		Cell<Actor> cell = scroll.getCell(item);
		if (cell != null) {
			cell.setActor(selection);
		}
		selection = item;
		setActor(item);
		invalidateHierarchy();
	}

	private void addToList(Actor actor) {
		scroll.add(actor);
		scroll.row();
	}

	public void setDisabled(boolean disabled) {
		if (disabled && !this.disabled)
			hideList();
		this.disabled = disabled;
	}

	public void showList() {
		scroll.show(getStage());
	}

	public void hideList() {
		scroll.hide();
	}

	/** Returns the list shown when the select box is open. */
	public Table getList() {
		return scroll;
	}

	private class ListScroll extends HiddenPanel {

		private Cell<Actor> first;
		private final Runnable showChildren = new Runnable() {

			@Override
			public void run() {

				for (Cell cell : getCells()) {
					Actor actor = cell.getActor();
					if (actor != null) {
						actor.addAction(Actions.fadeIn(.2f, Interpolation.fade));
					}
				}
			}
		};
		private final Runnable addSelection = new Runnable() {

			@Override
			public void run() {
				selection.setPosition(0f, 0f);
				first.setActor(selection);
				selection = null;
			}
		};

		private final Runnable setSelection = new Runnable() {

			@Override
			public void run() {
				setSelected(selection);
				selection.setTouchable(Touchable.enabled);
			}
		};

		public ListScroll(Skin skin) {
			super(skin);
			setTransform(true);
			defaults().uniform();

			addListener(new InputListener() {
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					if (changeIcon) {
						Actor target = event.getTarget();
						if (target != null) {
							Cell<Actor> cell = getCell(target);
							if (cell != null && cell != first) {
								setSelected(target);
								cell.setActor(first.getActor());
								ChangeEvent changeEvent = Pools
										.obtain(ChangeEvent.class);
								changeEvent.setListenerActor(DropDown.this);
								DropDown.this.fire(changeEvent);
								Pools.free(changeEvent);
							}
						}
					}
					hideList();
					return false;
				}
			});
		}

		public void show(Stage stage) {
			selection.localToStageCoordinates(tmpCoords.set(0f, 0f));
			int selectionX = MathUtils.round(tmpCoords.x);
			int selectionY = MathUtils.round(tmpCoords.y);
			first.setActor(selection);

			Drawable bg = getBackground();
			if (bg == null) {
				bg = style.listBackgroundDown;
			}
			float iconHeight = DropDown.this.getPrefHeight()
					- DropDown.this.getPadTop() + bg.getTopHeight();
			float prefWidth = getPrefWidth();
			DropDown.this.localToStageCoordinates(tmpCoords.set(
					(DropDown.this.getWidth() - prefWidth) * .5f, iconHeight));

			float height = getPrefHeight();

			float heightBelow = tmpCoords.y;
			float heightAbove = stage.getCamera().viewportHeight - tmpCoords.y
					- iconHeight;
			boolean below = true;
			if (height > heightBelow) {
				if (heightAbove > heightBelow) {
					below = false;
					height = Math.min(height, heightAbove);
				} else {
					height = heightBelow;
				}
			}

			if (below) {
				setY(MathUtils.round(tmpCoords.y - height));
				setBackground(style.listBackgroundDown);
				setOriginY(height);
			} else {
				setY(MathUtils.round(tmpCoords.y + DropDown.this.getHeight()));
				setBackground(style.listBackgroundUp);
				setOriginY(0);
			}
			setX(MathUtils.round(tmpCoords.x));
			setWidth(MathUtils.round(prefWidth));
			setHeight(MathUtils.round(height));
			validate();

			if (getX() > stage.getCamera().viewportWidth * .5f) {
				setOriginX(getWidth());
			} else {
				setOriginX(0);
			}

			clearActions();
			setScale(0f);
			getColor().a = 1f;

			super.show(stage, Actions.sequence(IconWithScalePanel.showAction(
					FADE, FADE * 2f, showChildren), Actions.run(addSelection),
					Actions.touchable(Touchable.enabled)));
			selection.setPosition(selectionX, selectionY);
			stage.addActor(selection);
			for (Cell<Actor> cell : getCells()) {
				Actor actor = cell.getActor();
				if (actor != null) {
					actor.getColor().a = 0f;
				}
			}
		}

		@Override
		public void hide() {
			if (selection == null || getActions().size > 0) {
				clearActions();
				setTouchable(Touchable.disabled);
				if (selection == null) {
					selection = first.getActor();
				}
				selection.setTouchable(Touchable.disabled);
				selection.localToStageCoordinates(tmpCoords.set(0f, 0f));
				int selectionX = MathUtils.round(tmpCoords.x);
				int selectionY = MathUtils.round(tmpCoords.y);
				selection.setPosition(selectionX, selectionY);
				getStage().addActor(selection);
				hide(Actions.sequence(fadeOut(FADE, Interpolation.fade),
						Actions.run(setSelection)));
			} else {
				hide(fadeOut(FADE, Interpolation.fade));
			}
		}
	}

	/**
	 * The style for a drop down, see {@link DropDown}.
	 * 
	 */
	static public class DropDownStyle {

		/** Optional. */
		public Drawable background;

		public Drawable listBackgroundUp, listBackgroundDown,
				backgroundOpenDown, backgroundOpenUp;

		/** Optional. */
		public Drawable backgroundOver, backgroundDisabled;

		public DropDownStyle() {
		}

		public DropDownStyle(Drawable background, Drawable listBackgroundUp,
				Drawable listBackgroundDown, ListStyle listStyle) {
			this.background = background;
			this.listBackgroundUp = listBackgroundUp;
			this.listBackgroundDown = listBackgroundDown;
		}

		public DropDownStyle(DropDownStyle style) {
			this.background = style.background;
			this.backgroundOver = style.backgroundOver;
			this.backgroundOpenUp = style.backgroundOpenUp;
			this.backgroundOpenDown = style.backgroundOpenDown;
			this.backgroundDisabled = style.backgroundDisabled;
			this.listBackgroundUp = style.listBackgroundUp;
			this.listBackgroundDown = style.listBackgroundDown;
		}
	}

	public static class DropdownChangeListener extends ChangeListener {

		private Actor currentSelected;

		@Override
		public void changed(ChangeEvent changeEvent, Actor actor) {
			DropDown listenerActor = (DropDown) changeEvent.getListenerActor();
			Actor selected = listenerActor.getSelected();
			if (currentSelected != selected) {
				currentSelected = selected;
				changed(selected, listenerActor);
			}
		}

		public void changed(Actor selected, DropDown listenerActor) {

		}
	}
}
