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
package es.eucm.ead.editor.control.actions.editor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.ead.editor.control.actions.EditorAction;

/**
 * <p>
 * Shows a modal in the given position. (Invokes
 * {@link es.eucm.ead.editor.control.Views#showModal(com.badlogic.gdx.scenes.scene2d.Actor, float, float)}
 * )
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Actor}</em> The modal to show</dd>
 * <dd><strong>args[1]</strong> <em>{@link Float}</em> x coordinate</dd>
 * <dd><strong>args[2]</strong> <em>{@link Float}</em> y coordinate</dd>
 * </dl>
 */
public class ShowModal extends EditorAction {

	public ShowModal() {
		super(true, false, Actor.class, Float.class, Float.class);
	}

	@Override
	public void perform(Object... args) {
		controller.getViews().showModal((Actor) args[0], (Float) args[1],
				(Float) args[2]);
	}
}
