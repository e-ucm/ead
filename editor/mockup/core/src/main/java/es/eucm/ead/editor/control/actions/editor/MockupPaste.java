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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.SerializationException;

import es.eucm.ead.editor.control.Clipboard;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.model.AddLabelToScene;
import es.eucm.ead.schema.components.controls.Label;

/**
 * <p>
 * Pastes the content of the clipboard. If it fails, creates a scene element
 * with a label as content of the clipboard.
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd>None</dd>
 * </dl>
 */
public class MockupPaste extends EditorAction {

	private static final int MAX_CHARACTERS = 750;

	@Override
	public void perform(Object... args) {
		Clipboard clipboard = controller.getClipboard();
		try {
			clipboard.paste();
		} catch (SerializationException se) {
			Label label = new Label();
			String contents = clipboard.getContents();
			if (contents.length() > MAX_CHARACTERS) {
				contents = contents.substring(0, MAX_CHARACTERS);
			}
			label.setText(contents);
			controller.action(AddLabelToScene.class, label);
		} catch (Exception ex) {
			Gdx.app.error("MockupPaste", "Error performing paste", ex);
		}

	}

}
