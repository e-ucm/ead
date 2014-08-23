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
package es.eucm.ead.editor.view.builders.gallery;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.control.MockupController.BackListener;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.ExitMockup;
import es.eucm.ead.editor.control.actions.editor.NewGame;
import es.eucm.ead.editor.control.actions.editor.OpenMockupGame;
import es.eucm.ead.editor.view.widgets.gallery.GalleryItem;
import es.eucm.ead.editor.view.widgets.gallery.ProjectItem;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;

public class ProjectsView extends BaseGallery implements BackListener {

	public static final FileHandle MOCKUP_PROJECT_FILE = Gdx.files
			.external("/eAdventureMockup/");

	@Override
	protected Actor createPlayButton() {
		return null;
	}

	@Override
	protected Actor createBackButton() {
		return null;
	}

	@Override
	protected Actor createToolbarText() {
		Image eAdventure = new Image(skin, "eAdventure");
		eAdventure.setScaling(Scaling.fit);
		return eAdventure;
	}

	@Override
	protected String getNewButtonIcon() {
		return "new_project80x80";
	}

	@Override
	protected void newItem() {
		ModelEntity defaultGame = controller.getTemplates().createGame("", "",
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		int i = 0;
		FileHandle file;
		do {

			file = MOCKUP_PROJECT_FILE.child(String.valueOf(++i));

		} while (file.exists());

		controller.action(NewGame.class, file.file().getAbsolutePath(),
				defaultGame);
		controller.action(ChangeView.class, ScenesView.class);
	}

	@Override
	protected void loadItems(Array<GalleryItem> items) {
		items.clear();
		FileHandle[] list = MOCKUP_PROJECT_FILE.list();
		for (int i = 0; i < list.length; ++i) {
			FileHandle child = list[i];
			if (child.child(GameStructure.GAME_FILE).exists()) {
				items.add(new ProjectItem(controller, child.file()
						.getAbsolutePath(), this));
			}
		}
	}

	@Override
	public void itemClicked(GalleryItem item) {
		controller.action(OpenMockupGame.class,
				((ProjectItem) item).getProjectPath(), topBar.getStage());
		if (controller.getViews().getCurrentView() == this) {
			controller.action(ChangeView.class, ScenesView.class);
		}
	}

	@Override
	public void onBackPressed() {
		controller.action(ExitMockup.class);
	}
}
