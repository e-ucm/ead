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
package es.eucm.ead.editor.view.widgets.layouts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.view.widgets.AbstractWidget;

/**
 * 
 * <p>
 * Layouts widgets horizontally or vertically. The container has a padding, and
 * each widget can have a margin. Space is distributed like this for horizontal:
 * </p>
 * 
 * <pre>
 *     _________________________________________
 *     |         padding top                   |
 *     |    _____________________________      |
 *     |pad |   __mt__                   |pad  |
 *     |left|  |      |                  |right|
 *     |    |ml|widget|mr ...            |     |
 *     |    |  |______|                  |     |
 *     |    |_____mb_____________________|     |
 *     |           padding bottom              |
 *     |_______________________________________|
 * 
 * </pre>
 * <p>
 * and like this for vertical:
 * </p>
 * 
 * <pre>
 *     __________________________
 *     |      padding top       |
 *     |    ______________      |
 *     |pad |   __mt__    |pad  |
 *     |left|  |      |   |right|
 *     |    |ml|widget|mr |     |
 *     |    |  |______|   |     |
 *     |    |     mb      |     |
 *     |    |     ...     |     |
 *     |    |_____________|
 *     |     padding bottom     |
 *     |________________________|
 * 
 * </pre>
 * <p>
 * where ml, mt, mr and mb are the left, top, right and bottom margin of the
 * widget. Each widget can have its own margin. Also, each widget can be
 * positioned to the left/bottom or the right/top of the container, with
 * {@link Constraints#left()}/ {@link Constraints#bottom()} and
 * {@link Constraints#right()}/ {@link Constraints#top()}.
 * </p>
 * <p>
 * By default, each widget width and height is set to the value returned by
 * {@link #getPrefWidth(Actor)} and {@link #getPrefHeight(Actor)}. When
 * {@link Constraints#expand(boolean, boolean)} is called, the widget will try
 * to expand its horizontal/vertical space to fill the remaining space in the
 * container.
 * </p>
 * <p>
 * With {@link #addSpace()} you can add some space between widgets. The space
 * will try to fill all remaining width and height.
 * </p>
 * <p>
 * To try to fit all widgets in the available space, the container padding and
 * the widget margins will be ignored when the width/height required by the
 * children exceeds the container width/height.
 * </p>
 */
public class LinearLayout extends AbstractWidget {

	protected boolean computeInvisibles;

	private boolean horizontal;

	private Drawable background;

	protected Array<Constraints> constraints;

	protected Insets padding;

	protected Insets defaultMargin;

	public LinearLayout(boolean horizontal) {
		this(horizontal, null);
	}

	public LinearLayout(boolean horizontal, Drawable background) {
		this.horizontal = horizontal;
		this.background = background;
		constraints = new Array<Constraints>();
		padding = new Insets();
		defaultMargin = new Insets();
	}

	/**
	 * Sets if invisible widgets should be taken into account for the layout
	 */
	public void setComputeInvisibles(boolean computeInvisibles) {
		this.computeInvisibles = computeInvisibles;
	}

	/**
	 * Sets background for the widget
	 */
	public LinearLayout background(Drawable background) {
		this.background = background;
		return this;
	}

	/**
	 * Sets the padding for the widget
	 */
	public LinearLayout pad(float padding) {
		this.padding.set(padding);
		return this;
	}

	/**
	 * Sets the padding for the widget
	 */
	public LinearLayout pad(float left, float top, float right, float bottom) {
		this.padding.set(left, top, right, bottom);
		return this;
	}

	/**
	 * Sets the default margin to be set in the widgets added to this container
	 */
	public LinearLayout defaultWidgetsMargin(float margin) {
		this.defaultMargin.set(margin);
		return this;
	}

	/**
	 * Sets the default margin to be set in the widgets added to this container
	 */
	public LinearLayout defaultWidgetsMargin(float left, float top,
			float right, float bottom) {
		this.defaultMargin.set(left, top, right, bottom);
		return this;
	}

	/**
	 * Adds a widget to the container
	 * 
	 * @param index
	 *            position to add the actor
	 * @param actor
	 *            the widget to add
	 * @return the constraints for the widget
	 */
	public Constraints add(int index, Actor actor) {
		Constraints c = new Constraints(actor);
		c.margin.set(defaultMargin);
		if (index == -1) {
			constraints.add(c);
		} else {
			constraints.insert(index, c);
		}
		addActor(actor);
		return c;
	}

