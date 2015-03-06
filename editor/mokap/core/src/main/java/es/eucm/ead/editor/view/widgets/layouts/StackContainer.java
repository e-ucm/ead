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
import es.eucm.ead.engine.gdx.AbstractWidget;

/**
 * A widget that stack things one above the others
 */
public class StackContainer extends AbstractWidget {

	public StackContainer(Actor... actors) {
		for (Actor actor : actors) {
			addActor(actor);
		}
	}

	@Override
	public float getPrefWidth() {
		return super.getChildrenMaxWidth();
	}

	public float getPrefHeight() {
		return super.getChildrenMaxHeight();
	}

	@Override
	public void layout() {
		for (Actor a : getChildren()) {
			float width = Math.min(getWidth(), getPrefWidth(a));
			float height = Math.min(getHeight(), getPrefHeight(a));
			float x = (getWidth() - width) / 2;
			float y = (getHeight() - height) / 2;
			setBounds(a, x, y, width, height);
		}
	}
}
