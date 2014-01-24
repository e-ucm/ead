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
package es.eucm.ead.editor.view.options;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.CommandManager;
import es.eucm.ead.editor.model.EditorModel;

/**
 * An option in the user interface.
 * <p>
 * Exposes a control that can display and/or modify a piece of the underlying
 * model. Titles are intended as always-visible labels. Tooltips are only
 * displayed on-demand.
 * 
 */
public interface Option extends EditorModel.ModelListener {

	/**
	 * @return the title to be used in the interface (can be null)
	 */
	String getTitle();

	/**
	 * @return tooltip text. Please do not leave as null
	 */
	String getTooltipText();

	/**
	 * Creates a widget group representing the option
	 * 
	 * @param manager
	 *            command manager
	 * @param skin
	 *            the skin for widgets
	 * @return a widget group
	 */
	Actor getControl(CommandManager manager, Skin skin);

}
