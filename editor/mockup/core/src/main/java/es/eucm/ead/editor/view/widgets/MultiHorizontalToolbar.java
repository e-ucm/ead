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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;

public class MultiHorizontalToolbar extends Container<HorizontalToolbar> {

	private static final float ANIM_TIME = 0.2f;

	private static final Vector2 TEMP = new Vector2();

	private Array<HorizontalToolbar> arrayBars;

	private float maxHeight;

	private HorizontalToolbar toHide;

	protected HorizontalToolbar toShow;

	private Runnable actionAddActor;

	boolean sameToolbar;

	public MultiHorizontalToolbar() {
		this(null, null, null);
	}

	public MultiHorizontalToolbar(Skin skin, String string, Color color) {
		arrayBars = new Array<HorizontalToolbar>();
		maxHeight = 0;
		fill();

		actionAddActor = new Runnable() {
			@Override
			public void run() {
				setActor(toShow);
			}
		};

		if (skin != null) {
			setBackground(skin.getDrawable(string));
			setColor(color);
			sameToolbar = true;
		} else {
			sameToolbar = false;
		}
	}

	public void addHorizontalToolbar(HorizontalToolbar... toolbars) {
		for (HorizontalToolbar toolbar : toolbars) {
			toolbar.setTouchable(Touchable.disabled);
			if (sameToolbar) {
				toolbar.background(null);
			}

			arrayBars.add(toolbar);
			if (maxHeight < toolbar.getPrefHeight()) {
				maxHeight = toolbar.getPrefHeight();
			}
		}

		if (getActor() == null) {
			setActor(toolbars[0]);
			toolbars[0].setTouchable(Touchable.enabled);
		}
	}

	public void resetShow() {
		HorizontalToolbar toolbar = arrayBars.first();
		toolbar.setTouchable(Touchable.enabled);
		toolbar.getColor().a = 1;
		setActor(toolbar);
		toShow = null;
		toHide = null;
	}

	public void show(final HorizontalToolbar newBar) {
		if (arrayBars.contains(newBar, true) && newBar != toShow
				&& getStage() != null) {
			for (HorizontalToolbar toolbar : arrayBars) {
				toolbar.clearActions();
			}

			Actor current = getActor();
			if (current != null) {
				current.setTouchable(Touchable.disabled);
			} else {
				current = toShow;
			}

			toShow = newBar;
			if (toHide != null && toHide.hasParent() && toHide != current
					&& toHide != toShow) {
				toHide.remove();
			}
			toHide = (HorizontalToolbar) current;
			toHide.setOrigin(Align.center);

			localToStageCoordinates(TEMP.set(toHide.getX(), toHide.getY()));
			float y = TEMP.y;
			float x = TEMP.x;

			if (getStage().getRoot() != toShow.getParent()) {
				getStage().addActor(toShow);
			}

			// If is the first time than add the toolbar and not have
			// coordinates.
			if (toShow.getY() == 0 && toShow.getX() == 0) {
				toShow.setBounds(x, y, toHide.getWidth(), toHide.getHeight());
				toShow.setOrigin(Align.center);
				toShow.getColor().a = 0;
				toShow.setScaleY(0);
			}

			float timeHide = ANIM_TIME * Math.abs(toHide.getScaleY());
			float timeShow = ANIM_TIME * (1 - Math.abs(toShow.getScaleY()));

			if (getStage().getRoot() != toHide.getParent()) {
				getStage().addActor(toHide);
				toHide.setPosition(x, y);
			}

			toHide.addAction(Actions.sequence(Actions.parallel(
					Actions.scaleTo(1, 0, timeHide, Interpolation.sineOut),
					Actions.fadeOut(timeHide)), Actions.addAction(Actions
					.sequence(Actions.parallel(Actions.scaleTo(1, 1, timeShow,
							Interpolation.sineIn), Actions.fadeIn(timeShow)),
							Actions.run(actionAddActor), Actions
									.touchable(Touchable.enabled), Actions
									.removeActor(toHide)), newBar)));
		}
	}

	@Override
	public float getPrefHeight() {
		float backgroungPadding = 0;
		if (getBackground() != null) {
			backgroungPadding = getBackground().getBottomHeight()
					+ getBackground().getTopHeight();
		}
		return maxHeight + backgroungPadding;
	}

	public Array<HorizontalToolbar> getHorizontalToolbars() {
		return arrayBars;
	}

	public HorizontalToolbar getCurrentToolbar() {
		return this.getActor();
	}

	public void release() {
		for (HorizontalToolbar toolbar : arrayBars) {
			toolbar.clearActions();
		}
	}
}
