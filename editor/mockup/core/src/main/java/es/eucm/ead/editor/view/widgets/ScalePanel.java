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

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

/**
 * A panel with a scale animation.
 */
public abstract class ScalePanel extends Table {

	protected static final float FADE = .5F;
	protected static final float CHILD_DELAY_OFFSET = .035F;
	protected static final float CHILD_FADE = .15F;

	private final Runnable showCells = new Runnable() {

		public void run() {
			Array<Cell> cells = getCells();
			for (int i = 0; i < cells.size; ++i) {
				Actor actor = cells.get(i).getActor();
				if (actor != null) {
					actor.addAction(Actions.delay(i * CHILD_DELAY_OFFSET,
							Actions.fadeIn(CHILD_FADE, Interpolation.fade)));
				}
			}
		}
	};

	public ScalePanel() {
		setTransform(true);
	}

	/**
	 * The default animation used to show the {@link ScalePanel}.
	 * 
	 */
	public Action getShowAction() {
		float xDuration, yDuration;
		float w = getPrefWidth(), h = getPrefHeight();
		if (w > h) {
			yDuration = FADE * h / w;
			xDuration = FADE;
		} else {
			xDuration = FADE * w / h;
			yDuration = FADE;
		}
		float minDuration = Math.min(xDuration, yDuration);
		return Actions.parallel(
				Actions.fadeIn(minDuration, Interpolation.fade),
				Actions.scaleBy(1f, 0f, xDuration, Interpolation.pow2Out),
				Actions.scaleBy(0f, 1f, yDuration, Interpolation.pow2Out),
				Actions.delay(minDuration, Actions.run(showCells)));
	}

	/**
	 * The default animation used to hide the {@link ScalePanel}.
	 * 
	 */
	protected Action getHideAction() {
		return Actions.fadeOut(FADE, Interpolation.fade);
	}

}
