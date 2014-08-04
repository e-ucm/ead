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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SnapshotArray;

import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

/**
 * A select box (aka a drop-down list) allows a user to choose one of a number
 * of values from a list. When inactive, the selected value is displayed. When
 * activated, it shows the list of values that may be selected.
 * <p>
 * {@link ChangeEvent} is fired when the selectbox selection changes.
 * <p>
 * The preferred size of the select box is determined by the maximum text bounds
 * of the items and the size of the {@link SelectBoxStyle#background}.
 */
public class DropDown extends WidgetGroup implements Disableable {
	static final Vector2 tmpCoords = new Vector2();

	DropDownStyle style;
	ListScroll scroll;
	Actor previousScrollFocus;
	Actor previousSelection;
	private float prefWidth, prefHeight;
	private ClickListener clickListener;
	boolean disabled;

	public DropDown(Skin skin) {
		this(skin.get(DropDownStyle.class));
	}

	public DropDown(Skin skin, String styleName) {
		this(skin.get(styleName, DropDownStyle.class));
	}

	public DropDown(DropDownStyle style) {
		setStyle(style);
		scroll = new ListScroll();
		setSize(getPrefWidth(), getPrefHeight());

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
	 * Returns the select box's style. Modifying the returned style may not have
	 * an effect until {@link #setStyle(SelectBoxStyle)} is called.
	 */
	public DropDownStyle getStyle() {
		return style;
	}

	public void setItems(Actor... newItems) {
		if (newItems == null)
			throw new IllegalArgumentException("newItems cannot be null.");

		scroll.list.clearChildren();
		for (Actor item : newItems) {
			scroll.list.add(item);
		}

		if (newItems.length > 0) {
			setSelected(newItems[0]);
		}

		invalidateHierarchy();
	}

	public void setItems(Array<Actor> newItems) {
		if (newItems == null)
			throw new IllegalArgumentException("newItems cannot be null.");

		scroll.list.clearChildren();
		for (Actor item : newItems) {
			scroll.list.add(item);
		}

		if (newItems.size > 0) {
			setSelected(newItems.first());
		}

		invalidateHierarchy();
	}

	public Array<Actor> getItems() {
		return scroll.list.getChildren();
	}

	public void layout() {
		Drawable bg = style.background;

		prefHeight = Math.max(bg.getTopHeight() + bg.getBottomHeight()
				+ scroll.list.getChildrenMaxHeight(), bg.getMinHeight());

		float maxItemWidth = scroll.list.getChildrenMaxWidth();

		prefWidth = bg.getLeftWidth() + bg.getRightWidth() + maxItemWidth;

		ScrollPaneStyle scrollStyle = style.scrollStyle;
		prefWidth = Math
				.max(prefWidth,
						maxItemWidth
								+ scrollStyle.background.getLeftWidth()
								+ scrollStyle.background.getRightWidth()
								+ Math.max(
										style.scrollStyle.vScroll != null ? style.scrollStyle.vScroll
												.getMinWidth() : 0,
										style.scrollStyle.vScrollKnob != null ? style.scrollStyle.vScrollKnob
												.getMinWidth() : 0));
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		Drawable background;
		if (disabled && style.backgroundDisabled != null)
			background = style.backgroundDisabled;
		else if (scroll.hasParent() && style.backgroundOpen != null)
			background = style.backgroundOpen;
		else if (clickListener.isOver() && style.backgroundOver != null)
			background = style.backgroundOver;
		else
			background = style.background;

		background.draw(batch, 0f, 0f, getWidth(), getHeight());
		super.drawChildren(batch, parentAlpha);
	}

	/** Returns the selected item, or null. */
	public Actor getSelected() {
		return null;
	}

	/**
	 * Sets the selection
	 */
	public void setSelected(Actor item) {
		if (previousSelection != null) {
			scroll.list.add(previousSelection);
		}
		previousSelection = item;
		addActor(item);
	}

	/**
	 * @return The index of the first selected item. The top item has an index
	 *         of 0. Nothing selected has an index of -1.
	 */
	public int getSelectedIndex() {
		SnapshotArray<Actor> children = scroll.list.getChildren();
		return -1;
	}

	public void setDisabled(boolean disabled) {
		if (disabled && !this.disabled)
			hideList();
		this.disabled = disabled;
	}

	public float getPrefWidth() {
		validate();
		return prefWidth;
	}

	public float getPrefHeight() {
		validate();
		return prefHeight;
	}

	public void showList() {
		scroll.list.setTouchable(Touchable.enabled);
		scroll.show(getStage());
	}

	public void hideList() {
		if (!scroll.hasParent())
			return;
		scroll.list.setTouchable(Touchable.disabled);
		Stage stage = scroll.getStage();
		if (stage != null) {
			if (previousScrollFocus != null
					&& previousScrollFocus.getStage() == null)
				previousScrollFocus = null;
			Actor actor = stage.getScrollFocus();
			if (actor == null || actor.isDescendantOf(scroll))
				stage.setScrollFocus(previousScrollFocus);
		}
		scroll.addAction(sequence(fadeOut(0.15f, Interpolation.fade),
				Actions.removeActor()));
	}

	/** Returns the list shown when the select box is open. */
	public LinearLayout getList() {
		return scroll.list;
	}

	/**
	 * Returns the scroll pane containing the list that is shown when the select
	 * box is open.
	 */
	public ScrollPane getScrollPane() {
		return scroll;
	}

	private class ListScroll extends ScrollPane {
		final LinearLayout list;
		final Vector2 screenCoords = new Vector2();

		public ListScroll() {
			super(null, style.scrollStyle);

			setOverscroll(false, false);
			setFadeScrollBars(false);

			list = new LinearLayout(false);
			setWidget(list);

			addListener(new InputListener() {
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					if (event.getTarget() == list)
						return true;
					hideList();
					return false;
				}

				public void touchUp(InputEvent event, float x, float y,
						int pointer, int button) {
					if (hit(x, y, true) == list) {
						ChangeEvent changeEvent = Pools
								.obtain(ChangeEvent.class);
						DropDown.this.fire(changeEvent);
						Pools.free(changeEvent);
						hideList();
					}
				}
			});
		}

		public void show(Stage stage) {
			stage.addActor(this);

			DropDown.this.localToStageCoordinates(tmpCoords.set(0, 0));
			screenCoords.set(tmpCoords);

			float height = list.getHeight();
			Drawable background = getStyle().background;
			if (background != null)
				height += background.getTopHeight()
						+ background.getBottomHeight();

			float heightBelow = tmpCoords.y;
			float heightAbove = stage.getCamera().viewportHeight - tmpCoords.y
					- DropDown.this.getHeight();
			boolean below = true;
			if (height > heightBelow) {
				if (heightAbove > heightBelow) {
					below = false;
					height = Math.min(height, heightAbove);
				} else
					height = heightBelow;
			}

			if (below)
				setY(tmpCoords.y - height);
			else
				setY(tmpCoords.y + DropDown.this.getHeight());
			setX(tmpCoords.x);
			setWidth(DropDown.this.getWidth());
			setHeight(height);

			validate();
			// TODO scroll to center
			// scrollToCenter(0, list.getHeight() - getSelectedIndex()
			// * itemHeight - itemHeight / 2, 0, 0);
			// updateVisualScroll();

			clearActions();
			getColor().a = 0;
			addAction(fadeIn(0.3f, Interpolation.fade));

			previousScrollFocus = null;
			Actor actor = stage.getScrollFocus();
			if (actor != null && !actor.isDescendantOf(this))
				previousScrollFocus = actor;

			stage.setScrollFocus(this);
		}

		@Override
		public Actor hit(float x, float y, boolean touchable) {
			Actor actor = super.hit(x, y, touchable);
			return actor != null ? actor : this;
		}

		public void act(float delta) {
			super.act(delta);
			DropDown.this.localToStageCoordinates(tmpCoords.set(0, 0));
			if (tmpCoords.x != screenCoords.x || tmpCoords.y != screenCoords.y)
				hideList();
		}
	}

	/**
	 * The style for a drop down, see {@link DropDown}.
	 * 
	 */
	static public class DropDownStyle {
		/** Optional. */
		public Drawable background;
		public ScrollPaneStyle scrollStyle;
		/** Optional. */
		public Drawable backgroundOver, backgroundOpen, backgroundDisabled;

		public DropDownStyle() {
		}

		public DropDownStyle(Drawable background, ScrollPaneStyle scrollStyle,
				ListStyle listStyle) {
			this.background = background;
			this.scrollStyle = scrollStyle;
		}

		public DropDownStyle(DropDownStyle style) {
			this.background = style.background;
			this.backgroundOver = style.backgroundOver;
			this.backgroundOpen = style.backgroundOpen;
			this.backgroundDisabled = style.backgroundDisabled;
			this.scrollStyle = new ScrollPaneStyle(style.scrollStyle);
		}
	}
}