	/**
	 * Adds a widget to the container
	 * 
	 * @param actor
	 *            the widget to add
	 * @return the constraints for the widget
	 */
	public Constraints add(Actor actor) {
		return add(horizontal ? -1 : 0, actor);
	}

	/**
	 * Adds a space between widgets. The space will try to fill all the
	 * remaining horizontal/vertical space.
	 */
	public void addSpace() {
		add(new SpaceConsumer()).expand(true, true);
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		batch.setColor(Color.WHITE);
		if (background != null) {
			background.draw(batch, 0, 0, getWidth(), getHeight());
		}
		super.drawChildren(batch, parentAlpha);
	}

	@Override
	public float getPrefWidth() {
		return horizontal ? prefWidth() : prefHeight();
	}

	@Override
	public float getPrefHeight() {
		return horizontal ? prefHeight() : prefWidth();
	}

	@Override
	public boolean removeActor(Actor actor) {
		for (Constraints c : constraints) {
			if (c.actor == actor) {
				constraints.removeValue(c, true);
				break;
			}
		}
		return super.removeActor(actor);
	}

	@Override
	public void clearChildren() {
		constraints.clear();
		super.clearChildren();
	}

	@Override
	public void layout() {
		// Check if there is enough width to layout all children without problem
		float childrenWidth = prefWidth() - paddingWidth();
		int widgetsExpanded = 0;
		for (Constraints c : constraints) {
			if (expandX(c)) {
				widgetsExpanded++;
			}
		}

		float availableWidth = availableWidth();

		// If there is no space, ignore container padding and check again
		boolean ignorePadding = childrenWidth > availableWidth;
		boolean ignoreMargins = false;
		boolean collapsed = false;

		if (ignorePadding) {
			availableWidth += paddingWidth();
			ignoreMargins = childrenWidth > availableWidth;

			// If there is no space, ignore also widget margins
			if (ignoreMargins) {
				childrenWidth = 0.0f;
				for (Constraints c : constraints) {
					childrenWidth += actorWidth(c.actor);
				}
				collapsed = childrenWidth > availableWidth;
			}
		}

		float leftX = ignorePadding ? 0.0f : paddingLeft();
		float expandedWidth = 0.0f;

		if (!collapsed) {
			expandedWidth = (availableWidth - childrenWidth) / widgetsExpanded;
		}

		for (Constraints c : constraints) {
			if (c.actor.isVisible() || computeInvisibles) {
				Actor actor = c.actor;
				float width = actorWidth(actor)
						+ (expandX(c) ? expandedWidth : 0.0f);

				float totalWidth = width + (ignoreMargins ? 0 : marginRight(c));

				float x = ignoreMargins ? leftX : leftX + marginLeft(c);

				float height = expandY(c) ? containerHeight() - paddingHeight()
						- marginHeight(c) : actorHeight(actor);

				float y = getYAligned(c, height);

				setBoundsForActor(actor, x, y, width, height);
				leftX = x + totalWidth;
			}
		}
	}

	public class SpaceConsumer extends Widget {
		@Override
		public float getPrefWidth() {
			return 0;
		}

		@Override
		public float getPrefHeight() {
			return 0;
		}
	}

	/**
	 * 
	 * @param c
	 *            constrains
	 * @param height
	 *            widget height
	 * @return the y coordinate according to the alignment and height of the
	 *         widget, calculated from the container size.
	 */
	protected float getYAligned(Constraints c, float height) {
		switch (verticalAlign(c)) {
		case Align.top:
		case Align.right:
			return (containerHeight() - height - paddingTop() - marginTop(c));
		case Align.left:
		case Align.bottom:
			return paddingBottom() + marginBottom(c);
		default:
			// Align.center
			return (containerHeight() - height - paddingHeight() - marginHeight(c))
					/ 2.0f + paddingBottom() + marginBottom(c);
		}

	}

	public float prefWidth() {
		float prefWidth = 0.0f;
		for (Constraints c : constraints) {
			if (c.actor.isVisible() || computeInvisibles) {
				prefWidth += marginWidth(c) + actorWidth(c.actor);
			}
		}
		return prefWidth + paddingWidth();
	}

