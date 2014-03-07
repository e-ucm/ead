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
package es.eucm.ead.editor.control.actions;

import es.eucm.ead.editor.control.FieldNameForActions;
import es.eucm.ead.editor.control.commands.MultipleFieldsCommand;

public class MoveOrigin extends EditorAction {

	public static final String NAME = "moveOrigin";

	public MoveOrigin() {
		super(NAME);
	}

	@Override
	public void perform(Object... args) {
		Object target = args[0];
		float originX = (Float) args[1];
		float originY = (Float) args[2];
		float newX = (Float) args[3];
		float newY = (Float) args[4];
		boolean combine = (Boolean) args[5];

		MultipleFieldsCommand command = new MultipleFieldsCommand(target,
				combine).field(FieldNameForActions.ORIGIN_X, originX)
				.field(FieldNameForActions.ORIGIN_Y, originY)
				.field(FieldNameForActions.X, newX)
				.field(FieldNameForActions.Y, newY);
		controller.command(command);
	}
}
