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
package es.eucm.ead.editor.view.listeners;

import com.badlogic.gdx.Gdx;

import es.eucm.ead.schemax.FieldNames;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.schema.editor.components.Note;

/**
 * Convenience implementation of a {@link Model.FieldListener} for {@link Note}.
 * 
 * Has convenience methods for changes to the {@link FieldNames#NOTE_TITLE
 * title} ( {@link #titleChanged(FieldEvent)} ) and
 * {@link FieldNames#NOTE_DESCRIPTION description} (
 * {@link #descriptionChanged(FieldEvent)} ).
 */
public class ChangeNoteFieldListener implements Model.FieldListener {
	private static final String CHANGENOTE_LOGTAG = "ChangeNoteFieldListener";
	private boolean isDescription;

	@Override
	public boolean listenToField(FieldNames fieldName) {
		this.isDescription = fieldName == FieldNames.NOTE_DESCRIPTION;
		return isDescription || FieldNames.NOTE_TITLE == fieldName;
	}

	@Override
	public void modelChanged(FieldEvent event) {
		if (this.isDescription) {
			descriptionChanged(event);
		} else {
			titleChanged(event);
		}
	}

	/**
	 * This is the method invoked if the {@link String description} of the
	 * {@link Note} has changed correctly.
	 * 
	 * @param event
	 */
	public void descriptionChanged(FieldEvent event) {
		Gdx.app.log(CHANGENOTE_LOGTAG, "Description changed");
	}

	/**
	 * This is the method invoked if the {@link String title} of the
	 * {@link Note} has changed correctly.
	 * 
	 * @param event
	 */
	public void titleChanged(FieldEvent event) {
		Gdx.app.log(CHANGENOTE_LOGTAG, "Title changed");
	}
}
