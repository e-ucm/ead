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
package es.eucm.ead.editor.control.workers;

import com.badlogic.gdx.files.FileHandle;
import es.eucm.utils.gdx.ZipUtils;

/**
 * Uncompresses a given .zip file.
 * <dl>
 * <dt><strong>The input arguments are</strong></dt>
 * <dd><strong>args[0]</strong> <em>FileHandle</em> FileHandle to the compressed
 * (.zip) file.
 * <dd><strong>args[1]</strong> <em>FileHandle</em> FileHandle to the
 * destination (output) folder.</dd>
 * </dl>
 * <dl>
 * <dt><strong>The result argument is</strong></dt>
 * <dd><strong>args[0]</strong> <em>Boolean</em> success.
 * </dl>
 */
public class UnzipFile extends Worker {

	@Override
	protected void prepare() {

	}

	@Override
	protected boolean step() {
		FileHandle sourceZip = (FileHandle) args[0];
		FileHandle outputFolder = (FileHandle) args[1];
		result(ZipUtils.unzip(sourceZip, outputFolder));
		return true;
	}
}
