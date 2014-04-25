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

import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.appdata.ReleaseInfo;
import es.eucm.ead.editor.control.updatesystem.Updater;

/**
 * Checks if there is a newer version of the eAdventure editor available for
 * download. If a newer version is available, tries to open a web browser
 * pointing to the application bundle that is appropriate for the user's
 * platform. Along the process the user may be asked to confirm or deny the
 * update (see comment on the arguments admitted below).
 * 
 * Arguments accepted by this action:
 * {@link es.eucm.ead.editor.control.actions.editor.CheckUpdates} accepts 2
 * arguments that may NOT be null:
 * <ol>
 * <li>
 * <b>args[0]</b> The {@link es.eucm.ead.editor.control.appdata.ReleaseInfo}
 * object that contains information about the local installation of the
 * application. This is necessary to find out user's platform and current app
 * version.</li>
 * <li>
 * <b>args[1]</b> A boolean value indicating if the user must be asked to
 * confirm the update (false) or if the user confirmation can be skipped (true).
 * The appropriate value depends on the source of the action: if it is
 * user-generated, then explicit confirmation can be skipped; if it is
 * system-generated, then the user must confirm the operation, no doubt.</li>
 * </ol>
 * 
 * Created by Javier Torrente on 8/04/14.
 */
public class CheckUpdates extends EditorAction {
	public CheckUpdates() {
		super(true, false, ReleaseInfo.class, Boolean.class);
	}

	@Override
	public void perform(Object... args) {
		ReleaseInfo releaseInfoArg = (ReleaseInfo) args[0];
		Boolean skipUserConfirmationArg = (Boolean) args[1];

		// Initialize the updater
		Updater updater = new Updater(releaseInfoArg, controller,
				skipUserConfirmationArg);
		updater.startUpdateProcess();
	}
}
