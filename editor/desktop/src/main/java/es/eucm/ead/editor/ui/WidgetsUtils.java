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
package es.eucm.ead.editor.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.Action;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.IconButton;

/**
 * Contains a bunch of util methods to create editor widgets
 */
public class WidgetsUtils {

	/**
	 * @return a button with the given drawable, that, when clicked executes the
	 *         given {@link Action}
	 */
	public static <T extends Action> IconButton createIcon(
			Controller controller, String drawable, float imagePadding,
			Skin skin, String tooltip, Class<T> editorAction,
			Object... actionArgs) {
		IconButton iconButton = new IconButton(drawable, imagePadding, skin);
		iconButton.addListener(new ActionOnClickListener(controller,
				editorAction, actionArgs));
		iconButton.setTooltip(tooltip);
		return iconButton;
	}
}
