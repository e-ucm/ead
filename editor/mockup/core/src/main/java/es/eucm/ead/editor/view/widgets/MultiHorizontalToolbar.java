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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.utils.Array;

public class MultiHorizontalToolbar extends Container<HorizontalToolbar> {

	private static final float ANIM_TIME = 0.3f;

	private static final Vector2 TEMP = new Vector2();

	private Array<HorizontalToolbar> arrayBars;

	private float maxHeight;

	private HorizontalToolbar toHide;

	protected HorizontalToolbar toShow;

	private Runnable actionAddActor;

	public MultiHorizontalToolbar() {
		arrayBars = new Array<HorizontalToolbar>();
		maxHeight = 0;
		fill();

		actionAddActor = new Runnable() {
			@Override
			public void run() {
				setActor(toShow);
			}
		};
	}

	public void addHorizontalToolbar(HorizontalToolbar... toolbars) {
		for (HorizontalToolbar toolbar : toolbars) {
			toolbar.setTouchable(Touchable.disabled);
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
			if (toHide != null && toHide != current && toHide != toShow) {
				toHide.remove();
			}
			toHide = (HorizontalToolbar) current;

			localToStageCoordinates(TEMP.set(0, 0));
			float goalY;
			float y = TEMP.y;
			float x = TEMP.x;

			if (y > Gdx.graphics.getHeight() * 0.5f) {
				goalY = y + maxHeight;
			} else {
				goalY = y - maxHeight;
			}

			if (getStage().getRoot() != toShow.getParent()) {
				getStage().addActor(toShow);
			}

			// If is the first time than add the toolbar and not have
			// coordinates.
			if (toShow.getY() == 0 && toShow.getX() == 0) {
				toShow.setBounds(x, goalY, getWidth(), getHeight());
			}

			float time = ANIM_TIME * Math.abs(y - newBar.getY()) / maxHeight;

			newBar.addAction(Actions.sequence(
					Actions.moveTo(x, y, time, Interpolation.sineIn),
					Actions.touchable(Touchable.enabled)));

			hide(x, goalY, time);
		}
	}

	private void hide(float x, float y, float time) {
		localToStageCoordinates(TEMP.set(toHide.getX(), toHide.getY()));

		float currentX = TEMP.x;
		float currenyY = TEMP.y;
		if (getStage().getRoot() != toHide.getParent()) {
			getStage().addActor(toHide);

		}
		toHide.setPosition(currentX, currenyY);
		toHide.addAction(Actions.sequence(
				Actions.moveTo(x, y, 1.7f * time, Interpolation.sineOut),
				Actions.run(actionAddActor), Actions.removeActor()));
	}

	@Override
	public float getPrefHeight() {
		return maxHeight;
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
