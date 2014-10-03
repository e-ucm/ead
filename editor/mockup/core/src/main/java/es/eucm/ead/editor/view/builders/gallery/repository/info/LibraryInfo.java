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
package es.eucm.ead.editor.view.builders.gallery.repository.info;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.view.widgets.gallery.repository.LibraryItem;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.repo.RepoLibrary;

public class LibraryInfo extends ItemInfo<LibraryItem> {

	private Label size, numberOfelements;
	private String goLibrary, downloadLibrary;

	public LibraryInfo(Controller controller, Actor rootView) {
		super(controller, rootView);
	}

	@Override
	protected void buildWidgets(Table table, I18N i18n, Skin skin) {
		size = new Label("", skin);
		size.setAlignment(Align.center);
		numberOfelements = new Label("", skin);
		numberOfelements.setAlignment(Align.center);
		table.add(i18n.m("size") + ":").left();
		table.row();
		table.add(size).colspan(2);
		table.row();
		table.add(i18n.m("numberOfElements") + ":").left();
		table.row();
		table.add(numberOfelements).colspan(2);
		table.row();

		goLibrary = i18n.m("goToLibrary");
		downloadLibrary = i18n.m("downloadLibrary");
	}

	@Override
	public void show(LibraryItem item) {
		RepoLibrary repoLibrary = item.getRepoLibrary();
		boolean isDownloaded = ((MockupController) controller)
				.getRepositoryManager().isDownloaded(repoLibrary,
						controller.getEditorGameAssets());
		String actionString = null;
		if (isDownloaded) {
			actionString = goLibrary;
		} else {
			actionString = downloadLibrary;
		}
		actionButton.setText(actionString);
		size.setText(String.valueOf(repoLibrary.getSize()));
		numberOfelements.setText(String.valueOf((int) repoLibrary
				.getNumberOfElements()));

		super.show(item);
	}

}