	public float prefHeight() {
		float prefHeight = 0.0f;
		for (Constraints c : constraints) {
			if (c.actor.isVisible() || computeInvisibles) {
				prefHeight = Math.max(prefHeight, marginHeight(c)
						+ actorHeight(c.actor));
			}
		}
		return prefHeight + paddingHeight();
	}

	protected float containerHeight() {
		return horizontal ? getHeight() : getWidth();
	}

	private float containerWidth() {
		return horizontal ? getWidth() : getHeight();
	}

	protected float availableWidth() {
		return horizontal ? getWidth() - padding.getWidth() : getHeight()
				- padding.getHeight();
	}

	protected float paddingWidth() {
		return horizontal ? padding.getWidth() : padding.getHeight();
	}

	protected float actorWidth(Actor actor) {
		return horizontal ? getPrefWidth(actor) : getPrefHeight(actor);
	}

	protected float actorHeight(Actor actor) {
		return horizontal ? getPrefHeight(actor) : getPrefWidth(actor);
	}

	protected float paddingLeft() {
		return horizontal ? padding.left : padding.bottom;
	}

	private float paddingBottom() {
		return horizontal ? padding.bottom : padding.left;
	}

	protected float paddingHeight() {
		return horizontal ? padding.getHeight() : padding.getWidth();
	}

	private float paddingTop() {
		return horizontal ? padding.top : padding.right;
	}

	private float marginWidth(Constraints c) {
		return horizontal ? c.margin.getWidth() : c.margin.getHeight();
	}

	protected float marginHeight(Constraints c) {
		return horizontal ? c.margin.getHeight() : c.margin.getWidth();
	}

	protected float marginLeft(Constraints c) {
		return horizontal ? c.margin.left : c.margin.bottom;
	}

	private float marginRight(Constraints c) {
		return horizontal ? c.margin.right : c.margin.top;
	}

	private float marginTop(Constraints c) {
		return horizontal ? c.margin.top : c.margin.right;
	}

	private float marginBottom(Constraints c) {
		return horizontal ? c.margin.bottom : c.margin.left;
	}

	private boolean expandX(Constraints c) {
		return horizontal ? c.expandX : c.expandY;
	}

	protected boolean expandY(Constraints c) {
		return horizontal ? c.expandY : c.expandX;
	}

	private int horizontalAlign(Constraints c) {
		return horizontal ? c.horizontalAlign : c.verticalAlign;
	}

	private int verticalAlign(Constraints c) {
		return horizontal ? c.verticalAlign : c.horizontalAlign;
	}

	protected void setBoundsForActor(Actor actor, float x, float y,
			float width, float height) {
		if (horizontal) {
			super.setBounds(actor, x, y, width, height);
		} else {
			super.setBounds(actor, y, x, height, width);
		}
	}

	/**
	 * Holds contraints for a widget inside a LinearLayout container
	 */
	public static class Constraints {

		protected Actor actor;

		protected Insets margin = new Insets();

		private boolean expandX = false;

		private boolean expandY = false;

		private int verticalAlign;

		private int horizontalAlign;

		public Constraints(Actor actor) {
			this.actor = actor;
		}

		public Actor getActor() {
			return actor;
		}

		public Constraints margin(float m) {
			margin.set(m);
			return this;
		}

		public Constraints margin(float left, float top, float right,
				float bottom) {
			margin.set(left, top, right, bottom);
			return this;
		}

		public Constraints expand(boolean expandX, boolean expandY) {
			this.expandX = expandX;
			this.expandY = expandY;
			return this;
		}

		public Constraints expandX() {
			this.expandX = true;
			return this;
		}

		public Constraints expandY() {
			this.expandY = true;
			return this;
		}

		public Constraints centerX() {
			this.horizontalAlign = Align.center;
			return this;
		}

		public Constraints centerY() {
			this.verticalAlign = Align.center;
			return this;
		}

		public Constraints left() {
			this.horizontalAlign = Align.left;
			return this;
		}

		public Constraints right() {
			this.horizontalAlign = Align.right;
			return this;
		}

		public Constraints bottom() {
			this.verticalAlign = Align.bottom;
			return this;
		}

		public Constraints top() {
			this.verticalAlign = Align.top;
			return this;
		}
	}
}
