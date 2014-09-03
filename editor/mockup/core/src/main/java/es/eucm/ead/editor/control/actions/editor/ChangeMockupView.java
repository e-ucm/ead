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

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.Views;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * <p>
 * Changes the editor main view without performing a command.
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>Class</em> The view class</dd>
 * <dd><strong>args[1..n]</strong> <em>Object</em> (Optional) The new view
 * arguments</dd>
 * </dl>
 */
public class ChangeMockupView extends EditorAction {

	@Override
	public boolean validate(Object... args) {
		return args.length > 0 && args[0] instanceof Class;
	}

	@Override
	public void perform(Object... args) {
		Object[] viewArguments = new Object[args.length - 1];
		System.arraycopy(args, 1, viewArguments, 0, viewArguments.length);
		Views views = controller.getViews();

		views.setView((Class) args[0], viewArguments);

		Model model = controller.getModel();
		ModelEntity game = model.getGame();
		if (game != null) {
			EditState editState = Q.getComponent(game, EditState.class);
			String gameId = model.getIdFor(game);
			model.getResource(gameId).setModified(true);
			editState.setView(views.getCurrentView().getClass().getName());
			Array<Object> arguments = editState.getArguments();
			arguments.clear();
			for (int i = 0; i < viewArguments.length; ++i) {
				arguments.add(viewArguments[i]);
			}
			Object object = model.getSelection().getSingle(Selection.SCENE);
			if (object instanceof ModelEntity) {
				String id = model.getIdFor(object);
				editState.setEditScene(id);
			}
		}
	}
}
