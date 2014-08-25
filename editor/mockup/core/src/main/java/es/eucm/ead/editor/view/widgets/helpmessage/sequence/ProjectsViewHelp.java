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
package es.eucm.ead.editor.view.widgets.helpmessage.sequence;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.RepositoryManager;
import es.eucm.ead.editor.view.builders.gallery.ProjectsView;
import es.eucm.ead.editor.view.widgets.PositionedHiddenPanel.Position;
import es.eucm.ead.editor.view.widgets.helpmessage.TextHelpMessage;
import es.eucm.ead.engine.I18N;

public class ProjectsViewHelp extends HelpSequence {

	public ProjectsViewHelp(Skin skin, I18N i18n, ProjectsView projectScreen,
			Actor newProjectButton) {
		super(projectScreen);

		addHelpMessage(new TextHelpMessage(skin, i18n, Position.BOTTOM,
				newProjectButton, "help.newProject"));
	}

	@Override
	public boolean getCondition() {
		boolean show = true;
		FileHandle[] list = ProjectsView.MOCKUP_PROJECT_FILE.list();
		if (list != null) {
			for (int i = 0; i < list.length; ++i) {
				FileHandle fileHandle = list[i];
				if (!fileHandle.path().endsWith(
						RepositoryManager.REPOSITORY_FOLDER_NAME)) {
					show = false;
					break;
				}
			}
		}
		return super.getCondition() && show;
	}
}
