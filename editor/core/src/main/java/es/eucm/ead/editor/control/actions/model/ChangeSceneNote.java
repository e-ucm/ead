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
package es.eucm.ead.editor.control.actions.model;

import com.badlogic.gdx.Gdx;

import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.FieldNames;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * This class changes the {@link Note} of a
 * {@link es.eucm.ead.schema.entities.ModelEntity}. args[0] can be a
 * {@link es.eucm.ead.schema.entities.ModelEntity} or a {@link String sceneId}.
 * args[1] must be a {@link FieldNames#NOTE_TITLE} or
 * {@link FieldNames#NOTE_DESCRIPTION}. args[2] is the new value.
 * 
 * Created by Cristian Rotaru on 20/03/14.
 */
public class ChangeSceneNote extends ModelAction {

	private static final String CHANGESCENENAME_LOGTAG = "ChangeSceneNote";

	/**
	 * 0: {@link es.eucm.ead.schema.entities.ModelEntity} or a {@link String
	 * sceneId} to be renamed, 1: {@link FieldNames}, 3: new Value
	 * 
	 * @param args
	 */
	@Override
	public Command perform(Object... args) {
		// There should be at least one argument
		if (args.length != 3) {
			throw new EditorActionException("Error in action "
					+ this.getClass().getCanonicalName()
					+ ": expected 3 arguments, received ~> " + args.length);
		}

		Note objectToRename = null;
		// IF first argument is null or a String, it has to be found. In this
		// case, findObjectById is invoked. Subclasses should override this
		// method
		// If they want to support this feature
		if (args[0] != null) {
			if (args[0] instanceof String) {
				objectToRename = Model.getComponent(controller.getModel()
						.getScenes().get(args[0].toString()), Note.class);
			}
			// If the first argument is not to be found, it should have a name
			// attribute declared. Otherwise, throw exception
			else if (args[0] instanceof ModelEntity) {
				objectToRename = Model.getComponent((ModelEntity) args[0],
						Note.class);
			}
		}

		// At this point, if objectToRename is still null, return without doing
		// anything
		if (objectToRename == null) {
			Gdx.app.error(CHANGESCENENAME_LOGTAG, "Error in action "
					+ this.getClass().getCanonicalName()
					+ ": unexpected args[0] ~> " + args[0]);
			return null;
		}

		FieldNames field = null;
		String oldValue = null;
		if (args[1] != null) {
			if (args[1] instanceof FieldNames) {
				field = (FieldNames) args[1];
				switch (field) {
				case NOTE_DESCRIPTION:
					oldValue = objectToRename.getDescription();
					break;
				case NOTE_TITLE:
					oldValue = objectToRename.getTitle();
					break;
				default:
					field = null;
					break;
				}
			}
		}
		if (field == null) {
			Gdx.app.error(CHANGESCENENAME_LOGTAG, "Error in action "
					+ this.getClass().getCanonicalName()
					+ ": unexpected args[1] ~> " + args[1]);
			return null;
		}

		// Now check the second argument.
		String newValue = null;
		if (args[2] != null) {
			newValue = args[2].toString();
		} else {
			Gdx.app.error(CHANGESCENENAME_LOGTAG, "Error in action "
					+ this.getClass().getCanonicalName() + ": args[2] is null.");
			return null;
		}

		if (newValue != null
				&& (oldValue == null || !oldValue.equals(newValue))) {
			return new FieldCommand(objectToRename, field, newValue, true);
		}
		return null;
	}
}
