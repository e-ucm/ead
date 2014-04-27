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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.view.widgets.AbstractWidget;

/**
 * Layouts widgets horizontally
 */
public class HorizontalLayout extends AbstractWidget {

	private Drawable background;

	private Array<Constrains> constrains;

	private Insets padding;

	private Insets defaultMargin;

	private int align;

	public HorizontalLayout() {
		constrains = new Array<Constrains>();
		padding = new Insets();
		defaultMargin = new Insets();
		align = Align.center;
	}

	/**
	 * Vertical align for widgets
	 * 
	 * @param align
	 *            a constant from {@link Align}
	 */
	public void setAlign(int align) {
		this.align = align;
	}

	/**
	 * Sets background for the widget
	 */
	public void setBackground(Drawable background) {
		this.background = background;
	}

	/**
	 * Sets the padding for the widget
	 */
	public void setPadding(float padding) {
		this.padding.set(padding);
	}

	/**
	 * Sets the padding for the widget
	 */
	public void setPadding(float left, float top, float right, float bottom) {
		this.padding.set(left, top, right, bottom);
	}

	/**
	 * Sets the default margin to be set in the widgets added to this container
	 */
	public void setDefaultMargin(float margin) {
		this.defaultMargin.set(margin);
	}

	/**
	 * Sets the default margin to be set in the widgets added to this container
	 */
	public void setDefaultMargin(float left, float top, float right,
			float bottom) {
		this.defaultMargin.set(left, top, right, bottom);
	}

	/**
	 * Adds a widget to the container
	 * 
	 * @param actor
	 *            the widget to add
	 * @return the constrains for the widget
	 */
	public Constrains add(Actor actor) {
		Constrains c = new Constrains(actor);
		c.margin.set(defaultMargin);
		constrains.add(c);
		addActor(actor);
		return c;
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		if (background != null) {
			background.draw(batch, 0, 0, getWidth(), getHeight());
		}
		super.drawChildren(batch, parentAlpha);
	}

	@Override
	public float getPrefWidth() {
		float prefWidth = 0.0f;
		for (Constrains c : constrains) {
			prefWidth += c.margin.getWidth() + getPrefWidth(c.actor);
		}
		return prefWidth + padding.getWidth();
	}

	@Override
	public float getPrefHeight() {
		float prefHeight = 0.0f;
		for (Constrains c : constrains) {
			prefHeight = Math.max(prefHeight, c.margin.getHeight()
					+ getPrefHeight(c.actor));
		}
		return prefHeight + padding.getHeight();
	}

	@Override
	public void layout() {
		// Check if there is enough width to layout all children without problem
		float childrenWidth = 0.0f;
		float widgetsExpanded = 0.0f;
		for (Constrains c : constrains) {
			if (!c.expand) {
				childrenWidth += c.margin.getWidth() + getPrefWidth(c.actor);
			} else {
				widgetsExpanded++;
			}
		}

		float availableWidth = getWidth() - padding.getWidth();

		// If there is no space, ignore container padding and check again
		boolean ignorePadding = childrenWidth > availableWidth;
		boolean ignoreMargins = false;
		boolean collapsed = false;

		if (ignorePadding) {
			availableWidth += padding.getWidth();
			ignoreMargins = childrenWidth > availableWidth;

			if (ignoreMargins) {
				childrenWidth = 0.0f;
				for (Constrains c : constrains) {
					if (!c.expand) {
						childrenWidth += getPrefWidth(c.actor);
					} else {
						widgetsExpanded++;
					}
				}
				collapsed = childrenWidth > availableWidth;
			}
		}

		// Draw until there is available space
		float leftX = ignorePadding ? 0.0f : padding.left;
		float rightX = (ignorePadding ? 0.0f : padding.left) + availableWidth;
		float expandedWidth = 0.0f;

		if (!collapsed) {
			expandedWidth = (availableWidth - childrenWidth) / widgetsExpanded;
		}

		for (Constrains c : constrains) {
			Actor actor = c.actor;
			float width = c.expand ? expandedWidth
					- (ignoreMargins ? 0.0f : c.margin.getWidth())
					: getPrefWidth(actor);

			float height = getPrefHeight(actor);
			float x;
			if (c.left) {
				x = ignoreMargins ? leftX : leftX + c.margin.left;
			} else {
				x = ignoreMargins ? rightX - width : rightX - width
						- c.margin.right;
			}

			float y = getYAligned(height);

			setBounds(actor, x, y, width, height);

			availableWidth -= width;
			if (c.left) {
				leftX = x + width + (ignoreMargins ? 0 : c.margin.right);
			} else {
				rightX = x - (ignoreMargins ? 0 : c.margin.left);
			}

		}

	}

	private float getYAligned(float height) {
		switch (align) {
		case Align.top:
			return (getHeight() - height - padding.top);
		case Align.bottom:
			return padding.bottom;
		default:
			// Align.center
			return (getHeight() - height - padding.getHeight()) / 2.0f
					+ padding.bottom;
		}

	}

	public static class Constrains {

		private Actor actor;

		private Insets margin = new Insets();

		private boolean expand = false;

		private boolean left = true;

		public Constrains(Actor actor) {
			this.actor = actor;
		}

		public Constrains setMargin(float m) {
			margin.set(m);
			return this;
		}

		public Constrains setLeftMargin(float m) {
			margin.setLeft(m);
			return this;
		}

		public Constrains setTopMargin(float m) {
			margin.setTop(m);
			return this;
		}

		public Constrains setRightMargin(float m) {
			margin.setRight(m);
			return this;
		}

		public Constrains setBottomMargin(float m) {
			margin.setBottom(m);
			return this;
		}

		public Constrains expand() {
			expand = true;
			return this;
		}

		public Constrains expand(boolean expand) {
			this.expand = expand;
			return this;
		}

		public Constrains left() {
			this.left = true;
			return this;
		}

		public Constrains right() {
			this.left = false;
			return this;
		}

	}
}
