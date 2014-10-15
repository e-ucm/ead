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
package es.eucm.ead.editor.view.widgets.iconwithpanel;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.view.widgets.PositionedHiddenPanel.Position;

/**
 * A {@link IconWithPanel} that has a scale in/out animation and a FadeIn for
 * each children when showing.
 */
public abstract class BaseIconWithScalePanel extends IconWithPanel {

	private final Runnable showCells = new Runnable() {

		public void run() {
			Array<Cell> cells = panel.getCells();
			for (int i = 0; i < cells.size; ++i) {
				Actor actor = cells.get(i).getActor();
				if (actor != null) {
					actor.addAction(Actions.delay(i * .05f,
							Actions.fadeIn(.2f, Interpolation.fade)));
				}
			}
		}
	};

	public BaseIconWithScalePanel(String icon, float separation, Skin skin,
			Position position, int paneCol, String styleName) {
		super(icon, separation, skin, position, paneCol, styleName);
		panel.setTransform(true);
	}

	@Override
	protected Action getShowAction() {
		float xDuration, yDuration;
		float w = panel.getPrefWidth(), h = panel.getPrefHeight();
		if (w > h) {
			yDuration = IN_DURATION * h / w;
			xDuration = IN_DURATION;
		} else {
			xDuration = IN_DURATION * w / h;
			yDuration = IN_DURATION;
		}

		panel.setScale(0f);
		for (Cell<Actor> cell : panel.getCells()) {
			Actor actor = cell.getActor();
			if (actor != null) {
				actor.getColor().a = 0f;
			}
		}
		return showAction(xDuration, yDuration, showCells);
	}

	@Override
	protected Action getHideAction() {
		return hideAction();
	}

	public static Action showAction(float xDuration, float yDuration,
			Runnable runnable) {
		return Actions.parallel(
				Actions.scaleBy(1f, 0f, xDuration, Interpolation.pow2Out),
				Actions.scaleBy(0f, 1f, yDuration, Interpolation.pow2Out),
				Actions.delay(Math.min(xDuration, yDuration),
						Actions.run(runnable)));
	}

	public static Action hideAction() {
		return Actions.scaleTo(0f, 0f, OUT_DURATION, Interpolation.pow2In);
	}
}
