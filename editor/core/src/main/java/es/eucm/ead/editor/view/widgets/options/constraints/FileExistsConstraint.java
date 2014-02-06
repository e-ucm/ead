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
package es.eucm.ead.editor.view.widgets.options.constraints;

import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.view.widgets.options.Option;

/**
 * Constraint for file existence
 */
public class FileExistsConstraint extends Constraint {

	/**
	 * The file must be a directory
	 */
	private boolean directory;

	public FileExistsConstraint(Option<?> option) {
		super(option);
	}

	@Override
	public String getTooltip() {
		// return Editor.i18n.m("constraints.fileexists");
		return null;
	}

	@Override
	public boolean isValid() {
		String file = (String) option.getControlValue();
		FileHandle fh = null; // Editor.assets.resolve(file);
		return fh.exists()
				&& ((directory && fh.isDirectory()) || (!directory && !fh
						.isDirectory()));
	}

	/**
	 * Sets if the file must be a directory
	 * 
	 * @param directory
	 */
	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public boolean isDirectory() {
		return directory;
	}
}
