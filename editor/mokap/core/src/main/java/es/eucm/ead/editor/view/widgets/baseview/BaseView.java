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
package es.eucm.ead.editor.view.widgets.baseview;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.view.listeners.GestureListener;
import es.eucm.ead.engine.gdx.AbstractWidget;

/**
 * Base container view for all Mokap interactions. It contains the following
 * layers (from top to bottom):
 * <ol>
 * <li><strong>Modals (all screen)</strong>: to show modal widgets (e.g.:
 * contextual/selection menus)</li>
 * <li><strong>Navigation</strong> (left): widget to navigate between different
 * views in the application</li>
 * <li><strong>Toolbar (top)</strong>: contains all buttons to perform
 * operations in the current context</li>
 * <li><strong>Selection context (right)</strong>: to show contextual
 * information of the current selection</li>
 * </ol>
 */
public class BaseView extends AbstractWidget {

	public static final float FLING_AREA_CM = 1.0f;

	public static final float FLING_MIN_VELOCITY_CM = 5.0f;

	public static final float HIDE_TIME = 0.5f;

	private static final int NAVIGATION_AREA = 0, SELECTION_CONTEXT_AREA = 1;

	private Actor toolbar;

	private Navigation navigation;

	protected SelectionContext selectionContext;

	private Actor content;

	private boolean lockPanels;

	private boolean lockContext;

	public BaseView(Skin skin) {
		this(skin.get(BaseViewStyle.class));
	}

	public BaseView(BaseViewStyle style) {
		addActor(selectionContext = new SelectionContext());
		addActor(navigation = buildNavigation(style));
		lockPanels = false;
		lockContext = false;

		// Listens for fling gestures to quickly show hidden panels
		addCaptureListener(new GestureListener() {

			private int area;

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				area = pointer != 0 ? -1 : area(x, y);
				return super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void pan(float x, float y, float deltaX, float deltaY) {
				if (area != -1) {
					getStage().cancelTouchFocusExcept(this, BaseView.this);
				}
				switch (area) {
				case NAVIGATION_AREA:
					navigation.displace(deltaX, 0);
					break;
				case SELECTION_CONTEXT_AREA:
					selectionContext.displace(deltaX, 0);
					break;
				}
			}

			@Override
			public void panStop(float x, float y, int pointer, int button) {
				if (pointer == 0) {
					switch (area) {
					case NAVIGATION_AREA:
						navigation.dragStop();
						break;
					case SELECTION_CONTEXT_AREA:
						selectionContext.dragStop();
						break;
					}
				}
			}

			@Override
			public void fling(float velocityX, float velocityY, int button) {
				switch (area) {
				case NAVIGATION_AREA:
					if (velocityX > cmToXPixels(FLING_MIN_VELOCITY_CM)) {
						navigation.show();
					}
					break;
				case SELECTION_CONTEXT_AREA:
					if (velocityX < cmToXPixels(FLING_MIN_VELOCITY_CM)) {
						selectionContext.show();
					}
					break;
				case -1:
					if (velocityX > cmToXPixels(FLING_MIN_VELOCITY_CM)) {
						getStage().cancelTouchFocusExcept(this, BaseView.this);
						selectionContext.hide();
					}
					break;
				}
			}
		});
	}

	private int area(float x, float y) {
		if (!lockPanels) {
			if (navigation.hasContent() && x < cmToXPixels(FLING_AREA_CM)) {
				return NAVIGATION_AREA;
			} else if (!lockContext && selectionContext.hasContent()
					&& x > Gdx.graphics.getWidth() - cmToXPixels(FLING_AREA_CM)) {
				return SELECTION_CONTEXT_AREA;
			}
		}

		return -1;
	}

	protected Navigation buildNavigation(BaseViewStyle style) {
		return new Navigation(style);
	}

	/**
	 * Sets the navigation actor, removing the current one (if any)
	 */
	public void setNavigation(Actor navigation) {
		this.navigation.setNavigation(navigation);
	}

