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
package es.eucm.ead.editor.utils;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

/**
 * Some additional actions
 */
public class Actions2 {

	static public MoveUnidimensional moveToX(float x, float duration,
			Interpolation interpolation) {
		MoveUnidimensional action = Actions.action(MoveUnidimensional.class);
		action.setPosition(x);
		action.setHorizontal(true);
		action.setDuration(duration);
		action.setInterpolation(interpolation);
		return action;
	}

	static public MoveUnidimensional moveToY(float y, float duration,
			Interpolation interpolation) {
		MoveUnidimensional action = Actions.action(MoveUnidimensional.class);
		action.setPosition(y);
		action.setHorizontal(false);
		action.setDuration(duration);
		action.setInterpolation(interpolation);
		return action;
	}

	static public ScaleUnidimensional scaleToX(float scaleX, float duration,
			Interpolation interpolation) {
		ScaleUnidimensional action = Actions.action(ScaleUnidimensional.class);
		action.setScale(scaleX);
		action.setHorizontal(true);
		action.setDuration(duration);
		action.setInterpolation(interpolation);
		return action;
	}

	static public ScaleUnidimensional scaleToY(float scaleY, float duration,
			Interpolation interpolation) {
		ScaleUnidimensional action = Actions.action(ScaleUnidimensional.class);
		action.setScale(scaleY);
		action.setHorizontal(false);
		action.setDuration(duration);
		action.setInterpolation(interpolation);
		return action;
	}

	public static class MoveUnidimensional extends TemporalAction {
		private boolean horizontal;
		private float start;
		private float end;

		public void setHorizontal(boolean horizontal) {
			this.horizontal = horizontal;
		}

		protected void begin() {
			start = horizontal ? actor.getX() : actor.getY();
		}

		protected void update(float percent) {
			float value = start + (end - start) * percent;
			if (horizontal) {
				actor.setX(value);
			} else {
				actor.setY(value);
			}
		}

		public void setPosition(float value) {
			end = value;
		}
	}

	public static class ScaleUnidimensional extends TemporalAction {
		private boolean horizontal;
		private float start;
		private float end;

		public void setHorizontal(boolean horizontal) {
			this.horizontal = horizontal;
		}

		protected void begin() {
			start = horizontal ? actor.getScaleX() : actor.getScaleY();
		}

		protected void update(float percent) {
			float value = start + (end - start) * percent;
			if (horizontal) {
				actor.setScaleX(value);
			} else {
				actor.setScaleY(value);
			}
		}

		public void setScale(float value) {
			end = value;
		}
	}
}
