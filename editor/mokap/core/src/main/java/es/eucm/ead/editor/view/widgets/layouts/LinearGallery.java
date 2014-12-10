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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;

/**
 * A scrollable horizontal linear gallery of items.
 * <p>
 * {@link ChangeEvent} is fired when the main page changes.
 * <p>
 */
public class LinearGallery extends ScrollPane {

	private Actor currentPage;

	private Pages content;

	private boolean wasPanDragFling = false;

	public LinearGallery(Skin skin, String background) {
		this(skin.getDrawable(background));
	}

	public LinearGallery(Drawable background) {
		super(null);
		setWidget(content = new Pages());
		getStyle().background = background;
		setScrollingDisabled(false, true);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (wasPanDragFling && !isPanning() && !isDragging() && !isFlinging()) {
			wasPanDragFling = false;
			scrollToPage();
		} else if (!wasPanDragFling && isPanning() || isDragging()
				|| isFlinging()) {
			wasPanDragFling = true;
		}

		float cx = getScrollX() + getParent().getWidth() * .5f;
		float cy = getScrollY() + getParent().getHeight() * .5f;
		checkCurrentPage(cx, cy);
	}

	private void checkCurrentPage(float cx, float cy) {
		if (currentPage == null || !isVisible(currentPage, cx, cy)) {
			Actor visibleActor = getVisibleActor(cx, cy);
			if (visibleActor != null) {
				currentPage = visibleActor;
				fireChanged();
			}
		}
	}

	private Actor getVisibleActor(float cx, float cy) {
		for (Actor child : content.getChildren()) {
			if (isVisible(child, cx, cy)) {
				return child;
			}
		}
		return null;
	}

	private void fireChanged() {
		ChangeEvent event = Pools.obtain(ChangeEvent.class);
		fire(event);
		Pools.free(event);
	}

	public Actor getCurrentPage() {
		return currentPage;
	}

	private boolean isVisible(Actor actor, float cx, float cy) {
		return (cx > actor.getX() && cx <= actor.getRight()
				&& cy > actor.getY() && cy <= actor.getTop());
	}

	protected int scrollToPage() {
		final float width = getWidth();
		final float scrollX = getScrollX();
		final float maxX = getMaxX();

		if (scrollX >= maxX || scrollX <= 0)
			return -1;

		Array<Actor> pages = content.getChildren();
		float pageX = 0;
		float pageWidth = 0;
		int pageIndex = -1;
		if (pages.size > 0) {

			for (Actor a : pages) {
				pageX = a.getX();
				pageWidth = a.getWidth();
				++pageIndex;
				if (scrollX < (pageX + pageWidth * 0.5)) {
					break;
				}
			}
			super.setScrollX(pageX - (width - pageWidth) * .5f);
		}

		return pageIndex;
	}

	public void scrollToPage(int page) {
		Array<Actor> childen = content.getChildren();
		Actor nextPage = childen.get(page);
		super.setScrollX(nextPage.getX() - (getWidth() - nextPage.getWidth())
				* .5f);
	}

	/**
	 * @param pad
	 *            pad between items
	 */
	public void pad(float pad) {
		content.pad(pad);
	}

	@Override
	public void clearChildren() {
		content.clearChildren();
	}

	public void add(Actor actor) {
		content.addActor(actor);
	}

	private static class Pages extends AbstractWidget {

		private float pad = WidgetBuilder.dpToPixels(8);

		@Override
		public void layout() {
			int count = 0;
			float rowHeight = getPrefHeight();
			float actorWidth = getParent().getWidth();
			for (Actor actor : getChildren()) {
				setBounds(actor, pad + count * actorWidth, pad, actorWidth - 2
						* pad, rowHeight - 2 * pad);
				count++;
			}
		}

		@Override
		public float getPrefWidth() {
			float width = getParent().getWidth() * getChildren().size;
			return width;
		}

		@Override
		public float getPrefHeight() {
			return getParent().getHeight();
		}

		public void pad(float pad) {
			this.pad = pad;
		}
	}
}
