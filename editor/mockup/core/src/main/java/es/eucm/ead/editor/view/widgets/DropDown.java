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

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

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
	private static final Vector2 tmpCoords = new Vector2();

	private ClickListener clickListener;
	private Actor previousScrollFocus;
	private DropDownStyle style;
	private ListScroll scroll;
	private boolean disabled;
	private Actor selection;

	public DropDown(Skin skin) {
		this(skin.get(DropDownStyle.class));
	}

	public DropDown(Skin skin, String styleName) {
		this(skin.get(styleName, DropDownStyle.class));
	}

	public DropDown(DropDownStyle style) {
		setStyle(style);
		setBackground(style.background);
		scroll = new ListScroll();
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

		scroll.clearChildren();
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

		scroll.clearChildren();
		for (int i = 1; i < newItems.size; ++i) {
			addToList(newItems.get(i));
		}

		if (newItems.size > 0) {
			setSelected(newItems.first());
		}

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
	private void setSelected(Actor item) {
		Cell cell = scroll.getCell(item);
		if (selection != null) {
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
		scroll.setTouchable(Touchable.enabled);
		scroll.show(getStage());
	}

	public void hideList() {
		if (!scroll.hasParent())
			return;
		scroll.setTouchable(Touchable.disabled);
		Stage stage = scroll.getStage();
		if (stage != null) {
			if (previousScrollFocus != null
					&& previousScrollFocus.getStage() == null)
				previousScrollFocus = null;
			Actor actor = stage.getScrollFocus();
			if (actor == null || actor.isDescendantOf(scroll))
				stage.setScrollFocus(previousScrollFocus);
		}
		scroll.addAction(sequence(fadeOut(0.2f, Interpolation.fade),
				Actions.removeActor()));
	}

	/** Returns the list shown when the select box is open. */
	public Table getList() {
		return scroll;
	}

	private class ListScroll extends Table {

		public ListScroll() {
			defaults().uniform();

			addListener(new InputListener() {
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					Actor target = event.getTarget();
					if (target != null) {
						Cell cell = getCell(target);
						if (cell != null) {
							setSelected(target);
							ChangeEvent changeEvent = Pools
									.obtain(ChangeEvent.class);
							changeEvent.setListenerActor(DropDown.this);
							DropDown.this.fire(changeEvent);
							Pools.free(changeEvent);
						}
					}
					hideList();
					return false;
				}
			});
		}

		public void show(Stage stage) {
			stage.addActor(this);

			float prefWidth = getPrefWidth();
			DropDown.this.localToStageCoordinates(tmpCoords.set(
					(DropDown.this.getWidth() - prefWidth) * .5f, 0));

			float height = getPrefHeight();
			Drawable background = getStyle().background;
			if (background != null) {
				height += background.getTopHeight()
						+ background.getBottomHeight();
			}

			float heightBelow = tmpCoords.y;
			float heightAbove = stage.getCamera().viewportHeight - tmpCoords.y
					- DropDown.this.getHeight();
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
			} else {
				setY(MathUtils.round(tmpCoords.y + DropDown.this.getHeight()));
				setBackground(style.listBackgroundUp);
			}
			setX(MathUtils.round(tmpCoords.x));
			setWidth(MathUtils.round(prefWidth));
			setHeight(MathUtils.round(height));
			validate();

			clearActions();
			getColor().a = 0;
			addAction(fadeIn(0.3f, Interpolation.fade));

			previousScrollFocus = null;
			Actor actor = stage.getScrollFocus();
			if (actor != null && !actor.isDescendantOf(this)) {
				previousScrollFocus = actor;
			}

			stage.setScrollFocus(this);
		}

		@Override
		public Actor hit(float x, float y, boolean touchable) {
			Actor actor = super.hit(x, y, touchable);
			return actor != null ? actor : this;
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
}
