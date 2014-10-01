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
import com.badlogic.gdx.scenes.scene2d.Touchable;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.control.commands.SelectionCommand;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.widgets.scenes.SceneEditor;
import es.eucm.ead.schema.editor.components.ElementState;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;

/**
 * Does not touchable the actors selected. Adds a {@link LockProperty} component
 * in {@link ModelEntity} selected.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link SceneEditor}</em></dd>
 * </dl>
 */
public class LockSelection extends ModelAction {

	public LockSelection() {
		super(true, false, SceneEditor.class);
	}

	@Override
	public Command perform(Object... args) {
		SceneEditor sceneEditor = (SceneEditor) args[0];
		Object[] objects = controller.getModel().getSelection()
				.get(Selection.SCENE_ELEMENT);

		CompositeCommand compositeCommand = new CompositeCommand();
		for (Object object : objects) {
			if (object instanceof ModelEntity) {
				ModelEntity entity = (ModelEntity) object;
				Actor actor = sceneEditor.findActor(entity);
				ElementState lock = Q.getComponent(entity, ElementState.class);
				compositeCommand.addCommand(new FieldCommand(actor,
						FieldName.TOUCHABLE, Touchable.disabled));
				compositeCommand.addCommand(new FieldCommand(lock,
						FieldName.LOCK, true));
			}
		}
		compositeCommand.addCommand(new SelectionCommand(controller.getModel(),
				Selection.EDITED_GROUP, Selection.SCENE_ELEMENT,
				new Object[] {}));

		return compositeCommand;
	}

}
