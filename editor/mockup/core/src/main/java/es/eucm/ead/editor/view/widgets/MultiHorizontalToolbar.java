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

	private static final Vector2 TEMP = new Vector2();

	private Array<HorizontalToolbar> arrayBars;

	private float maxHeight;

	public MultiHorizontalToolbar() {
		arrayBars = new Array<HorizontalToolbar>();
		maxHeight = 0;
		fill();
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

	public void show(final HorizontalToolbar newBar) {
		if (arrayBars.contains(newBar, true)) {
			Actor current = getActor();
			current.setTouchable(Touchable.disabled);

			localToStageCoordinates(TEMP.set(current.getX(), current.getY()));
			float goalY = TEMP.y;

			setActor(newBar);
			if (TEMP.y > Gdx.graphics.getHeight() * 0.5f) {
				goalY += maxHeight;
				newBar.setPosition(0, maxHeight);
			} else {
				goalY -= maxHeight;
				newBar.setPosition(0, -maxHeight);
			}

			newBar.addAction(Actions.sequence(
					Actions.moveTo(0, 0, 0.4f, Interpolation.pow2In),
					Actions.touchable(Touchable.enabled)));

			getStage().addActor(current);
			current.setBounds(TEMP.x, TEMP.y, getWidth(), getHeight());
			current.addAction(Actions.sequence(
					Actions.moveTo(TEMP.x, goalY, 0.4f, Interpolation.pow2Out),
					Actions.removeActor()));
		}
	}

	@Override
	public float getPrefHeight() {
		return maxHeight;
	}

	public Array<HorizontalToolbar> getHorizontalToolbars() {
		return arrayBars;
	}
}
