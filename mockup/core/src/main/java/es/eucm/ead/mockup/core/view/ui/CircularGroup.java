/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.mockup.core.view.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import es.eucm.ead.mockup.core.control.listeners.FocusListener;

public class CircularGroup extends Group implements FocusListener {

	public CircularGroup(float radius, int startingAngle, int angleScope,
			boolean clockwise, Actor... actors) {
		int numActors = actors.length;
		float increment = angleScope / numActors;
		if (clockwise) {
			increment = -increment;
		}
		Actor a = null;
		for (int i = 0; i < numActors; ++i) {
			a = actors[i];
			a.setX(radius * MathUtils.cosDeg(startingAngle) - a.getWidth() / 2f);
			a.setY(radius * MathUtils.sinDeg(startingAngle) - a.getHeight()
					/ 2f);
			addActor(a);
			startingAngle += increment;
		}
	}

	@Override
	public void show() {

	}

	@Override
	public void hide() {

	}
}