	/**
	 * Toggles navigation panel
	 */
	public void toggleNavigation() {
		this.navigation.toggle();
	}

	public boolean isNavigationVisible() {
		return !navigation.isHidden();
	}

	public void hideNavigationRightAway() {
		navigation.hideRightAway();
	}

	public void exitFullScreen() {
		toolbar.clearActions();
		toolbar.addAction(Actions.sequence(
				Actions.touchable(Touchable.enabled), Actions.moveTo(0,
						getHeight() - toolbar.getHeight(), 0.25f,
						Interpolation.exp5Out)));

		if (!selectionContext.isHidden()) {
			selectionContext.show();
		}
	}

	public void enterFullScreen() {
		toolbar.clearActions();
		toolbar.addAction(Actions.sequence(
				Actions.touchable(Touchable.disabled),
				Actions.moveTo(0, getHeight(), 0.25f, Interpolation.exp5Out)));

		if (!selectionContext.isHidden()) {
			selectionContext.hide();
			selectionContext.hidden = false;
		}
	}

	/**
	 * Sets the toolbar actor, removing the current one (if any)
	 */
	public void setToolbar(Actor toolbar) {
		if (this.toolbar != null) {
			this.toolbar.remove();
		}

		this.toolbar = toolbar;
		if (toolbar != null) {
			addActorBefore(navigation, toolbar);
			layoutToolbar();
		}
	}

	public void setContent(Actor content) {
		if (this.content != null) {
			this.content.remove();
		}

		this.content = content;

		if (content != null) {
			addActorAt(0, content);
		}
	}

	/**
	 * Sets the selection context, removing the current one (if any)
	 */
	public void setSelectionContext(Actor selectionContext) {
		this.selectionContext.setSelectionContext(selectionContext);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (navigation != null) {
			navigation.invalidate();
		}
	}

	@Override
	public void layout() {
		setBounds(navigation, 0, 0, getWidth(), getHeight());
		layoutToolbar();
		layoutSelectionContext();
		if (content != null) {
			setBounds(content, 0, 0, getWidth(), getHeight());
		}
	}

	protected void layoutSelectionContext() {
		setBounds(selectionContext, 0, 0, getWidth(), getHeight()
				- (toolbar == null ? 0 : getPrefHeight(toolbar)));
	}

	private void layoutToolbar() {
		if (toolbar != null
				&& !MathUtils.isEqual(toolbar.getWidth(), getWidth(), 0.1f)) {
			setBounds(toolbar, 0, getHeight() - getPrefHeight(toolbar),
					getWidth(), getPrefHeight(toolbar));
		}
	}

	private static MoveByAction move(Actor actor, float targetX, float targetY) {
		return Actions.moveBy(targetX - actor.getX(), targetY - actor.getY(),
				HIDE_TIME, Interpolation.exp5Out);
	}

	/**
	 * Moves with the default time and interpolation the given actor to the
	 * given coordinates
	 */
	public static void moveTo(Actor actor, float targetX, float targetY) {
		actor.clearActions();
		actor.addAction(move(actor, targetX, targetY));
	}

	public static class BaseViewStyle {

		public Drawable navigationBackground;

		/**
		 * Max value for the navigation background alpha
		 */
		public float navigationBackgroundAlpha = 0.5f;

		public BaseViewStyle() {
		}

		public BaseViewStyle(Drawable navigationBackground) {
			this.navigationBackground = navigationBackground;
		}
	}

	public void lockPanels(boolean lockPanels) {
		this.lockPanels = lockPanels;
		if (lockPanels) {
			if (!selectionContext.isHidden()) {
				selectionContext.hide();
			}
			if (!navigation.isHidden()) {
				navigation.hideRightAway();
			}
		}
	}

	public void lockContextOnly(boolean lockContext) {
		this.lockContext = lockContext;
		if (lockContext && !selectionContext.isHidden()) {
			selectionContext.hide();
		}
	}

}
